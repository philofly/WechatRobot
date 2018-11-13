package com.czfrobot.wechatrobot.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.czfrobot.wechatrobot.activity.WelcomeActivity;
import com.czfrobot.wechatrobot.constant.Constants;
import com.czfrobot.wechatrobot.constant.HttpConstants;
import com.czfrobot.wechatrobot.http.model.BaseBean;
import com.czfrobot.wechatrobot.http.model.UpdateModel;
import com.czfrobot.wechatrobot.http.parser.BaseCallbackImpl;
import com.czfrobot.wechatrobot.http.request.BaseHttpRequest;
import com.czfrobot.wechatrobot.listener.UpdateListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by caoxianjin on 17/6/19.
 */

public class UpdateUtil {

    private static UpdateUtil mInstance;
    private UpdateListener mCallback;

    private UpdateUtil() {
    }

    public static UpdateUtil getInstance() {
        if (mInstance == null) {
            initInstane();
        }
        return mInstance;
    }

    private static synchronized void initInstane() {
        if (mInstance == null) {
            mInstance = new UpdateUtil();
        }
    }

    public void checkUpdate(final UpdateListener callback){
        mCallback = callback;
        String url = HttpConstants.COMMON_DOMAIN + HttpConstants.APP_UPDATE;
        BaseHttpRequest<BaseBean<UpdateModel>> request = new BaseHttpRequest<BaseBean<UpdateModel>>(Request.Method.GET, url, null, null, new updateCallback<BaseBean<UpdateModel>>());
        VolleyUtil.getRequestQueue().add(request);
    }

    public class updateCallback<T> extends BaseCallbackImpl<T> {
        @Override
        public void onErrorResponse(VolleyError volleyError) {
            super.onErrorResponse(volleyError);
            super.onErrorResponse(volleyError);
            if (volleyError != null && volleyError.networkResponse != null) {
                byte[] htmlBodyBytes = volleyError.networkResponse.data;
                BaseBean<String> dataBean = JSON.parseObject(new String(htmlBodyBytes), new TypeReference<BaseBean<String>>() {
                });
                mCallback.OnUpdateResult("", 0, dataBean.getMessage());
            }
        }

        @Override
        public void onResponse(T t) {
            super.onResponse(t);
            BaseBean<UpdateModel> model = JSON.parseObject(t.toString(), new TypeReference<BaseBean<UpdateModel>>() {
            });
            if (model != null && model.getData()!=null) {
                mCallback.OnUpdateResult(model.getData().getUrl(), model.getData().getVersion(),  "");
            }
        }
    }


    /**
     * 正常方式安装APK
     */
    public  void installApk(Activity context, String filePath) {
        Log.i(Constants.LOG_TAG, "install apk:" + filePath);
        Utils.execShellCmd("chmod 777 " + filePath , "");
        Uri uri = Uri.fromFile(new File(filePath));
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        context.finish();
    }





    public static class downloadApkThread extends Thread {

        private String downloadUrl, fileName;
        private UpdateListener mCallback;
        @Override
        public void run() {
            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection connection;
            fileName = "new.apk";
            Log.i(Constants.LOG_TAG, "start download apk, url:" + downloadUrl);
            try {
                long startTime = System.currentTimeMillis();
                URL url = new URL(downloadUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(10 * 1000);
                connection.setReadTimeout(10 * 1000);
                connection.setDoInput(true);
                connection.connect();
                Log.i(Constants.LOG_TAG, "http response code:" + connection.getResponseCode());
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    // 获取下载文件总大小
                    final long totalSize = connection.getContentLength();
                    Log.i(Constants.LOG_TAG, "writeInputStreamToCache totalSize = " + totalSize
                            + "  appUrl = " + downloadUrl);
                    is = connection.getInputStream();
                    byte[] bytes = new byte[1024];
                    int len;
                    // 判断sdcard的状态
                    String sdcardState = Environment.getExternalStorageState();
                    if (Environment.MEDIA_MOUNTED.equals(sdcardState)) {
                        // 判断path有没有
                        File filePath = new File(Constants.APP_DOWNLOAD_PATH);//此处为常量地址：/mnt/sdcard/picture,可以自己定义                                             <span style="font-family: Arial, Helvetica, sans-serif;">                      if (!filePath.exists()) {</span>
                        filePath.mkdirs();
                    }
                    // 判断file有没有
                    File videoFile  = new File(Constants.APP_DOWNLOAD_PATH, fileName);
                    if (videoFile.exists()) {
                        videoFile.delete();
                    }
                    os = new FileOutputStream(videoFile);
                    int count = 0;
                    while ((len = is.read(bytes)) > 0) {
                        os.write(bytes, 0, len);
                        count += len;
                        mCallback.OnUpdateProgress((int)(count*100/totalSize));
                    }
                    mCallback.OnUpdateProgress(100);
                    os.flush();
                }
                connection.disconnect();
                long endTime = System.currentTimeMillis();
                Log.i(Constants.LOG_TAG, "download apk time = "
                        + (endTime - startTime) + "  appUrl = " + downloadUrl);
            } catch (FileNotFoundException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } catch (MalformedURLException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } catch (IOException e) {
                Log.i(Constants.LOG_TAG, e.toString());
            } finally {
                IOUtils.closeStream(os);
                if (is != null) {
                    IOUtils.closeStream(is);
                }
                mCallback.OnDownloadFinished();
            }
        }

        public downloadApkThread(String url, String filename, UpdateListener callback) {
            this.downloadUrl = url;
            fileName = filename;
            mCallback = callback;
        }
    }
}
