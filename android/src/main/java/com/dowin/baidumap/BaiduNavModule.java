package com.dowin.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.location.LocationClient;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BNaviSettingManager;
import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dowin on 2016/12/7.
 */

public class BaiduNavModule extends ReactContextBaseJavaModule implements ActivityEventListener, LifecycleEventListener {

    public static final String RCT_CLASS = "BaiduApi";
    public static final String TAG = "BaiduApi";

    private Activity mActivity;
    private ReactContext mContext;
    private String mSDCardPath;
    private String dir;
    private boolean isInit = false;
    private boolean isIniting = false;

    private LocationClient mLocationClient;
    private BaiduLocation bdLocation;

    public BaiduNavModule(ReactApplicationContext reactContext, Activity activity) {
        super(reactContext);
        reactContext.addActivityEventListener(this);
        reactContext.addActivityEventListener(this);
        mActivity = activity;
        BNOuterLogUtil.setLogSwitcher(true);
        mContext = reactContext;
        mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        dir = mContext.getPackageName();
        File file = new File(mSDCardPath, dir);
        if (!file.exists()) {
            file.mkdirs();
        }
        if (!file.exists()) {
            file = new File(mContext.getFilesDir(), dir);
            file.mkdirs();
        }
        Log.i(TAG, file.getAbsolutePath());

    }

    @Override
    public String getName() {
        return RCT_CLASS;
    }

    public void initNav(BaiduNaviManager.NaviInitListener initListener) {
        if (isIniting) {
            Toast.makeText(mContext, "百度导航引擎正在初始化", Toast.LENGTH_SHORT).show();
            return;
        }
        isIniting = true;
        BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

            @Override
            public void playEnd() {
            }

            @Override
            public void playStart() {
            }
        };
        Handler ttsHandler = new Handler() {
            public void handleMessage(Message msg) {
                int type = msg.what;
                switch (type) {
                    case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                        break;
                    }
                    case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                        break;
                    }
                    default:
                        break;
                }
            }
        };
        BaiduNaviManager.getInstance().init(mActivity, mSDCardPath, dir, initListener, null, ttsHandler, ttsPlayStateListener);
    }

    public void navigationDetect(double startLongitude, double startLatitude, String startDesc, double endLongitude, double endLatitude, String endDesc) {
        BNRoutePlanNode sNode = new BNRoutePlanNode(startLongitude, startLatitude, startDesc, null, BNRoutePlanNode.CoordinateType.BD09LL);
        BNRoutePlanNode eNode = new BNRoutePlanNode(endLongitude, endLatitude, endDesc, null, BNRoutePlanNode.CoordinateType.BD09LL);
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(mActivity, list, 1, true, new DemoRoutePlanListener(mActivity, BaiduNavigationActivity.class, sNode));
        }
    }

    private void initSetting() {
        BNaviSettingManager.setDayNightMode(BNaviSettingManager.DayNightMode.DAY_NIGHT_MODE_DAY);
        BNaviSettingManager.setShowTotalRoadConditionBar(BNaviSettingManager.PreViewRoadCondition.ROAD_CONDITION_BAR_SHOW_ON);
        BNaviSettingManager.setVoiceMode(BNaviSettingManager.VoiceMode.Veteran);
        BNaviSettingManager.setPowerSaveMode(BNaviSettingManager.PowerSaveMode.DISABLE_MODE);
        BNaviSettingManager.setRealRoadCondition(BNaviSettingManager.RealRoadCondition.NAVI_ITS_ON);
    }

    /**
     * 百度导航
     *
     * @param map
     * @param promise
     */
    @ReactMethod
    public void openBaiduNavigationDetect(ReadableMap map, Promise promise) {
        Log.i(TAG, "[openBaiduLocationDetect]-" + map == null ? "null" : map.toString());

//        'startlongitude':'113.324049','startlatitude':'23.120321','endlongitude':'113.331230','endlatitude':'23.154947'
//        address
        final double startLongitude = map.getDouble("startlongitude");
        final double startLatitude = map.getDouble("startlatitude");
        final String startDesc = "start";

        final double endLongitude = map.getDouble("endlongitude");
        final double endLatitude = map.getDouble("endlatitude");
        final String endDesc = "end";
        if (isInit) {
            navigationDetect(startLongitude, startLatitude, startDesc, endLongitude, endLatitude, endDesc);
        } else {
            initNav(new BaiduNaviManager.NaviInitListener() {
                @Override
                public void onAuthResult(int status, String msg) {
                    final String authinfo = 0 == status ? "key校验成功!" : "key校验失败, " + msg;
                    mActivity.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            Toast.makeText(mContext, authinfo, Toast.LENGTH_LONG).show();
                        }
                    });
                }

                public void initSuccess() {
                    isInit = true;
                    Toast.makeText(mActivity, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
                    navigationDetect(startLongitude, startLatitude, startDesc, endLongitude, endLatitude, endDesc);
                    initSetting();
                }

                public void initStart() {
                    Toast.makeText(mActivity, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
                }

                public void initFailed() {
                    Toast.makeText(mActivity, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    public void addLocation() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(mContext);
            bdLocation = new BaiduLocation(null, mContext);
            mLocationClient.registerLocationListener(bdLocation);
            BaiduMapUtil.initLocation(mLocationClient, 5000);
        }
    }


    /**
     * 百度地图界面
     *
     * @param map
     * @param promise
     */
    @ReactMethod
    public void openBaiduLocationDetect(ReadableMap map, Promise promise) {

        Log.i(TAG, "[openBaiduLocationDetect]-" + map == null ? "null" : map.toString());
    }
    @ReactMethod
    public void stopLocation(ReadableMap map, Promise promise) {

        Log.i(TAG, "[stopLocation]-" + map == null ? "null" : map.toString());
        if (mLocationClient != null) {
            mLocationClient.stop();
        }
    }

    /**
     * 百度定位
     *
     * @param map
     * @param promise
     */
    @ReactMethod
    public void openMapLocation(ReadableMap map, Promise promise) {

        Log.i(TAG, "[openMapLocation]-" + map == null ? "null" : map.toString());
        if (mLocationClient == null) {
            addLocation();
        }
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        mLocationClient.requestLocation();
        bdLocation.sendLocationJs();
    }


    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult");
    }

    @Override
    public void onNewIntent(Intent intent) {
        Log.i(TAG, "onNewIntent");
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
        mLocationClient.stop();
    }
}
