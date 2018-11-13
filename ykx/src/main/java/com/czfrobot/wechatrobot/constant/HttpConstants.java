package com.czfrobot.wechatrobot.constant;

/**
 * Created by caoxianjin on 17/5/10.
 */

public class HttpConstants {
    public static final  String COMMON_DOMAIN="http://120.24.74.161:8091";

    public static final  String COMMON_DOMAIN_NOPORT="http://120.24.74.161";

    public static final  String COMMON_HTTP="http://";


    public static final String USER_ADD = "/api/user";

    public static final  String USER_LOGIN = "/api/user/login";
    public static final  String USER_LOGOUT = "/api/user/logout";

    public static final  String  USER_CHANGE_PASSWORD = "/api/user/passwd";

    public static final String  BIND_DEVICE="/api/device";

    public static final String IS_BIND_DEVICE = "/api/device/by_imei";

    public static final String  UNBIND_DEVICE="/api/device/";

    public static final String  UPDATE_DEVICE="/api/device/";


    public static final String  APP_UPDATE="/api/version/last";

    public static final String  GET_CONTACT_LIST="/api/target_contacts/";

    public static final String  TASK_FEEBACK="/api/task_feedback";

    public static final String  WECHAT_FEEBACK="/api/wechat_feedback";


}
