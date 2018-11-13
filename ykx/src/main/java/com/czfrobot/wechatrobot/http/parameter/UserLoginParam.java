package com.czfrobot.wechatrobot.http.parameter;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class UserLoginParam implements Serializable {
    private String name;
    private String password;

    public UserLoginParam(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
