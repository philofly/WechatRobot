package com.czfrobot.wechatrobot.utils;

/**
 * Created by caoxianjin on 17/5/14.
 */


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Handler;
import android.util.Log;


@SuppressLint("NewApi")
public class ProgressDialogUtils {
    private static ProgressDialog sDialog = null;
    private static Runnable sRunable;

    public static ProgressDialog getInstance(Context context) {
        if (sDialog == null) {
            synchronized (ProgressDialogUtils.class) {
                try {
                    if (sDialog == null) {
                        sDialog = new ProgressDialog(context);
                    }
                } catch (Exception e) {
                    Log.e("WechatRobot", "create dialog failed:" + e.toString());
                }
            }
        }
        return sDialog;
    }

    /**
     * 显示loading对话框方法
     */
    public static Dialog showProgressDialog(Fragment fragment,
                                            final String message) {
        final Activity activity = fragment.getActivity();
        return showProgressDialog(activity, message);
    }

    /**
     * 显示loading对话框方法
     */
    public static Dialog showProgressDialog(final Activity activity,
                                            final String message) {
        if (sDialog == null || !sDialog.isShowing()) {
            if (null != activity && !activity.isFinishing()) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        sDialog = new ProgressDialog(activity);
                        sDialog.setCanceledOnTouchOutside(false);
                        sDialog.show();
                        sDialog.setMessage(message);
                    }
                });
            }
        }
        return sDialog;
    }

    /**
     * 延迟显示loading对话框方法
     * time 毫秒
     */
    public static void showProgressDialogDelay(long time,
                                               final Fragment fragment, final String message, Handler handler) {
        // 每次调用时都要清除消息队列中showDialog消息，解决多次调用showProgressDialogDelay，Dialog不消失问题
        if (sRunable != null) {
            handler.removeCallbacks(sRunable);
            sRunable = null;
        }
        sRunable = new Runnable() {

            @Override
            public void run() {
                showProgressDialog(fragment, message);
            }
        };
        handler.postDelayed(sRunable, time);
    }

    /**
     * 取消延迟显示loading对话框
     */
    public static void dismissDelayDialog(Handler handler) {
        if (sRunable != null) {
            handler.removeCallbacks(sRunable);
            sRunable = null;
        }
        dismissDialog();
    }

    /**
     * 判断dialog是否正在显示
     */
    public static boolean isShowingProgressDialog() {
        if (sDialog != null) {
            return sDialog.isShowing();
        } else {
            return false;
        }
    }

    public static void dismissDialog() {
        if (sDialog != null) {
            try {
                sDialog.dismiss();
                sDialog = null;
            } catch (Exception e) {
                Log.e("WechatRobot", "dismiss dialog failed:" + e.toString());
            }
        }

    }


}
