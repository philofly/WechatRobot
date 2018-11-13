package com.czfrobot.wechatrobot.http.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.czfrobot.wechatrobot.utils.Preferences;

import java.util.List;

/**
 * Created by caoxianjin on 17/5/20.
 */

/*
8091/api/material/591c811040ff753e2398d687
 "_id": "591c811040ff753e2398d687",
    "__t": "Material",
    "create_at": 1495040272000,
    "create_user_id": "5919c114d7155d7f38eeea12",
    "update_at": 1495040272000,
    "update_user_id": "5919c114d7155d7f38eeea12",
    "content": "规范个 合格合格合格合格好 h",
    "material_type": 1,
    "__v": 0,
    "vedio_url": "",
    "pictures": [
      "http://yunkongxia2.oss-cn-shenzhen.aliyuncs.com/6bTJL1XlxPycllUjyZm1q-Gz.jpeg"
    ],
    "is_deleted": false
 */

public class MomentMaterialModel {

    private String _id;
    private String __t;
    private long create_at;
    private String create_user_id;
    private long update_at;
    private String update_user_id;
    private String content;
    private int material_type;
    private int __v;
    private String vidio_url;
    private List<String> pictures;
    private boolean is_deleted;

    @Override
    public String toString() {
        return "MomentMaterialModel{" +
                "_id='" + _id + '\'' +
                ", __t='" + __t + '\'' +
                ", create_at=" + create_at +
                ", create_user_id='" + create_user_id + '\'' +
                ", update_at=" + update_at +
                ", update_user_id='" + update_user_id + '\'' +
                ", content='" + content + '\'' +
                ", material_type=" + material_type +
                ", __v=" + __v +
                ", vidio_url='" + vidio_url + '\'' +
                ", pictures=" + pictures +
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getMaterial_type() {
        return material_type;
    }

    public void setMaterial_type(int material_type) {
        this.material_type = material_type;
    }

    @JSONField(name="__v")
    public int get__v() {
        return __v;
    }

    @JSONField(name="__v")
    public void set__v(int __v) {
        this.__v = __v;
    }

    public String getVidio_url() {
        return vidio_url;
    }

    public void setVidio_url(String vidio_url) {
        this.vidio_url = vidio_url;
    }

    public List<String> getPictures() {
        return pictures;
    }

    public void setPictures(List<String> pictures) {
        this.pictures = pictures;
    }

    public boolean is_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(boolean is_deleted) {
        this.is_deleted = is_deleted;
    }
}
