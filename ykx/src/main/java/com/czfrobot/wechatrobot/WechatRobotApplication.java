package com.czfrobot.wechatrobot;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.ImageCacheConfig;
import com.czfrobot.wechatrobot.utils.LocationUtil;
import com.czfrobot.wechatrobot.utils.UpdateUtil;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import cn.jpush.android.api.JPushInterface;

public class WechatRobotApplication extends Application {

	private static WechatRobotApplication instance;

	public static WechatRobotApplication getInstance()
	{
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

		//114.037941,22.554323
//		Utils.setLatitude(114.037941);
//		Utils.setLongitude(22.554323);

		// 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
		SDKInitializer.initialize(getApplicationContext());

		DeviceUtils.initLockScreen(this);

		LocationUtil.initLocation(this);
		try {
			LocationUtil.initLocationManager();
		} catch (Exception e) {
			e.printStackTrace();
		}


		JPushInterface.setDebugMode(true);     // 设置开启日志,发布时请关闭日志

		VolleyUtil.initialize(this);

		ImageCacheConfig.initConfig(this);
	}

}