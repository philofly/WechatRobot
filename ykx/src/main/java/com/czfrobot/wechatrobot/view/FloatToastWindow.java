package com.czfrobot.wechatrobot.view;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.czfrobot.wechatrobot.R;

/**
 * Created by caoxianjin on 17/5/17.
 */

public class FloatToastWindow{
    //使用反射Toast的悬浮窗
    protected FloatToastManager mfloatToastManager;
    protected Context mContext;
    protected View mView;

    protected boolean mIsShow = false;

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.system_float_window_layout,null);
        mView = rootView;
    }

    public FloatToastWindow(Context context) {
        mContext = context;
    }

    private void init(Context context) {
        mfloatToastManager = new FloatToastManager(mContext);
        initView();
    }

    public void remove() {
        if (mfloatToastManager != null) {
            try {
                if (null != mView) {
                    mfloatToastManager.hideView();
                    mView = null;
                }
                mIsShow = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show() {
        init(mContext);

        if (null != mfloatToastManager && null != mView) {
            try {
                mfloatToastManager.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL , 0 , 0 );
                mfloatToastManager.setSize(WindowManager.LayoutParams.WRAP_CONTENT,WindowManager.LayoutParams.WRAP_CONTENT);
                mfloatToastManager.showViewOther(mView);
                mIsShow = true;
            } catch (Exception e) {

            }
        }
    }



    public boolean isShow() {
        return mIsShow;
    }
}