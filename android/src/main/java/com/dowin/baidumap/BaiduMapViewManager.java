package com.dowin.baidumap;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.navi.BaiduMapNavigation;
import com.baidu.mapapi.navi.NaviParaOption;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRoutePlanOption;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRoutePlanOption;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.LayoutShadowNode;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import java.util.Map;

import javax.annotation.Nullable;

/**
 * Created by dowin on 2016/11/28.
 */

public class BaiduMapViewManager extends SimpleViewManager<View> implements LifecycleEventListener {
    public static final String RCT_CLASS = "RCTBaiduMap";
    public static final String TAG = "RCTBaiduMap";

    private Activity mActivity;
    private MapView view;
    private BaiduMapView baiduMapView;
    private ReactContext reactContext;
    private BaiduNav baiduNav;

    @Override
    public LayoutShadowNode createShadowNodeInstance() {
        return new BaiduMapNode();
    }

    @Override
    public Class getShadowNodeClass() {
        return BaiduMapNode.class;
    }

    public BaiduMapViewManager(Activity activity) {
        mActivity = activity;
    }

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    @Nullable
    @Override
    public Map<String, Object> getExportedCustomDirectEventTypeConstants() {
        MapBuilder.Builder builder = MapBuilder.builder();
        builder.put("onLocation", MapBuilder.of("registrationName", "onLocation"));
        return builder.build();
    }

    @Override
    protected View createViewInstance(ThemedReactContext reactContext) {
        Log.i(TAG, "createViewInstance--" + reactContext.getClass().getName());
        reactContext.addLifecycleEventListener(this);
        this.reactContext = reactContext;
        baiduMapView = new BaiduMapView(mActivity);
        view = baiduMapView.getBaiduView();
        baiduMapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), ((TextView) v).getText(), Toast.LENGTH_SHORT).show();

                final boolean toApp = false;
                if (!toApp) {
                    if (baiduNav == null) {
                        baiduNav = new BaiduNav(mActivity);
                    }
                    baiduNav.routeplanToNavi(startLatlng, startAddress, endLatlng, endtAddress);
                } else {
                    NaviParaOption option = new NaviParaOption();
                    option.startPoint(startLatlng).startName(startAddress)
                            .endPoint(endLatlng).endName(endtAddress);
                    BaiduMapNavigation.setSupportWebNavi(true);
                    BaiduMapNavigation.openBaiduMapNavi(option, mActivity);
                }

            }
        });
        setInit(baiduMapView, true);
        return baiduMapView;
    }

    Marker marker;

    LatLng startLatlng;
    String startAddress = "";
    LatLng endLatlng;
    String endtAddress = "";

    public void addTouch(final MapView mapView) {
        mapView.getMap().setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (marker != null) {
                    marker.remove();
                    marker = null;
                }
                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
                OverlayOptions options = new MarkerOptions()
                        .position(latLng)
                        .icon(bitmapDescriptor)
                        .zIndex(9)
                        .draggable(true);
                marker = (Marker) (mapView.getMap().addOverlay(options));

                if (startLatlng != null) {
                    PlanNode begin = PlanNode.withLocation(startLatlng);
                    endLatlng = latLng;
                    PlanNode end = PlanNode.withLocation(endLatlng);
                    search(routePlanSearch, 3, begin, end);
                }
                WritableMap object = Arguments.createMap();
                object.putDouble("latitude", latLng.latitude);
                object.putDouble("longitude", latLng.longitude);
                reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onLocation", object);
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
    }

    RoutePlanSearch routePlanSearch;
    WalkingRouteOverlay walkingRouteOverlay;

    public void addSearch(final MapView mapView) {
        routePlanSearch = RoutePlanSearch.newInstance();
        routePlanSearch.setOnGetRoutePlanResultListener(new OnGetRoutePlanResultListener() {
            @Override
            public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

                if (walkingRouteResult == null || walkingRouteResult.error != SearchResult.ERRORNO.NO_ERROR) {
                    return;
                }
                if (walkingRouteResult.getRouteLines() != null && walkingRouteResult.getRouteLines().size() > 0) {
                    WalkingRouteLine route = walkingRouteResult.getRouteLines().get(0);

                    BaiduMap baiduMap = mapView.getMap();
//                    baiduMap.clear();
                    if (walkingRouteOverlay != null) {
                        walkingRouteOverlay.removeFromMap();
                    }
                    WalkingRouteOverlay overlay = new WalkingRouteOverlay(baiduMap);
                    walkingRouteOverlay = overlay;
                    baiduMap.setOnMarkerClickListener(overlay);
                    overlay.setData(route);
                    overlay.addToMap();
                    overlay.zoomToSpan();
                }
            }

            @Override
            public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

            }

            @Override
            public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

            }

            @Override
            public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {

            }

            @Override
            public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

            }

            @Override
            public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

            }
        });
    }

    public void search(RoutePlanSearch routePlanSearch, int searchType, PlanNode beginNode, PlanNode endNode) {
        switch (searchType) {
            case 0:
                routePlanSearch.masstransitSearch(new MassTransitRoutePlanOption().from(beginNode).to(endNode));
                break;
            case 1:
                routePlanSearch.drivingSearch(new DrivingRoutePlanOption().from(beginNode).to(endNode));
                break;
            case 2:
                routePlanSearch.bikingSearch(new BikingRoutePlanOption().from(beginNode).to(endNode));
                break;
            case 3:
                routePlanSearch.walkingSearch(new WalkingRoutePlanOption().from(beginNode).to(endNode));
                break;
        }
    }

    @ReactProp(name = "init", defaultBoolean = true)
    public void setInit(BaiduMapView view, boolean init) {
        Log.i(TAG, "init:" + init);
        if (!init) {
            return;
        }
        MapView mapView = view.getBaiduView();
        addTouch(mapView);
        addSearch(mapView);
    }

    /**
     * 地图模式
     *
     * @param view
     * @param span
     */
    @ReactProp(name = "span", defaultInt = 0)
    public void setSpan(BaiduMapView view, int span) {
        Log.i(TAG, "mode:" + span);

    }

    /**
     * 地图模式
     *
     * @param view
     * @param type 1. 普通
     *             2.卫星
     */
    @ReactProp(name = "mode", defaultInt = 1)
    public void setMode(BaiduMapView view, int type) {
        Log.i(TAG, "mode:" + type);
        MapView mapView = view.getBaiduView();
        mapView.getMap().setMapType(type);
        mapView.getMap().setMyLocationEnabled(true);
    }

    /**
     * 实时交通图
     *
     * @param view
     * @param isEnabled
     */
    @ReactProp(name = "trafficEnabled", defaultBoolean = false)
    public void setTrafficEnabled(BaiduMapView view, boolean isEnabled) {
        Log.d(TAG, "trafficEnabled:" + isEnabled);
        MapView mapView = view.getBaiduView();
        mapView.getMap().setTrafficEnabled(isEnabled);
    }

    /**
     * 实时道路热力图
     *
     * @param view
     * @param isEnabled
     */
    @ReactProp(name = "heatMapEnabled", defaultBoolean = false)
    public void setHeatMapEnabled(BaiduMapView view, boolean isEnabled) {
        Log.d(TAG, "heatMapEnabled" + isEnabled);
        MapView mapView = view.getBaiduView();
        mapView.getMap().setBaiduHeatMapEnabled(isEnabled);
    }


    /**
     * 显示地理标记
     *
     * @param view
     * @param array
     */
    @ReactProp(name = "marker")
    public void setMarker(BaiduMapView view, ReadableArray array) {
        Log.d(TAG, "marker:" + array);
        if (array != null) {
            for (int i = 0; i < array.size(); i++) {
                ReadableArray sub = array.getArray(i);
                if (sub == null) {
                    continue;
                }
                //定义Maker坐标点
                LatLng point = new LatLng(sub.getDouble(0), sub.getDouble(1));
                //构建Marker图标
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
                //构建MarkerOption，用于在地图上添加Marker
                OverlayOptions option = new MarkerOptions()
                        .position(point)
                        .icon(bitmap)
                        .draggable(true);
                //在地图上添加Marker，并显示
//                mapView.getMap().addOverlay(option);
            }
        }
    }

    @ReactProp(name = "location")
    public void setLocation(BaiduMapView view, ReadableMap location) {
        Log.d(TAG, "location:" + location);
        if (location == null) {
            return;
        }
//        address: '中国广东省广州市天河区临江大道5号',
//                city: '广州市',
//                country: '中国',
//                radius: 30,
//                longitude: 113.324209,
//                latitude: 23.120426
        LatLng ll = new LatLng(location.getDouble("latitude"),
                location.getDouble("longitude"));
        startLatlng = ll;
        startAddress = location.getString("address");
        MapView mapView = view.getBaiduView();
        float radius = (float) location.getDouble("radius");
        BaiduMapUtil.setBaiduLocation(mapView, ll, radius);
    }

    @Override
    public void onHostResume() {
        Log.i(TAG, "onHostDestroy");
    }

    @Override
    public void onHostPause() {
        Log.i(TAG, "onHostResume");
    }

    @Override
    public void onHostDestroy() {
        Log.i(TAG, "onHostDestroy");
    }
}
