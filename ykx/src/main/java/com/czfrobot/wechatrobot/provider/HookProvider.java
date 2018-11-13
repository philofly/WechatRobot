package com.czfrobot.wechatrobot.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
/**
 * Created by caoxianjin on 17/6/24.
 */

public class HookProvider extends  ContentProvider {

        private static final String AUTHORITIES = "com.czfrobot.wechatrobot.provider";
        private static final int DICE = 1;
        private static final int MORRA = 2;
        private static UriMatcher mUriMatcher = new UriMatcher(-1);
        private HookDataBaseHelper mHookDataBaseHelper;

        static
        {
            mUriMatcher.addURI("com.zfy.mockshake.provider", "wx_plugs_setting", 1);
            mUriMatcher.addURI("com.zfy.mockshake.provider", "wx_plugs_setting/#", 1);
            mUriMatcher.addURI("com.zfy.mockshake.provider", "wx_plugs_setting", 2);
            mUriMatcher.addURI("com.zfy.mockshake.provider", "wx_plugs_setting/#", 2);
        }

        public int delete(Uri paramUri, String paramString, String[] paramArrayOfString)
        {
            return 0;
        }

        public String getType(Uri paramUri)
        {
            return "vnd.android.cursor.dir/vnd.com.czfrobot.wechatrobot.wx_plugs_setting";
        }

        public Uri insert(Uri paramUri, ContentValues paramContentValues)
        {
            return null;
        }

        public boolean onCreate()
        {
            mHookDataBaseHelper = new HookDataBaseHelper(getContext());
            if (this.mHookDataBaseHelper == null) {
                return  false;
            }else{
                return  true;
            }
        }

        public Cursor query(Uri paramUri, String[] paramArrayOfString1, String paramString1, String[] paramArrayOfString2, String paramString2)
        {
            return DaoHandler.getInstance().query(paramArrayOfString1, paramString1, paramArrayOfString2, paramString2);
        }

        public int update(Uri paramUri, ContentValues paramContentValues, String paramString, String[] paramArrayOfString)
        {
            return DaoHandler.getInstance().update(paramContentValues, paramString, paramArrayOfString);
        }

}
