package com.dowin.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.baidu.navisdk.adapter.BaiduNaviManager;

/**
 * Created by dowin on 2016/12/8.
 */

public class DemoRoutePlanListener implements BaiduNaviManager.RoutePlanListener {

    public final static String ROUTE_PLAN_NODE = "route_plan_node";
    private BNRoutePlanNode mBNRoutePlanNode = null;
    private Activity mActivity;
    private Class guideClass;

    public DemoRoutePlanListener(Activity mActivity, Class guideClass, BNRoutePlanNode node) {
        mBNRoutePlanNode = node;
        this.mActivity = mActivity;
        this.guideClass = guideClass;
    }

    @Override
    public void onJumpToNavigator() {
        Intent intent = new Intent(mActivity, guideClass);
        Bundle bundle = new Bundle();
        bundle.putSerializable(ROUTE_PLAN_NODE, (BNRoutePlanNode) mBNRoutePlanNode);
        intent.putExtras(bundle);
        mActivity.startActivity(intent);

    }

    @Override
    public void onRoutePlanFailed() {
        // TODO Auto-generated method stub
        Toast.makeText(mActivity, "算路失败", Toast.LENGTH_SHORT).show();
    }
}