package com.czfrobot.wechatrobot.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;

import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.provider.DaoHandler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.List;



public class Utils {

    /*
     * 获取版本号
     * @return version code
     */
    public static int getVersionCode(Context ctx) {
        try {
            PackageInfo packInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (Throwable t) {
            Log.e(Constants.LOG_TAG, "getVersionCode occur exception");
            t.printStackTrace();
            return 0;
        }
    }

    /*
     * 获取版本名称
     * @return version name
     */
    public static String getVersionName(Context ctx) {
        try {
            PackageInfo packInfo = ctx.getPackageManager().getPackageInfo(
                    ctx.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Throwable t) {
            Log.e(Constants.LOG_TAG, "getVersionName occur exception");
            t.printStackTrace();
            return "";
        }
    }


    /**
     * 获取微信的版本号
     * @param context
     * @return
     */
    public static String getWechatVersion(Context context){
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfoList = packageManager.getInstalledPackages(0);

        for(PackageInfo packageInfo:packageInfoList){
            if(Constants.WECHAT_PACKAGE_NAME.equals(packageInfo.packageName)){
                return packageInfo.versionName;
            }
        }
        return "6.5.4";
    }

    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                /*
                BACKGROUND=400 EMPTY=500 FOREGROUND=100
                GONE=1000 PERCEPTIBLE=130 SERVICE=300 ISIBLE=200
                 */
                Log.i(context.getPackageName(), "此appimportace ="
                        + appProcess.importance
                        + ",context.getClass().getName()="
                        + context.getClass().getName());
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Log.i(context.getPackageName(), "处于后台"
                            + appProcess.processName);
                    return true;
                } else {
                    Log.i(context.getPackageName(), "处于前台"
                            + appProcess.processName);
                    return false;
                }
            }
        }
        return false;
    }

    public static String getTopPackage(Context context)
    {
        return ((ActivityManager.RunningTaskInfo)((ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(1).get(0)).topActivity.getPackageName();
    }

    /**
     * 判断指定的应用是否在前台运行
     *
     * @param context, packageName
     * @return
     */
    public static boolean  isAppForeground(Context context, String  packageName) {
        ActivityManager am = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        for(ActivityManager.RunningTaskInfo info:list){
            Log.i(Constants.LOG_TAG, "packageName:" + info.topActivity.getPackageName() + ", seond:" + info .baseActivity.getPackageName());
            if(info.topActivity.getPackageName().equals(packageName) && info.baseActivity.getPackageName().equals(packageName)){
                return true;
            }
        }
//        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
//
//        String currentPackageName = cn.getPackageName();
//        if (!TextUtils.isEmpty(currentPackageName) && currentPackageName.equals(packageName)) {
//            return true;
//        }

        return false;
    }

    //当前应用是否处于前台
    public static boolean isForeground(Context context, String packageName) {
        if (context != null) {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> processes = activityManager.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo: processes) {
                Log.i(Constants.LOG_TAG, "importance:" + processInfo.importance +", packageName:" + processInfo.processName);
                if (processInfo.processName.equals(packageName)) {
                    if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    /**
     * 回到系统桌面
     */
    public static void back2Home(Context context) {
        Intent home = new Intent(Intent.ACTION_MAIN);

        home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        home.addCategory(Intent.CATEGORY_HOME);

        context.startActivity(home);
    }

    /**
     * 根据进程名称获取进程id
     * @param name
     *            进程名
     * @return
     */
    public static int getPidByProcessName(Context context, String name) {
        if (name == null || name.length() == 0) {
            return -1;
        }
        ActivityManager mActivityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList = mActivityManager
                .getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcessList) {
            if (appProcess.processName.equals(name)) {
                return appProcess.pid;
            }
        }
        return -1;
    }

    public static void forceStopAPK(String pkgName){
        Process sh = null;
        DataOutputStream os = null;
        try {
            sh = Runtime.getRuntime().exec("su");
            os = new DataOutputStream(sh.getOutputStream());
            final String Command = "am force-stop "+pkgName+ "\n";
            os.writeBytes(Command);
            os.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static boolean checckServiceAccessibility(Context context, String service) {
        int ok = 0;
        try {
            ok = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
        }

        TextUtils.SimpleStringSplitter ms = new TextUtils.SimpleStringSplitter(':');
        if (ok == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                ms.setString(settingValue);
                while (ms.hasNext()) {
                    String accessibilityService = ms.next();
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        return true;
                    }

                }
            }
        }
        return   false;
    }

    public  static void startAccessibilitySetting(Context context){
        final String action = Settings.ACTION_ACCESSIBILITY_SETTINGS;
        Intent intent = new Intent(action);
        context.startActivity(intent);
    }


    /**
     * 执行shell命令
     *
     * @param cmd
     */
    public static void execShellCmd(String cmd) {

        try {
            // 申请获取root权限，这一步很重要，不然会没有作用
            java.lang.Process process = Runtime.getRuntime().exec("su");
            // 获取输出流
            OutputStream outputStream = process.getOutputStream();
            DataOutputStream dataOutputStream = new DataOutputStream(
                    outputStream);
            dataOutputStream.writeBytes(cmd);
            dataOutputStream.flush();
            dataOutputStream.close();
            outputStream.close();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }





    public static String execShellCmd(String cmd, String filter) {
        String result = "";
        String line;
        InputStreamReader is = null;
        BufferedReader br = null;
        try {
            Process proc = Runtime.getRuntime().exec(cmd);
            is = new InputStreamReader(proc.getInputStream());
            br = new BufferedReader(is);
            // Execute command CMD, just take the results of this line containing filter
            while ((line = br.readLine()) != null && !line.contains(filter)) {
            }
            result = line;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(is);
            IOUtils.closeStream(br);
        }
        return result;
    }

    public  static  void startShakeoff(){
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("is_start", 1);
        DaoHandler.getInstance().add(localContentValues);
    }


    public  static  void stopShakeoff(){
        ContentValues localContentValues = new ContentValues();
        localContentValues.put("is_start", 0);
        DaoHandler.getInstance().add(localContentValues);
    }





    private static boolean isWechatStarted;

    public static  boolean isIsWechatStarted(){
        return  isWechatStarted;
    }

    public static  void setIsWechatStarted( boolean isStarted){
        isWechatStarted = isStarted;
    }

    private static boolean isWechatStopped;

    public static  boolean isIsWechatStopped(){
        return  isWechatStopped;
    }

    public static  void setIsWechatStopped( boolean isStopped){
        isWechatStopped = isStopped;
    }


    private static Activity activity;
    public static void setAddMoreFriendsActivity(Activity newActivity){
        activity = newActivity;
    }

    public static Activity getActivity(){
        return  activity;
    }


    private static List<String> contacts;

    public  static  void setContactList( List<String> newContacts){
        contacts = newContacts;
    }

    public static  List<String> getContactList(){
        return  contacts;
    }




    private static  double longitude  = 0.0;

    public static  void  setLongitude(double dbLongitude){
        longitude = dbLongitude;
    }
    public static  double getLongitude(){
        return longitude;
    }

    private static  double latitude  = 0.0;

    public static  void  setLatitude(double dbLatitude){
        latitude = dbLatitude;
    }
    public  static double getLatitude(){
        return longitude;
    }

    private  static boolean isPushlishSns = false;//是否发朋友圈

    public static  void setAutoPublishSns(boolean isAutoPublishSns){
        isPushlishSns = isAutoPublishSns;
    }

    public  static  boolean isAutoPublishSns(){
        return isPushlishSns;
    }


    private  static boolean isAddContactFriends = false;//是否通讯录加好友

    public static  void setIsAddContactFriends(boolean isAddContactFriend){
        isAddContactFriends = isAddContactFriend;
    }

    public  static  boolean isAddContactFriends(){
        return isAddContactFriends;
    }

    private  static boolean isSearchFriends = false;//是否搜索好友

    public static  void setIsSearchFriends(Context context, boolean isSearch){
        isSearchFriends = isSearch;
        Preferences.setIsSearchFriends(context, isSearch);
    }

    public  static  boolean isSearchFriends(){
        return isSearchFriends;
    }


    private static boolean isSendAllfriendsMessage = false;//是否自动群发消息

    public static  void setIsSendAllfriendsMessage(boolean isSendMessage){
        isSendAllfriendsMessage = isSendMessage;
    }

    public  static  boolean isSendAllfriendsMessage(){
        return isSendAllfriendsMessage;
    }

    private static boolean isSendLike = false;//是否自动点赞

    public static  void setIsSendLike(boolean isLike){
        isSendLike = isLike;
    }

    public  static  boolean isSendLike(){
        return isSendLike;
    }

    private static boolean isSearchNearbyFriends = false;//是否找附近的人

    public static  void setIsSearchNearbyFriends(boolean isSearch){
        isSearchNearbyFriends = isSearch;
    }

    public  static  boolean isIsSearchNearbyFriends(){
        return isSearchNearbyFriends;
    }

    private static boolean isShakeOff = false;//是否摇一摇

    public static  void setIsShakeOff( Context context, boolean isShake){
        isShakeOff = isShake;
        Preferences.setIsShakoff(context, isShake);
        if(isShake) {
            startShakeoff();
        }else{
            stopShakeoff();
        }
    }

    public  static  boolean isIsShakeOff(){
        return isShakeOff;
    }


    private static boolean isPublishVideo = false;//是否发视频

    public static void setIsPublishVideo(boolean isVideo){
        isPublishVideo = isVideo;
    }

    public static  boolean isIsPublishVideo(){
        return  isPublishVideo;
    }

    private static boolean isDriftBottle = false;//是否发送漂流瓶

    public static  void setIsDriftBottle(boolean isDrift){
        isDriftBottle = isDrift;
    }

    public  static  boolean isIsDriftBottle(){
        return isDriftBottle;
    }

    private static boolean isScanRaddar = false;//是否扫雷达

    public static  void setIsScanRaddar(boolean isScan){
        isScanRaddar = isScan;
    }

    public  static  boolean isIsScanRaddar(){
        return isScanRaddar;
    }


    private static boolean isStandStreet = false;//是否站街扫街

    public static  void setIsStandStreet(boolean isStand){
        isStandStreet = isStand;
    }

    public  static  boolean isIsStandStreet(){
        return isStandStreet;
    }



}
