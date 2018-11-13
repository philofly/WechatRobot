package com.czfrobot.wechatrobot.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by caoxianjin on 17/5/17.
 */

public class FloatToastManager {
    //反射Toast的使用类
    private Context mContext;
    private Field mParamsField;
    private WindowManager.LayoutParams mParams;
    private Toast mToast;
    private Object mTN;
    private Method mShowMethod;
    private Method mHideMethod;
    private boolean isShow = false;
    private int mGravity = Gravity.CENTER;
    private int mXOffset = 0;
    private int mYOffset = 0;

    private int mHeight = WindowManager.LayoutParams.MATCH_PARENT;
    private int mWidth = WindowManager.LayoutParams.MATCH_PARENT;

    private boolean misInited = false;
    private View mView;

    public FloatToastManager(Context context){
        mContext = context;
        mToast = new Toast(mContext);
    }

    public void setGravity(int gravity,int xOffset,int yOffset){
        mGravity = gravity;
        mXOffset = xOffset;
        mYOffset = yOffset;
    }

    public void setSize(int height,int width){
        mHeight = height;
        mWidth = width;
    }

    private void initVar(){
        if (misInited){
            return;
        }

        try {
            Field tnField =mToast.getClass().getDeclaredField("mTN");
            tnField.setAccessible(true);
            mToast.setGravity(mGravity,mXOffset,mYOffset);

            mTN = tnField.get(mToast);
            mShowMethod = mTN.getClass().getMethod("show");
            mHideMethod = mTN.getClass().getMethod("hide");

            mParamsField = mTN.getClass().getDeclaredField("mParams");
            mParamsField.setAccessible(true);
            mParams = (WindowManager.LayoutParams) mParamsField.get(mTN);
            mParams.height = mHeight;
            mParams.width = mWidth;
            mParams.format = PixelFormat.RGBA_8888;

            mParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                    | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showViewOther(View view){
        initVar();

        if (isShow) return;
        mToast.setView(view);
        try {
            /**调用tn.mShowMethod()之前一定要先设置mNextView*/
            Field tnNextViewField = mTN.getClass().getDeclaredField("mNextView");
            tnNextViewField.setAccessible(true);
            tnNextViewField.set(mTN,mToast.getView());
            mShowMethod.invoke(mTN);
            isShow = true;
            mView = view;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void hideView() {
        if (!isShow) return;

        try {
            if (null != mView) {
                // 解决消失时渐隐动画导致的闪烁问题
                mView.setVisibility(View.INVISIBLE);
                mView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (null != mView) {
                            mView.setVisibility(View.VISIBLE);
                            mView = null;
                        }
                    }
                }, 100);
            }
            mHideMethod.invoke(mTN);
            //mToast.cancel();

            isShow = false;
            Log.i("FloatToastManager", "hideView called");
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("FloatToastManager", "hideView exception " + e.getMessage());
        }
    }



}