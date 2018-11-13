package com.czfrobot.wechatrobot.http.model;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class BaseReponse<T> implements Serializable {

    private String message;
    private String code;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "BaseReponse{" +
                "message='" + message + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
