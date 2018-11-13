package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.LocalBroadcastManager;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.accessibility.RobotService;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.model.ContactListModel;
import com.czfrobot.wechatrobot.http.model.DeviceBindModel;
import com.czfrobot.wechatrobot.http.model.MomentMaterialModel;
import com.czfrobot.wechatrobot.http.parameter.DeviceIsBindParam;
import com.czfrobot.wechatrobot.http.parameter.DeviceUpateParam;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.listener.UpdateListener;
import com.czfrobot.wechatrobot.utils.BitmapUtils;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.FileUtils;
import com.czfrobot.wechatrobot.utils.IOUtils;
import com.czfrobot.wechatrobot.utils.ImageCacheConfig;
import com.czfrobot.wechatrobot.utils.JPushUtil;
import com.czfrobot.wechatrobot.utils.LocationUtil;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.SystemControl;
import com.czfrobot.wechatrobot.utils.UpdateUtil;
import com.czfrobot.wechatrobot.utils.Utils;
import com.czfrobot.wechatrobot.utils.VolleyUtil;
import com.czfrobot.wechatrobot.utils.WechatControl;
import com.czfrobot.wechatrobot.view.SystemToastWindow;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;


public class MainActivity extends Activity implements UpdateListener{

    private TextView username, imei, device_group, device_imei,wifi_power,app_version,jpush_status;
    private LinearLayout layout_user, layout_version, layout_device;
    private ImageView wifi;
    private RelativeLayout layout_download;
    private ProgressBar progressBar;

    private Intent intent = new Intent();
    private SystemToastWindow systemToastWindow;
    public static boolean isForeground = false;
    public static boolean isCreated = false;

    private boolean isBind = false;
    private TelephonyManager mTelephonyManager;
    private PhoneStatListener mListener;
    private NetWorkBroadCastReceiver mNetWorkBroadCastReceiver;
    private int mGsmSignalStrength;
    private String appUrl, appFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isCreated = true;

        username = (TextView) findViewById(R.id.username);
        imei = (TextView)findViewById(R.id.imei);
        device_imei = (TextView)findViewById(R.id.device_imei);
        device_group = (TextView)findViewById(R.id.device_group);
        wifi_power = (TextView)findViewById(R.id.wifi_power);
        app_version = (TextView)findViewById(R.id.app_version);
        jpush_status =(TextView)findViewById(R.id.jpush_status);
        wifi = (ImageView)findViewById(R.id.wifi);

        imei.setText("IEMI:" + DeviceUtils.getIEMI(this));
        device_imei.setText("IEMI:" + DeviceUtils.getIEMI(this));
        device_group.setText(Preferences.getDeviceName(this) + "(" + Preferences.getJPushTag(this) + ")");
        app_version.setText("当前版本 " + Utils.getVersionName(this));

        String density = DeviceUtils.getDensity(this);
        int width = DeviceUtils.getScreenWidth(this);
        int height = DeviceUtils.getScreenHeight(this);

        layout_download = (RelativeLayout)findViewById(R.id.layout_download);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        appFileName = "new.apk";

        Log.i(Constants.LOG_TAG, "density:" + density + ", width:" + width + ", height:" + height);

        int signalLevel  = DeviceUtils.getWifiSignalLevel(this);
        Log.i(Constants.LOG_TAG, "signalLevel:" + signalLevel);
        if(signalLevel >= 4 ){
            wifi_power.setText("信号强");
            wifi.setImageResource(R.drawable.fullwifi);
        }else if(signalLevel >=1 ){
            wifi_power.setText("信号弱");
            wifi.setImageResource(R.drawable.weakwifi);
        }else{
            wifi_power.setText("无信号");
            wifi.setImageResource(R.drawable.nowifi);
        }
        //获取telephonyManager
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //开始监听
        mListener = new PhoneStatListener();
        /**由于信号值变化不大时，监听反应不灵敏，所以通过广播的方式同时监听wifi和信号改变更灵敏*/
        mNetWorkBroadCastReceiver = new NetWorkBroadCastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        registerReceiver(mNetWorkBroadCastReceiver, intentFilter);


        username.setText(Preferences.getUserName(this));
        layout_user = (LinearLayout) findViewById(R.id.layout_user);
        layout_version = (LinearLayout)findViewById(R.id.layout_version);
        layout_device = (LinearLayout)findViewById(R.id.layout_device);

        layout_version.setClickable(true);
        layout_user.setClickable(true);
        layout_device.setClickable(true);

        layout_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_download.getVisibility() ==View.VISIBLE){
                    return;
                }
                intent.setClass(MainActivity.this, LogoutActivity.class);
                startActivity(intent);
            }
        });

        layout_device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(layout_download.getVisibility() ==View.VISIBLE){
                    return;
                }
                isBind = Preferences.isBind(MainActivity.this);
                if(isBind){
                    intent.setClass(MainActivity.this, UnbindDeviceActivity.class);
                }else{
                    intent.setClass(MainActivity.this, BindDeviceActivity.class);
                }
                startActivity(intent);
            }
        });


        layout_version.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DeviceUtils.isNetworkAvailable(MainActivity.this)) {
                    Toast.makeText(MainActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(layout_download.getVisibility() ==View.VISIBLE){
                    return;
                }
                UpdateUtil.getInstance().checkUpdate(MainActivity.this);
            }

        });
        systemToastWindow = new SystemToastWindow(this);

        if (!Utils.checckServiceAccessibility(this, getPackageName() + "/" + RobotService.class.getCanonicalName())) {
            Log.i("wechatrobot", "start accessibility  service ");
            String[] cmd = new String[2];
            cmd[0] = "settings put secure enabled_accessibility_services com.czfrobot.wechatrobot/com.czfrobot.wechatrobot.accessibility.RobotService";
            cmd[1] = "settings put secure accessibility_enabled 1";
            String result = Utils.execShellCmd("settings get secure enabled_accessibility_services", "czfrobot");
            if(TextUtils.isEmpty(result)){
                Utils.execShellCmd(cmd[0]);
            }
            result = Utils.execShellCmd("settings get secure enabled_accessibility_services", "1");
            if(TextUtils.isEmpty(result)){
                Utils.execShellCmd(cmd[1]);
            }

        } else {
            Log.i("wechatrobot", "accessibility  service has been started ");
        }

        registerMessageReceiver();  // used for receive msg
        DeviceUtils.initWakeScrenUnlock(this);
        isBind = Preferences.isBind(MainActivity.this);
        if(isBind){
            JPushUtil.init(this);
            JPushInterface.setLatestNotificationNumber(this, 1);
        }

        mCheckWechatThread = new checkWechatThread();
        mCheckWechatThread.start();
    }

    private void NoUpdateHandle(){
        Toast.makeText(this, "已是最新版本", Toast.LENGTH_SHORT).show();
    }




    @Override
    public void OnUpdateResult(String url, int version, String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage)) {
            Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(url)) {
            if (version > Utils.getVersionCode(MainActivity.this)) {
                appUrl = url;
                showDialog("有最新版本, 是否升级");
            }
        } else {
            NoUpdateHandle();
        }
    }

    @Override
    public void OnUpdateProgress(int progress) {
        progressBar.setProgress(progress);
    }

    @Override
    public void OnDownloadFinished() {
        UpdateUtil.getInstance().installApk(this, Constants.APP_DOWNLOAD_PATH + "/" + appFileName);
    }


    private void showDialog( String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        layout_download.setVisibility(View.VISIBLE);
                        progressBar.setProgress(0);
                        progressBar.setMax(100);
                        new UpdateUtil.downloadApkThread(appUrl, appFileName, MainActivity.this).start();
                    }

                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    private class PhoneStatListener extends PhoneStateListener {
        //获取信号强度

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            //获取网络信号强度
            //获取0-4的5种信号级别，越大信号越好,但是api23开始才能用
//            int level = signalStrength.getLevel();
            mGsmSignalStrength = signalStrength.getGsmSignalStrength();
            //获取网络类型
            int netWorkType = DeviceUtils.getNetWorkType(MainActivity.this);
//            Log.i(Constants.LOG_TAG, "onSignalStrengthsChanged, signal strength:" + mGsmSignalStrength + ", netWorkType:" + netWorkType);

            switch (netWorkType) {
                case DeviceUtils.NETWORKTYPE_WIFI:
                    break;
                case DeviceUtils.NETWORKTYPE_2G:
                    break;
                case DeviceUtils.NETWORKTYPE_4G:
                    break;
                case DeviceUtils.NETWORKTYPE_NONE:
                    break;
                case -1:
                    break;
            }
        }
    }

    //接收网络状态改变的广播
    class NetWorkBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getNetWorkInfo();
        }
    }

    /**
     * 获取网络的信息
     */
    private void getNetWorkInfo() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            switch (info.getType()) {
                case ConnectivityManager.TYPE_WIFI:
                    //wifi
                    WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo connectionInfo = manager.getConnectionInfo();
                    int rssi = connectionInfo.getRssi();
                    Log.i(Constants.LOG_TAG, "当前为wifi网络，信号强度=" + rssi);
                    //TODO(caoxianjin) for test
                    wifi.setImageResource(R.drawable.fullwifi);
                    wifi_power.setText("信号强");
                    break;
                case ConnectivityManager.TYPE_MOBILE:
                    //移动网络,可以通过TelephonyManager来获取具体细化的网络类型
                    String netWorkStatus = DeviceUtils.isFastMobileNetwork(MainActivity.this) ? "4G网络" : "2G网络";
                    Log.i(Constants.LOG_TAG, "当前为" + netWorkStatus + "，信号强度=" + mGsmSignalStrength);
                    wifi.setImageResource(R.drawable.fullwifi);
                    wifi_power.setText("信号强");
                    break;
            }
        } else {
            Log.i(Constants.LOG_TAG,"没有可用网络");
            //TODO(caoxianjin) for test
            wifi.setImageResource(R.drawable.nowifi);
            wifi_power.setText("无信号");
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        Log.i(Constants.LOG_TAG, "MainActivity, onNewIntent");
        super.onNewIntent(intent);
        String urivalue = intent.getStringExtra(Constants.NOTIFICATION_URI);
        if (urivalue == null || urivalue.equals("")) {        //判断URI消息是否有效
            boolean connect = intent.getBooleanExtra("connected", false);
            String messge = intent.getStringExtra(KEY_MESSAGE);
            String extras = intent.getStringExtra(KEY_EXTRAS);
            if(!TextUtils.isEmpty(messge)) {
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!TextUtils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
                Toast.makeText(this, showMsg.toString(), Toast.LENGTH_SHORT).show();
                Log.i(Constants.LOG_TAG, "onNewIntent, handleMessage");
                handleMessage(MainActivity.this, messge);
            }else{
                if(TextUtils.isEmpty(extras)) {
//                    if (connect) {
//                        jpush_status.setVisibility(View.INVISIBLE);
//                    } else {
//                        jpush_status.setVisibility(View.VISIBLE);
//                    }
                }else {
                    if(  ( push_type>=1 && push_type <= 12 && push_type !=5 )  || push_type == 20 || push_type == 21) {
                        taskFeedback(prod_type, push_type, Preferences.getJPushAlias(this), task_id);
                        DeviceUtils.wakeScreen(this);            //如果处于关闭屏幕状态则唤醒屏幕
                        SystemControl.playRing(this);
                    }
                }
            }
        }
    }


    //for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.czfrobot.wechatrobot.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, filter);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                    boolean connect = intent.getBooleanExtra("connected", false);

                    String messge = intent.getStringExtra(KEY_MESSAGE);
                    String extras = intent.getStringExtra(KEY_EXTRAS);

                    if(TextUtils.isEmpty(messge)){
//                        if(connect){
//                            jpush_status.setVisibility(View.INVISIBLE);
//                        }else{
//                            jpush_status.setVisibility(View.VISIBLE);
//                        }
                    }

                    StringBuilder showMsg = new StringBuilder();
                    showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                    if (!TextUtils.isEmpty(extras)) {
                        showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                    }
                    if(!TextUtils.isEmpty(showMsg.toString())) {
                        Toast.makeText(context, showMsg.toString(), Toast.LENGTH_SHORT).show();
                    }
                    Log.i(Constants.LOG_TAG, "onReceive, handleMessage");
                    handleMessage(MainActivity.this, messge);
                }
            } catch (Exception e) {
            }
        }
    }


    private int delay = 0;
    private int push_type = 0;
    private int prod_type = 2;
    private String task_id = null;
    private boolean isShowSystemToast = false;

    private synchronized void handleMessage(final Context context, String message) {
        JSONObject jsonObject = JSON.parseObject(message);
        if (jsonObject.containsKey("push_type")) {
            push_type = jsonObject.getIntValue("push_type");
        }
        if (jsonObject.containsKey("delay")) {
            delay = jsonObject.getIntValue("delay");
        }

        String greet = null;
        if(jsonObject.containsKey("greet")) {
            greet = jsonObject.getString("greet");
        }

        boolean needHi = false;
        if(jsonObject.containsKey("needHi")) {
            needHi = jsonObject.getBoolean("needHi");
        }

        if(jsonObject.containsKey("task_id")) {
            task_id =jsonObject.getString("task_id");
        }
        if(greet != null ) {
            Preferences.setGreet(this, greet);
        }
        if(needHi) {
            Preferences.setNeedHi(this, 1);
        }else{
            Preferences.setNeedHi(this, 0);
        }
        JSONArray jsonArray = null;
        String url = null;
        int scan_time = -1, add_model = -1,  force_add = -1;
        String deleteDiectory = "rm -rf " + ImageCacheConfig.IMAGE_CACHE_PATH;
        Log.i(Constants.LOG_TAG, "handleMessage, push_type:" + push_type);
        switch (push_type) {
            case 1:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到朋友圈发图文指令");
                }
                Utils.execShellCmd(deleteDiectory);
                url = jsonObject.getString("url");
                final String realUrl = url;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadMomentMaterial(realUrl);
                    }
                }, delay*1000);
                break;
            case 2:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到通讯录加好友指令");
                }
                Log.i(Constants.LOG_TAG, "收到通讯录加好友指令");
                getContactList(task_id);
                break;
            //{"url":":8091/api/material/592fb9bb40ff753e2398dda5","delay":0,"push_type":3}
            case 3:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到朋友圈发视频指令");
                }
                Utils.execShellCmd(deleteDiectory);
                if(jsonObject.containsKey("url")) {
                    url = jsonObject.getString("url");
                }
                final String finalUrl = url;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadMomentMaterial(finalUrl);
                    }
                }, delay*1000);
                break;
            case 4:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到点赞指令");
                }
                int favor_time = 0;
                if(jsonObject.containsKey("favor_time")) {
                    favor_time = jsonObject.getIntValue("favor_time");
                }
                Log.i(Constants.LOG_TAG, "favor time:" + favor_time);
                if (favor_time != 0) {
                    Preferences.setSnsLikeCount(context, favor_time);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, "开始执行点赞指令", Toast.LENGTH_SHORT).show();
                            WechatControl.autolike(MainActivity.this);
                        }
                    }, delay * 1000);

                }
                break;
            case 6:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到搜索加加好友指令");
                }
                getContactList(task_id);
                break;
            case 7:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到好友群发消息指令");
                }
                if(jsonObject.containsKey("url")) {
                    url = jsonObject.getString("url");
                }
                final String newUrl = url;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        downloadMomentMaterial(newUrl);
                    }
                }, delay*1000);
                break;
            //{"loop_duration":1800,"scan_time":2,"delay":0,"push_type":8}
            case 8:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到站街扫街指令");
                }
                int loop_duration = 0;
                if(jsonObject.containsKey("scan_time")) {
                    loop_duration = jsonObject.getIntValue("loop_duration");
                }
                if(jsonObject.containsKey("scan_time")) {
                    scan_time = jsonObject.getIntValue("scan_time");
                }
                Preferences.setStandCount(MainActivity.this, scan_time);
                Preferences.setStandGap(MainActivity.this, loop_duration);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "开始执行站街扫街指令", Toast.LENGTH_SHORT).show();
                        WechatControl.standStreet(MainActivity.this);
                    }
                }, delay*1000);
                break;
            // {"add_model":1,"force_add":1,"scan_time":3,"delay":0,"push_type":9}
            case 9:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到扫雷达指令");
                }
                add_model = jsonObject.getIntValue("add_model");
                force_add = jsonObject.getIntValue("force_add");
                if(jsonObject.containsKey("scan_time")) {
                    scan_time = jsonObject.getIntValue("scan_time");
                }
                Preferences.setScanRaddarCount(MainActivity.this, scan_time);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "开始执行扫雷达指令", Toast.LENGTH_SHORT).show();
                        WechatControl.ScanRaddar(MainActivity.this);
                    }
                }, delay*1000);
                break;
            //{"add_model":1,"force_add":1,"scan_time":3,"delay":0,"push_type":10}
            case 10:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到摇一摇指令");
                }
                add_model = jsonObject.getIntValue("add_model");
                force_add = jsonObject.getIntValue("around_type");
                if(jsonObject.containsKey("scan_time")) {
                    scan_time = jsonObject.getIntValue("scan_time");
                }
                Preferences.setShakeoffTimes(this, scan_time);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "开始执行摇一摇指令", Toast.LENGTH_SHORT).show();
                        WechatControl.shakeOff(MainActivity.this);
                    }
                }, delay*1000);
                break;
            //{"add_model":1,"around_type":1,"say_hi_time":5,"delay":0,"push_type":11}
            case 11:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到附近的人指令");
                }
                int say_hi_time = jsonObject.getIntValue("say_hi_time");
                int add_mode = jsonObject.getIntValue("add_mode");
                int around_type = jsonObject.getIntValue("around_type");
                Preferences.setSearchnearbyFriendsCount(this, say_hi_time);
                Preferences.setAroundtype(this, around_type);

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "开始执行附近的人指令", Toast.LENGTH_SHORT).show();
                        WechatControl.searchNearbyFriends(MainActivity.this);
                    }
                }, delay*1000);
                break;

            //{"bottle_count":2,"pick_up":1,"plp_content":"test","delay":0,"push_type":12}
            case 12:
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到漂流瓶指令");
                }
                int bottle_count = jsonObject.getIntValue("bottle_count");
                int pick_up = jsonObject.getIntValue("pick_up");
                String plp_content = jsonObject.getString("plp_content");
                Preferences.setDriftBottleContent(this, plp_content);
                Preferences.setDriftBottleCount(this, bottle_count);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Constants.LOG_TAG, "throw bottle command");
                        Toast.makeText(context, "开始执行漂流瓶指令", Toast.LENGTH_SHORT).show();
                        WechatControl.DrifBottle(MainActivity.this);
                    }
                }, delay*1000);
                break;
            case 13://关机
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SystemControl.shutdown();
                    }
                }, delay * 1000);
                break;
            case 14://重启
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到重启指令");
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SystemControl.restart();
                    }
                }, delay * 1000);
                break;
            case 15://锁屏
                if(isShowSystemToast) {
                    systemToastWindow.remove();
                    systemToastWindow.show("收到锁屏指令");
                }
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        DeviceUtils.lockScreen(MainActivity.this);
                    }
                }, delay * 1000);
                break;
            case 16://唤醒
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.i(Constants.LOG_TAG, "isScreenlock:" + DeviceUtils.isScreenLocked(context));
                        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                        DeviceUtils.wakeScreen(context);            //如果处于关闭屏幕状态则唤醒屏幕
                        SystemControl.playRing(context);
                        if(isShowSystemToast) {
                            //屏幕唤醒
                            systemToastWindow.remove();
                            systemToastWindow.show("收到唤醒指令");
                        }
                    }
                }, delay * 1000);
                break;
            //{"location":{"lat":39.141773,"lng":117.216586},"delay":0,"push_type":17}
            case 17://虚拟定位
                LocationUtil.stopMockLocation();
                String address = null;
                if(jsonObject.containsKey("address")) {
                    address = jsonObject.getString("address");
                }
                jsonObject  = jsonObject.getJSONObject("location");
                final double lat = jsonObject.getDoubleValue("lat");
                final double lng = jsonObject.getDoubleValue("lng");
                Log.i(Constants.LOG_TAG, "lat:" + lat + ", lng:" + lng +", address:" + address);
                final String realAddress = address;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "模拟位置成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.setAction("COM_SEO4J_LOCATION_BROADCAST_RECEIVER_ACTION");
                        intent.putExtra("lat", lat);
                        intent.putExtra("lon", lng);
                        MainActivity.this.sendBroadcast(intent);
                        LocationUtil.setLongitudeAndLatitude(lng,lat);
                        LocationUtil.startLocaton();
                        SystemControl.playRing(context);
                        updateDevice(lat,lng, realAddress);
                    }
                }, delay*1000);

                break;
            case 18:
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(context, "启动微信", Toast.LENGTH_SHORT).show();
                        SystemControl.playRing(context);
                        WechatControl.startWechat(MainActivity.this);
                    }
                }, delay*1000);
                break;

            case 19:
                SystemControl.playRing(context);
                updateDevice();
                break;

            default:
                break;
        }

    }

    private void getContactList(String task_id){
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.GET_CONTACT_LIST + task_id + "/" + Preferences.getJPushAlias(this);
        String token = Preferences.getUserToken(this);
        BaseHttpRequest<BaseBean<ContactListModel>> request = new BaseHttpRequest<BaseBean<ContactListModel>>(Request.Method.GET, url, null, token, new getContackListCallback<BaseBean<ContactListModel>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    public class getContackListCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if(htmlBodyBytes != null) {
                    BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                    });
                    if(dataBean != null && !TextUtils.isEmpty(dataBean.getMessage())) {
                        Toast.makeText(MainActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<ContactListModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<ContactListModel>>() {
            });
            if (model != null && model.getData() != null) {
               Log.i(Constants.LOG_TAG,  "model data:" + model.getData().toString());
                Utils.setContactList(model.getData().getContacts());
                if(push_type == 2){
                    List<String> contactList = model.getData().getContacts();
                    for(String mobile:contactList){
                        String name = SystemControl.getContactDisplayNameByNumber(MainActivity.this, mobile);
                        if(TextUtils.isEmpty(name)){
                            SystemControl.insertContact(MainActivity.this, mobile, mobile);
                        }
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "开始执行通讯录加好友命令", Toast.LENGTH_SHORT).show();
                            WechatControl.addFriendContacts(MainActivity.this);
                        }
                    }, delay*1000);
                }else if(push_type == 6){
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "开始执行搜索加好友命令", Toast.LENGTH_SHORT).show();
                            WechatControl.searchFriends(MainActivity.this);
                        }
                    }, delay*1000);
                }
            }
        }
    }


    private void updateDevice() {
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.UPDATE_DEVICE + Preferences.getJPushAlias(this);
        String token = Preferences.getUserToken(this);
        BaseHttpRequest<BaseBean<DeviceBindModel>> request = new BaseHttpRequest<BaseBean<DeviceBindModel>>(Request.Method.PUT, url, null, token, new updateDeviceCallback<BaseBean<DeviceBindModel>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    private boolean isUpdateAddress = false;
    private void updateDevice(double lat, double lng, String address) {
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.UPDATE_DEVICE + Preferences.getJPushAlias(this);
        String token = Preferences.getUserToken(this);
        JSONObject jsonObjectPosition = new JSONObject();
        jsonObjectPosition.put("lat", lat);
        jsonObjectPosition.put("lng", lng);
        JSONObject jsonObject= new JSONObject();
        jsonObject.put("position", jsonObjectPosition);
        if(address != null) {
            jsonObject.put("address", address);
        }else{
            jsonObject.put("address" , "");
        }
        Log.i(Constants.LOG_TAG, "map:"+jsonObject.toString());
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", JSON.toJSONString(jsonObject));
        BaseHttpRequest<BaseBean<DeviceBindModel>> request = new BaseHttpRequest<BaseBean<DeviceBindModel>>(Request.Method.PUT, url, map, token, new updateDeviceCallback<BaseBean<DeviceBindModel>>());
        VolleyUtil.getRequestQueue().add(request);
        isUpdateAddress = true;
    }

    public class updateDeviceCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                });
                Toast.makeText(MainActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<DeviceBindModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<DeviceBindModel>>() {
            });
            if (model != null && model.getData() != null) {
                if(isUpdateAddress){
                    Toast.makeText(MainActivity.this, "虚拟定位回传成功", Toast.LENGTH_LONG).show();
                    isUpdateAddress = false;
                }else {
                    Toast.makeText(MainActivity.this, "更新设备信息成功", Toast.LENGTH_LONG).show();
                    Preferences.saveDeviceName(MainActivity.this, model.getData().getDesc());
                    Preferences.saveJPushTag(MainActivity.this, model.getData().getGroup_tag());
                    device_group.setText(model.getData().getDesc() + "(" + model.getData().getGroup_tag() + ")");
                }
            }
        }
    }

    private String videoFileName = null;
    private File videoFile = null;

    private class downloadVideoThread extends Thread {

        private String videoUrl;

        @Override
        public void run() {
            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection connection;
            videoFileName = System.currentTimeMillis() + ".mp4";
            Log.i(Constants.LOG_TAG, "startDownloadVideo, videoUrl:" + videoUrl);
            try {
                long startTime = System.currentTimeMillis();
                URL url = new URL(videoUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.setDoInput(true);
                connection.connect();
                Log.i(Constants.LOG_TAG, "http response code:" + connection.getResponseCode());
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 获取下载文件总大小
                    final long totalSize = connection.getContentLength();
                    Log.i(Constants.LOG_TAG, "writeInputStreamToCache totalSize = " + totalSize
                            + "  videoUrl = " + videoUrl);
                    is = connection.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    // 判断sdcard的状态
                    String sdcardState = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                        // 判断path有没有
                        File filePath = new File(ImageCacheConfig.IMAGE_CACHE_PATH);//此处为常量地址：/mnt/sdcard/picture,可以自己定义                                             <span style="font-family: Arial, Helvetica, sans-serif;">                      if (!filePath.exists()) {</span>
                        filePath.mkdirs();
                    }
                    // 判断file有没有
                    videoFile  = new File(ImageCacheConfig.IMAGE_CACHE_PATH, videoFileName);
                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                    os = new FileOutputStream(videoFile);
                    while ((len = is.read(bytes)) > 0) {
                        os.write(bytes, 0, len);
                    }
                    os.flush();
                }
                connection.disconnect();
                long endTime = System.currentTimeMillis();
                Log.i(Constants.LOG_TAG, "download video time = "
                        + (endTime - startTime) + "  videoUrl = " + videoUrl);
            } catch (FileNotFoundException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } catch (MalformedURLException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } catch (IOException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } finally {
                IOUtils.closeStream(os);
                if (is != null) {
                    IOUtils.closeStream(is);
                }
                mHandler.sendEmptyMessage(MSG_DOWNLOAD_VIDEO_FINISHED);
            }
        }

        public downloadVideoThread(String url) {
            this.videoUrl = url;
        }
    }

    private void startDownloadVideo(String videoUrl){

        new MainActivity.downloadVideoThread(videoUrl).start();
    }

    private static final int MSG_DOWNLOAD_MATERIAL = 1004;
    private static final int MSG_DOWNLOAD_VIDEO_FINISHED = 1005;
    private static final int MSG_ADD_FRIEND_BY_CONTACT = 1006;
    private static final int MSG_ADD_FRIEND_BY_SEARCH = 1007;



    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DOWNLOAD_MATERIAL:
                    startDownloadMaterial();
                    break;
                case MSG_DOWNLOAD_VIDEO_FINISHED:
                    BitmapUtils.addVideotoAlbumLibrary(MainActivity.this, videoFile);
                    if (FileUtils.getFileSize(ImageCacheConfig.IMAGE_CACHE_PATH) == 0) {
                        Toast.makeText(MainActivity.this, "文件下载损坏，请检查网络", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(MainActivity.this, "开始执行指令", Toast.LENGTH_SHORT).show();
                    Utils.setIsPublishVideo(true);
                    WechatControl.publishSns(MainActivity.this);
                    break;
                default:
                    Log.i(Constants.LOG_TAG, "Unhandled msg - " + msg.what);
                    break;
            }
        }
    };

    private void downloadMomentMaterial(String materialUrl) {
        //String url = HttpConstants.COMMON_DOMAIN_NOPORT + materialUrl;
        String url = HttpConstants.COMMON_HTTP + materialUrl;
        String token = Preferences.getUserToken(this);
        BaseHttpRequest<BaseBean<MomentMaterialModel>> request = new BaseHttpRequest<BaseBean<MomentMaterialModel>>(Request.Method.GET, url, null, token, new downloadMomentMaterialCallback<BaseBean<MomentMaterialModel>>());
        VolleyUtil.getRequestQueue().add(request);
    }




    private  int material_index = 0;
    private List<String> picUrls;

    public class downloadMomentMaterialCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if(htmlBodyBytes != null) {
                    BaseBean<MomentMaterialModel> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<MomentMaterialModel>>() {
                    });
                    if(dataBean!=null && !TextUtils.isEmpty(dataBean.getMessage())) {
                        Toast.makeText(MainActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<MomentMaterialModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<MomentMaterialModel>>() {
            });
            if (model != null && model.getData() != null) {
                Log.i(Constants.LOG_TAG, "downloadMomentMaterialCallback, content:" + model.getData().getContent() + ", url:" + model.getData().getPictures().toString());
                Preferences.saveMomentMaterialContent(MainActivity.this, model.getData().getContent());
                if (push_type == 1) {
                    picUrls = model.getData().getPictures();
                    Preferences.saveMomentMaterialCount(MainActivity.this, picUrls.size());
                    material_index = 0;
                    mHandler.sendEmptyMessage(MSG_DOWNLOAD_MATERIAL);
                }else if(push_type == 3){
                    picUrls = model.getData().getPictures();
                    if(picUrls.size() > 0) {
                        String videoUrl =picUrls.get(0);
                        Preferences.saveMomentMaterialCount(MainActivity.this, 1);
                        startDownloadVideo(videoUrl);
                    }
                }
                else if (push_type == 7) {
                    Toast.makeText(MainActivity.this, "开始执行指令", Toast.LENGTH_SHORT).show();
                    WechatControl.sendFriendsMessage(MainActivity.this);
                }


            }
        }
    }
    //先从最后一个素材下载，因为图库的排序顺序是按照时间来排序，这样第1个素材就排在第1位
    private void startDownloadMaterial(){
        final int size = picUrls.size();
        ImageLoader.getInstance().loadImage(picUrls.get(size - 1 - material_index), new SimpleImageLoadingListener() {
            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                super.onLoadingFailed(imageUri, view, failReason);
                mHandler.removeMessages(MSG_DOWNLOAD_MATERIAL);
                //TODO(caoxianjin) for test
                Toast.makeText(MainActivity.this, "素材下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                super.onLoadingComplete(imageUri, view, loadedImage);
                String saveImageFile = "wechatrobot" ;
                BitmapUtils.saveBitmap(MainActivity.this, saveImageFile, loadedImage);
                SystemControl.delay(2000);
                if (material_index == (size -1)  ){
                    mHandler.removeMessages(MSG_DOWNLOAD_MATERIAL);
                    Toast.makeText(MainActivity.this, "已经下载完第"+(material_index+1)+"个素材", Toast.LENGTH_SHORT).show();
                    if(FileUtils.getFileSize(ImageCacheConfig.IMAGE_CACHE_PATH)  == 0){
                        Toast.makeText(MainActivity.this, "文件下载损坏，请检查网络", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Toast.makeText(MainActivity.this, "开始执行指令", Toast.LENGTH_SHORT).show();
                    WechatControl.publishSns(MainActivity.this);
                } else {
                    //TODO(caoxianjin) for test
                    Toast.makeText(MainActivity.this, "已经下载完第"+(material_index+1)+"个素材", Toast.LENGTH_SHORT).show();
                    material_index++;
                    mHandler.removeMessages(MSG_DOWNLOAD_MATERIAL);
                    mHandler.sendEmptyMessage(MSG_DOWNLOAD_MATERIAL);
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isForeground = true;
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }


    @Override
    protected void onPause() {
        super.onPause();
        isForeground = false;
        mTelephonyManager.listen(mListener, PhoneStatListener.LISTEN_SIGNAL_STRENGTHS);
    }

    @Override
    public void onBackPressed() {
        if(layout_download.getVisibility() ==View.VISIBLE){
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
            unregisterReceiver(mNetWorkBroadCastReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }
        mHandler.removeCallbacksAndMessages(null);
        mCheckWechatThread.interrupt();
        isCreated = false;
    }

    private   checkWechatThread mCheckWechatThread;

    public  class checkWechatThread extends Thread implements Runnable {
        @Override
        public void run() {
                while (!mCheckWechatThread.isInterrupted()) {
                    try {
                        if(push_type >=1 && push_type <= 12) {
                            if (Utils.isIsWechatStopped() && !Utils.isIsWechatStarted()) {
                                Log.i(Constants.LOG_TAG, "checkWechatThread, start wechat");
                                WechatControl.startWechatLoop(MainActivity.this);
                            }
                        }
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
            }

    }

    /*
       任务完成上报结果
       @para prod_type 1:养客  2：拓客
             push_type: 推送类型
             deviceId: 设备ID
             taskId: 任务id
       @return void
     */

    private void taskFeedback(int prod_type, int push_type, String deviceId, String taskId) {
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.TASK_FEEBACK;
        String token = Preferences.getUserToken(this);
        JSONObject jsonObject = new JSONObject();
        //data:{"prod_type":2,"push_type":3,"device_id":"593cde7e73fef74fb5aab96d","task_id":"594e055f43b128ea6fcca537"}
        jsonObject.put("prod_type", prod_type);
        jsonObject.put("push_type", push_type);
        jsonObject.put("device_id", deviceId);
        jsonObject.put("task_id", taskId);
        Log.i(Constants.LOG_TAG, "map:"+jsonObject.toString());
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", JSON.toJSONString(jsonObject));
        BaseHttpRequest<BaseBean<String>> request = new BaseHttpRequest<BaseBean<String>>(Request.Method.POST, url, map, token, new taskFeedbackCallback<BaseBean<String>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    public class taskFeedbackCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if(htmlBodyBytes != null) {
                    try{
                         BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {});
                         Toast.makeText(MainActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                       }catch(Exception e){
                          e.printStackTrace();
                       }
                }
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<String> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<String>>() {
            });
            if (model != null && model.getData() != null) {
                Toast.makeText(MainActivity.this, model.getData().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


}
