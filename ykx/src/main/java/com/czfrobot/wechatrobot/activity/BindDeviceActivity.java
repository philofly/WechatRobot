package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.model.DeviceBindModel;
import com.czfrobot.wechatrobot.http.parameter.DeviceBindParam;
import com.czfrobot.wechatrobot.http.parameter.DeviceUpateParam;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.JPushUtil;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;


public class BindDeviceActivity extends Activity {

    private static final int MSG_SET_ALIAS = 1200;
    private static final int MSG_SET_TAGS = 1201;
    private static final int MSG_GET_REGISTRAIONI_ID = 1202;


    private String registrationid = null;

    private ProgressBar progressBar;
    private TextView bind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_binddevice);

        TextView imei = (TextView) findViewById(R.id.imei);

        imei.setText(DeviceUtils.getIEMI(this));

        progressBar = (ProgressBar)findViewById(R.id.progress);
        bind = (TextView)findViewById(R.id.bind);
        FrameLayout layout_bind = (FrameLayout) findViewById(R.id.layout_bind);
        layout_bind.setClickable(true);
        layout_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!DeviceUtils.isNetworkAvailable(BindDeviceActivity.this)) {
                    Toast.makeText(BindDeviceActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }else {
                    if (progressBar.getVisibility() == View.VISIBLE) {
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    bind.setText("绑定设备中...");
                    bindDevice();
                }
            }
        });


    }

    private void bindDevice() {
        String imei = DeviceUtils.getIEMI(this);
        String lastFour = null;
        if(imei.length() >= 4){
            lastFour = imei.substring(imei.length()-4);
        }
        String desc = Constants.DEVICE_DESC +  lastFour;
        if(!TextUtils.isEmpty(Preferences.getDeviceName(this))){
            desc = Preferences.getDeviceName(this);
        }
        DeviceBindParam param = new DeviceBindParam(imei, desc);
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", JSON.toJSONString(param));
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.BIND_DEVICE;
        String token = Preferences.getUserToken(this);
        BaseHttpRequest<BaseBean<DeviceBindModel>> request = new BaseHttpRequest<BaseBean<DeviceBindModel>>(Request.Method.POST, url, map, token, new bindDeviceCallback<BaseBean<DeviceBindModel>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    public class bindDeviceCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                if(htmlBodyBytes != null) {
                    BaseBean<DeviceBindModel> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<DeviceBindModel>>() {
                    });
                    if(dataBean != null && !TextUtils.isEmpty(dataBean.getMessage())) {
                        Toast.makeText(BindDeviceActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.INVISIBLE);
                bind.setText("绑定设备");
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<DeviceBindModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<DeviceBindModel>>() {
            });
            if (model != null && model.getData() != null) {
                Log.i(Constants.LOG_TAG, "device uuid:" + model.getData().getUuid() + ", id:" + model.getData().get_id());
                Log.i(Constants.LOG_TAG, "device __v:" + model.getData().get__v() + ", __t:" + model.getData().get__t());
                Preferences.saveJPushAlias(BindDeviceActivity.this, model.getData().get_id());
                Preferences.saveJPushTag(BindDeviceActivity.this, model.getData().getGroup_tag());
                setAlias(model.getData().get_id());//设置别名
                Preferences.saveBindResult(BindDeviceActivity.this, true);
                Preferences.saveDeviceName(BindDeviceActivity.this, model.getData().getDesc());

                if(JPushUtil.isStopped(BindDeviceActivity.this)){
                    JPushUtil.resume(BindDeviceActivity.this);
                }
                Toast.makeText(BindDeviceActivity.this, "绑定设备成功", Toast.LENGTH_LONG).show();

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar.setVisibility(View.INVISIBLE);
                        Intent intent = new Intent();
                        intent.setClass(BindDeviceActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, 2000);
            }
        }
    }

    private void setAlias(String alias) {
        if (TextUtils.isEmpty(alias)) {
            return;
        }
        if (!JPushUtil.isValidTagAndAlias(alias)) {
            Toast.makeText(this, "alias无效", Toast.LENGTH_SHORT).show();
            return;
        }

        //调用JPush API设置Alias
        mHandler.sendMessage(mHandler.obtainMessage(MSG_SET_ALIAS, alias));
    }




    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SET_ALIAS:
                    Log.i(Constants.LOG_TAG, "Set alias in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), (String) msg.obj, null, mAliasCallback);
                    break;
                case MSG_SET_TAGS:
                    Log.i(Constants.LOG_TAG, "Set tags in handler.");
                    JPushInterface.setAliasAndTags(getApplicationContext(), null, (Set<String>) msg.obj, mTagsCallback);
                    break;
                case MSG_GET_REGISTRAIONI_ID:
                    registrationid = JPushInterface.getRegistrationID(BindDeviceActivity.this);
                    updateDevice();
                    break;
                default:
                    Log.i(Constants.LOG_TAG, "Unhandled msg - " + msg.what);
                    break;
            }
        }
    };

    private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(Constants.LOG_TAG, logs);
                    mHandler.sendEmptyMessage(MSG_GET_REGISTRAIONI_ID);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(Constants.LOG_TAG, logs);
                    if (JPushUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_ALIAS, alias), 1000 * 60);
                    } else {
                        Log.i("wechat", "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(Constants.LOG_TAG, logs);
            }

            Toast.makeText(getApplicationContext(), logs, Toast.LENGTH_SHORT).show();
        }

    };

    private final TagAliasCallback mTagsCallback = new TagAliasCallback() {

        @Override
        public void gotResult(int code, String alias, Set<String> tags) {
            String logs;
            switch (code) {
                case 0:
                    logs = "Set tag and alias success";
                    Log.i(Constants.LOG_TAG, logs);
                    break;

                case 6002:
                    logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
                    Log.i(Constants.LOG_TAG, logs);
                    if (JPushUtil.isConnected(getApplicationContext())) {
                        mHandler.sendMessageDelayed(mHandler.obtainMessage(MSG_SET_TAGS, tags), 1000 * 60);
                    } else {
                        Log.i(Constants.LOG_TAG, "No network");
                    }
                    break;

                default:
                    logs = "Failed with errorCode = " + code;
                    Log.e(Constants.LOG_TAG, logs);
            }
            Toast.makeText(getApplicationContext(), logs, Toast.LENGTH_SHORT).show();

        }

    };


    private void updateDevice() {
        String imei = DeviceUtils.getIEMI(this);
        String lastFour = null;
        if(imei.length() >= 4){
            lastFour = imei.substring(imei.length()-4);
        }
        String desc = Constants.DEVICE_DESC +  lastFour;
        if(!TextUtils.isEmpty(Preferences.getDeviceName(this))){
            desc = Preferences.getDeviceName(this);
        }
        DeviceUpateParam param = new DeviceUpateParam(desc, registrationid);
        Map<String, String> map = new HashMap<String, String>();
        map.put("data", JSON.toJSONString(param));
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.UPDATE_DEVICE + Preferences.getJPushAlias(this);
        String token = Preferences.getUserToken(this);
        BaseHttpRequest<BaseBean<String>> request = new BaseHttpRequest<BaseBean<String>>(Request.Method.PUT, url, map, token, new updateDeviceCallback<BaseBean<String>>());
        VolleyUtil.getRequestQueue().add(request);
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
                Toast.makeText(BindDeviceActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<String> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<String>>() {
            });
            if (model != null && model.getData() != null) {
                Toast.makeText(BindDeviceActivity.this, "更新设备成功", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }


}