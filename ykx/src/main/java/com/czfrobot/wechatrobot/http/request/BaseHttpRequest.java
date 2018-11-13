package com.czfrobot.wechatrobot.http.request;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.http.parser.BaseCallback;


import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by caoxianjin on 17/5/13.
 */

public class BaseHttpRequest<T> extends Request<T> {

    private static final  int SOCKET_TIMEOUT = 10*1000;

    private Map<String, String> mParams;
    private BaseCallback<T> callback;
    private String token;

    public BaseHttpRequest(int method, String url, Map<String, String>  params, String token, BaseCallback<T> callback)
    {
        super(method, url, callback);
        Log.i(Constants.LOG_TAG, "url:" + url + ", param:" + params);
        this.callback = callback;
        this.mParams = params;
        this.token = token;
        setShouldCache(false);
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError
    {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Charset", "UTF-8");
        headers.put("accept-version", "v1");
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        if(!TextUtils.isEmpty(token)){
            headers.put("Authorization", "babel " + token);
        }
        return headers;
    }


    @Override
    public RetryPolicy getRetryPolicy()
    {
        RetryPolicy retryPolicy = new DefaultRetryPolicy(SOCKET_TIMEOUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        return retryPolicy;
    }

    /**
     * 这里开始解析数据
     * @param response Response from the network
     * @return
     */
    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            T result ;
            String jsonString =
                    new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.i(Constants.LOG_TAG, "response:" + jsonString);
            result = JSON.parseObject(jsonString, new TypeReference<T>(){});
            return Response.success(result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }catch(JSONException e){
            return Response.error(new ParseError(e));
        }
    }

    /**
     * 回调正确的数据
     * @param response The parsed response returned by
     */
    @Override
    protected void deliverResponse(T response) {
        callback.onResponse(response);
    }

    //关键代码就在这里，在 Volley 的网络操作中，如果判断请求方式为 Post 则会通过此方法来获取 param，所以在这里返回我们需要的参数，
    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        if(mParams != null) {
            return mParams;
        }else{
            return  super.getParams();
        }
    }

    @Override
    public byte[] getBody() throws AuthFailureError
    {
        return  super.getBody();
    }

}
