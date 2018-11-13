package com.czfrobot.wechatrobot.accessibility;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.activity.MainActivity;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.LocationUtil;
import com.czfrobot.wechatrobot.utils.PerformClickUtils;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.SystemControl;
import com.czfrobot.wechatrobot.utils.Utils;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RobotService extends AccessibilityService {

    //    private final  static  String LOG = "AutoReplyService";
    private final static String LOG = "wechatrobot";

    private final static String MM_PNAME = Constants.WECHAT_PACKAGE_NAME;
    private final static int WECHAT_VERSION_654 = 654;//6.5.4
    private final static int WECHAT_VERSION_6318 = 6318;//6.3.18

    private final static String WECHAT_FIND_LABEL= "发现";
    private final static String WECHAT_MOMENT_LABEL= "朋友圈";
    private final static String WECHAT_SELECTFROM_ALBUM_LABEL= "从相册选择";
    private final static String WECHAT_FINISH_LABEL= "完成";
    private final static String WECHAT_SEND_LABEL= "发送";
    private final static String WECHAT_NEARBY_LABEL= "附近的人";
    private final static String WECHAT_SHAKEOFF_LABEL= "摇一摇";
    private final static String WECHAT_BOTTLE_LABEL= "漂流瓶";

    private final static String WECHAT_ADDFRIEND_LABEL= "添加朋友";
    private final static String WECHAT_ADDFRIENDBYRADDAR_LABEL= "雷达加朋友";
    private final static String WECHAT_PHONECONTACT_LABEL= "手机联系人";
    private final static String WECHAT_ADDRESSLIST_LABEL= "添加到通讯录";

    private final static String WECHAT_MY_LABEL= "我";
    private final static String WECHAT_SETTINGS_LABEL= "设置";
    private final static String WECHAT_COMMON_LABEL= "通用";
    private final static String WECHAT_FUNCTION_LABEL= "功能";
    private final static String WECHAT_ASSISTANT_LABEL= "群发助手";
    private final static String WECHAT_BEGINMASSSEND_LABEL= "开始群发";
    private final static String WECHAT_NEWMASSSEND_LABEL= "新建群发";
    private final static String WECHAT_NEXTSTEP_LABEL= "下一步";

    private final static String WECHAT_NUMBER_LABEL= "微信号";

    private final static String WECHAT_SAYHI_LABEL= "打招呼";








    private int current_wechat_version = 654;

    boolean hasAction = false;
    boolean locked = false;
    boolean background = false;
    private String name;
    private String scontent;
    private String mUserName = "";
    private String description = "";
    private boolean ifOnce = false;
    private int nLikeCount = 0;
    private int nLikeTotalCount = 0;

    AccessibilityNodeInfo itemNodeinfo;
    List<AccessibilityNodeInfo> likeList;
    private KeyguardManager.KeyguardLock kl;
    private Handler handler = new Handler();


    @Override
    protected void onServiceConnected() {//辅助服务被打开后 执行此方法
        super.onServiceConnected();
        try {
            Toast.makeText(getApplicationContext(), "_已开启自动微信机器人服务_", Toast.LENGTH_LONG).show();
        } catch (Exception e) {

        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    /**
     * 必须重写的方法，响应各种事件。
     *
     * @param event
     */
    @Override
    public void onAccessibilityEvent(final AccessibilityEvent event) {
        int eventType = event.getEventType();
        android.util.Log.i(LOG, "packageName:" + event.getPackageName() + "");//响应事件的包名，也就是哪个应用才响应了这个事件
//        android.util.Log.i(LOG,"source:" + event.getSource() + "");//事件源信息
        android.util.Log.i(LOG, "source class:" + event.getClassName() + "");//事件源的类名，比如android.widget.TextView
        android.util.Log.i(LOG, "event type(int):" + eventType + "");

        switch (eventType) {
            case AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED:// 通知栏事件
                android.util.Log.d(LOG, "get notification event");
                handleNotification(event);
                break;
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                String className = event.getClassName().toString();
                android.util.Log.i(LOG, "get type window down event, hasAction:" + hasAction + ", className:" + className);
                Log.i(Constants.LOG_TAG, "top package:" + Utils.getTopPackage(this));

                if(!Utils.isIsWechatStarted()) {
                    if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                       Utils.setIsWechatStarted(true);
                    }
                }


                if (className.equals("com.tencent.mm.sandbox.updater.AppUpdaterUI")) {
                    AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
                    List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("取消");
                    if (list.size() > 0) {
                        list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }
                if (Utils.isAutoPublishSns()) {
                    publishSns(className); //发送朋友圈
                } else if (Utils.isAddContactFriends()) {
                    addConactFriends(className); //加朋友
                } else if (Utils.isSendAllfriendsMessage()) {
                    sendAllFriendsMessage(className);//群发消息
                } else if (Utils.isSearchFriends()) {
                    searchFriends(className);//搜索加朋友
                } else if (Utils.isSendLike()) {
                    likeFriends(className);//点赞
                } else if (Utils.isIsSearchNearbyFriends()) {
                    searchNearbyFriends(className);
                } else if (Utils.isIsShakeOff()) {
                    bIsfromShakeReportUI = false;
                    shakeOff(className);
                } else if (Utils.isIsDriftBottle()) {
                    driftBottle(className);
                } else if (Utils.isIsScanRaddar()) {
                    scanRaddar(className);
                } else if (Utils.isIsStandStreet()) {
                    standStreet(className);
                } else if (hasAction) {
                    handleNotification(className);
                }
                break;
        }
    }

    /**
     * 自制通知栏文本过滤方法
     *
     * @param event
     */
    private void handleNotification(AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                Log.i(Constants.LOG_TAG, "handleNotification, content is:" +content);
            }
            sendNotifacationReply(event);
        }
    }

    private String nickName = "", wechatName = "", region = "", source = "", phoneNumber = "", desc = "";

    private void handleNotification(String className) {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            findLoopbyID("id");
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            desc = getText("la"); //备注名
            nickName = getText("adf");//昵称
            if (TextUtils.isEmpty(nickName)) {
                nickName = desc;
            }
            wechatName = getText("ad6"); //微信号
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("android:id/summary");
            if (list.size() > 0) {
                region = list.get(0).getText().toString(); //地区
            }
            Log.i(Constants.LOG_TAG, "handleNotification, desc:" + desc + ", nickName:" + nickName + ",wechatName:" + wechatName + ", region:" + region);
            list = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c28");
            if (list.size() > 0) {
                phoneNumber = list.get(0).getChild(0).getText().toString(); //电话号码
            }
            Log.i(Constants.LOG_TAG, "handleNotification, phoneNumber:" + phoneNumber);

            list = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/i3");
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到更多");
                String[] result = region.split(" ");
                String province = "", city = "";
                if (result != null) {
                    province = result[0];
                    city = result[1];
                }
                Log.i(Constants.LOG_TAG, "handleNotification, province:" + province + ",city:" + city);
                wechatFeedback(nickName, province, city, wechatName, desc, System.currentTimeMillis(), source);

                quitWechat();
                hasAction = false;
            }

        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactMoreInfoUI")) {
            List<AccessibilityNodeInfo> list = rootNode.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c2_");
            if (list.size() > 0) {
                if(list.size() == 1) {
                    source = list.get(0).getText().toString(); ////来源：通过什么途径添加的微信
                }else if(list.size() == 2){
                    source = list.get(1).getText().toString(); ////来源：通过什么途径添加的微信
                }
            }
            Log.i(Constants.LOG_TAG, "handleNotification, source:" + source );
            String[] result = region.split("\\s+");
            for(String ss : result){
                Log.i(Constants.LOG_TAG, ss);
            }
            String province = "", city = "";
            if (result != null) {
                if(result.length > 0) {
                    province = result[0];
                }
                if(result.length > 1) {
                    city = result[1];
                }
            }
            Log.i(Constants.LOG_TAG, "handleNotification, province:" + province + ",city:" + city);


            wechatFeedback(nickName, province, city, wechatName, desc, System.currentTimeMillis(), source);

            quitWechat();
            hasAction = false;
        }

    }

//    avatar: // 头像
//
//    nickname: // 昵称
//
//    sex: // 性别，1:男、2:女、3:未知
//
//    province: // 省份
//
//    city: // 城市
//
//    wechat: // 微信号
//
//    desc: // 备注名
//
//    device_id: // 设备Id
//
//    device_desc: // 设备备注名
//
//    device_wechat: // 设备微信号
//
//    done_at: // 好友添加时间
//
//    source_type: // 来源，对应推送类型，1:通过搜索手机号／微信号／QQ、2:通过手机联系人、3:通过附近的人、4:通过摇一摇、5:通过群添加、6:通过扫雷达、7:通过扫二维码、8:通过站街扫街、9:通过漂流瓶

  //  data:{"avatar":"http://","nickname":"hello","sex":1,"province":"上海","city":"上海",
  // "wechat":"gholdnet","desc":"老","device_id":"593cde7e73fef74fb5aab96d",
  // "device_desc":"world","device_wechat":"kkk","done_at":1494672436000,"source_type":2}
    private void wechatFeedback(String nickName, String province, String city, String wechat, String desc, long time, String source) {
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.WECHAT_FEEBACK;
        String token = Preferences.getUserToken(this);
        JSONObject jsonObject = new JSONObject();

        int source_type = -1;
        if (source.contains("搜索")) {
            source_type = 1;
        } else if (source.contains("手机通讯录")) {
            source_type = 2;
        } else if (source.contains("附近")) {
            source_type = 3;
        } else if (source.contains("摇一摇")) {
            source_type = 4;
        } else if (source.contains("群聊")) {
            source_type = 5;
        } else if (source.contains("扫雷达")) {
            source_type = 6;
        } else if (source.contains("二维码")) {
            source_type = 7;
        } else if (source.contains("站街扫街")) {
            source_type = 8;
        } else if (source.contains("漂流瓶")) {
            source_type = 9;
        }
        //data:{"avatar":"http://","nickname":"hello","sex":1,"province":"上海","city":"上海",
        // "wechat":"gholdnet","desc":"老","device_id":"593cde7e73fef74fb5aab96d",
        // "device_desc":"world","device_wechat":"kkk","done_at":1494672436000,"source_type":2}
        jsonObject.put("avatar", "");
        jsonObject.put("nickname", nickName);
        jsonObject.put("sex", 3);
        jsonObject.put("province", province);
        jsonObject.put("city", city);
        jsonObject.put("wechat", wechat);
        jsonObject.put("desc", desc);
        jsonObject.put("device_id", Preferences.getJPushAlias(this));
        jsonObject.put("device_desc", Preferences.getDeviceName(this));
        jsonObject.put("device_wechat", "");
        jsonObject.put("done_at", time);
        jsonObject.put("source_type", source_type);

        Log.i(Constants.LOG_TAG, "map:"+jsonObject.toString());
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", JSON.toJSONString(jsonObject));
        BaseHttpRequest<BaseBean<String>> request = new BaseHttpRequest<BaseBean<String>>(Request.Method.POST, url, map, token, new wechatFeedbackCallback<BaseBean<String>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    public class wechatFeedbackCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if(htmlBodyBytes != null) {
                    BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                    });
                    Toast.makeText(RobotService.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<String> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<String>>() {
            });
            if (model != null && model.getData() != null) {
                Toast.makeText(RobotService.this, model.getData().toString(), Toast.LENGTH_LONG).show();
            }
        }
    }


    private void AutoReply(final AccessibilityEvent event) {
        List<CharSequence> texts = event.getText();
        if (!texts.isEmpty()) {
            for (CharSequence text : texts) {
                String content = text.toString();
                if (!TextUtils.isEmpty(content)) {
                    if (isScreenLocked()) {
                        locked = true;
                        wakeAndUnlock();
                        android.util.Log.i(LOG, "the screen is locked");
                        if (Utils.isAppForeground(this, MM_PNAME)) {
                            background = false;
                            android.util.Log.i("maptrix", "is mm in foreground");
                            sendNotifacationReply(event);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    sendNotifacationReply(event);
                                    if (fill()) {
                                        send();
                                    }
                                }
                            }, 1000);
                        } else {
                            background = true;
                            android.util.Log.i(LOG, "is mm in background");
                            sendNotifacationReply(event);
                        }
                    } else {
                        locked = false;
                        android.util.Log.d(LOG, "the screen is unlocked");
                        if (Utils.isAppForeground(this, MM_PNAME)) {
                            background = false;
                            android.util.Log.i(LOG, "is mm in foreground");
                            sendNotifacationReply(event);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (fill()) {
                                        send();
                                    }
                                }
                            }, 1000);
                        } else {
                            background = true;
                            android.util.Log.d(LOG, "is mm in background");
                            sendNotifacationReply(event);
                        }
                    }
                }
            }
        }
    }

    private void AutoReply(String className) {
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (fill()) {
                send();
            } else {
                if (itemNodeinfo != null) {
                    itemNodeinfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (fill()) {
                                send();
                            }
                            Utils.back2Home(getApplicationContext());
                            release();
                            hasAction = false;
                        }
                    }, 1000);
                }
            }
        }
        Utils.back2Home(this);
        release();
        hasAction = false;
    }


    private long  standStreetGap = 0;
    private int standStreetCount = 0;
    private int standStreetTotalCount = 0;
    private void standStreet(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();

        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }

            if (standStreetCount == 0) {
                findLoop(nodeInfo, WECHAT_FIND_LABEL);
            }
            findLoop(nodeInfo, WECHAT_NEARBY_LABEL);
            standStreetGap = System.currentTimeMillis();
            standStreetTotalCount = Preferences.getStandCount(this);

        } else if (className.equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI")) {
            if (standStreetCount == standStreetTotalCount) {
                Utils.setIsStandStreet(false);
                standStreetCount = 0;
                quitWechat();
            }
            addMeBynearbyFriends();

        }
    }

    private void addMeBynearbyFriends(){
        int gap = Preferences.getStandGap(this)*1000;
        if( (System.currentTimeMillis() - standStreetGap) >= gap ){
            standStreetCount++;
            PerformClickUtils.performBack(this);
        }else{
            SystemControl.delay(2000);
            addMeBynearbyFriends();
        }
    }

    private int driftBottleTotalCount=0, drifBottleCount=0;
    private void driftBottle(String className){
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();
        Log.i(LOG, "driftBottle, start");
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            findLoop(nodeInfo, WECHAT_FIND_LABEL);
            SystemControl.delay(2000);

            drifBottleCount = 0;
            driftBottleTotalCount = Preferences.getDriftBottleCount(this);
            if (nodeInfo != null) {
                list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_BOTTLE_LABEL);
                if (list.size() > 0) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    Log.i(LOG, "没找到漂流瓶");
                    Utils.setIsDriftBottle(false);
                    quitWechat();
                }
            }
            Preferences.setIsThrowBottleUI(this, false);

        }else if(className.equals("com.tencent.mm.plugin.bottle.ui.BottleBeachUI")){
            driftBottleLoop();
        }

    }

    private void driftBottleLoop(){
        AccessibilityNodeInfo nodeInfo = null;
        nodeInfo = getRootInActiveWindow();
        performClick("r2");
        SystemControl.delay(500);
        Preferences.setIsThrowBottleUI(this, true);
        AccessibilityNodeInfo targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/s1");
        if (targetNode != null) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
        SystemControl.delay(1000);
        setText("rw", Preferences.getDriftBottleContent(this));
        SystemControl.delay(2000);
        PerformClickUtils.performBack(this);
        Preferences.setIsThrowBottleUI(this, true);
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/s3");
        if(list.size() > 0){
            Log.i(Constants.LOG_TAG, "driftBottleLoop, size:" + list.size());
            boolean ret = list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK); //does not work
            Log.i(Constants.LOG_TAG, "driftBottleLoop, click result:" + ret+", text:" + list.get(0).getText().toString());
            list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else{
            Log.i(Constants.LOG_TAG, "没找到扔出去");
        }

        SystemControl.delay(5000);
        drifBottleCount++;
        if(driftBottleTotalCount == drifBottleCount){
            Utils.setIsDriftBottle(false);
            quitWechat();
        }else{
            driftBottleLoop();
        }
    }


    private long scanRaddarTime = 0;//扫描时间间隔
    private int scanRaddarTotalCount = 0;
    private int scanRaddarCount = 0;
    private int scanRaddarFriendCount = 0;
    private void scanRaddar(String className){
            AccessibilityNodeInfo nodeInfo = null;
            List<AccessibilityNodeInfo> list = null;
            nodeInfo = getRootInActiveWindow();
            phones = Utils.getContactList();
            if (className.equals("com.tencent.mm.ui.LauncherUI")) {
                if (nodeInfo == null) {
                    Log.i(LOG, "rootWindow为空");
                    Utils.setIsAddContactFriends(false);
                    return;
                }

                findLoopbyID("f_");//点击微信首页的"+"按钮
                scanRaddarCount = 0;
                scanRaddarTotalCount = Preferences.getScanRaddarCount(this);

            } else if (className.equals("android.widget.FrameLayout")) {
                list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDFRIEND_LABEL);
                if (list.size() > 0) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    Log.i(LOG, "没找到添加朋友");
                    Utils.setIsAddContactFriends(false);
                }
            } else if (className.equals("com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsUI")) {
                if(scanRaddarCount == scanRaddarTotalCount){
                    Utils.setIsScanRaddar(false);
                    quitWechat();
                    return;
                }
                list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDFRIENDBYRADDAR_LABEL);
                if (list.size() > 0) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    scanRaddarTime = System.currentTimeMillis();
                    scanRaddarTotalCount = Preferences.getScanRaddarCount(this);
                    scanRaddarCount++;
                    Log.i(LOG, "第"+scanRaddarCount+"次雷达加朋友");

                } else {
                    Log.i(LOG, "没找到雷达加朋友");
                    Utils.setIsAddContactFriends(false);
                }
            }else if(className.equals("com.tencent.mm.plugin.radar.ui.RadarSearchUI")){
                scanRaddarFriendCount = 0;
                addRaddarFriend();
            }
    }


    private void addRaddarFriend(){
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();
        String friendName;
        //扫描5分钟退出
        if((System.currentTimeMillis()-scanRaddarTime) >= (5*6*1000)){
            PerformClickUtils.performBack(this);
            return;
        }
        SystemControl.delay(1000);
        list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c43");
        if (list.size() > 0) {
            list.get(scanRaddarFriendCount).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(scanRaddarFriendCount).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            scanRaddarFriendCount++;
            if(scanRaddarFriendCount == list.size()){
                scanRaddarFriendCount = 0;
            }
        } else {
            Log.i(LOG, "没找到朋友");
            Utils.setIsAddContactFriends(false);
        }
        SystemControl.delay(1000);
        list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/c41");
        if (list.size() > 0) {
            if(list.get(list.size() - 1).getText() != null) {
                friendName = list.get(list.size() - 1).getText().toString();
            }
        } else {
            Log.i(LOG, "没找到朋友");
            Utils.setIsAddContactFriends(false);
        }
        list = nodeInfo.findAccessibilityNodeInfosByText("加为好友");
        if (list.size() > 0) {
            list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            Log.i(LOG, "没找到朋友");
            Utils.setIsAddContactFriends(false);
        }
        SystemControl.delay(1000);
        addRaddarFriend();
    }


    private boolean bIsfromSayHiEditUIWhenShake = false;
    private boolean bIsfromShakeReportUI = false;
    private int shakeFriendCount = 0, shakeFriendTotalCount = 0;
    private String shakeFriendName = null;

    private  void shakeOff(String className){
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if(!bIsfromShakeReportUI) {
                if (nodeInfo == null) {
                    Log.i(LOG, "rootWindow为空");
                    return;
                }
                findLoop(nodeInfo, WECHAT_FIND_LABEL);
                SystemControl.delay(2000);
                shakeFriendCount = 0;
                shakeFriendTotalCount = Preferences.getShakeoffTimes(this);
            }else {
                bIsfromShakeReportUI = false;
                Utils.startShakeoff();
            }
            findLoop(nodeInfo, WECHAT_SHAKEOFF_LABEL);
        } else if(className.equals("com.tencent.mm.ui.base.i")){
            findLoop(nodeInfo, "知道了");
        } else if (className.equals("com.tencent.mm.plugin.shake.ui.ShakeReportUI")) {
            SystemControl.delay(2000);
            Utils.stopShakeoff();
            SystemControl.delay(3000);

//            list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fx");
//            if (list.size() > 0) {
//                list.get(0).getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }

            list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cem");
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "no man found");
                bIsfromShakeReportUI = true;
                shakeFriendCount++;
                if(shakeFriendTotalCount == shakeFriendCount) {
                    Utils.setIsShakeOff(this, false);
                    quitWechat();
                }else {
                    PerformClickUtils.performBack(this);
                }
            }

            bIsfromSayHiEditUIWhenShake = false;
        }else if(className.equals("com.tencent.mm/.plugin.shake.ui.ShakePersonalInfoUI")){
            SystemControl.delay(1000);
            shakeFriendCount++;
            if(shakeFriendTotalCount == shakeFriendCount) {
                Utils.setIsShakeOff(this, false);
                quitWechat();
            }else {
                PerformClickUtils.performBack(this);
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            if(bIsfromSayHiEditUIWhenShake){
                PerformClickUtils.performBack(this);
                Utils.startShakeoff();
                return;
            }
            SystemControl.delay(1000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SAYHI_LABEL);
            if (list.size() > 0) {
                shakeFriendName = getText("la");
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到详细资料的打招呼功能");
                if (shakeFriendTotalCount == shakeFriendCount) {
                    Utils.setIsShakeOff(this, false);
                    quitWechat();
                } else {
                    PerformClickUtils.performBack(this);
                }
            }
        }else if(className.equals("com.tencent.mm.ui.contact.SayHiEditUI")){
            if(bIsfromSayHiEditUIWhenShake){
                return;
            }
            SystemControl.delay(500);
            setText("c9p", "Hi, " + shakeFriendName + ", 你好");
            SystemControl.delay(2000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsSearchNearbyFriends(false);
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                shakeFriendCount++;
            } else {
                Log.i(LOG, "没找到验证申请的发送功能");
            }

            SystemControl.delay(3000);
            if(shakeFriendTotalCount == shakeFriendCount) {
                Utils.setIsShakeOff(this, false);
                quitWechat();
            }else {
                bIsfromSayHiEditUIWhenShake = true;
            }
        }
    }


    private int nFinduserCount = 0;
    private int nFinduserTotalCount = 0;
    private boolean bLastPage = false;
    private boolean bIsfromSayHiWithSnsPermissionUI = false;
    private List<String> lastUsernameList = new ArrayList<String>();
    private List<String> addedUsernameList = new ArrayList<String>();
    List<String> phones = null;
    List<AccessibilityNodeInfo> friendsList = null;


    private void addConactFriends(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();
        phones = Utils.getContactList();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsAddContactFriends(false);
                return;
            }

            findLoopbyID("f_");//点击微信首页的"+"按钮
            nFinduserCount = 0;
            lastUsernameList.clear();
            addedUsernameList.clear();
            ;
            bLastPage = false;

        } else if (className.equals("android.widget.FrameLayout")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDFRIEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到添加朋友");
                Utils.setIsAddContactFriends(false);
            }
        } else if (className.equals("com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsUI")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_PHONECONTACT_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到LinkedIn/手机联系人");
                Utils.setIsAddContactFriends(false);
            }
        } else if (className.equals("com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsByOtherWayUI")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_PHONECONTACT_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到手机联系人");
                Utils.setIsAddContactFriends(false);
            }
        } else if (className.equals("com.tencent.mm.ui.bindmobile.MobileFriendUI")) {
            if (bIsfromSayHiWithSnsPermissionUI) {
                bIsfromSayHiWithSnsPermissionUI = false;
            } else {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            friendsList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/atd");
            nFinduserTotalCount = phones.size();
            findContactUser();

        }
        //详细资料：com.tencent.mm/.plugin.profile.ui.ContactInfoUI
        else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            if (bIsfromSayHiWithSnsPermissionUI) {
                PerformClickUtils.performBack(this);
                Log.i(LOG, "from ContactInfoUI back");
                return;
            }
            SystemControl.delay(1000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDRESSLIST_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到详细资料的添加通讯录功能");
                if (nFinduserCount == nFinduserTotalCount) {
                    Utils.setIsAddContactFriends(false);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    quitWechat();
                } else {
                    PerformClickUtils.performBack(this);
                    Log.i(LOG, "from ContactInfoUI back");
                }
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI")) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String greet = Preferences.getGreet(this);
            if(TextUtils.isEmpty(greet)){
                greet = "你还记得我吗";
            }
            if(Preferences.getNeedHi(this) == 1){
                greet = "Hi, "+ getText("c9t") + ", " + greet;
            }
            setText("c9p", greet);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsAddContactFriends(false);
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到验证申请的发送功能");
            }
            if (nFinduserCount == nFinduserTotalCount) {
                Utils.setIsAddContactFriends(false);
                SystemControl.delay(3000);
                quitWechat();
            } else {
                SystemControl.delay(3000);
                Log.i(LOG, "from SayHiWithSnsPermissionUI back");
                bIsfromSayHiWithSnsPermissionUI = true;
            }

        }


    }

    private void findContactUser() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText("添加");
        List<AccessibilityNodeInfo> nameList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/awk");
        Log.i(LOG, "list size:" + list.size() + ", nameList size:" + nameList.size());
        if (list.size() > 0) {
            for (int m = 0; m < list.size(); m++) {
                if (list.get(m).getParent().getParent().getChild(0).getText() != null)
                    Log.i(LOG, "list text:" + list.get(m).getParent().getParent().getChild(0).getText().toString());
            }

            for (int n = 0; n < nameList.size(); n++) {
                if (nameList.get(n).getText() != null)
                    Log.i(LOG, "namelist text:" + nameList.get(n).getText().toString());
            }
            AccessibilityNodeInfo lastAddBtn = list.get(0);
            Rect lastAddBtnOutBound = new Rect();
            lastAddBtn.getBoundsInScreen(lastAddBtnOutBound);
            Log.i(LOG, ", lastAddBtnOutBound top:" + lastAddBtnOutBound.top + ", lastAddBtnOutBound bottom:" + lastAddBtnOutBound.bottom);
            //删掉超出屏幕的添加按钮
            lastAddBtn = list.get(list.size() - 1);
            lastAddBtn.getBoundsInScreen(lastAddBtnOutBound);
            Log.i(LOG, ", lastAddBtnOutBound top:" + lastAddBtnOutBound.top + ", lastAddBtnOutBound bottom:" + lastAddBtnOutBound.bottom);
            if (lastAddBtnOutBound.top > DeviceUtils.getScreenHeight(this)) {
                list.remove(lastAddBtn);
                nameList.remove(nameList.size() - 1);
            }
            //TODO(caoxianjin)for test, must delete
//                Log.i(LOG, "list.get(list.size()-1).getParent().getParent():" + list.get(list.size()-1).getParent().getParent());
//                int tmpSize = list.get(list.size()-1).getParent().getParent().getChildCount();
//                for(int j=0;j<tmpSize;j++){
//                    Log.i(LOG, "list.get(list.size()-1).getParent().getParent() child:" + j+ ":"  +list.get(list.size()-1).getParent().getParent().getChild(j));
//
//                }

            String userName = nameList.get(list.size() - 1).getText().toString();
            Log.i(LOG, "最后一个用户：" + userName);
            if (lastUsernameList.contains(userName) == false) {
                lastUsernameList.add(userName);
                bLastPage = false;
            } else {
                bLastPage = true;
            }
            int index = -1;
            //找到下发通信录的好友名单，并做记录，防止重复添加；再排除已添加的用户
            for (int i = 0; i < list.size(); i++) {
                boolean findName = false;
                userName = nameList.get(i).getText().toString();
                Log.i(LOG, "当前第" + i + "个用户的用户名：" + userName);
                if (addedUsernameList.contains(userName) == false) {
                    for (String phone : phones) {
                        if (userName.equals(phone)) {
                            findName = true;
                            nFinduserCount++;
                            addedUsernameList.add(userName);
                            break;
                        }
                    }
                }
                //只处理需要添加的用户，排除重复添加或者已添加的用户
                if (list.get(i).getText().toString().equals("添加")) {
                    if (findName) {
                        index = i;
                        break;
                    }
                }
            }
            if (index == -1) {//没有找到用户
                if (bLastPage) {//最后一页，而且没有找到对应的用户，退出微信
                    Utils.setIsAddContactFriends(false);
                    SystemControl.delay(2000);
                    Log.i(LOG, "found no user, quit wechat");
                    quitWechat();
                } else {//不是最后一页，翻页
                    Log.i(LOG, "scroll to next page");
                    friendsList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    findContactUser();
                }
            } else {
                Log.i(LOG, "从第" + index + "个用户开始添加");
                bIsfromSayHiWithSnsPermissionUI = false;
                nameList.get(index).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            Log.i(LOG, "没找到手机通讯录的添加功能");
            Utils.setIsAddContactFriends(false);
        }
    }


    private void searchFriends(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();
        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsSearchFriends(this, false);
                return;
            }
            if (current_wechat_version == WECHAT_VERSION_654) {
//                findSearchLoop();
                findLoopbyID("f_");//点击微信首页的"+"按钮  6.5.4版本
            } else if (current_wechat_version == WECHAT_VERSION_6318) {
                findLoopbyID("dn");//点击微信首页的"+"按钮
            }
            bIsfromSayHiWithSnsPermissionUI = false;
        } else if (className.equals("android.widget.FrameLayout")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDFRIEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到添加朋友");
                Utils.setIsSearchFriends(this, false);
            }
            addFriendCount = 0;
        }

        else if (className.equals("com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsUI")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_NUMBER_LABEL);
            if(list.size() > 0){
                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
//
//            performClick("gr");//does not work
//            performClick("bql");//does not work
//
//            if (current_wechat_version == WECHAT_VERSION_654) {
//                list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/i3");
//            } else {
//                list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/g1");
//            }
//            if (list.size() > 0) {
//                Log.i(LOG, "i3 node:" + list.get(0).isClickable());
//                Log.i(LOG, "i3 node parent :" + list.get(0).getParent().isClickable());
//                list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);//does not work
//                list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);//does not work
//            }
//
//            list = nodeInfo.findAccessibilityNodeInfosByViewId("android:id/list");
//            if(list.size() > 0){
//                Log.i(LOG, "list node child 1 clickable:" + list.get(0).getChild(1).isClickable());
//                list.get(0).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//            list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bru");
//            if(list.size() > 0){
//                Log.i(LOG, "bru node child 0 child 1 clickable:" + list.get(0).getChild(0).getChild(1).isClickable());
//                list.get(0).getChild(0).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }


        } else if (className.equals("com.tencent.mm.plugin.search.ui.FTSMainUI")) {  //从搜索入口来的，在微信6.5.4版本上not work

            setText("eu", "33333");
            performClick("aph");

            SystemControl.delay(1000);

            list = nodeInfo.findAccessibilityNodeInfosByText("该用户不存在");
            if (list.size() > 0) {
                Utils.setIsSearchFriends(this, false);
                quitWechat();
            } else {
                Log.i(LOG, "没找到该用户不存在");
            }

        } else if (className.equals("com.tencent.mm.plugin.search.ui.FTSAddFriendUI")) {
            if(Utils.getContactList() != null) {
                addFriendTotalCount = Utils.getContactList().size();
            }else{
                addFriendTotalCount = 1;
            }
            bIsfromSayHiWithSnsPermissionUI = false;
            addFriendBysearch();
        }
        //详细资料：com.tencent.mm/.plugin.profile.ui.ContactInfoUI
        else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            if(bIsfromSayHiWithSnsPermissionUI){
                if(addFriendTotalCount == addFriendCount) {
                    Utils.setIsSearchFriends(this, false);
                    quitWechat();
                }else {
                    PerformClickUtils.performBack(this);
                    return;
                }
            }
            SystemControl.delay(1000);

            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ADDRESSLIST_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到详细资料的添加通讯录功能");
                addFriendCount++;
                if(addFriendTotalCount == addFriendCount) {
                    Utils.setIsSearchFriends(this, false);
                    quitWechat();
                }else {
                    PerformClickUtils.performBack(this);
                }
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI")) {
            SystemControl.delay(500);
            String greet = Preferences.getGreet(this);
            if(TextUtils.isEmpty(greet)){
                greet = "你还记得我吗";
            }
            if(Preferences.getNeedHi(this) == 1){
                greet = "Hi, "+ getText("c9t") + ", " + greet;
            }
            setText("c9p", greet);
            SystemControl.delay(2000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsSearchFriends(this, false);
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                addFriendCount++;
            } else {
                Log.i(LOG, "没找到验证申请的发送功能");
            }

            bIsfromSayHiWithSnsPermissionUI = true;

        }


    }

    private int addFriendTotalCount = 0;
    private int addFriendCount = 0;

    private void addFriendBysearch(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();;
        List<AccessibilityNodeInfo> list = null;
        SystemControl.delay(1000);


        if(Utils.getContactList() != null) {
            setText("gr", Utils.getContactList().get(addFriendCount));
        }else{
            setText("gr", "28888");
        }

        SystemControl.delay(2000);

        performClick("alq");
        AccessibilityNodeInfo targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/ho");
        SystemControl.delay(1000);

        list = nodeInfo.findAccessibilityNodeInfosByText("该用户不存在");
        if (list.size() > 0) {
            addFriendCount++;
            if(addFriendCount == addFriendTotalCount){
                Utils.setIsSearchFriends(this, false);
                quitWechat();
            }else{
                Log.i(LOG, "addFriendBysearch go back, addFriendCount:" + addFriendCount);
                PerformClickUtils.performBack(this);
            }
        } else {
            Log.i(LOG, "没找到该用户不存在");
            list = nodeInfo.findAccessibilityNodeInfosByText("操作过于频繁");
            if(list.size() > 0){
                Utils.setIsSearchFriends(this, false);
                quitWechat();
            }
        }
    }


    private void likeFriends(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();

        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            findLoop(nodeInfo, "发现");

            SystemControl.delay(2000);

            if (nodeInfo != null) {
                list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_MOMENT_LABEL);
                if (list.size() > 0) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    Log.i(LOG, "没找到朋友圈");
                }
            }

        } else if (className.equals("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI")) {////com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI
            if (nodeInfo.getContentDescription() != null) {
                description = nodeInfo.getContentDescription().toString();
            }
            Log.i(LOG, "likeFriends, description: " + description + ", userName:"+mUserName);

            //自动点赞流程
            if (mUserName.equals("")) {
                likeList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cn0");
                Log.i(LOG, "likeFriends, 找到的Lv数量: " + likeList.size());
                //如果size不为0，证明当前在朋友圈页面下,开始执行逻辑
                if (likeList.size() != 0) {
                    //1.先记录用户名
                    List<AccessibilityNodeInfo> userNames = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/afa");
                    int size = userNames.size();
                    if (size != 0) {
                        for (int i = 0; i < size; i++) {
                            Log.d(LOG, "likeFriends,  userNames: " + userNames.get(i).getText().toString());
                        }
                        Log.d(LOG, "likeFriends,  userNames.get(0).getParent(): " + userNames.get(0).getParent());
                        Log.d(LOG, "likeFriends,  userNames.get(0).getParent().getChildCount: " + userNames.get(0).getParent().getChildCount());
                        if (userNames.get(0).getParent() != null ) {
                            nLikeTotalCount = Preferences.getSnsLikeCount(this);
                            mUserName = userNames.get(0).getText().toString();
                            topList.clear();
                            if (!mUserName.equals("") && !ifOnce) {
                                Log.d(LOG, "初始化，只会执行一次");
                                Log.i(LOG, "当前的用户名:" + mUserName);
                                nLikeCount = 0;
                                //测试朋友圈点赞
                                if (likeLoop(nodeInfo) == true) {
                                    mUserName = "";
                                    nLikeCount = 0;
                                    ifOnce = false;
                                    Utils.setIsSendLike(false);
                                    SystemControl.delay(1000);
                                    quitWechat();
                                }
                            }
                        }
                    }
                } else {
                    ifOnce = false;
                    mUserName = "";
                }

            }
        }


    }

    ArrayList<Integer> topList = new ArrayList<>();

    private synchronized boolean likeLoop(AccessibilityNodeInfo rootNodeInfo) {
        SystemControl.delay(1000);

        if (!ifOnce) {
            List<AccessibilityNodeInfo> env = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ckj");//朋友圈封面
            if (env.size() == 0) {//如果不在最顶层，先滚到最顶层
                if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_BACKWARD)) {
                    return likeLoop(getRootInActiveWindow());
                }
            }
            ifOnce = true;
        }

        //找到评论按钮
        List<AccessibilityNodeInfo> plBtns = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cj9");
        if (plBtns.size() == 0) {
            if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                return likeLoop(getRootInActiveWindow());
            }
        }

        Log.i(Constants.LOG_TAG, "plBtns size:" + plBtns.size());
        for (int i = 0; i < plBtns.size(); i++) {
            Rect outBounds = new Rect();
            plBtns.get(i).getBoundsInScreen(outBounds);
            int top = outBounds.top;
            Log.i(Constants.LOG_TAG, " top:" + top);
            if (top >= DeviceUtils.getScreenHeight(this)) {
                if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                    return likeLoop(getRootInActiveWindow());
                }
            }
            if (i == (plBtns.size() - 1)) {
                Log.i(Constants.LOG_TAG, " top list:" + topList.toString());
                if (topList.contains(top)) {
                    Log.i(Constants.LOG_TAG, "last page");
                    return true;//最后一页了
                } else {
                    topList.add(top);
                }
            }
            List<AccessibilityNodeInfo> zanBtns = plBtns.get(i).getParent().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cnn");
            if (zanBtns.size() > 0) {
                Log.i(Constants.LOG_TAG, "点赞的人包括：" + zanBtns.get(0).getText().toString());
                //已经点赞了
                if (zanBtns.get(0).getText().toString().contains(mUserName)) {
                    continue;
                }
            }
            if (plBtns.get(i).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                List<AccessibilityNodeInfo> likeBnts = rootNodeInfo.
                        findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cj3");
                if (likeBnts.size() != 0) {
                    if (likeBnts.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                        nLikeCount++;
                        Log.i(Constants.LOG_TAG, " nLikeCount:" + nLikeCount + ", nLikeTotalCount:"+nLikeTotalCount);
                        if (nLikeCount >= nLikeTotalCount) {
                            return true;
                        }
                    }
                }
            }
        }
        boolean ret = likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        Log.i(Constants.LOG_TAG, " scroll ret:" + ret);
        if (ret) {
            return likeLoop(getRootInActiveWindow());
        } else {
            return true;
        }
    }






    /**
     * com.tencent.mm:id/cn0
     * 朋友圈点赞 (目前实现手动滚动全部点赞)
     * 上方固定显示的名字：com.tencent.mm:id/afa
     * 下方点赞：显示id：com.tencent.mm:id/cnn
     * 每发现一个【评论按钮】，就去搜索当前同父组件下的点赞区域有没有自己的ID。
     * 如果有就不点赞，如果没有就点赞
     * 这里要改成不通过Id抓取提高稳定性
     *
     * @param rootNodeInfo
     */
    private synchronized boolean like(AccessibilityNodeInfo rootNodeInfo) {
        Log.i(LOG, "当前线程:" + Thread.currentThread());
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        topList.clear();

        if (nLikeCount >= nLikeTotalCount) {
            return true;
        }

        if (!mUserName.equals("")) {

            //测试获得评论按钮的父节点，再反推出点赞按钮
            List<AccessibilityNodeInfo> fuBtns =
                    rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/co0");

            Log.i(LOG, "fuBtns数量：" + fuBtns.size());

            if (fuBtns.size() != 0) {

                //删掉超出屏幕的fuBtn
                AccessibilityNodeInfo lastFuBtn = fuBtns.get(fuBtns.size() - 1);
                Rect lastFuBtnOutBound = new Rect();
                lastFuBtn.getBoundsInScreen(lastFuBtnOutBound);
                Log.i(LOG, "lastFuBtnOutBound top：" + lastFuBtnOutBound.top + ", lastFuBtnOutBound bottom:" + lastFuBtnOutBound.bottom);

                if (lastFuBtnOutBound.bottom >= DeviceUtils.getScreenHeight(this)) {
                    fuBtns.remove(lastFuBtn);
                }

                Log.i(LOG, "fuBtns数量：" + fuBtns.size());

                if (fuBtns.size() == 0) {
                    if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                        return like(getRootInActiveWindow());
                    }
                }

                for (int i = 0; i < fuBtns.size(); i++) {
                    AccessibilityNodeInfo fuBtn = fuBtns.get(i);
                    Log.i(LOG, "fuBtn的子节点数量:" + fuBtn.getChildCount());//3-4个
                    List<AccessibilityNodeInfo> plBtns = rootNodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cj9");
                    Log.i(LOG, "从这里发现评论按钮:" + plBtns.size());

                    if (plBtns.size() == 0) {
                        if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                            return like(getRootInActiveWindow());
                        }
                    }

                    AccessibilityNodeInfo plbtn = plBtns.get(0);    //评论按钮
                    List<AccessibilityNodeInfo> zanBtns = fuBtn.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cnn");
                    Log.i(LOG, "从这里发现点赞文字显示区域:" + zanBtns.size());
                    if (zanBtns.size() != 0) {
                        //2.如果不为空，则查找有没有自己点过赞，有则不点，没有则点
                        AccessibilityNodeInfo zanbtn = zanBtns.get(0);
                        Log.i(LOG, "点赞的人是:" + zanbtn.getText().toString());
                        if (zanbtn != null && zanbtn.getText() != null &&
                                zanbtn.getText().toString().contains(mUserName)) {
                            Log.i(LOG, "*********************这一条已经被赞过辣");
                            //判断是否需要翻页，如果当前所有页面的父节点都没点过了，就需要翻页
                            boolean ifxuyaofanye = false;
                            Log.i(LOG, "Ｏ(≧口≦)Ｏ: i=" + i + "  fuBtns.size():" + fuBtns.size());
                            if (i == fuBtns.size() - 1) {
                                ifxuyaofanye = true;
                                nLikeCount++;
                                Log.i(LOG, "like count:" + nLikeCount);
                            }
                            if (ifxuyaofanye) {
                                //滑动前检测一下是否还有没有点过的点
                                if (jianceIfLou()) {
                                    Log.i(LOG, "还有遗漏的点！！！！再检查一遍!!!!!!!!!!");
                                    return like(getRootInActiveWindow());
                                } else {
                                    if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                                        return like(getRootInActiveWindow());
                                    }
                                }
                            }

                        } else {
                            Log.i(LOG, "**************************:自己没有赞过!");
                            //开始执行点赞流程
                            if (plBtns.size() != 0) {
                                Rect outBounds = new Rect();
                                plbtn.getBoundsInScreen(outBounds);
                                int top = outBounds.top;

                                //根据top判断如果已经点开了就不重复点开了
                                if (topList.contains(top)) {
                                    if (nLikeCount >= nLikeTotalCount) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                                //com.tencent.mm:id/cj5 赞
                                if (plbtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                                    List<AccessibilityNodeInfo> zanlBtns = rootNodeInfo.
                                            findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cj3");
                                    if (zanlBtns.size() != 0) {
                                        if (!topList.contains(top) && zanlBtns.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                                            topList.add(top);
                                            Log.i(LOG, "topList:" + topList.toString());

                                            //判断是否需要翻页，如果当前所有页面的父节点都没点过了，就需要翻页
                                            boolean ifxuyaofanye = false;
                                            Log.i(LOG, "Ｏ(≧口≦)Ｏ: i=" + i + "  fuBtns.size():" + fuBtns.size());
                                            if (i == fuBtns.size() - 1) {
                                                ifxuyaofanye = true;
                                                nLikeCount++;
                                                Log.i(LOG, "like count:" + nLikeCount);
                                            }
                                            if (ifxuyaofanye) {
                                                //滑动前检测一下是否还有没有点过的点
                                                if (jianceIfLou()) {
                                                    Log.i(LOG, "还有遗漏的点！！！！再检查一遍!!!!!!!!!!");
                                                    return like(getRootInActiveWindow());
                                                } else {
                                                    if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                                                        return like(getRootInActiveWindow());
                                                    }
                                                }


                                            }

                                        }
                                    }
                                }
                            }
                        }

                    } else {
                        Log.i(LOG, "**************************:点赞区域为空!plBtns.size() :" + plBtns.size());

                        //开始执行点赞流程
                        if (plBtns.size() != 0) {

                            Rect outBounds = new Rect();
                            plbtn.getBoundsInScreen(outBounds);
                            int top = outBounds.top;

                            //根据top判断如果已经点开了就不重复点开了
                            if (topList.contains(top)) {
                                if (nLikeCount >= nLikeTotalCount) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                            //com.tencent.mm:id/cj5 赞
                            if (plbtn.performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                                List<AccessibilityNodeInfo> zanlBtns = rootNodeInfo.
                                        findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cj3");
                                if (zanlBtns.size() != 0) {
                                    if (!topList.contains(top) && zanlBtns.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK)) {
                                        topList.add(top);
                                        Log.i(LOG, "topList:" + topList.toString());

                                        //判断是否需要翻页，如果当前所有页面的父节点都没点过了，就需要翻页
                                        boolean ifxuyaofanye = false;
                                        Log.i(LOG, "Ｏ(≧口≦)Ｏ: i=" + i + "  fuBtns.size():" + fuBtns.size());
                                        if (i == fuBtns.size() - 1) {
                                            ifxuyaofanye = true;
                                            nLikeCount++;
                                            Log.i(LOG, "like count:" + nLikeCount);
                                        }
                                        if (ifxuyaofanye) {
                                            //滑动前检测一下是否还有没有点过的点
                                            if (jianceIfLou()) {
                                                Log.i(LOG, "还有遗漏的点！！！！再检查一遍!!!!!!!!!!");
                                                return like(getRootInActiveWindow());
                                            } else {
                                                if (likeList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD)) {
                                                    return like(getRootInActiveWindow());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
        return false;
    }

    private boolean jianceIfLou() {
        boolean result = false;
        List<AccessibilityNodeInfo> fuBtns =
                getRootInActiveWindow().findAccessibilityNodeInfosByViewId("com.tencent.mm:id/co0");
        Log.i(LOG, "jianceIfLou, 检查的父节点数量:" + fuBtns.size());
        if (fuBtns.size() != 0) {
            for (AccessibilityNodeInfo fuBtn : fuBtns) {
                //点赞区域
                List<AccessibilityNodeInfo> zanBtns = fuBtn.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/cnn");
                Log.i(LOG, "jianceIfLou, 检查的父节点的点赞区域数量:" + zanBtns.size());
                if (zanBtns.size() != 0) {
                    AccessibilityNodeInfo zanbtn = zanBtns.get(0);
                    Log.i(LOG, " jianceIfLou, zanbtn.getText().toString():" + zanbtn.getText().toString());
                    if (zanbtn != null && zanbtn.getText() != null &&
                            zanbtn.getText().toString().contains(mUserName)) {
                        result = false;
                    } else {
                        result = true;
                    }
                } else {
                    result = true;
                }
            }
        }

        return result;
    }

    private static boolean oneImageSelected = false;
    private static int imageCount = 0;
    private static int imageIndex = 0;
    private boolean isVideo = false;
    private boolean isFirstSelectImage = true;

    private void publishSns(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();



        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            findLoop(nodeInfo, WECHAT_FIND_LABEL);
            findLoop(nodeInfo, WECHAT_MOMENT_LABEL);
        } else if (className.equals("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI")) {////com.tencent.mm/.plugin.sns.ui.SnsTimeLineUI
            performClick("f_");
            imageCount = 0;
            imageIndex = 0;
            oneImageSelected = false;
            isFirstSelectImage = true;
            isVideo = false;
        } else if (className.equals("com.tencent.mm.ui.base.k")) {//从相册中选择
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SELECTFROM_ALBUM_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到从相册选择");
            }
        } else if (className.equals("com.tencent.mm.plugin.gallery.ui.AlbumPreviewUI")) {//图片选择

            if(isFirstSelectImage && !Utils.isIsPublishVideo() ) {//发送朋友圈小视频，直接选择视频，不用选择image目录
                performClick("c_z");
                list = nodeInfo.findAccessibilityNodeInfosByText("image");
                if (list.size() > 0) {
                    list.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } else {
                    Log.i(LOG, "没找到image选项");
                }
                isFirstSelectImage = false;
            }
            SystemControl.delay(2000);


            if (!oneImageSelected) {
                list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/a1a");
                if (list.size() > 0 && imageIndex < list.size()) {
                    list.get(imageIndex).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(imageIndex).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    imageIndex++;
                    isVideo = false;
                    return;
                }

            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_FINISH_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到完成");
            }

        } else if (className.equals("com.tencent.mm.plugin.gallery.ui.ImagePreviewUI")) {
            if (isVideo) {
                PerformClickUtils.performBack(this);
            }
            if(nodeInfo == null){
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/b6l");
            if (list.size() > 0) {
                isVideo = true;
            } else {
                imageCount++;
                performClick("b6g");
            }
            if (isVideo && Utils.isIsPublishVideo()) {//选择视频，发送朋友圈小视频
                findLoop(nodeInfo, WECHAT_FINISH_LABEL);
            } else {
                PerformClickUtils.performBack(this);
                if (imageCount == Preferences.getMomentMaterialCount(this)) {
                    oneImageSelected = true;
                    Log.i(LOG, "选择完毕， 共" + Preferences.getMomentMaterialCount(this) + "张图片");

                }
            }
            //TODO(caoxianjin) for test
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } else if (className.equals("com.tencent.mm.plugin.sns.ui.SnsUploadUI")) { //文字朋友圈
            setText("cn4", Preferences.getMomentMaterialContent(this));
            SystemControl.delay(1000);
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到发送");
            }
            Utils.setAutoPublishSns(false);
            if(Utils.isIsPublishVideo()){
                SystemControl.delay(5000);
                Utils.setIsPublishVideo(false);
            }else{
                SystemControl.delay(3000);
            }
            quitWechat();
        }

    }


    private boolean bIsfromSayHiEditUI = false;
    private int searchNearbyFriendTotalCount = 0;
    private  int searchNearbyFriendCount = 0;
    private String nearbyFriendName = null;
    private int nearbyFriendUIcount = 0;//第一次进入附件的人找的是本地的附近的人，第二次才是虚拟地址的附近的人
    List<AccessibilityNodeInfo> nearbyFriendsList = null;
    private  boolean isSelectAroundType = false;

    private void searchNearbyFriends(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();

        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            //Reset
            if(nearbyFriendUIcount >= 2){
                nearbyFriendUIcount = 0;
            }

            if(nearbyFriendUIcount == 0) {
                findLoop(nodeInfo, WECHAT_FIND_LABEL);
            }
            findLoop(nodeInfo, WECHAT_NEARBY_LABEL);

            searchNearbyFriendTotalCount = Preferences.getSearchnearbyFriendsCount(this);
            searchNearbyFriendCount = 0;
            isSelectAroundType = false;

        }else if(className.equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendsIntroUI")){
            findLoop(nodeInfo, "开始查看");
        }else if(className.equals("com.tencent.mm.ui.base.h")){
            findLoop(nodeInfo, "确定");
        }
        else if(className.equals("com.tencent.mm.plugin.nearby.ui.NearbyFriendsUI")){
            if(!isSelectAroundType) {
                list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/fx");
                if (list.size() > 0) {
                    list.get(0).getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
                int type = Preferences.getAroundtype(this);
                list = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bpv");
                if (list.size() > 0) {
                    switch(type){
                        case 1:
                            list.get(0).getChild(2).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        case 2:
                            list.get(0).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        case 3:
                            list.get(0).getChild(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                            break;
                        default:
                            break;
                    }
                    return;
                }

                SystemControl.delay(3000);
                isSelectAroundType = true;
            }

            nearbyFriendsList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/bv3");
            bIsfromSayHiEditUI = false;
            if(searchNearbyFriendTotalCount == searchNearbyFriendCount) {
                Utils.setIsSearchNearbyFriends(false);
                quitWechat();
            }
            nearbyFriendUIcount++;
            if(nearbyFriendUIcount >= 2) {
                findNearbyUser();
            }else{
                SystemControl.delay(3000);
                PerformClickUtils.performBack(this);
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {
            if(bIsfromSayHiEditUI){
                if(searchNearbyFriendTotalCount == searchNearbyFriendCount) {
                    Utils.setIsSearchNearbyFriends(false);
                    quitWechat();
                }else {
                    PerformClickUtils.performBack(this);
                    return;
                }
            }
            SystemControl.delay(1000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SAYHI_LABEL);
            if (list.size() > 0) {
                nearbyFriendName = getText("la");
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到详细资料的添加通讯录功能");
                if(searchNearbyFriendTotalCount == searchNearbyFriendCount) {
                    Utils.setIsSearchNearbyFriends(false);
                    quitWechat();
                }else {
                    PerformClickUtils.performBack(this);
                }
            }
        }else if(className.equals("com.tencent.mm.ui.contact.SayHiEditUI")){
            if(bIsfromSayHiEditUI){
                return;
            }
            SystemControl.delay(500);

            String greet = Preferences.getGreet(this);
            if(TextUtils.isEmpty(greet)){
                greet = "你还记得我吗";
            }
            if(Preferences.getNeedHi(this) == 1){
                greet = "Hi, "+ nearbyFriendName + ", " +  greet;
            }
            setText("c9p", greet);
            SystemControl.delay(2000);
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                Utils.setIsSearchNearbyFriends(false);
                return;
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                searchNearbyFriendCount++;
                Log.i(LOG, "searchNearbyFriendCount:"+searchNearbyFriendCount);
            } else {
                Log.i(LOG, "没找到验证申请的发送功能");
            }
            bIsfromSayHiEditUI = true;
//            try {
//                Thread.sleep(3000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            if(searchNearbyFriendTotalCount == searchNearbyFriendCount) {
//                Utils.setIsSearchNearbyFriends(false);
//                quitWechat();
//            }else {
//                bIsfromSayHiEditUI = true;
//            }
        }
    }

    private List<String> lastNearbyUsernameList = new ArrayList<String>();
    private List<String> addedNearbyUsernameList = new ArrayList<String>();
    private boolean bNearbyUserLastPage = false;


    private void findNearbyUser() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> nameList = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/ali");
        Log.i(LOG,  "findNearbyUser, nameList size:" + nameList.size());
        if (nameList.size() > 0) {
            for (int n = 0; n < nameList.size(); n++) {
                Log.i(LOG, "namelist text:" + nameList.get(n).getText().toString());
            }
            AccessibilityNodeInfo lastAddBtn =  nameList.get(nameList.size() - 1);
            Rect lastAddBtnOutBound = new Rect();
            //删掉超出屏幕的添加按钮
            lastAddBtn.getBoundsInScreen(lastAddBtnOutBound);
            Log.i(LOG, ", lastAddBtnOutBound top:" + lastAddBtnOutBound.top + ", lastAddBtnOutBound bottom:" + lastAddBtnOutBound.bottom);
            if (lastAddBtnOutBound.top > DeviceUtils.getScreenHeight(this)) {
                nameList.remove(nameList.size() - 1);
            }
            String userName = nameList.get(nameList.size() - 1).getText().toString();
            Log.i(LOG, "最后一个附近的人：" + userName);
            if (lastNearbyUsernameList.contains(userName) == false) {
                lastNearbyUsernameList.add(userName);
                bNearbyUserLastPage = false;
            } else {
                bNearbyUserLastPage = true;
            }
            int index = -1;
            //找到附近人名单，并做记录，防止重复添加；再排除已添加的用户
            for (int i = 0; i < nameList.size(); i++) {
                userName = nameList.get(i).getText().toString();
                Log.i(LOG, "当前第" + i + "个附近的人的用户名：" + userName);
                if (addedNearbyUsernameList.contains(userName) == false) {
                    addedNearbyUsernameList.add(userName);
                    index = i;
                    break;
                }
            }
            if (index == -1) {//没有找到用户
                if (bNearbyUserLastPage) {//最后一页，而且没有找到对应的用户，退出微信
                    Utils.setIsSearchNearbyFriends(false);
                    SystemControl.delay(2000);
                    Log.i(LOG, "found no nearby friend, quit wechat");
                    quitWechat();
                } else {//不是最后一页，翻页
                    Log.i(LOG, "scroll to next page");
                    nearbyFriendsList.get(0).performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
                    SystemControl.delay(1000);
                    findNearbyUser();
                }
            } else {
                Log.i(LOG, "从第" + index + "个附近的人开始打招呼");
                bIsfromSayHiEditUI = false;
                nameList.get(index).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        } else {
            Log.i(LOG, "没找到附近的人ali的id");
            Utils.setIsSearchNearbyFriends(false);
        }
    }


    private void sendAllFriendsMessage(String className) {
        AccessibilityNodeInfo nodeInfo = null;
        List<AccessibilityNodeInfo> list = null;
        nodeInfo = getRootInActiveWindow();

        if (className.equals("com.tencent.mm.ui.LauncherUI")) {
            if (nodeInfo == null) {
                Log.i(LOG, "rootWindow为空");
                return;
            }

            findLoop(nodeInfo, WECHAT_MY_LABEL);

            findLoop(nodeInfo, WECHAT_SETTINGS_LABEL);


        } else if (className.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsUI")) {//从设置中选择
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_COMMON_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到通用");
            }
        } else if (className.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsAboutSystemUI")) {//功能选择
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_FUNCTION_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到功能");
            }

        } else if (className.equals("com.tencent.mm.plugin.setting.ui.setting.SettingsPluginsUI")) {//群发助手
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_ASSISTANT_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到群发助手");
            }
        } else if (className.equals("com.tencent.mm.plugin.profile.ui.ContactInfoUI")) {//开始群发
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_BEGINMASSSEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到开始群发");
            }
        }
        //com.tencent.mm/.plugin.masssend.ui.MassSendHistoryUI
        else if (className.equals("com.tencent.mm.plugin.masssend.ui.MassSendHistoryUI")) {
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_NEWMASSSEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到新建群发");
            }
        }
        //com.tencent.mm/.plugin.masssend.ui.MassSendSelectContactUI
        else if (className.equals("com.tencent.mm.plugin.masssend.ui.MassSendSelectContactUI")) {
//            performCheck("n2");
            performClick("bov");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_NEXTSTEP_LABEL);
            if (list.size() > 0) {
                if(list.get(0).isEnabled()) {
                    list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }else{
                    Utils.setIsSendAllfriendsMessage(false);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    quitWechat();
                }
            } else {
                Log.i(LOG, "没找到下一步");
            }
        }
        else if (className.equals("com.tencent.mm.plugin.masssend.ui.MassSendMsgUI")) {
            setText("a2v", Preferences.getMomentMaterialContent(this));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            list = nodeInfo.findAccessibilityNodeInfosByText(WECHAT_SEND_LABEL);
            if (list.size() > 0) {
                list.get(list.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                list.get(list.size() - 1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
            } else {
                Log.i(LOG, "没找到发送");
            }
            Utils.setIsSendAllfriendsMessage(false);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            quitWechat();
        }

    }

    private void performClick(String resourceId) {

        Log.i(LOG, "点击执行");

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/" + resourceId);


        if (targetNode != null) {
            targetNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            targetNode.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private boolean performCheck(String resourceId, int index) {
        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        List<AccessibilityNodeInfo> targetNode = nodeInfo.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/" + resourceId);
        Log.i(LOG, "index:" + index);
        if(targetNode.size() > 0) {
            if (targetNode.get(0) != null) {
                try {
                    targetNode.get(0).getChild(index).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return  true;
        }
        return  false;
    }


    private void setText(String resourceId, String text) {

        Log.i(LOG, "输入文字:" + text);

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/" + resourceId);


        if (SystemControl.getAndroidSDKVersion() > 21) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            if(targetNode != null) {
                targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }
        } else if (SystemControl.getAndroidSDKVersion() > 18) {
            //this means you have a value for that ET
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("WechatRobot", text);
            clipboardManager.setPrimaryClip(clipData);

            if (targetNode != null) {
                try {
                    targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void setTextLoop(String resourceId, String text) {

        Log.i(LOG, "输入文字:" + text);

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/" + resourceId);


        if (SystemControl.getAndroidSDKVersion() > 21) {
            Bundle arguments = new Bundle();
            arguments.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, text);
            if(targetNode != null) {
                targetNode.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, arguments);
            }else{
                setText(resourceId, text);
            }
        } else if (SystemControl.getAndroidSDKVersion() > 18) {
            //this means you have a value for that ET
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("WechatRobot", text);
            clipboardManager.setPrimaryClip(clipData);

            if (targetNode != null) {
                try {
                    targetNode.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                    targetNode.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }



    private String getText(String resourceId) {

        Log.i(LOG, "获取文字");

        AccessibilityNodeInfo nodeInfo = this.getRootInActiveWindow();
        AccessibilityNodeInfo targetNode = null;
        targetNode = findNodeInfosById(nodeInfo, "com.tencent.mm:id/" + resourceId);


        if (targetNode != null) {
            try {
                String ret = targetNode.getText().toString();
                return ret;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    private void findSearchLoop(){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WECHAT_PACKAGE_NAME + ":id/f_");
        if(list.size() > 0){
            int size = list.get(0).getParent().getChildCount();
            for(int i=0;i<size;i++){
                Log.i(Constants.LOG_TAG, "content desc:" + list.get(0).getParent().getChild(i).getContentDescription());
                if(list.get(0).getParent().getChild(i).getContentDescription().equals("搜索") ){
                    list.get(0).getParent().getChild(i).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
        }else {
            findSearchLoop();
        }
    }


    private void findLoop(AccessibilityNodeInfo nodeInfo, String text){
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list.size() > 0){
            list.get(list.size()-1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size()-1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
            nodeInfo = getRootInActiveWindow();
            findLoop(nodeInfo, text);
        }
    }

    private void clickSelfLoop(AccessibilityNodeInfo nodeInfo, String text){
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if(list.size() > 0){
            list.get(list.size()-1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
            nodeInfo = getRootInActiveWindow();
            clickSelfLoop(nodeInfo, text);
        }
    }

    private void findLoopbyID(String resId){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WECHAT_PACKAGE_NAME + ":id/" + resId);
        if(list.size() > 0){
            list.get(list.size()-1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            list.get(list.size()-1).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
            findLoopbyID(resId);
        }
    }

    private void clickSelfbyID(String resId){
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(Constants.WECHAT_PACKAGE_NAME + ":id/" + resId);
        if(list.size() > 0){
            list.get(list.size()-1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }else {
            clickSelfbyID(resId);
        }
    }


    //调用兵力（通过id查找）
    public static AccessibilityNodeInfo findNodeInfosById(AccessibilityNodeInfo nodeInfo, String resId) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByViewId(resId);
            if (list != null && !list.isEmpty()) {
                return list.get(0);
            }
        }
        return null;
    }

    //调用船只（通过文本查找）
    public static AccessibilityNodeInfo findNodeInfosByText(AccessibilityNodeInfo nodeInfo, String text) {
        List<AccessibilityNodeInfo> list = nodeInfo.findAccessibilityNodeInfosByText(text);
        if (list == null || list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }


    /**
     * 30      * 执行Shell命令
     * 31      *
     * 32      * @param commands
     * 33      *            要执行的命令数组
     * 34
     */
    public void execShell(String[] commands) {
        // 获取Runtime对象
        Runtime runtime = Runtime.getRuntime();
        DataOutputStream os = null;
        try {
            // 获取root权限，这里大量申请root权限会导致应用卡死，可以把Runtime和Process放在Application中初始化
            java.lang.Process process = runtime.exec("su");
            os = new DataOutputStream(process.getOutputStream());
            for (String command : commands) {
                if (command == null) {
                    continue;
                }

                // donnot use os.writeBytes(commmand), avoid chinese charset
                // error
                os.write(command.getBytes());
                os.writeBytes("\n");
                os.flush();
            }
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟触摸事件
     */
    private void pressTap(int x, int y) {
        String[] touch = {
                "sendevent /dev/input/event0 3 0 " + x,
                "sendevent /dev/input/event0 3 1 " + y,
                "sendevent /dev/input/event0 1 330 1",//touch
                "sendevent /dev/input/event0 0 0 0", //it must have
                "sendevent /dev/input/event0 1 330 0", //untouch
                "sendevent /dev/input/event0 0 0 0", //it must have
        };
        execShell(touch);
    }

    private void Swipe() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input swipe 700 100 700 100 1000");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void inputText(String text) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input text '" + text + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 模拟按键
     */
    private void pressButton(int keyValue) {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + keyValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 寻找窗体中的“发送”按钮，并且点击。
     */
    @SuppressLint("NewApi")
    private void send() {
        AccessibilityNodeInfo nodeInfo = getRootInActiveWindow();
        if (nodeInfo != null) {
            List<AccessibilityNodeInfo> list = nodeInfo
                    .findAccessibilityNodeInfosByText("发送");
            if (list != null && list.size() > 0) {
                for (AccessibilityNodeInfo n : list) {
                    if (n.getClassName().equals("android.widget.Button") && n.isEnabled()) {
                        n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    }
                }

            } else {
                List<AccessibilityNodeInfo> liste = nodeInfo
                        .findAccessibilityNodeInfosByText("Send");
                if (liste != null && liste.size() > 0) {
                    for (AccessibilityNodeInfo n : liste) {
                        if (n.getClassName().equals("android.widget.Button") && n.isEnabled()) {
                            n.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        }
                    }
                }
            }
            pressBackButton();
        }
    }

    /**
     * 模拟back按键
     */
    private void pressBackButton() {
        Runtime runtime = Runtime.getRuntime();
        try {
            runtime.exec("input keyevent " + KeyEvent.KEYCODE_BACK);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 拉起微信界面
     *
     * @param event
     */
    private void sendNotifacationReply(AccessibilityEvent event) {
        if (event.getParcelableData() != null
                && event.getParcelableData() instanceof Notification) {
            Notification notification = (Notification) event
                    .getParcelableData();
            String content = notification.tickerText.toString();
            String[] cc = content.split(":");
            name = cc[0].trim();
            scontent = cc[1].trim();

            android.util.Log.i(Constants.LOG_TAG, "sender name =" + name);
            android.util.Log.i(Constants.LOG_TAG, "sender content =" + scontent);

            //我通过了你的朋友验证请求，现在我们可以开始聊天了
            if(scontent.contains("我通过了你的朋友验证请求")  && scontent.contains("可以开始聊天")) {
                PendingIntent pendingIntent = notification.contentIntent;
                try {
                    pendingIntent.send();
                } catch (PendingIntent.CanceledException e) {
                    e.printStackTrace();
                }
                hasAction = true;
            }

        }
    }

    private boolean findEditText(AccessibilityNodeInfo rootNode, String content) {
        int count = rootNode.getChildCount();

        android.util.Log.i("maptrix", "root class=" + rootNode.getClassName() + "," + rootNode.getText() + "," + count);
        for (int i = 0; i < count; i++) {
            AccessibilityNodeInfo nodeInfo = rootNode.getChild(i);
            if (nodeInfo == null) {
                android.util.Log.i("maptrix", "nodeinfo = null");
                continue;
            }

            android.util.Log.i("maptrix", "class=" + nodeInfo.getClassName());
            android.util.Log.e("maptrix", "ds=" + nodeInfo.getContentDescription());
            if (nodeInfo.getContentDescription() != null) {
                int nindex = nodeInfo.getContentDescription().toString().indexOf(name);
                int cindex = nodeInfo.getContentDescription().toString().indexOf(scontent);
                android.util.Log.e("maptrix", "nindex=" + nindex + " cindex=" + cindex);
                if (nindex != -1) {
                    itemNodeinfo = nodeInfo;
                    android.util.Log.i("maptrix", "find node info");
                }
            }
            if ("android.widget.EditText".equals(nodeInfo.getClassName())) {
                android.util.Log.i("maptrix", "==================");
                Bundle arguments = new Bundle();
                arguments.putInt(AccessibilityNodeInfo.ACTION_ARGUMENT_MOVEMENT_GRANULARITY_INT,
                        AccessibilityNodeInfo.MOVEMENT_GRANULARITY_WORD);
                arguments.putBoolean(AccessibilityNodeInfo.ACTION_ARGUMENT_EXTEND_SELECTION_BOOLEAN,
                        true);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PREVIOUS_AT_MOVEMENT_GRANULARITY,
                        arguments);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_FOCUS);
                ClipData clip = ClipData.newPlainText("label", content);
                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                clipboardManager.setPrimaryClip(clip);
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_PASTE);
                return true;
            }

            if (findEditText(nodeInfo, content)) {
                return true;
            }
        }

        return false;
    }


    @SuppressLint("NewApi")
    private boolean fill() {
        AccessibilityNodeInfo rootNode = getRootInActiveWindow();
        if (rootNode != null) {
            return findEditText(rootNode, "正在忙,稍后回复你");
        }
        return false;
    }

    @Override
    public void onInterrupt() {

    }


    /**
     * 将当前应用运行到前台
     */
    private void bring2Front() {
        ActivityManager activtyManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activtyManager.getRunningTasks(3);
        for (ActivityManager.RunningTaskInfo runningTaskInfo : runningTaskInfos) {
            if (this.getPackageName().equals(runningTaskInfo.topActivity.getPackageName())) {
                activtyManager.moveTaskToFront(runningTaskInfo.id, ActivityManager.MOVE_TASK_WITH_HOME);
                return;
            }
        }
    }


    /**
     * 系统是否在锁屏状态
     *
     * @return
     */
    private boolean isScreenLocked() {
        KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        return keyguardManager.inKeyguardRestrictedInputMode();
    }

    private void wakeAndUnlock() {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //获取PowerManager.WakeLock对象，后面的参数|表示同时传入两个值，最后的是调试用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "bright");

        //点亮屏幕
        wl.acquire(1000);

        //得到键盘锁管理器对象
        KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        kl = km.newKeyguardLock("unLock");

        //解锁
        kl.disableKeyguard();

    }

    private void release() {

        if (locked && kl != null) {
            android.util.Log.d("maptrix", "release the lock");
            //得到键盘锁管理器对象
            kl.reenableKeyguard();
            locked = false;
        }
    }

    private void quitWechat(){
//        Intent intent = new Intent(Intent.ACTION_MAIN);
//        ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");
//
//        intent.addCategory(Intent.CATEGORY_LAUNCHER);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        intent.setComponent(cmp);
//        startActivity(intent);

        Utils.back2Home(this);
        release();
//        Utils.forceStopAPK(Constants.WECHAT_PACKAGE_NAME);
        SystemControl.delay(2000);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(MainActivity.KEY_EXTRAS, "quitwechat");
        startActivity(intent);
    }

    /**
     * 64      * 把中文转成Unicode码
     * 65      * @param str
     * 66      * @return
     * 67
     */
    public String chineseToUnicode(String str) {
        String result = "";
        for (int i = 0; i < str.length(); i++) {
            int chr1 = (char) str.charAt(i);
            if (chr1 >= 19968 && chr1 <= 171941) {//汉字范围 \u4e00-\u9fa5 (中文)
                result += "\\u" + Integer.toHexString(chr1);
            } else {
                result += str.charAt(i);
            }
        }
        return result;
    }

    /**
     * 判断是否为中文字符
     *
     * @param c
     * @return
     */
    public boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }


    //模拟点击
    private void setSimulateClick(View view, float x, float y) {

        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        downTime += 1000;
        final MotionEvent upEvent = MotionEvent.obtain(downTime, downTime,
                MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }
}
