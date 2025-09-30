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
import com.luck.picture.lib.style.ReselectionBarStyle;
import com.luck.picture.lib.style.PictureSelectorStyle;
import com.luck.picture.lib.utils.StyleUtils;

public class ReselectionBar extends LinearLayout implements View.OnClickListener {

    protected LinearLayout llReselectionBar;
    protected TextView tvTips;
    protected TextView tvMore;
    protected SelectorConfig config;


    public ReselectionBar(Context context) {
        super(context);
        init();
    }

    public ReselectionBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ReselectionBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    protected void init() {
        inflateLayout();
        config = SelectorProviders.getInstance().getSelectorConfig();
        llReselectionBar = findViewById(R.id.ll_reselection);
        tvTips = findViewById(R.id.tv_reselection_tips);
        tvMore = findViewById(R.id.tv_reselection_btn);
        tvMore.setOnClickListener(this);
        handleLayoutUI();
    }

    protected void inflateLayout() {
        LayoutInflater.from(getContext()).inflate(R.layout.ps_reselection_bar, this);
    }

    protected void handleLayoutUI() {

    }

    public LinearLayout getReselectionBar() {
        return llReselectionBar;
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

    public void setReselectionBarStyle() {
        PictureSelectorStyle selectorStyle = config.selectorStyle;
        ReselectionBarStyle barStyle = selectorStyle.getReselectionBarStyle();

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
        if (id == R.id.tv_reselection_btn) {
            if (reselectionBarListener != null) {
                reselectionBarListener.onReselectionClick();
            }
        }
    }

    protected OnReselectionBarListener reselectionBarListener;

    /**
     * ReselectionBar 的功能事件回调
     *
     * @param listener
     */
    public void setOnReselectionBarListener(OnReselectionBarListener listener) {
        this.reselectionBarListener = listener;
    }

    public static class OnReselectionBarListener {
        /**
         * 点击选择更多
         */
        public void onReselectionClick() {

        }
    }
}
