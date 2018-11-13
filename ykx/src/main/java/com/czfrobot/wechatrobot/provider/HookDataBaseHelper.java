package com.czfrobot.wechatrobot.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by caoxianjin on 17/6/24.
 */

public class HookDataBaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "WxPlugs.db";
    private static int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "wx_plugs_setting";

    private static String DB_PATH = "";
    private Context mContext;
    private static boolean mainTmpDirSet = false;
    @Override
    public SQLiteDatabase getReadableDatabase() {
//        if (!mainTmpDirSet) {
//            boolean rs = new File("/data/data/com.czfrobot.wechatrobot/databases/main").mkdir();
//            super.getReadableDatabase().execSQL("PRAGMA temp_store_directory = '/data/data/com.czfrobot.wechatrobot/databases/main'");
//            mainTmpDirSet = true;
//            return super.getReadableDatabase();
//        }
        return super.getReadableDatabase();
    }


    public HookDataBaseHelper(Context paramContext) {
        super(paramContext, DATABASE_NAME, null, DATABASE_VERSION);
        if(android.os.Build.VERSION.SDK_INT >= 4.2){
            DB_PATH = paramContext.getApplicationInfo().dataDir + "/databases/";
        } else {
            DB_PATH = "/data/data/" + paramContext.getPackageName() + "/databases/";
        }
        this.mContext = paramContext;
    }

    public void onCreate(SQLiteDatabase paramSQLiteDatabase) {
        paramSQLiteDatabase.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( _id INTEGER PRIMARY KEY AUTOINCREMENT, is_start INTEGER, morra_num INTEGER);");
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{
        //Open your local db as the input stream
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }
        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DATABASE_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            //database does't exist yet.
            e.printStackTrace();
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    public void onUpgrade(SQLiteDatabase paramSQLiteDatabase, int paramInt1, int paramInt2) {
        paramSQLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(paramSQLiteDatabase);
    }
}

