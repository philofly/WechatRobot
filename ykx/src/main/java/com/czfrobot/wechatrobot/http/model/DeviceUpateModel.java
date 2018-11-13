package com.czfrobot.wechatrobot.http.model;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/15.
 */
/*
    {"desc": "我是描述3","registrationid":"hhhhh"}

 */

public class DeviceUpateModel implements Serializable {
    private String desc;
    private String registrationid;

    @Override
    public String toString() {
        return "DeviceUpateModel{" +
                "desc='" + desc + '\'' +
                ", registrationid='" + registrationid + '\'' +
                '}';
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getRegistrationid() {
        return registrationid;
    }

    public void setRegistrationid(String registrationid) {
        this.registrationid = registrationid;
    }
}
