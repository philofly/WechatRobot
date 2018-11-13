package com.czfrobot.wechatrobot.http.parser;

import com.android.volley.Response;

/**
 * Created by caoxianjin on 17/5/13.
 */

public interface BaseCallback<T>  extends Response.ErrorListener, Response.Listener<T>{
}


