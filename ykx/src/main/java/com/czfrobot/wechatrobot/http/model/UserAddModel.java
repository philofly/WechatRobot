package com.czfrobot.wechatrobot.http.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

/*
    "__v": 0,
    "__t": "User",
    "create_at": 1494659007000,
    "create_user_id": "590fe29cf8276151240babf4",
    "update_at": 1494659007000,
    "update_user_id": "590fe29cf8276151240babf4",
    "mobile": "18823171201",
    "address": "天河区",
    "device_max": 100,
    "valid_end_at": 1496246400000,
    "valid_start_at": 1490976000000,
    "fee_type": 1,
    "name": "caoxianjin",
    "_id": "5916afbfdd761b2d18142203",
    "is_deleted": false
 */

public class UserAddModel implements Serializable{

    private  int  __v;
    private  String __t;
    private  long create_at;
    private  String create_user_id;
    private  int update_at;
    private  String update_user_id;
    private  String mobile;
    private  String address;
    private  int device_max;
    private  long valid_end_at;
    private  long valid_start_at;
    private  int fee_type;
    private  String name;
    private  String _id;
    private  boolean is_deleted;

    @JSONField(name="__v")
    public int get__v() {
        return __v;
    }

    @JSONField(name="__v")
    public void set__v(int __v) {
        this.__v = __v;
    }

    @JSONField(name="__t")
    public String get__t() {
        return __t;
    }

    @JSONField(name="__t")
    public void set__t(String __t) {
        this.__t = __t;
    }

    public long getCreate_at() {
        return create_at;
    }

    public void setCreate_at(int create_at) {
        this.create_at = create_at;
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }

    public int getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(int update_at) {
        this.update_at = update_at;
    }

    public String getUpdate_user_id() {
        return update_user_id;
    }

    public void setUpdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getDevice_max() {
        return device_max;
    }

    public void setDevice_max(int device_max) {
        this.device_max = device_max;
    }

    public long getValid_end_at() {
        return valid_end_at;
    }

    public void setValid_end_at(int valid_end_at) {
        this.valid_end_at = valid_end_at;
    }

    public long getValid_start_at() {
        return valid_start_at;
    }

    public void setValid_start_at(int valid_start_at) {
        this.valid_start_at = valid_start_at;
    }

    public int getFee_type() {
        return fee_type;
    }

    public void setFee_type(int fee_type) {
        this.fee_type = fee_type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JSONField(name="_id")
    public String get_id() {
        return _id;
    }

    @JSONField(name="_id")
    public void set_id(String _id) {
        this._id = _id;
    }

    public boolean is_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }

    @Override
    public String toString() {
        return "UserAddModel{" +
                "__v=" + __v +
                ", __t='" + __t + '\'' +
                ", create_at=" + create_at +
                ", create_user_id='" + create_user_id + '\'' +
                ", update_at=" + update_at +
                ", update_user_id='" + update_user_id + '\'' +
                ", mobile='" + mobile + '\'' +
                ", address='" + address + '\'' +
                ", device_max=" + device_max +
                ", valid_end_at=" + valid_end_at +
                ", valid_start_at=" + valid_start_at +
                ", fee_type=" + fee_type +
                ", name='" + name + '\'' +
                ", _id='" + _id + '\'' +
                ", is_deleted=" + is_deleted +
                '}';
    }
}
