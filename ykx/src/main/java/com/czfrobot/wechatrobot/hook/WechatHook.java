package com.czfrobot.wechatrobot.hook;

import de.robv.android.xposed.*;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import de.robv.android.xposed.XposedBridge;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;


import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.utils.SettingsHelper;
import com.czfrobot.wechatrobot.utils.SystemControl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

import static android.provider.Settings.Secure.LOCATION_MODE_SENSORS_ONLY;

/*
  private void 叄()
  {
    this.ʼ = (this.倪 + ".ui.chatting.cv");
    this.अनु = (this.倪 + ".model.bc");
    this.तल = (this.倪 + ".plugin.sns.ui.SnsTimeLineUI");
    this.圙 = (this.倪 + ".sdk.platformtools.y");
    this.圐 = (this.倪 + ".plugin.chatroom.ui.ChatroomInfoUI");
    this.淼 = (this.倪 + ".ui.LauncherUI");
    this.焱 = (this.倪 + ".plugin.profile.ui.ContactInfoUI");
    this.鸭 = (this.倪 + ".plugin.sns.ui.SightUploadUI");
    this.厷 = (this.倪 + ".plugin.sns.ui.ao");
    this.厸 = "lT";
    this.厹 = "hcp";
    this.厺 = "mContext";
    this.厼 = (this.倪 + ".plugin.sns.ui.ak");
    this.厽 = "getItem";
    this.厾叀 = "aoU";
    this.叁参 = (this.倪 + ".plugin.radar.ui.RadarSearchUI");
    this.叅 = (this.倪 + ".plugin.radar.ui.RadarMemberView");
    this.叄 = (this.倪 + ".plugin.radar.ui.RadarMemberView$a");
    this.叇 = "setListener";
    this.叧 = (this.倪 + ".plugin.radar.a.c$d");
    this.叨叭 = (this.倪 + ".plugin.radar.ui.RadarViewController");
    this.叱叴 = "G";
    this.叵叺 = "b";
    this.叻 = (this.倪 + ".ui.transmit.SelectConversationUI");
    this.叼 = (this.倪 + ".ui.transmit.MsgRetransmitUI");
    this.叽叾卟 = (this.倪 + ".model.ah");
    this.叿吀吁 = "sP";
    this.吂吅吆 = "qH";
    this.吇 = "dB";
    this.吋 = "field_imgPath";
    this.吒吔 = (this.倪 + ".ab.n");
    this.吖吘 = "zA";
    this.吙吚 = "r";
    this.圂 = "qE";
    this.吜吡吢 = (this.倪 + ".plugin.nearby.ui.NearbyFriendsUI");
    this.吣吤 = "dDd";
    this.吥吧 = "com.tencent.map.geolocation.TencentLocation";
    this.吩吪 = (this.倪 + ".modelgeo.f");
    this.囘囙 = (this.倪 + ".modelgeo.e");
    this.囜 = "jhU";
    this.囝 = "ebx";
    this.回 = "iON";
    this.囟 = (this.倪 + ".model.h");
    this.囡 = "rt";
    this.団 = (this.倪 + ".pluginsdk.model.l");
    this.囤 = (this.倪 + ".model.ah");
    this.囥 = "sQ";
    this.囦 = "d";
    this.囨 = "qF";
    this.囩 = "Ev";
    this.囱 = "ayW";
    this.囻 = "jFi";
    this.囫 = "jFl";
    this.囬 = "aft";
    this.囮 = "eaV";
    this.囯 = "ebm";
    this.囸 = "jat";
    this.囹 = "jau";
    this.囼 = "jsw";
    this.图 = "eOm";
    this.囿 = "nP";
    this.圀 = (this.倪 + ".g.a");
    this.圗 = "e";
    this.团 = "K";
    this.圁 = (this.倪 + ".r.j");
    this.圃 = (this.倪 + ".ui.contact.AddressUI.a");
    this.圄 = "aZf";
    this.圅 = "lci";
    this.圆 = "lbP";
    this.圈 = (this.倪 + ".plugin.sns.ui.SnsUserUI");
    this.圉 = "aAB";
    this.圊 = 2131168568;
    this.国 = "b";
    this.圎 = (this.倪 + ".ag.b.c");
    this.圏 = "b";
    this.圑 = (this.倪 + ".model.f");
    this.园 = "dW";
    this.圔 = (this.倪 + ".sdk.platformtools.ba");
    this.圕 = "kO";
    this.के = "fsp";
    this.लिए = "a";
    this.अनुवाद = (this.倪 + ".ui.j.b");
  }

  private void 倪()
  {
    this.ˑˑ = (this.倪 + ".pluginsdk.ui.applet.e");
    this.ᵔᵔ = "mmw";
    this.יי = 44;
    this.ᵎᵎ = (this.倪 + ".plugin.chatroom.ui.SeeRoomMemberUI");
    this.ⁱⁱ = "eYW";
    this.ﹳﹳ = "eZd";
    this.ٴٴ = "biY";
    this.ﹶﹶ = "eBw";
    this.ᵢᵢ = 2131756508;
    this.ʼʼ = (this.倪 + ".ui.bindmobile.MobileFriendUI");
    this.ˊˊ = 2131757115;
    this.ʿʿ = "oKU";
    this.ــ = (this.倪 + ".ui.bindmobile.b");
    this.ˉˉ = 2131758446;
    this.ˆˆ = (this.倪 + ".ui.bindmobile.c");
    this.ˈˈ = 2131757234;
    this.ʾʾ = (this.倪 + ".plugin.profile.ui.SayHiWithSnsPermissionUI");
    this.ˋˋ = (this.倪 + ".ui.MMActivity");
    this.ˏˏ = (this.倪 + ".model.a.b");
    this.ˎˎ = "gd";
    this.ᴵ = true;
    this.ᵎ = (this.倪 + ".plugin.luckymoney.ui.LuckyMoneyMoneyInputView");
    this.ᵔ = "azO";
    this.ᵢ = (this.倪 + ".plugin.luckymoney.ui.LuckyMoneyNumInputView");
    this.ⁱ = "azV";
    this.ﹳ = (this.倪 + ".plugin.luckymoney.ui.LuckyMoneyPrepareUI");
    this.ﹶ = "b";
    this.ﾞ = (this.倪 + ".plugin.wallet.pay.ui.WalletPayUI");
    this.ﾞﾞ = (this.倪 + ".wallet_core.ui.e");
    this.ᐧᐧ = "d";
    this.ᴵᴵ = "Qy";
    this.ʻʻ = (this.倪 + ".plugin.wallet_core.ui.l");
    this.ʽʽ = "Ed";
    this.बटन = true;
    this.ʻ = true;
    this.ʼ = (this.倪 + ".ui.chatting.cv");
    this.ʽ = (this.倪 + ".ui.j");
    this.ʾ = (this.倪 + ".ui.chatting.cp");
    this.ʿ = "getView";
    this.ˆ = "getItem";
    this.ˈ = "bzW";
    this.ˉ = "field_type";
    this.ˎ = "field_isSend";
    this.ˏ = "field_content";
    this.ˑ = "field_imgPath";
    this.י = (this.倪 + ".modelvoice.q");
    this.ـ = "I";
    this.ٴ = "g";
    this.ᐧ = "oXA";
    this.पर = "kqB";
    this.क्लिक = 2131759615;
    this.कर = (this.倪 + ".plugin.sns.e.ak.a");
    this.अपनी = "a";
    this.सामग्री = (this.倪 + ".plugin.sns.storage.k");
    this.दर्ज = (this.倪 + ".protocal.c.awi");
    this.करें = "field_likeFlag";
    this.पतु = "pjG";
    this.रंत = "gYV";
    this.圙 = (this.倪 + ".sdk.platformtools.aa");
    this.厷 = (this.倪 + ".plugin.sns.ui.au");
    this.厸 = "rw";
    this.厹 = "ksl";
    this.厼 = (this.倪 + ".plugin.sns.ui.aq");
    this.厾叀 = "biQ";
    this.叆 = 2131758870;
    this.叇 = null;
    this.亝 = "iBN";
    this.叧 = (this.倪 + ".plugin.radar.a.e$d");
    this.叨叭 = (this.倪 + ".plugin.radar.ui.RadarViewController");
    this.叱叴 = "S";
    this.叵叺 = "b";
    this.叻 = (this.倪 + ".ui.transmit.SelectConversationUI");
    this.叼 = (this.倪 + ".ui.transmit.MsgRetransmitUI");
    this.囨 = "wF";
    this.囩 = "MF";
    this.叽叾卟 = (this.倪 + ".model.c");
    this.叿吀吁 = null;
    this.吂吅吆 = "wH";
    this.吇 = "em";
    this.吋 = "field_imgPath";
    this.吒吔 = (this.倪 + ".ad.n");
    this.吖吘 = "GN";
    this.吙吚 = "x";
    this.圂 = "wE";
    this.圎 = (this.倪 + ".aj.c");
    this.圏 = "b";
    this.吜吡吢 = (this.倪 + ".plugin.nearby.ui.NearbyFriendsUI");
    this.吣吤 = "fPK";
    this.吥吧 = "com.tencent.map.geolocation.TencentLocation";
    this.吩吪 = (this.倪 + ".modelgeo.f");
    this.囘囙 = (this.倪 + ".modelgeo.e");
    this.囜 = "mYW";
    this.囝 = "gtn";
    this.回 = "emQ";
    this.囟 = (this.倪 + ".model.k");
    this.囡 = "xG";
    this.団 = (this.倪 + ".pluginsdk.model.m");
    this.囤 = (this.倪 + ".model.ak");
    this.囥 = "vy";
    this.囦 = "a";
    this.囧 = true;
    this.囱 = "aWN";
    this.囻 = "nzD";
    this.囫 = "nzG";
    this.囬 = "aXQ";
    this.囮 = "gsA";
    this.囯 = "gsZ";
    this.囸 = "mRb";
    this.囹 = "mRc";
    this.困 = "nln";
    this.囲 = "nlp";
    this.囼 = "nkY";
    this.図 = (this.倪 + ".modelsfs.a");
    this.囵 = "dad";
    this.囶 = "free";
    this.囷 = "j";
    this.图 = "hqB";
    this.囿 = "st";
    this.圀 = (this.倪 + ".h.a");
    this.圗 = "d";
    this.圁 = (this.倪 + ".u.k");
    this.团 = "aG";
    this.अनु = (this.倪 + ".model.bp");
    this.圃 = (this.倪 + ".ui.contact.AddressUI.a");
    this.圄 = "bCn";
    this.圅 = "piP";
    this.圆 = "piw";
    this.圉 = null;
    this.圊 = 2131759615;
    this.圌 = true;
    this.国 = "a";
    this.圑 = (this.倪 + ".model.i");
    this.园 = "em";
    this.圚 = (this.倪 + ".storage.av");
    this.圛 = (this.倪 + ".storage.aw");
    this.圜 = "R";
    this.圝 = "field_talker";
    this.圞 = "field_isSend";
    this.凹 = "field_type";
    this.凸 = (this.倪 + ".ui.chatting.ChattingUI.a");
    this.अ = "biC";
    this.न = "hvT";
    this.वाद = "htW";
    this.करने = "htX";
    this.के = "pmy";
    this.लिए = "a";
    this.अनुवाद = (this.倪 + ".ui.k.b");
  }


* */
public class WechatHook implements IXposedHookLoadPackage
{
    private final String TAG = "wechatrobot";
    private XSharedPreferences mPref;
    private LoadPackageParam mLpp;
    private static SettingsHelper mSettings = new SettingsHelper();

    public void log(String s)
    {
        Log.i(TAG, s);
        XposedBridge.log(s);
    }

    private void hook_method(Class<?> clazz, String methodName, Object... parameterTypesAndCallback)
    {
        try {
          XposedHelpers.findAndHookMethod(clazz, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
          XposedBridge.log(e);
        }
    }

    // idem
    private void hook_method(String className, ClassLoader classLoader, String methodName,
                  Object... parameterTypesAndCallback)
    {
        log(className);
        log(methodName);
        try {
          XposedHelpers.findAndHookMethod(className, classLoader, methodName, parameterTypesAndCallback);
        } catch (Exception e) {
          XposedBridge.log(e);
        }
    }

    private void hook_methods(String className, String methodName, XC_MethodHook xmh)
    {
        try {
            Class<?> clazz = Class.forName(className);

            for (Method method : clazz.getDeclaredMethods())
                if (method.getName().equals(methodName)
                        && !Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                    XposedBridge.hookMethod(method, xmh);
                }
        } catch (Exception e) {
            XposedBridge.log(e);
        }
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


    static  boolean isFirst = false;

    @Override
    public void handleLoadPackage(final LoadPackageParam lpp) throws Throwable
    {
        mLpp = lpp;


        if (!mLpp.packageName.equals(Constants.WECHAT_PACKAGE_NAME))
            return;



        log("hook com.tencent.mm");


        XposedHelpers.findAndHookMethod(Location.class, "isFromMockProvider", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return false;
            }
        });

        //劫持判断是否使用虚拟定位
        // boolean isOpen = Settings.Secure.getInt(getContentResolver(),Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0;
        XposedHelpers.findAndHookMethod("android.provider.Settings$Secure", lpp.classLoader, "getInt", ContentResolver.class, String.class, int.class, new XC_MethodHook() {

            /**
             * 设置返回值类型：
             * 1、gpsEnabled && networkEnabled：LOCATION_MODE_HIGH_ACCURACY
             * 2、gpsEnabled：LOCATION_MODE_SENSORS_ONLY
             * 3、networkEnabled：LOCATION_MODE_BATTERY_SAVING
             * 4、disable：LOCATION_MODE_OFF
             */
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                param.setResult(LOCATION_MODE_SENSORS_ONLY);
            }
        });

        XposedHelpers.findAndHookMethod(Settings.Secure.class, "getInt", ContentResolver.class, String.class, int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                return LOCATION_MODE_SENSORS_ONLY;
            }
        });

        XposedHelpers.findAndHookMethod(Location.class, "isFromMockProvider", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                param.setResult(false);
            }
        });


//        try {
//            XposedHelpers.findAndHookMethod("com.tencent.mm.ui.LauncherUI", lpp.classLoader, "onResume", new XC_MethodHook() {
//                protected void afterHookedMethod(MethodHookParam methodHookParam) {
//                    if(isFirst) {
//                        isFirst = false;
//                        Object obj = methodHookParam.thisObject;
//                        Activity UploadActivity = (Activity) obj;
//                        try {
//                            String activityName = "com.tencent.mm.plugin.sns.ui.SnsUploadUI";
//                            Intent intent = new Intent();
//                            intent.setClassName(UploadActivity.getApplicationContext(), activityName);
//                            intent.putExtra("Kdescription", "咔咔");
//                            intent.putExtra("Kis_take_photo", false);
//                            intent.putExtra("need_result", true);
//                            intent.putExtra("Ksnsupload_type", 14);
//                            intent.putExtra("K_go_to_SnsTimeLineUI", false);
//                            UploadActivity.startActivity(intent);
//                            log("successful!");
//                        } catch (Throwable e) {
//                            log("a error:" + Log.getStackTraceString(e));
//                        }
//                    }
//                    Object obj = methodHookParam.thisObject;
//                    Activity UploadActivity = (Activity) obj;
//                    String videoImg = "/mnt/sdcard/DCIM/Camera/PANO_20150501_113104.jpg";
//                    String videoPath = "/mnt/sdcard/DCIM/Camera/VID_20170423_212327.mp4";
//                    File imgFile = new File(videoImg);
//                    File videoFile = new File(videoPath);
//                    if (videoFile.exists() && imgFile.exists()) {
//                        try {
//                            String md5 = countmd5.g(videoFile);
//                            String activityName = "com.tencent.mm.plugin.sns.ui.SightUploadUI";
//                            Intent intent = new Intent();
//                            intent.setClassName(UploadActivity.getApplicationContext(), activityName);
//                            intent.putExtra("sight_md5", md5);
//                            intent.putExtra("KSightDraftEntrance", false);
//                            intent.putExtra("KSightPath", videoPath);
//                            intent.putExtra("KSightThumbPath", videoImg);
//                            intent.putExtra("Kdescription", "TestTestTest...");
//                            UploadActivity.startActivity(intent);
//                            log("successful!");
//                        } catch (Throwable e) {
//                            log("a error:" + Log.getStackTraceString(e));
//                        }
//                    } else {
//                        log( "file not exist...");
//                    }
//                }
//            });
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }



//        try {
//            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.sns.ui.SnsTimeLineUI", lpp.classLoader, "onStart", new XC_MethodHook() {
//                        @Override
//                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                            XposedBridge.log("Hooked. ");
//                            Object currentObject = param.thisObject;
//                            Field krw = null;
//                            for (Field field : currentObject.getClass().getDeclaredFields()) { //遍历类成员
//                                field.setAccessible(true);
//                                log("field name:" + field.getName());
//                                if (field.getName().equals("krw")) {
//                                    log("Child krw found.");
//                                    krw = field;
//                                    break;
//                                }
//                            }
//                            if(krw != null){
//                                final View v = (View) krw.get(currentObject.getClass());
//                                if(v != null){
//                                    log("width:" + v.getLayoutParams().width + ", height:" + v.getLayoutParams().height);
//                                    new Handler().postDelayed(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            setSimulateClick(v,v.getWidth()/2,v.getHeight()/2);
//                                        }
//                                    },1000);
//                                }
//                            }
//                        }
//
//                    });
//                }
//            });
//        } catch (Throwable t) {
//            t.printStackTrace();
//        }



            try {
                XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.subapp.ui.pluginapp.AddMoreFriendsUI", lpp.classLoader, "onResume", new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                XposedBridge.log("Hooked. ");
                                XSharedPreferences pre = new XSharedPreferences("com.czfrobot.wechatrobot", "user");
                                boolean isSearchFriends = pre.getBoolean("isSearchFriends", false);
                                log("isSearchFriends:" + isSearchFriends);
                                Object obj = param.thisObject;
                                Activity AddMoreFriendsActivity = (Activity) obj;
                                if (isSearchFriends) {
                                    try {
                                        String activityName = "com.tencent.mm.plugin.search.ui.FTSAddFriendUI";
                                        Intent intent = new Intent();
                                        intent.setClassName(AddMoreFriendsActivity.getApplicationContext(), activityName);
                                        AddMoreFriendsActivity.startActivity(intent);
                                        log("successful!");
                                    } catch (Throwable e) {
                                        log("a error:" + Log.getStackTraceString(e));
                                    }
                                }
                            }

                        });

                        XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.bottle.ui.BottleBeachUI", lpp.classLoader, "g", int.class, int.class, int.class, int.class,  new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                XposedBridge.log("Hooked. ");
                                XSharedPreferences pre = new XSharedPreferences("com.czfrobot.wechatrobot", "user");
//                                SystemControl.delay(10*1000);
                                boolean isThrowBottleUI = pre.getBoolean("isThrowBottleUI", false);
                                log("isThrowBottleUI:" + isThrowBottleUI);
//                                if (isThrowBottleUI) {
                                    Object obj = param.thisObject;
                                    try {
                                        Field eAj = obj.getClass().getDeclaredField("eAj");
                                        eAj.setAccessible(true);
                                        Class deClazz = eAj.getType();
                                        Log.i(Constants.LOG_TAG,"eAj type-->"+deClazz.getSimpleName());
                                        Object throwBottleUI = eAj.get(obj);
                                        Class bottleClazz = throwBottleUI.getClass();
                                        Field eDs = bottleClazz.getDeclaredField("eDs");
                                        eDs.setAccessible(true);
                                        Class clazz = eDs.getType();
                                        Log.i(Constants.LOG_TAG,"eDs type-->"+clazz.getSimpleName());
                                        if (eDs.get(throwBottleUI) instanceof Button) {
                                            final Button s3 = (Button) eDs.get(throwBottleUI);
                                            Log.i(Constants.LOG_TAG, "x and y-->" + s3.getX() + "," + s3.getY());
                                            Log.i(Constants.LOG_TAG, "width and height-->" + s3.getWidth() + "," + s3.getHeight());
                                            //(128,1188),(702,1272)
                                            setSimulateClick(s3, s3.getX() + s3.getWidth() / 2, s3.getY() + s3.getHeight() / 2);
                                        }

                                    } catch (Throwable e) {
                                        log("a error:" + Log.getStackTraceString(e));
                                    }
//                                }
                            }

                        });
                    }
                });
            } catch (Throwable t) {
                t.printStackTrace();
            }


    }

    private void hookgps2(final  LoadPackageParam lpp){

        ClassLoader classLoader = lpp.classLoader;

        final double latitude = 114.037941;
        final double longtitude = 22.554323;

        XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                "getCellLocation", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        param.setResult(null);
                    }
                });


        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            XposedHelpers.findAndHookMethod("android.telephony.TelephonyManager", classLoader,
                    "getNeighboringCellInfo", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            param.setResult(null);
                        }
                    });
        }


        XposedHelpers.findAndHookMethod("android.net.wifi.WifiManager", classLoader, "getScanResults", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(null);

            }
        });


        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(latitude);
                l.setLongitude(longtitude);
                l.setAccuracy(100f);
                l.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                param.setResult(l);
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                Location l = new Location(LocationManager.GPS_PROVIDER);
                l.setLatitude(latitude);
                l.setLongitude(longtitude);
                l.setAccuracy(100f);
                l.setTime(System.currentTimeMillis());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    l.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                }
                param.setResult(l);
            }
        });


        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                ArrayList<String> arrayList = new ArrayList<>();
                arrayList.add("gps");
                param.setResult(arrayList);
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult("gps");
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener", GpsStatus.Listener.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (param.args[0] != null) {
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 1);
                    XposedHelpers.callMethod(param.args[0], "onGpsStatusChanged", 3);
                }
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener", GpsStatus.NmeaListener.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(false);
            }
        });


    }

    private void hookgps(final LoadPackageParam lpp){

//        hook_method("android.net.wifi.WifiManager", lpp.classLoader, "getScanResults", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                //return empty ap list, force apps using gps information
//                param.setResult(null);
//            }
//        });
//
//        hook_method("android.telephony.TelephonyManager", lpp.classLoader, "getCellLocation", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                //return empty cell id list
//                param.setResult(null);
//            }
//        });
//
//        hook_method("android.telephony.TelephonyManager", lpp.classLoader, "getNeighboringCellInfo", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                // return empty neighboring cell info list
//                param.setResult(null);
//            }
//        });

        try {

            XposedHelpers.findAndHookMethod("com.android.server.LocationManagerService", mLpp.classLoader, "reportLocation", Location.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Location location = (Location) param.args[0];
//                    log("实际 系统 经度" + location.getLatitude() + " 系统 纬度" + location.getLongitude() + "系统 加速度 " + location.getAccuracy());
//                    XSharedPreferences xsp = new XSharedPreferences("com.czfrobot.wechatrobot", "location");
//                    if (xsp.getBoolean("enableHook", true) ) {
                        double latitude = 114.037941;//Double.valueOf(xsp.get、String("lan", "117.536246"))+ (double) new Random().nextInt(1000) / 1000000;
                        double longtitude = 22.554323;//Double.valueOf(xsp.getString("lon", "36.681752")) + (double) new Random().nextInt(1000) / 1000000;
                        location.setLongitude(longtitude);
                        location.setLatitude(latitude);
//                        log("hook 系统 经度" + location.getLatitude() + " 系统 纬度" + location.getLongitude() + "系统 加速度 " + location.getAccuracy());
//                    }
                }
            });
        }catch (Throwable t){
            t.printStackTrace();
        }

        XposedBridge.hookAllConstructors(LocationManager.class,new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.args.length==2) {
                    Context context = (Context) param.args[0]; //这里的 context
                    log("模拟位置");
                    //把权限的检查 hook掉
                    XposedHelpers.findAndHookMethod(context.getClass(), "checkCallingOrSelfPermission", String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if (param.args[0].toString().contains("INSTALL_LOCATION_PROVIDER")){
                                param.setResult(PackageManager.PERMISSION_GRANTED);
                            }
                        }
                    });
                    log("LocationManager : " + context.getPackageName() + " class:= " + param.args[1].getClass().toString());
                    //获取到  locationManagerService 主动调用 对象的 reportLocation 方法  可以去模拟提供位置信息
                    //这里代码中并没有涉及到主动调用
                    Object   locationManagerService = param.args[1];
                }
            }
        });



//        hook_methods("android.location.LocationManager", "requestLocationUpdates", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//
//                if (param.args.length == 4 && (param.args[0] instanceof String)) {
//
//                    LocationListener ll = (LocationListener)param.args[3];
//
//                    Class<?> clazz = LocationListener.class;
//                    Method m = null;
//                    for (Method method : clazz.getDeclaredMethods()) {
//                        if (method.getName().equals("onLocationChanged")) {
//                            m = method;
//                            break;
//                        }
//                    }
//
//                    try {
//                        if (m != null) {
//                            mSettings.reload();
//
//                            Object[] args = new Object[1];
//                            Location l = new Location(LocationManager.GPS_PROVIDER);
//
////                            double la = Double.parseDouble(mSettings.getString("latitude", "-10001"));
////                            double lo = Double.parseDouble(mSettings.getString("longitude","-10001"));
//                            //double la = Utils.getLatitude();
//                           // double lo = Utils.getLongitude();
//
//                            final double la = 114.037941;
//                            final double lo = 22.554323;
//
//                            l.setLatitude(la);
//                            l.setLongitude(lo);
//
//                            args[0] = l;
//
//                            //invoke onLocationChanged directly to pass location infomation
//                            m.invoke(ll, args);
//
//                            XposedBridge.log("fake location: " + la + ", " + lo);
//                        }
//                    } catch (Exception e) {
//                        XposedBridge.log(e);
//                    }
//                }
//            }
//        });
//
//
//        hook_methods("android.location.LocationManager", "getGpsStatus", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                GpsStatus gss = (GpsStatus)param.getResult();
//                if (gss == null)
//                    return;
//
//                Class<?> clazz = GpsStatus.class;
//                Method m = null;
//                for (Method method : clazz.getDeclaredMethods()) {
//                    if (method.getName().equals("setStatus")) {
//                        if (method.getParameterTypes().length > 1) {
//                            m = method;
//                            break;
//                        }
//                    }
//                }
//
//                //access the private setStatus function of GpsStatus
//                m.setAccessible(true);
//
//                //make the apps belive GPS works fine now
//                int svCount = 5;
//                int[] prns = {1, 2, 3, 4, 5};
//                float[] snrs = {0, 0, 0, 0, 0};
//                float[] elevations = {0, 0, 0, 0, 0};
//                float[] azimuths = {0, 0, 0, 0, 0};
//                int ephemerisMask = 0x1f;
//                int almanacMask = 0x1f;
//
//                //5 satellites are fixed
//                int usedInFixMask = 0x1f;
//
//                try {
//                    if (m != null) {
//                        m.invoke(gss,svCount, prns, snrs, elevations, azimuths, ephemerisMask, almanacMask, usedInFixMask);
//                        param.setResult(gss);
//                    }
//                } catch (Exception e) {
//                    XposedBridge.log(e);
//                }
//            }
//        });

    }

}




















