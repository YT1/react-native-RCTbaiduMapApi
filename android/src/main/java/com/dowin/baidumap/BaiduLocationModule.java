package com.dowin.baidumap;

import android.content.Context;
import android.util.Log;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;

/**
 * Created by dowin on 2016/12/2.
 */

public class BaiduLocationModule extends ReactContextBaseJavaModule {

    public static final String RCT_CLASS = "RNBaiduLocation";
    public static final String TAG = "RNBaiduLocation";
    private LocationClient mLocationClient;
    private Callback locationCallback;
    private Context context;

    public BaiduLocationModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext.getBaseContext();
    }

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    @ReactMethod
    public void requestLocation(Callback callback) {

        locationCallback = callback;
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(context.getApplicationContext());     //声明LocationClient类
            mLocationClient.registerLocationListener(new BDLocationListener() {
                @Override
                public void onReceiveLocation(BDLocation location) {

                    Log.d(TAG, "" + location.getLatitude()
                            + "" + location.getLongitude()
                            + "" + location.getCountry()
                            + "" + location.getCity()
                            + "" + location.getAddrStr());
                    if (locationCallback != null) {
                        WritableMap object = Arguments.createMap();
                            object.putString("latitude",location.getLatitude()+"");
                            object.putString("longitude",location.getLongitude()+"");
                            object.putString("country",location.getCountry());
                            object.putString("city",location.getCity());
                            object.putString("address",location.getAddrStr());
                        locationCallback.invoke(object);
                    }
                }
            });    //注册监听函数

            initLocation(mLocationClient);
        }
        if(!mLocationClient.isStarted()){
            mLocationClient.start();
        }
        mLocationClient.requestLocation();
    }

    void initLocation(LocationClient mLocationClient) {
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 30000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当GPS有效时按照1S/1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        option.SetIgnoreCacheException(true);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤GPS仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }
}
