package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
import com.czfrobot.wechatrobot.http.model.UserLoginModel;
import com.czfrobot.wechatrobot.http.parameter.DeviceIsBindParam;
import com.czfrobot.wechatrobot.http.parameter.UserLoginParam;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends Activity {

    private EditText login_username, login_password;
    private ProgressBar progressBar;
    private TextView login;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login_password = (EditText)findViewById(R.id.login_password);
        login_username = (EditText)findViewById(R.id.login_username);
        progressBar = (ProgressBar)findViewById(R.id.progress);
        FrameLayout layout_login = (FrameLayout) findViewById(R.id.layout_login);
        login = (TextView)findViewById(R.id.login);
        layout_login.setClickable(true);
        layout_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(login_username.getText())){
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(TextUtils.isEmpty(login_password.getText())){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!DeviceUtils.isNetworkAvailable(LoginActivity.this)) {
                    Toast.makeText(LoginActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(progressBar.getVisibility() == View.VISIBLE){
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                login.setText("登录中...");
                UserLoginParam params = new UserLoginParam(login_username.getText().toString(), login_password.getText().toString());
                Map<String, String> map = new HashMap<String, String>();
                map.put("data", JSON.toJSONString(params));
                String url = HttpConstants.COMMON_DOMAIN + HttpConstants.USER_LOGIN;
                BaseHttpRequest<BaseBean<UserLoginModel>> request = new BaseHttpRequest<BaseBean<UserLoginModel>>(Request.Method.POST, url, map, null, new UserLoginCallback<BaseBean<UserLoginModel>>());
                VolleyUtil.getRequestQueue().add(request);
            }

        });


    }
    public class UserLoginCallback<T>  extends  BaseCallbackImpl<T>
     {

         @Override
         public void onErrorResponse(VolleyError volleyError) {
             super.onErrorResponse(volleyError);
             if(volleyError != null && volleyError.networkResponse !=null) {
                 byte[] htmlBodyBytes = volleyError.networkResponse.data;
                 if(htmlBodyBytes != null) {
                     BaseBean<UserLoginModel> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<UserLoginModel>>() {
                     });
                     if(dataBean != null && !TextUtils.isEmpty(dataBean.getMessage())) {
                         Toast.makeText(LoginActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
                 progressBar.setVisibility(View.GONE);
                 login.setText("登录");
             }
         }

         @Override
         public void onResponse(T t) {
             super.onResponse(t);
             BaseBean<UserLoginModel>  model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<UserLoginModel>>(){});
             if(model != null && model.getData() != null){
                 Log.i(Constants.LOG_TAG, "user token:" + model.getData().getToken());
                 Preferences.saveUserToken(LoginActivity.this, model.getData().getToken());
                 Preferences.saveUserName(LoginActivity.this, login_username.getText().toString());
                 Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_LONG).show();
                 if(Preferences.isBind(LoginActivity.this)){
                     Intent intent = new Intent();
                     intent.setClass(LoginActivity.this, MainActivity.class);
                     startActivity(intent);
                     progressBar.setVisibility(View.GONE);
                     finish();
                 }else {
                     DeviceIsBindParam params = new DeviceIsBindParam(DeviceUtils.getIEMI(LoginActivity.this));
                     Map<String, String> map = new HashMap<String, String>();
                     map.put("data", JSON.toJSONString(params));
                     String url = HttpConstants.COMMON_DOMAIN + HttpConstants.IS_BIND_DEVICE;
                     String token = Preferences.getUserToken(LoginActivity.this);
                     BaseHttpRequest<BaseBean<DeviceBindModel>> request = new BaseHttpRequest<BaseBean<DeviceBindModel>>(Request.Method.POST, url, map, token, new LoginActivity.judgeDeviceBindCallback<BaseBean<DeviceBindModel>>());
                     VolleyUtil.getRequestQueue().add(request);
                 }
             }
         }
     }


    public class judgeDeviceBindCallback<T> extends BaseCallbackImpl<T> {
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
                        Toast.makeText(LoginActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
                login.setText("登录");
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<DeviceBindModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<DeviceBindModel>>() {
            });
            if (model != null) {
                Intent intent = new Intent();
                if (model.getCode() != null && model.getCode().equals("10015")) {//设备未绑定
                    intent.setClass(LoginActivity.this, BindDeviceActivity.class);
                } else if(model.getData() != null ){
                    Log.i(Constants.LOG_TAG, "judgeDeviceBindCallback, deviceName:" + model.getData().getDesc());
                    Log.i(Constants.LOG_TAG, "judgeDeviceBindCallback, tag:" + model.getData().getGroup_tag());
                    Preferences.saveBindResult(LoginActivity.this, true);
                    Preferences.saveDeviceName(LoginActivity.this, model.getData().getDesc());
                    Preferences.saveJPushAlias(LoginActivity.this, model.getData().get_id());
                    Preferences.saveJPushTag(LoginActivity.this, model.getData().getGroup_tag());
                    intent.setClass(LoginActivity.this, MainActivity.class);
                }
                startActivity(intent);
                progressBar.setVisibility(View.GONE);
                finish();

            }
        }
    }




//    Handler handler = new Handler(new Handler.Callback() {
//
//        @Override
//        public boolean handleMessage(final Message msg) {
//            runOnUiThread(new Runnable() {
//                public void run() {
//                    progressBar.setProgress(msg.arg1);
//                }
//            });
//            return false;
//        }
//    });
//
//    public class ThreadProgress extends Thread implements Runnable {
//
//        @Override
//        public void run() {
//
//            while( progressValue <= 100 && !mThreadProgress.isInterrupted()) {
//                try{
//                    progressValue += 10;
//                    Message message = new Message();
//                    message.arg1 = progressValue;
//                    handler.sendMessage(message);
//                    Thread.sleep(1000);
//                } catch (InterruptedException e){
//                    e.printStackTrace();
//                    break;
//                }
//            }
//        }
//
//    }
//
//    private  ThreadProgress  mThreadProgress;
//    private int progressValue = 0;
//
//    public void startProgress(){
//        // start progress bar
//        progressValue = 0;
//        progressBar.setProgress(progressValue);
//        mThreadProgress = new ThreadProgress();
//        mThreadProgress.start();
//
//    }
//
//    public void stopProgress(){
//        mThreadProgress.interrupt();
//        progressValue=0;
//        progressBar.setProgress(progressValue);
//    }


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
    }



}