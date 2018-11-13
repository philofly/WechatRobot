package com.czfrobot.wechatrobot.utils;

/**
 * Created by caoxianjin on 17/4/29.
 */

import android.Manifest;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Service;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.PowerManager;
import android.os.Vibrator;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.os.Process;

import com.czfrobot.wechatrobot.constant.Constants;

import java.io.File;
import java.util.Locale;

import static android.content.Context.WIFI_SERVICE;


/**
 * 设备工具类，操作一些硬件设备功能
 * Created by fengchong on 16/1/18.
 */
public class DeviceUtils {

    //锁屏部分-------------------------
    //继承了设备管理器的广播类，没做任何操作
    public static class AdminReceiver extends DeviceAdminReceiver {}
    private  static DevicePolicyManager policyManager;
    private static ComponentName componentName;
    private static final int MY_REQUEST_CODE = 9999;
    public static final int NETWORKTYPE_WIFI = 0;
    public static final int NETWORKTYPE_4G = 1;
    public static final int NETWORKTYPE_2G = 2;
    public static final int NETWORKTYPE_NONE = 3;

    /**
     * 初始化锁屏
     * @param context
     */
    public static void initLockScreen(Context context){
        policyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(context,DeviceUtils.AdminReceiver.class);

    }



    /**
     * 激活设备管理器，设置app为激活状态
     */
    public static void activeDeviceManager(Activity activity){
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活一键锁屏");
        activity.startActivityForResult(intent, MY_REQUEST_CODE);
    }

    /**
     * 判断是否处于锁屏状态
     * @param c
     * @return	返回ture为锁屏,返回flase为未锁屏
     */
    public final static boolean isScreenLocked(Context c) {
        KeyguardManager km = (KeyguardManager) c.getSystemService(c.KEYGUARD_SERVICE);
        return km.inKeyguardRestrictedInputMode();
    }

    /**
     * 被Activity的onActivityResult函数调用
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public static void onActivityResult(Activity activity,int requestCode,int resultCode,Intent data){
        if(requestCode==MY_REQUEST_CODE && resultCode== Activity.RESULT_OK){
            policyManager.lockNow();
            activity.finish();
        }else{
            activity.finish();
        }
    }



    /**
     * 锁定屏幕操作
     * @param activity
     */
    public static void lockScreen(Activity activity){
        if(policyManager.isAdminActive(componentName)){
            Log.i("WechatRobot", "lock screen");
            policyManager.lockNow();
        }else{
            Log.i("WechatRobot", "activeDeviceManager");
            activeDeviceManager(activity);
        }


    }
    //锁屏部分-------------------------

    //唤醒屏幕部分----------------------
    private static KeyguardManager km;                                                                                 //键盘管理
    private static PowerManager pm;                                                                                    //电源管理
    private static PowerManager.WakeLock wakeLock;                                                                      //屏幕唤醒对象
    private static  KeyguardManager.KeyguardLock keyguardLock;


    /**
     * 初始化唤醒屏幕
     * @param activity
     */
    public static void initWakeScrenUnlock(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        pm = (PowerManager) activity.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");

        if(wakeLock.isHeld()){
            wakeLock.release();
        }
    }

    /**
     * 唤醒屏幕
     */
    public static void wakeScreen(Context context){
        //屏幕解锁
        km= (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        kl.disableKeyguard();

        //屏幕唤醒
        if(wakeLock==null) {
            pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK,"bright");
        }
        wakeLock.acquire();
        wakeLock.release();

    }


    //唤醒屏幕部分----------------------

    //手机振动部分部分----------------------
    /**
     * 手机震动函数
     * @param activity
     * @param milliseconds
     */
    public static void vibrate(final Activity activity, long milliseconds) {
        Vibrator vib = (Vibrator) activity.getSystemService(Service.VIBRATOR_SERVICE);
        vib.vibrate(milliseconds);
    }

    //手机振动部分部分----------------------

    /**
     * 得到屏幕宽度
     */
    public static int getScreenWidth(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getWidth();
    }

    /**
     * 得到屏幕高度
     */
    public static int getScreenHeight(Context context) {
        return ((WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getHeight();
    }

    /**
     * Provide a method to get screen density
     *
     * @return string density
     */
    public static String getDensity(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density + "";
    }

    /**
     * Provide a method to get screen density
     *
     * @param context
     * @return float density
     */
    public static float getScreenDensity(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }



    private static String DEVICE_ID = null;// device unique id

    /**
     * If can get device id, return. else get wifi mac address etc.
     * and the result will encode by md5(32), if null, it will throws a runtime exception
     *
     * @param ctx context
     * @return
     *       encode string by md5(32)
     */
    public static String getIEMI(Context ctx) {

        try {
            if (DEVICE_ID != null) {
                Log.i(Constants.LOG_TAG, "imei:"+DEVICE_ID);
                return DEVICE_ID;
            }

            if (ctx.checkPermission(Manifest.permission.READ_PHONE_STATE,
                    Process.myPid(),
                    Process.myUid()) == PackageManager.PERMISSION_GRANTED){

                TelephonyManager telephonyManager = (TelephonyManager) ctx
                        .getSystemService(Context.TELEPHONY_SERVICE);
                if (telephonyManager != null) {
                    DEVICE_ID = telephonyManager.getDeviceId();
                    Log.i(Constants.LOG_TAG, "Permission granted. Get device id:" + DEVICE_ID);
                }
            }
            if (TextUtils.isEmpty(DEVICE_ID)) {
                return "";
            }
            return DEVICE_ID;
        } catch (Throwable t) {
            t.printStackTrace();
            return "";
        }

    }

    public static String getSDPath() {
        File sdDir = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            sdDir = Environment.getExternalStorageDirectory();
        }
        return sdDir == null ? null : sdDir.toString() + "/";
    }

    public static int getNetWorkType(Context context) {
        int mNetWorkType = -1;
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String type = networkInfo.getTypeName();
            if (type.equalsIgnoreCase("WIFI")) {
                mNetWorkType = NETWORKTYPE_WIFI;
            } else if (type.equalsIgnoreCase("MOBILE")) {
                return isFastMobileNetwork(context) ? NETWORKTYPE_4G : NETWORKTYPE_2G;
            }
        } else {
            mNetWorkType = NETWORKTYPE_NONE;//没有网络
        }
        return mNetWorkType;
    }

    /**判断网络类型*/
    public static boolean isFastMobileNetwork(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager.getNetworkType() == TelephonyManager.NETWORK_TYPE_LTE) {
            //这里只简单区分两种类型网络，认为4G网络为快速，但最终还需要参考信号值
            return true;
        }
        return false;
    }

    public static int getWifiSignalLevel(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo.getBSSID() != null) {
            //wifi信号强度
            int signalLevel = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), 6);
            return  signalLevel;
        }
        return 0;
    }

    /*
    * 当前网络是否可用
    */
    public static boolean isNetworkAvailable(Context context) {
        boolean netSataus = false;
        ConnectivityManager cwjManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cwjManager.getActiveNetworkInfo();
        if (info != null) {
            netSataus = info.isAvailable() || info.isConnected();
        }
        return netSataus;
    }




    /**
     * 获取电话号码
     */
    public static  String getNativePhoneNumber(Context context) {
        String NativePhoneNumber=null;
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        NativePhoneNumber=telephonyManager.getLine1Number();
        return NativePhoneNumber;
    }

    /**
     * 获取手机服务商信息
     */
    public static  String getProvidersName(Context context) {
        String ProvidersName = "N/A";
        TelephonyManager telephonyManager = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        String IMSI = "";
        try{
            IMSI = telephonyManager.getSubscriberId();
            // IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
            System.out.println(IMSI);
            if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
                ProvidersName = "中国移动";
            } else if (IMSI.startsWith("46001")) {
                ProvidersName = "中国联通";
            } else if (IMSI.startsWith("46003")) {
                ProvidersName = "中国电信";
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return ProvidersName;
    }

    public static String  getPhoneInfo(Context context){
        TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();

        sb.append("\nDeviceId(IMEI) = " + tm.getDeviceId());
        sb.append("\nDeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion());
        sb.append("\nLine1Number = " + tm.getLine1Number());
        sb.append("\nNetworkCountryIso = " + tm.getNetworkCountryIso());
        sb.append("\nNetworkOperator = " + tm.getNetworkOperator());
        sb.append("\nNetworkOperatorName = " + tm.getNetworkOperatorName());
        sb.append("\nNetworkType = " + tm.getNetworkType());
        sb.append("\nPhoneType = " + tm.getPhoneType());
        sb.append("\nSimCountryIso = " + tm.getSimCountryIso());
        sb.append("\nSimOperator = " + tm.getSimOperator());
        sb.append("\nSimOperatorName = " + tm.getSimOperatorName());
        sb.append("\nSimSerialNumber = " + tm.getSimSerialNumber());
        sb.append("\nSimState = " + tm.getSimState());
        sb.append("\nSubscriberId(IMSI) = " + tm.getSubscriberId());
        sb.append("\nVoiceMailNumber = " + tm.getVoiceMailNumber());
        return  sb.toString();
    }

    /**
     * Provide a method to get device model
     *
     * @return android.os.build.MODEL
     */
    public static String getDeviceModel() {
        return android.os.Build.MODEL;
    }

}
