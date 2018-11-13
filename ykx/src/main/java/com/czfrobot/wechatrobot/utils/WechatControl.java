package com.czfrobot.wechatrobot.utils;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.czfrobot.wechatrobot.constant.Constants;

/**
 * Created by caoxianjin on 17/6/9.
 */

public class WechatControl {
    private static void stopWechat(){
        String result = Utils.execShellCmd("ps | grep com.tencent.mm", "com.tencent.mm");
        Log.i(Constants.LOG_TAG, "stopWechat, result:" + result);
        if(TextUtils.isEmpty(result)){
            Utils.setIsWechatStopped(true);
            Utils.setIsWechatStarted(false);
        }else {
            Utils.forceStopAPK(Constants.WECHAT_PACKAGE_NAME);
            SystemControl.delay(1000);
            stopWechat();
        }
//        result = Utils.execShellCmd("ps | grep com.tencent.mm", "com.tencent.mm");
//        Log.i(Constants.LOG_TAG, "stopWechat, result:" + result);
//        if(!TextUtils.isEmpty(result)){
//            SystemControl.delay(1000);
//            stopWechat();
//        }
    }

    public static void startWechatLoop(Context context){
        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(Constants.WECHAT_PACKAGE_NAME, "com.tencent.mm.ui.LauncherUI");
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(cmp);
        context.startActivity(intent);

//        String result = Utils.execShellCmd("ps | grep com.tencent.mm", "com.tencent.mm");
//        Log.i(Constants.LOG_TAG, "startWechatLoop, result:" + result);
//        if(TextUtils.isEmpty(result)){
//            SystemControl.delay(1000);
//            if(loopCount <= 6) {
//                loopCount++;
//                startWechatLoop(context);
//            }
//        }
//        boolean result = Utils.isAppForeground(context, Constants.WECHAT_PACKAGE_NAME);
//        Log.i(Constants.LOG_TAG, "startWechatLoop, result:" + result);
//
//        if(result == false){
//            SystemControl.delay(1000);
//            startWechatLoop(context);
//        }else{
//            Log.i(Constants.LOG_TAG, "startWechatLoop success");
//        }

    }

    public static void publishSns(Context context) {
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setAutoPublishSns(true);
    }

    public static  void autolike(Context context) {
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        //TODO(caoxianjin)
//        startWechatLoop(context);
        Utils.setIsSendLike(true);
    }

    public static void sendFriendsMessage(Context context) {
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsSendAllfriendsMessage(true);
    }

    public static void addFriendContacts(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsAddContactFriends(true);
    }

    public static void searchFriends(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsSearchFriends(context, true);
    }

    public static void searchNearbyFriends(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsSearchNearbyFriends(true);
    }

    public static void shakeOff(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsShakeOff(context, true);
    }


    public static void DrifBottle(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsDriftBottle(true);
    }

    public static void ScanRaddar(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsScanRaddar(true);
    }

    public static void startWechat(Context context){
        stopWechat();
        Utils.setIsWechatStopped(true);
        startWechatLoop(context);
    }

    public static void standStreet(Context context){
        Utils.back2Home(context);
        stopWechat();
        Utils.setIsWechatStopped(true);
        Utils.setIsStandStreet(true);
    }


}
