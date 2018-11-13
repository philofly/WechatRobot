
package com.czfrobot.wechatrobot.utils;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

import com.czfrobot.wechatrobot.constant.Constants;

import java.util.List;


public class LocationUtil {

    public static Double mLatitude = 30.6363334898;
    public static Double mLongitude = 104.0486168861;

    private static LocationManager locationManager;
    private static boolean canMockPosition;
    public static boolean hasAddTestProvider = false;
    private static Thread mMockThread;


    public static void initLocation(Context context) {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        canMockPosition = (Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION, 0) != 0) || Build.VERSION.SDK_INT > 22;
        Log.i(Constants.LOG_TAG, "hasAddTestProvider:" + canMockPosition);
    }

    public static Location getCurrentLocation(){
        String locationProvider;
        List<String> providers = locationManager.getProviders(true);
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            //如果是GPS
            locationProvider = LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            //如果是Network
            locationProvider = LocationManager.NETWORK_PROVIDER;
        }else{
            return null;
        }
        //获取Location
        Location location = locationManager.getLastKnownLocation(locationProvider);
        return  location;
    }


    /**
     * 配置LocationManger参数
     */
    public static void initLocationManager() throws Exception{
        if (canMockPosition && !hasAddTestProvider) {
            try {
                String providerStr = LocationManager.GPS_PROVIDER;
                LocationProvider provider = locationManager.getProvider(providerStr);
                if (provider != null) {
                    locationManager.addTestProvider(
                            provider.getName()
                            , provider.requiresNetwork()
                            , provider.requiresSatellite()
                            , provider.requiresCell()
                            , provider.hasMonetaryCost()
                            , provider.supportsAltitude()
                            , provider.supportsSpeed()
                            , provider.supportsBearing()
                            , provider.getPowerRequirement()
                            , provider.getAccuracy());
                } else {
                    locationManager.addTestProvider(
                            providerStr
                            , true, true, false, false, true, true, true
                            , Criteria.POWER_HIGH
                            , Criteria.ACCURACY_FINE);
                }
                locationManager.setTestProviderEnabled(providerStr, true);
                locationManager.requestLocationUpdates(providerStr, 0, 0, new LocationStatuListener());
                locationManager.setTestProviderStatus(providerStr, LocationProvider.AVAILABLE, null, System.currentTimeMillis());
                Log.i(Constants.LOG_TAG,"already open GPS!");
                // 模拟位置可用
                hasAddTestProvider = true;
                Log.d(Constants.LOG_TAG, "hasAddTestProvider：" + hasAddTestProvider);
                canMockPosition = true;
            } catch (Exception e) {
                canMockPosition = false;
                Log.i(Constants.LOG_TAG, "初始化异常：" + e);
                throw  e;
            }
        }
    }

    private static boolean isLog = true;
    private static  boolean isQuit = false;

    /**
     * 开启虚拟定位线程
     */
    public static void startLocaton(){
        isQuit = false;
        isLog = true;
        if (mMockThread == null) {
            mMockThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (!isQuit) {
                        try {
                            Thread.sleep(500);
                            if (!hasAddTestProvider) {
                                Log.i(Constants.LOG_TAG, "定位服务未打开");
                                continue;
                            }
                            setLocation(mLatitude, mLongitude);

                            //TODO(caoxianjin) for test
//                            if(isLog) {
//                                Log.i(Constants.LOG_TAG, "setLocation240=latitude:" + mLatitude + "?longitude:" + mLongitude);
//                                isLog = false;
//                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                 }
                }
            });
            mMockThread.start();
        }
    }

    /**
     * setLocation 设置GPS的位置
     */
    private static void setLocation(double latitude, double longitude) throws Exception{
        try {
            String providerStr = LocationManager.GPS_PROVIDER;
            Location mockLocation = new Location(providerStr);
            mockLocation.setLatitude(latitude);
            mockLocation.setLongitude(longitude);
            mockLocation.setAltitude(0);    // 高程（米）
            mockLocation.setBearing(0);   // 方向（度）
            mockLocation.setSpeed(0);    //速度（米/秒）
            mockLocation.setAccuracy(2);   // 精度（米）
            mockLocation.setTime(System.currentTimeMillis());   // 本地时间
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                //api 16以上的需要加上这一句才能模拟定位 , 也就是targetSdkVersion > 16
                mockLocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
            }
            locationManager.setTestProviderLocation(providerStr, mockLocation);
            isLog = true;
        } catch (Exception e) {
            // 防止用户在软件运行过程中关闭模拟位置或选择其他应用
            stopMockLocation();
            throw e;
        }
    }

    public static void stopMockLocation() {
//        if (hasAddTestProvider) {
//            try {
//                locationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
//            } catch (Exception ex) {
//                // 若未成功addTestProvider，或者系统模拟位置已关闭则必然会出错
//            }
//            hasAddTestProvider = false;
//        }

        isQuit = true;
        isLog = true;
        try {
            if(mMockThread != null) {
                mMockThread.join();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        mMockThread = null;
    }

    public static void setLongitudeAndLatitude(Double mLongitude, Double mLatitude) {
        LocationUtil.mLatitude = mLatitude;
        LocationUtil.mLongitude = mLongitude;
    }

    /**
     * 监听Location经纬度值的修改状态
     */
    private static class LocationStatuListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();
//            Log.d(Constants.LOG_TAG, String.format("location: x=%s y=%s", lat, lng));
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

}
