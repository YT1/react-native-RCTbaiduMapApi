package com.dowin.baidumap;

import com.facebook.csslayout.CSSMeasureMode;
import com.facebook.csslayout.CSSNode;
import com.facebook.csslayout.CSSNodeAPI;
import com.facebook.react.uimanager.LayoutShadowNode;

/**
 * Created by dowin on 2016/11/25.
 */

public class BaiduMapNode extends LayoutShadowNode implements CSSNode.MeasureFunction {

    public BaiduMapNode() {
        setMeasureFunction(this);
    }

    @Override
    public long measure(CSSNodeAPI node, float width, CSSMeasureMode widthMode, float height, CSSMeasureMode heightMode) {
        return 0;
    }
}

