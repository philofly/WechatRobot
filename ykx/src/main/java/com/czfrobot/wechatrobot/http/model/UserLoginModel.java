package com.czfrobot.wechatrobot.http.model;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class UserLoginModel implements Serializable{

    private  String token;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "UserLoginModel{" +
                "token='" + token + '\'' +
                '}';
    }
}
