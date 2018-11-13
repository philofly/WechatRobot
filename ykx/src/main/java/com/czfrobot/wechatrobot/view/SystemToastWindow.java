package com.czfrobot.wechatrobot.view;

/**
 * Created by caoxianjin on 17/5/17.
 */


import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.czfrobot.wechatrobot.R;

/**
 * Created by sunqi on 2017/3/10.
 */

public class SystemToastWindow {
    //系统Toast悬浮窗
    protected WindowManager mWindowManager;
    protected WindowManager.LayoutParams mLayoutParams;
    protected Context mContext;
    protected View mView;

    protected boolean mIsShow = false;
    private int mCurWindowType = WindowManager.LayoutParams.TYPE_TOAST;
//    private int mCurWindowType = WindowManager.LayoutParams.TYPE_PHONE;

    private void initView() {
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.system_float_window_layout,null);
        mView = rootView;


    }

    public SystemToastWindow(Context context) {
        mContext = context;
    }

    private void init(Context context) {
        mLayoutParams = new WindowManager.LayoutParams();
        mLayoutParams.format = PixelFormat.RGBA_8888;
        mLayoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mLayoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        mLayoutParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

        mLayoutParams.type = mCurWindowType;

        initView();
    }

    public void remove() {
        if (mWindowManager != null) {
            try {
                if (null != mView) {
                    mWindowManager.removeView(mView);
                    mView = null;
                }
                mIsShow = false;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void show(String text) {
        init(mContext);

        if (null != mWindowManager && null != mView) {
            try {
                ((TextView)mView.findViewById(R.id.guide)).setText(text);
                ((TextView)mView.findViewById(R.id.guide)).setVisibility(View.VISIBLE);
                mWindowManager.addView(mView, mLayoutParams);
                mIsShow = true;
            } catch (WindowManager.BadTokenException e) {

            } catch (Exception e) {

            }
        }
    }



    public boolean isShow() {
        return mIsShow;
    }
}