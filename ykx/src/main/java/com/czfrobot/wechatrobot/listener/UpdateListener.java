package com.czfrobot.wechatrobot.listener;

/**
 * Created by caoxianjin on 17/6/19.
 */


public interface UpdateListener {
    /**
     * 升级失败
     * @param url
     */
    void OnUpdateResult(String url, int versionCode, String errorMessage);

    void OnUpdateProgress(int progress);

    void OnDownloadFinished();

}

