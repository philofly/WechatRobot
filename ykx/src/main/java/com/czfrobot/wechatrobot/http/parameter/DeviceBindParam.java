package com.czfrobot.wechatrobot.http.parameter;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

/*
data:{"uuid":"12","desc":"我是设备","group_tag":"A"}
 */

public class DeviceBindParam implements Serializable {
    private String imei;
    private String desc;

    public DeviceBindParam(String imei, String desc) {
        this.imei = imei;
        this.desc = desc;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
