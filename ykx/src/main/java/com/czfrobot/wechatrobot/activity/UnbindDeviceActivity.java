package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.model.UserLoginModel;
import com.czfrobot.wechatrobot.http.parameter.UserLoginParam;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.JPushUtil;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;


public class UnbindDeviceActivity extends Activity {

    private TextView device_group, device_imei, device_name, bind_device_phone,bind_device_type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_unbind_device);

        device_imei = (TextView) findViewById(R.id.device_imei);
        device_group = (TextView)findViewById(R.id.device_group);
        device_name = (TextView)findViewById(R.id.device_name);
        bind_device_phone = (TextView)findViewById(R.id.bind_device_phone);
        bind_device_type = (TextView)findViewById(R.id.bind_device_type);



        device_group.setText(Preferences.getJPushTag(this));
        device_imei.setText(DeviceUtils.getIEMI(this));
        device_name.setText(Preferences.getDeviceName(this));
        bind_device_phone.setText(DeviceUtils.getNativePhoneNumber(this));
//        bind_device_type.setText(DeviceUtils.getDeviceModel());

        Button unbind = (Button) findViewById(R.id.unbind);
        unbind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceUtils.isNetworkAvailable(UnbindDeviceActivity.this)) {
                    showDialog("确定是否解绑设备");
                }else {
                    Toast.makeText(UnbindDeviceActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
                }
            }

        });
        ImageView back = (ImageView)findViewById(R.id.back);
        back.setClickable(true);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Log.i(Constants.LOG_TAG, DeviceUtils.getPhoneInfo(this));


    }
    public class UnbindDeviceCallback<T>  extends  BaseCallbackImpl<T>
     {

         @Override
         public void onErrorResponse(VolleyError volleyError) {
             super.onErrorResponse(volleyError);
             if(volleyError != null && volleyError.networkResponse !=null) {
                 byte[] htmlBodyBytes = volleyError.networkResponse.data;
                 try {
                     if(htmlBodyBytes != null) {
                         BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                         });
                         if(dataBean!=null && !TextUtils.isEmpty(dataBean.getMessage())) {
                             Toast.makeText(UnbindDeviceActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();
                         }
                     }

                 }catch (JSONException e){
                     e.printStackTrace();
                 }

             }
         }

         @Override
         public void onResponse(T t) {
             super.onResponse(t);
             BaseBean<String>  model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<String>>(){});
             if (model != null) {
                 if (model.getCode() == null ) {//解绑成功
                     Toast.makeText(UnbindDeviceActivity.this, "解绑成功", Toast.LENGTH_SHORT).show();
                     JPushUtil.stop(UnbindDeviceActivity.this);
                     Preferences.saveBindResult(UnbindDeviceActivity.this, false);
                     Intent intent = new Intent();
                     intent.setClass(UnbindDeviceActivity.this, BindDeviceActivity.class);
                     startActivity(intent);
                     finish();
                 }else if (!TextUtils.isEmpty(model.getMessage())) {
                     Toast.makeText(UnbindDeviceActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
                 }

             }
         }
     }


    private void showDialog(String title) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("data", "");
                        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.UNBIND_DEVICE + Preferences.getJPushAlias(UnbindDeviceActivity.this);
                        String token = Preferences.getUserToken(UnbindDeviceActivity.this);
                        BaseHttpRequest<BaseBean<String>> request = new BaseHttpRequest<BaseBean<String>>(Request.Method.DELETE, url, map, token, new UnbindDeviceCallback<BaseBean<String>>());
                        VolleyUtil.getRequestQueue().add(request);
                    }

                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
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
    protected void onDestroy() {
        super.onDestroy();
    }



}