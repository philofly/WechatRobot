package com.czfrobot.wechatrobot.provider;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.czfrobot.wechatrobot.WechatRobotApplication;

/**
 * Created by caoxianjin on 17/6/24.
 */


public class DaoHandler {
    private HookDataBaseHelper helper = new HookDataBaseHelper(WechatRobotApplication.getInstance());

    public static DaoHandler getInstance() {
        return SingleHolder.instance;
    }

    public void add(ContentValues paramContentValues) {
        SQLiteDatabase localSQLiteDatabase = this.helper.getWritableDatabase();
        if (localSQLiteDatabase.update("wx_plugs_setting", paramContentValues, null, null) == 0) {
            localSQLiteDatabase.insert("wx_plugs_setting", null, paramContentValues);
        }
    }

    public Cursor query(String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2) {
        SQLiteDatabase localSQLiteDatabase = this.helper.getReadableDatabase();
        Cursor cursor = localSQLiteDatabase.query("wx_plugs_setting", paramArrayOfString1, paramString1, paramArrayOfString2, null, null, paramString2);
        return cursor;
    }

    public int update(ContentValues paramContentValues, String paramString, String[] paramArrayOfString) {
        SQLiteDatabase localSQLiteDatabase = this.helper.getWritableDatabase();
        int ret =  localSQLiteDatabase.update("wx_plugs_setting", paramContentValues, paramString, paramArrayOfString);
        return ret;
    }

    private static class SingleHolder {
        private static DaoHandler instance = new DaoHandler();
    }
}

