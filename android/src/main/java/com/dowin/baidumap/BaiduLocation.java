package com.dowin.baidumap;

import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * Created by dowin on 2016/12/5.
 */

public class BaiduLocation implements BDLocationListener{

    final static String TAG = BaiduLocation.class.getSimpleName();
    private MapView mapView;

    private BDLocation location;
    private ReactContext mContext = null;
    public BaiduLocation(MapView mapView,ReactContext context){
        this.mapView = mapView;
        mContext = context;
    }
    @Override
    public void onReceiveLocation(BDLocation location) {

        this.location = location;
        Log.d(TAG, "" + location.getLatitude()
                + "" + location.getLongitude()
                + "" + location.getCountry()
                + "" + location.getCity()
                + "" + location.getAddrStr());

        sendLocationJs();
        LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
        BaiduMapUtil.setBaiduLocation(mapView, ll, location.getRadius());



    }

    public void sendLocationJs(){
        if(location==null){
            return;
        }
        WritableMap object = Arguments.createMap();
        object.putDouble("latitude", location.getLatitude());
        object.putDouble("longitude", location.getLongitude());
        object.putString("country", location.getCountry());
        object.putString("city", location.getCity());
        object.putDouble("radius", location.getRadius());
        object.putString("address", location.getAddrStr());
        mContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("callBack", object);
    }
    public BDLocation getLocation() {
        return location;
    }
}
