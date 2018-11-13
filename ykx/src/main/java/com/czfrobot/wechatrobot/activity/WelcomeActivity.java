package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.model.DeviceBindModel;
import com.czfrobot.wechatrobot.http.model.UpdateModel;
import com.czfrobot.wechatrobot.http.parameter.DeviceIsBindParam;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.listener.UpdateListener;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.IOUtils;
import com.czfrobot.wechatrobot.utils.ImageCacheConfig;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.UpdateUtil;
import com.czfrobot.wechatrobot.utils.Utils;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


public class WelcomeActivity extends Activity implements UpdateListener{

    private String appUrl, appFileName;
    private RelativeLayout layout_download;
    private ProgressBar progressBar;
    private PopupWindow popupWindow;

    private final Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
//            finish();
//            return;
//        }

        setContentView(R.layout.activity_welcome);
        layout_download = (RelativeLayout)findViewById(R.id.layout_download);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        appFileName = "new.apk";

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(DeviceUtils.isNetworkAvailable(WelcomeActivity.this)) {
                    UpdateUtil.getInstance().checkUpdate(WelcomeActivity.this);
                }else {
                    Toast.makeText(WelcomeActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                    finish();
                }

            }
        }, 2000);


    }

    private void initPopupWindow() {
        final View view = this.getLayoutInflater().inflate(R.layout.activity_download_progress, null);
        popupWindow = new PopupWindow(view, ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        popupWindow.setOutsideTouchable(false);
    }


    @Override
    public void OnUpdateResult(String url, int version, String errorMessage) {
        if (!TextUtils.isEmpty(errorMessage)) {
            Toast.makeText(WelcomeActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
        } else if (!TextUtils.isEmpty(url)) {
            if (version > Utils.getVersionCode(WelcomeActivity.this)) {
                appUrl = url;
                showDialog("有最新版本, 是否升级");
            }else{
                NoUpdateHandle();
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




    public class judgeDeviceBindCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                });
                Toast.makeText(WelcomeActivity.this, dataBean.getMessage(), Toast.LENGTH_SHORT).show();
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
                    intent.setClass(WelcomeActivity.this, BindDeviceActivity.class);
                } else if(model.getData() != null ){
                    Preferences.saveDeviceName(WelcomeActivity.this, model.getData().getDesc());
                    Preferences.saveJPushAlias(WelcomeActivity.this, model.getData().get_id());
                    Preferences.saveJPushTag(WelcomeActivity.this, model.getData().getGroup_tag());
                    intent.setClass(WelcomeActivity.this, MainActivity.class);
                }
                startActivity(intent);
                finish();

            }
        }
    }



    private void NoUpdateHandle(){
        if (TextUtils.isEmpty(Preferences.getUserToken(WelcomeActivity.this))) {
            Intent intent = new Intent();
            intent.setClass(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            if(DeviceUtils.isNetworkAvailable(WelcomeActivity.this)) {
                DeviceIsBindParam params = new DeviceIsBindParam(DeviceUtils.getIEMI(WelcomeActivity.this));
                Map<String, String> map = new HashMap<String, String>();
                map.put("data", JSON.toJSONString(params));
                String url = HttpConstants.COMMON_DOMAIN + HttpConstants.IS_BIND_DEVICE;
                String token = Preferences.getUserToken(WelcomeActivity.this);
                BaseHttpRequest<BaseBean<DeviceBindModel>> request = new BaseHttpRequest<BaseBean<DeviceBindModel>>(Request.Method.POST, url, map, token, new judgeDeviceBindCallback<BaseBean<DeviceBindModel>>());
                VolleyUtil.getRequestQueue().add(request);
            }else {
                Toast.makeText(WelcomeActivity.this, "请检查网络", Toast.LENGTH_LONG).show();
                finish();
            }
        }
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
                        new UpdateUtil.downloadApkThread(appUrl, appFileName, WelcomeActivity.this).start();
                    }

                });
        builder.setNegativeButton("跳过", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                NoUpdateHandle();
            }
        });
        builder.setCancelable(false);
        builder.show();
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
    public void onBackPressed() {
        if(layout_download.getVisibility() == View.VISIBLE){
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}