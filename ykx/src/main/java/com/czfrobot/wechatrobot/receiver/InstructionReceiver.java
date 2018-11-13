package com.czfrobot.wechatrobot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.czfrobot.wechatrobot.LocalBroadcastManager;
import com.czfrobot.wechatrobot.activity.MainActivity;
import com.czfrobot.wechatrobot.activity.WechatControlActivity;
import com.czfrobot.wechatrobot.constant.Constants;


import java.util.Set;

import cn.jpush.android.api.JPushInterface;


/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class InstructionReceiver extends BroadcastReceiver {
	private static final String TAG = Constants.LOG_TAG;

	@Override
	public void onReceive(Context context, Intent intent) {
		try {
			Bundle bundle = intent.getExtras();
			Log.d(TAG, "[InstructionReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Log.d(TAG, "[InstructionReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[InstructionReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[InstructionReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Log.d(TAG, "[InstructionReceiver] 接收到推送下来的通知的ID: " + notifactionId);

			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Log.d(TAG, "[InstructionReceiver] 用户点击打开了通知");

//				//打开自定义的Activity
//				Intent i = new Intent(context, TestActivity.class);
//				i.putExtras(bundle);
//				//i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP );
//				context.startActivity(i);

			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Log.d(TAG, "[InstructionReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Log.e(TAG, "[InstructionReceiver]" + intent.getAction() +" connected state change to "+connected);

				//TODO(caoxianjin) for test
				Intent msgIntent = null;
				if (MainActivity.isForeground) {
					Log.i(TAG, "processCustomMessage, isForeground");
					msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
				}else{
					Log.i(TAG, "processCustomMessage, not isForeground");
					msgIntent = new Intent(context, MainActivity.class);
					msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				}
				msgIntent.putExtra("connected", connected);
				if(MainActivity.isCreated) {
					if (MainActivity.isForeground) {
						LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
					} else {
						context.startActivity(msgIntent);
					}
				}
			}
			else {
				Log.d(TAG, "[InstructionReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = JSON.parseObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Set<String> it =  json.keySet();

					for(String value : it){
						sb.append("\nkey:" + key + ", value: [" +
								value + " - " +json.get(value) + "]");
					}

				} catch (JSONException e) {
					Log.e(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}




	//send msg to MainActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
		Intent msgIntent = null;
		if (MainActivity.isForeground) {
			Log.i(TAG, "processCustomMessage, isForeground");
			msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
		}else{
			Log.i(TAG, "processCustomMessage, not isForeground");
			msgIntent = new Intent(context, MainActivity.class);
			msgIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		}

		msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
		if (!TextUtils.isEmpty(extras)) {
			try {
				JSONObject extraJson = JSON.parseObject(extras);
				if (extraJson.toString().length() > 0) {
					msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
				}
			} catch (JSONException e) {

			}

		}

		if (MainActivity.isForeground) {
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}else{
			context.startActivity(msgIntent);
		}

	}
}
