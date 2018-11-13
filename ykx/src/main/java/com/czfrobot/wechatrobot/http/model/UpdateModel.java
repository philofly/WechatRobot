package com.czfrobot.wechatrobot.http.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.io.Serializable;

/**
 * Created by caoxianjin on 17/6/18.
 */

/*
  "data": {
    "_id": "594652df54e4ee071dbb5d99",
    "__t": "Version",
    "create_at": 1497780959000,
    "create_user_id": "5919c114d7155d7f38eeea12",
    "update_at": 1497780959000,
    "update_user_id": "5919c114d7155d7f38eeea12",
    "url": "http://yunkongxia2.oss-cn-shenzhen.aliyuncs.com/ykx-release-2.1.2-212.apk",
    "version": 212,
    "__v": 0,
    "is_deleted": false
  }
 */

public class UpdateModel implements Serializable {
    private String _id;
    private String __t;
    private  long create_at;
    private String create_user_id;
    private long update_at;
    private String update_user_id;
    private String url;
    private int version;
    private int __v;
    private boolean is_deleted;

    @Override
    public String toString() {
        return "UpdateModel{" +
                "_id='" + _id + '\'' +
                ", __t='" + __t + '\'' +
                ", create_at=" + create_at +
                ", create_user_id='" + create_user_id + '\'' +
                ", update_at=" + update_at +
                ", update_user_id='" + update_user_id + '\'' +
                ", url='" + url + '\'' +
                ", version=" + version +
                ", __v=" + __v +
                ", is_deleted=" + is_deleted +
                '}';
    }
    @JSONField(name="_id")
    public String get_id() {
        return _id;
    }

    @JSONField(name="_id")
    public void set_id(String _id) {
        this._id = _id;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @JSONField(name="__v")
    public int get__v() {
        return __v;
    }

    @JSONField(name="__v")
    public void set__v(int __v) {
        this.__v = __v;
    }

    public boolean is_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
