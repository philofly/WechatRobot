package com.czfrobot.wechatrobot.http.parameter;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

/*
{"imei":"866375029405478"}
 */

public class DeviceIsBindParam implements Serializable {
    private String imei;

    public DeviceIsBindParam(String imei) {
        this.imei = imei;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

}
