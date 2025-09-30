package com.luck.picture.lib.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.view.ViewCompat;

import com.luck.picture.lib.R;
import com.luck.picture.lib.config.SelectorConfig;
import com.luck.picture.lib.config.SelectorProviders;
import com.luck.picture.lib.style.DynamicAddSelectBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.utils.StyleUtils;

public class DynamicAddSelectBar extends LinearLayout implements View.OnClickListener {

    protected LinearLayout llMoreBar;
    protected TextView tvTips;
    protected TextView tvMore;
    protected SelectorConfig config;


    public DynamicAddSelectBar(Context context) {
        super(context);
        init();
    }

    public DynamicAddSelectBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DynamicAddSelectBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflateLayout();
        config = SelectorProviders.getInstance().getSelectorConfig();
        llMoreBar = findViewById(R.id.ll_dynamic_add_select);
        tvTips = findViewById(R.id.tv_dynamic_add_select_tips);
        tvMore = findViewById(R.id.tv_dynamic_add_select_btn);
        tvMore.setOnClickListener(this);
        handleLayoutUI();
    }

    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_dynamic_add_select_bar, this);
    }

    protected void handleLayoutUI() {

    }

    public LinearLayout getMoreBar() {
        return llMoreBar;
    }

    public TextView getTvTips() {
        return tvTips;
    }

    public TextView getTvMore() {
        return tvMore;
    }

    /**
     * Set tips
     *
     * @param tips
     */
    public void setTips(String tips) {
        tvTips.setText(tips);
    }

    /**
     * Get title text
     */
    public String getTipsText() {
        return tvTips.getText().toString();
    }

    public void setDynamicAddSelectBarStyle() {
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        DynamicAddSelectBarStyle barStyle = selectorStyle.getDynamicAddSelectBarStyle();

        int backgroundColor = barStyle.getBarBackgroundColor();
        if (StyleUtils.checkStyleValidity(backgroundColor)) {
            setBackgroundColor(backgroundColor);
        }

        int tipsTextSize = barStyle.getTipsTextSize();
        if (StyleUtils.checkSizeValidity(tipsTextSize)) {
            tvTips.setTextSize(tipsTextSize);
        }
        int tipsTextColor = barStyle.getTipsTextColor();
        if (StyleUtils.checkStyleValidity(tipsTextColor)) {
            tvTips.setTextColor(tipsTextColor);
        }
        int moreTextSize = barStyle.getMoreTextSize();
        if (StyleUtils.checkSizeValidity(moreTextSize)) {
            tvMore.setTextSize(moreTextSize);
        }
        int moreTextColor = barStyle.getMoreTextColor();
        if (StyleUtils.checkStyleValidity(moreTextColor)) {
            tvMore.setTextColor(moreTextColor);
        }
        int moreBackgroundColor = barStyle.getMoreBackgroundColor();
        if (StyleUtils.checkStyleValidity(moreBackgroundColor)) {
            ViewCompat.setBackgroundTintList(tvMore, ColorStateList.valueOf(moreBackgroundColor));
        }
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.tv_dynamic_add_select_btn) {
            if (pickSelectMoreBarListener != null) {
                pickSelectMoreBarListener.onDynamicAddClick();
            }
        }
    }

    protected OnDynamicAddSelectBarListener pickSelectMoreBarListener;

    /**
     * PickSelectMoreBar的功能事件回调
     *
     * @param listener
     */
    public void setOnPickSelectMoreBarListener(OnDynamicAddSelectBarListener listener) {
        this.pickSelectMoreBarListener = listener;
    }

    public static class OnDynamicAddSelectBarListener {
        /**
         * 点击选择更多
         */
        public void onDynamicAddClick() {

        }
    }
}
