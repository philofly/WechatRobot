-optimizationpasses 5
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-ignorewarnings

-keepattributes *Annotation*
-keepattributes Signature

#mi push
-dontwarn com.xiaomi.push.**

# universal-image-loader 混淆
-dontwarn com.nostra13.universalimageloader.**
-keep class com.nostra13.universalimageloader.** { *; }


# We should not proguard any data model class used utp.jar
-keep class android.os.** {*;}

# # ############### volley混淆 ###############
-dontwarn com.android.volley.jar.**
-keep class com.android.volley.** {*;}
-keep class com.android.volley.toolbox.** {*;}
-keep class com.android.volley.Response$* { *; }
-keep class com.android.volley.Request$* { *; }
-keep class com.android.volley.RequestQueue$* { *; }
-keep class com.android.volley.AuthFailureError* { *; }
-keep class com.android.volley.DefaultRetryPolicy* { *; }
-keep class com.android.volley.NetworkResponse* { *; }
-keep class com.android.volley.ParseError* { *; }
-keep class com.android.volley.RetryPolicy* { *; }

-keep class com.android.volley.toolbox.HurlStack$* { *; }
-keep class com.android.volley.toolbox.ImageLoader$* { *; }


# JPUSH
-dontoptimize
-dontpreverify

-dontwarn cn.jpush.**
-keep class cn.jpush.** { *; }

-dontwarn cn.jiguang.**
-keep class cn.jiguang.** { *; }

# # ############### baidu混淆 ###############

-keep class com.baidu.mapapi.** {*; }
-keep class com.baidu.platform.** {*; }
-keep class com.baidu.location.** {*; }
-keep class com.baidu.vi.** {*; }
-keep class vi.com.gdi.bgl.android.** {*; }

-keep class com.czfrobot.wechatrobot.http.model.** {*; }
-keep class com.czfrobot.wechatrobot.http.parameter.** {*; }

#xposed
-dontwarn de.robv.android.xposed.**
-keep class de.robv.android.xposed.** {*;}
-keep class de.robv.android.xposed.callbacks.** {*;}
-keep class de.robv.android.xposed.XposedBridge.** {*;}
-keep class com.czfrobot.wechatrobot.hook.WechatHook


# We should not proguard any data model class used fastjson
-dontwarn com.alibaba.fastjson.**
-keep class com.alibaba.fastjson.** {*;}


#plugin framework
-keep class letv.plugin.framework.**{*;}
-keep class com.letv.httpcoresdk.**{*;}
-keep class com.letv.upgrade.**{*;}
-keep class com.upgrade.**{*;}
-keep class com.letv.tv.plugin.**{*;}
-keep class com.letv.sdk.upgrade.httpentity.** {*;}
-keep class com.letv.sdk.upgrade.entity.UpgradeInfo {*;}
-keep class com.letv.sdk.upgrade.download.DownloadRecord {*;}

# # ############### 系统api等常规混淆 ###############

-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class com.android.vending.licensing.ILicensingService


-keepattributes *JavascriptInterface*

-keep class com.android.internal.util.** {*;}


-keepclasseswithmembernames class * {
    native <methods>;
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

-keep class * implements android.os.Serializable {
*;
}

# Also keep - Serialization code. Keep all fields and methods that are used for
# serialization.
-keepclassmembers class * extends java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Keep names - Native method names. Keep all native class/method names.
-keepclasseswithmembers,allowshrinking class * {
    native <methods>;
}