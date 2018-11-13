package com.czfrobot.wechatrobot.http.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/5/14.
 */

/*
05-15 22:23:30.381 10231-10231/? I/WechatRobot:
{"data":{
"__t":"Device",
"__v":0,
"_id":"5919ba62c86fd27b05cd0556",
"bind_user_id":"5916afbfdd761b2d18142203",
"create_at":1494858338000,"create_user_id":
"5916afbfdd761b2d18142203",
"desc":"我是设备",
"group_tag":"A",
"imei":"866375029405478",
"is_deleted":false,
"update_at":1494858338000,
"update_user_id":"5916afbfdd761b2d18142203",
"uuid":"94296660"}}

 */

/*"_id": "59243c6b40ff753e2398d999",
        "__t": "Device",
        "create_at": 1495546987000,
        "create_user_id": "5919c114d7155d7f38eeea12",
        "update_at": 1496927383000,
        "update_user_id": "5919c114d7155d7f38eeea12",
        "group_tag": "A",
        "uuid": "06673648",
        "imei": "864150039504921",
        "desc": "我是设备",
        "bind_user_id": "5919c114d7155d7f38eeea12",
        "__v": 0,
        "registrationid": "190e35f7e075785b16b",
        "delete_at": 1496927382000,
        "delete_user_id": "5919c114d7155d7f38eeea12",
        "is_deleted": false*/



public class DeviceBindModel implements Serializable {
    private int __v;
    private String __t;
    private long create_at;
    private String create_user_id;
    private long update_at;
    private String update_user_id;
    private String group_tag;
    private String uuid;
    private String imei;
    private String desc;
    private String bind_user_id;
    private String _id;
    private boolean is_deleted;

    @Override
    public String toString() {
        return "DeviceBindModel{" +
                "__v=" + __v +
                ", __t='" + __t + '\'' +
                ", create_at=" + create_at +
                ", create_user_id='" + create_user_id + '\'' +
                ", update_at=" + update_at +
                ", update_user_id='" + update_user_id + '\'' +
                ", group_tag='" + group_tag + '\'' +
                ", uuid='" + uuid + '\'' +
                ", imei='" + imei + '\'' +
                ", desc='" + desc + '\'' +
                ", bind_user_id='" + bind_user_id + '\'' +
                ", _id='" + _id + '\'' +
                ", is_deleted=" + is_deleted +
                '}';
    }
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

    public void setCreate_at(long create_at) {
        this.create_at = create_at;
    }

    public String getCreate_user_id() {
        return create_user_id;
    }

    public void setCreate_user_id(String create_user_id) {
        this.create_user_id = create_user_id;
    }

    public long getUpdate_at() {
        return update_at;
    }

    public void setUpdate_at(long update_at) {
        this.update_at = update_at;
    }

    public String getUpdate_user_id() {
        return update_user_id;
    }

    public void setUpdate_user_id(String update_user_id) {
        this.update_user_id = update_user_id;
    }

    public String getGroup_tag() {
        return group_tag;
    }

    public void setGroup_tag(String group_tag) {
        this.group_tag = group_tag;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    public String getBind_user_id() {
        return bind_user_id;
    }

    public void setBind_user_id(String bind_user_id) {
        this.bind_user_id = bind_user_id;
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
}

