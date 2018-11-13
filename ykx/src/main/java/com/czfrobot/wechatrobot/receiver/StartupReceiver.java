package com.czfrobot.wechatrobot.receiver;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;

import com.czfrobot.wechatrobot.activity.BindDeviceActivity;
import com.czfrobot.wechatrobot.activity.LoginActivity;
import com.czfrobot.wechatrobot.activity.MainActivity;
import com.czfrobot.wechatrobot.activity.WelcomeActivity;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.Preferences;

/**
 * Created by caoxianjin on 17/5/17.
 */


public class StartupReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(Constants.LOG_TAG, intent.getAction() );

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        DeviceUtils.wakeScreen(context);

        Log.i(Constants.LOG_TAG, "MainActivity isForeground:" + MainActivity.isForeground);

        Intent mainIntent = new Intent();
        if (!MainActivity.isForeground) {
            mainIntent.setClass(context, WelcomeActivity.class);
            mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainIntent);
        }
    }
}