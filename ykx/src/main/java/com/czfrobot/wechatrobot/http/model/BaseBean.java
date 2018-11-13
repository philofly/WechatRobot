package com.czfrobot.wechatrobot.http.model;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class BaseBean<T> extends  BaseReponse<T>{

    T data;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean{" +
                "data=" + data +
                '}';
    }
}


