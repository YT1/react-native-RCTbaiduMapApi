package com.dowin.baidumap;

import android.app.Activity;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.model.LatLng;
import com.baidu.navisdk.adapter.BNOuterLogUtil;
import com.baidu.navisdk.adapter.BNOuterTTSPlayerCallback;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dowin on 2016/12/5.
 */

public class BaiduNav {

    final static String TAG = BaiduNav.class.getSimpleName();

    private Activity mContext;

    public BaiduNav(Activity context) {
        BNOuterLogUtil.setLogSwitcher(true);
        mContext = context;
        String mSDCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String dir = mContext.getPackageName();
        File file = new File(mSDCardPath,dir);
        if(!file.exists()){
            file.mkdirs();
        }
        if(!file.exists()){
            file = new File(mContext.getFilesDir(),dir);
            file.mkdirs();
        }
        Log.i(TAG,file.getAbsolutePath());

        BaiduNaviManager.getInstance().init(mContext, mSDCardPath, dir, new BaiduNaviManager.NaviInitListener() {
            @Override
            public void onAuthResult(int status, String msg) {
                final String authinfo = 0 == status?"key校验成功!":"key校验失败, " + msg;
                mContext.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(mContext, authinfo, Toast.LENGTH_LONG).show();
                    }
                });
            }

            public void initSuccess() {
                isInit = true;
                Toast.makeText(mContext, "百度导航引擎初始化成功", Toast.LENGTH_SHORT).show();
//                initSetting();
            }

            public void initStart() {
                Toast.makeText(mContext, "百度导航引擎初始化开始", Toast.LENGTH_SHORT).show();
            }

            public void initFailed() {
                Toast.makeText(mContext, "百度导航引擎初始化失败", Toast.LENGTH_SHORT).show();
            }


        }, null, ttsHandler, ttsPlayStateListener);
    }

    boolean isInit = false;
    public void routeplanToNavi(LatLng sLatlng, String sDesc, LatLng eLatlng, String eDesc) {
        if(!isInit){
            Toast.makeText(mContext, "百度导航引擎！", Toast.LENGTH_SHORT).show();
            return;
        }
        BNRoutePlanNode sNode = null;
        BNRoutePlanNode eNode = null;

        BNRoutePlanNode.CoordinateType coType = BNRoutePlanNode.CoordinateType.BD09LL;
        switch (coType) {
            case GCJ02: {
                sNode = new BNRoutePlanNode(116.30142, 40.05087, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.39750, 39.90882, "北京天安门", null, coType);
                break;
            }
            case WGS84: {
                sNode = new BNRoutePlanNode(116.300821, 40.050969, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(116.397491, 39.908749, "北京天安门", null, coType);
                break;
            }
            case BD09_MC: {
                sNode = new BNRoutePlanNode(12947471, 4846474, "百度大厦", null, coType);
                eNode = new BNRoutePlanNode(12958160, 4825947, "北京天安门", null, coType);
                break;
            }
            case BD09LL: {
                sNode = new BNRoutePlanNode(sLatlng.longitude, sLatlng.latitude, sDesc, null, coType);
                eNode = new BNRoutePlanNode(eLatlng.longitude, eLatlng.latitude, eDesc, null, coType);
                break;
            }
            default:
                break;
        }
        if (sNode != null && eNode != null) {
            List<BNRoutePlanNode> list = new ArrayList<BNRoutePlanNode>();
            list.add(sNode);
            list.add(eNode);
            BaiduNaviManager.getInstance().launchNavigator(mContext, list, 1, true, new DemoRoutePlanListener(mContext, BaiduNavigationActivity.class, sNode));
        }
    }
    private void showToastMsg(final String msg) {
        mContext.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private BaiduNaviManager.TTSPlayStateListener ttsPlayStateListener = new BaiduNaviManager.TTSPlayStateListener() {

        @Override
        public void playEnd() {
            showToastMsg("TTSPlayStateListener : TTS play end");
        }

        @Override
        public void playStart() {
            showToastMsg("TTSPlayStateListener : TTS play start");
        }
    };
    private Handler ttsHandler = new Handler() {
        public void handleMessage(Message msg) {
            int type = msg.what;
            switch (type) {
                case BaiduNaviManager.TTSPlayMsgType.PLAY_START_MSG: {
                    showToastMsg("Handler : TTS play start");
                    break;
                }
                case BaiduNaviManager.TTSPlayMsgType.PLAY_END_MSG: {
                    showToastMsg("Handler : TTS play end");
                    break;
                }
                default:
                    break;
            }
        }
    };
    BNOuterTTSPlayerCallback callback = new BNOuterTTSPlayerCallback() {
        @Override
        public int getTTSState() {
            return 0;
        }

        @Override
        public int playTTSText(String s, int i) {
            return 0;
        }

        @Override
        public void phoneCalling() {

        }

        @Override
        public void phoneHangUp() {

        }

        @Override
        public void initTTSPlayer() {

        }

        @Override
        public void releaseTTSPlayer() {

        }

        @Override
        public void stopTTS() {

        }

        @Override
        public void resumeTTS() {

        }

        @Override
        public void pauseTTS() {

        }
    };
}
