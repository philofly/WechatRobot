package com.czfrobot.wechatrobot.http.parameter;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

/*
{"name":"caoxianjin",
"password":"123456",
"fee_type":1,
"valid_start_at":1490976000000,
"valid_end_at":1496246400000,
"device_max":100,
"address":"天河区",
"mobile":"18823171201"}
 */

public class UserAddParam implements Serializable {
    private String name;
    private String password;
    private int fee_type;
    private int valid_start_at;
    private int valid_end_at;
    private int device_max;
    private String address;
    private String mobile;

    public UserAddParam(String name, String password, int fee_type, int valid_start_at, int valid_end_at, int device_max, String address, String mobile) {
        this.name = name;
        this.password = password;
        this.fee_type = fee_type;
        this.valid_start_at = valid_start_at;
        this.valid_end_at = valid_end_at;
        this.device_max = device_max;
        this.address = address;
        this.mobile = mobile;
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

    public int getFee_type() {
        return fee_type;
    }

    public void setFee_type(int fee_type) {
        this.fee_type = fee_type;
    }

    public int getValid_start_at() {
        return valid_start_at;
    }

    public void setValid_start_at(int valid_start_at) {
        this.valid_start_at = valid_start_at;
    }

    public int getValid_end_at() {
        return valid_end_at;
    }

    public void setValid_end_at(int valid_end_at) {
        this.valid_end_at = valid_end_at;
    }

    public int getDevice_max() {
        return device_max;
    }

    public void setDevice_max(int device_max) {
        this.device_max = device_max;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }
}
