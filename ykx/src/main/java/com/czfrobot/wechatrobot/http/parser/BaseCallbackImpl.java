package com.czfrobot.wechatrobot.http.parser;

import android.util.Log;

import com.android.volley.VolleyError;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class BaseCallbackImpl<T> implements BaseCallback<T> {
    @Override
    public void onErrorResponse(VolleyError volleyError) {
        Log.e("wechatrobot", volleyError.getMessage(), volleyError);
        byte[] htmlBodyBytes = volleyError.networkResponse.data;
        Log.e("wechatrobot",  new String(htmlBodyBytes), volleyError);
    }

    @Override
    public void onResponse(T t) {
        Log.i("WechatRobot", t.toString());
    }
}
