package com.czfrobot.wechatrobot.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.czfrobot.wechatrobot.accessibility.InspectWechatFriendService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Preferences {

    public static void saveDeleteFriends(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("delete", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putStringSet("delete_friends", InspectWechatFriendService.deleteList).apply();
    }


    public static List<String> getDeleteFriends(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("delete", Context.MODE_WORLD_READABLE);
        Set<String> hashSet = sharedPreferences.getStringSet("delete_friends",new HashSet<String>());
        List<String> stringList = new ArrayList<>();
        for(String s:hashSet){
            stringList.add(s);
        }
        return stringList;

    }

    public static  void saveDeviceName(Context context, String deviceName){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("deviceName", deviceName).apply();
    }

    public static  String getDeviceName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("deviceName", "");
    }

    public static  void saveUserName(Context context, String userName){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("userName", userName).apply();
    }

    public static  String getUserName(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("userName", "");
    }

    public static  void saveUserToken(Context context, String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("token", token).apply();
    }

    public static  String getUserToken(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
         return sharedPreferences.getString("token", "");
    }

    public static  void saveJPushAlias(Context context, String JPushAlias){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("jpushalias", JPushAlias).apply();
    }

    public static  String getJPushAlias(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("jpushalias", "");
    }

    public static  void saveJPushTag(Context context, String JPushAlias){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("jpushtag", JPushAlias).apply();
    }

    public static  String getJPushTag(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("jpushtag", "");
    }

    public static  void saveBindResult(Context context, boolean isBind){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putBoolean("bindresult", isBind).apply();
    }

    public static  boolean isBind(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getBoolean("bindresult", false);
    }

    public static  void saveMomentMaterialContent(Context context, String content){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("content", content).apply();
    }

    public static  String getMomentMaterialContent(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("content", "hello");
    }

    public static  void saveMomentMaterialCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("contentCount", count).apply();
    }

    public static  int getMomentMaterialCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("contentCount", 0);
    }

    public static  int getSnsLikeCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("snslike", 0);
    }

    public static  void setSnsLikeCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("snslike", count).apply();
    }

    public static  int getSearchnearbyFriendsCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("nearbyFriendCount", 0);
    }

    public static  void setSearchnearbyFriendsCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("nearbyFriendCount", count).apply();
    }

    public static  int getShakeoffTimes(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("shakeofftimes", 0);
    }

    public static  void setShakeoffTimes(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("shakeofftimes", count).apply();
    }


    public static  void setIsSearchFriends(Context context, boolean isSearch){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putBoolean("isSearchFriends", isSearch).apply();
    }

    public static  void setIsShakoff(Context context, boolean isShakeoff){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_MULTI_PROCESS);
        sharedPreferences.edit().putBoolean("isShakeoff", isShakeoff).apply();
    }


    public static  int getDriftBottleCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("driftbottlecount", 0);
    }

    public static  void setDriftBottleCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("driftbottlecount", count).apply();
    }

    public static  String getDriftBottleContent(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("driftbottlecontent", "");
    }

    public static  void setDriftBottleContent(Context context, String content){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("driftbottlecontent", content).apply();
    }
    public static  void setIsThrowBottleUI(Context context, boolean isSearch){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putBoolean("isThrowBottleUI", isSearch).apply();
    }


    public static  int getScanRaddarCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("scanraddarcount", 0);
    }

    public static  void setScanRaddarCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("scanraddarcount", count).apply();
    }

    public static  void setStandGap(Context context, int gap){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("standfindgap", gap).apply();
    }


    public static  int getStandGap(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("standfindgap", 0);

    }

    public static  void setStandCount(Context context, int count){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("standfindcount", count).apply();
    }


    public static  int getStandCount(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("standfindcount", 0);

    }

    public static  void setGreet(Context context, String greet){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putString("greet", greet).apply();
    }


    public static  String getGreet(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getString("greet", "");

    }
    public static  void setNeedHi(Context context, int needHi){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("needHi", needHi).apply();
    }


    public static  int getNeedHi(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("needHi", 0);

    }

    public static  void setAroundtype(Context context, int aroundType){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        sharedPreferences.edit().putInt("aroundtype", aroundType).apply();
    }


    public static  int getAroundtype(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("user", Context.MODE_WORLD_READABLE);
        return sharedPreferences.getInt("aroundtype", 0);

    }

}
