package com.czfrobot.wechatrobot.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class ServiceUtils {
    // 启动一个service
    public static void startService(Context mContext, String action) {
        if (!isServiceExisted(mContext, action)) {
            mContext.startService(new Intent(action));
        }
    }

    // 停止一个service
    public static void stopService(Context mContext, String action) {
        if (isServiceExisted(mContext, action)) {
            mContext.stopService(new Intent(action));
        }
    }

    // service是否存在
    public static boolean isServiceExisted(Context context, String className) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        if (!(serviceList.size() > 0)) {
            return false;
        }
        for (int i = 0; i < serviceList.size(); i++) {
            RunningServiceInfo serviceInfo = serviceList.get(i);
            ComponentName serviceName = serviceInfo.service;
            if (serviceName.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }
}
