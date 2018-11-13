package com.czfrobot.wechatrobot.http.parameter;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/15.
 */
/*
    {"desc": "我是描述3","registrationid":"hhhhh"}

 */

public class DeviceUpateParam implements Serializable {
    private String desc;
    private String registrationid;

    public DeviceUpateParam(String desc, String registrationid) {
        this.desc = desc;
        this.registrationid = registrationid;
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
