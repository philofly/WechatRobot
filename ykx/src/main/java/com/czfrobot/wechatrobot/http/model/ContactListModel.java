package com.czfrobot.wechatrobot.http.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by caoxianjin on 17/5/14.
 */

public class ContactListModel implements Serializable{

    private List<String> contacts;

    public List<String> getContacts() {
        return contacts;
    }

    public void setContacts(List<String> contacts) {
        this.contacts = contacts;
    }
}
