package com.czfrobot.wechatrobot.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.R;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.utils.DeviceUtils;
import com.czfrobot.wechatrobot.utils.Preferences;
import com.czfrobot.wechatrobot.utils.UpdateUtil;
import com.czfrobot.wechatrobot.utils.VolleyUtil;

import java.util.HashMap;
import java.util.Map;


public class LogoutActivity extends Activity {

    private TextView username, device_max_num, bind_device_num;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_logout);

        username = (TextView) findViewById(R.id.username);
        username.setText(Preferences.getUserName(this));
        device_max_num = (TextView) findViewById(R.id.device_max_num);
        bind_device_num = (TextView) findViewById(R.id.bind_device_num);


        Button logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DeviceUtils.isNetworkAvailable(LogoutActivity.this)) {
                    showDialog("确定是否退出登录");
                }else {
                    Toast.makeText(LogoutActivity.this, "请检查网络", Toast.LENGTH_SHORT).show();
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


    }

    public class UserLogoutCallback<T>  extends BaseCallbackImpl<T>
    {

        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            if(volleyError != null && volleyError.networkResponse !=null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                try {
                    BaseBean<String>  dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                    });
                    Toast.makeText(LogoutActivity.this, dataBean.getMessage(), Toast.LENGTH_LONG).show();

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
                if (model.getCode() == null ) {
                    Preferences.saveUserToken(LogoutActivity.this, "");
                    Intent intent = new Intent();
                    intent.setClass(LogoutActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else if (!TextUtils.isEmpty(model.getMessage())) {
                    Toast.makeText(LogoutActivity.this, model.getMessage(), Toast.LENGTH_SHORT).show();
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
                        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.USER_LOGOUT ;
                        String token = Preferences.getUserToken(LogoutActivity.this);
                        BaseHttpRequest<BaseBean<String>> request = new BaseHttpRequest<BaseBean<String>>(Request.Method.POST, url, map, token, new UserLogoutCallback<BaseBean<String>>());
                        VolleyUtil.getRequestQueue().add(request);
                    }
                });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener(){

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