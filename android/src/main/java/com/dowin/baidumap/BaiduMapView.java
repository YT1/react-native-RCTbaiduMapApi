package com.dowin.baidumap;

import android.app.Activity;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.baidu.mapapi.map.MapView;
import com.facebook.react.bridge.ReactContext;

/**
 * Created by dowin on 2016/12/5.
 */

public class BaiduMapView extends FrameLayout implements View.OnClickListener {

    private MapView baiduView;
    private View.OnClickListener onClickListener;

    public BaiduMapView(Activity activity) {
        super(activity);

        baiduView = new MapView(activity);
        this.addView(baiduView);
        Button btn = new Button(activity);
        btn.setOnClickListener(this);
        btn.setText("导航");
        btn.setGravity(Gravity.CENTER);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT, Gravity.BOTTOM);
        this.addView(btn, params);
    }

    @Override
    public void onClick(View v) {
        if (onClickListener != null) {
            onClickListener.onClick(v);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        onClickListener = listener;
    }

    public MapView getBaiduView() {
        return baiduView;
    }
}
