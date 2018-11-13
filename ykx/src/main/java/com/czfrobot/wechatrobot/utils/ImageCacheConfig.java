package com.czfrobot.wechatrobot.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.czfrobot.wechatrobot.constant.Constants;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.LimitedAgeDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;


import java.io.File;


/**
 * @author caoxianjin
 */
public class ImageCacheConfig {


    public static final String IMAGE_CACHE_PATH = DeviceUtils.getSDPath() + "czfrobot/image";

    /**
     * 初始化
     *
     * @param context application context
     */
    public static void initConfig(Context context) {
        DisplayImageOptions opts = getDisplayOptions(null);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context)
                .memoryCache(getMemoryCache(context))
                .diskCache(getDiskCache())
                        // 本地缓存配置
                .threadPoolSize(3)
                .threadPriority(Thread.NORM_PRIORITY-1)
                .denyCacheImageMultipleSizesInMemory()
                .imageDownloader(new BaseImageDownloader(context))
                .tasksProcessingOrder(QueueProcessingType.LIFO)// 任务队列执行顺序 后进先出
                .defaultDisplayImageOptions(opts).build();

        ImageLoader.getInstance().init(config);
    }

    /**
     * 生成默认图片的显示选项
     *
     * @param defaultDrawable
     * @return
     */
    public static DisplayImageOptions getDisplayOptions(Drawable defaultDrawable) {
        return new DisplayImageOptions.Builder()
                .showImageForEmptyUri(defaultDrawable)
                .showImageOnFail(defaultDrawable).resetViewBeforeLoading(true)
                .showImageOnLoading(defaultDrawable).cacheInMemory(true)
                .displayer(commonBitmapDisplayer)
                .bitmapConfig(Bitmap.Config.ARGB_8888)
                .cacheOnDisk(true).build();
    }

    /**
     * 生成不给ImageView设置默认图片的显示选项
     *
     * @return
     */
    public static DisplayImageOptions getDisplayOptionsNoChange() {
        return new DisplayImageOptions.Builder().showImageForEmptyUri(null)
                .showImageOnFail(null).resetViewBeforeLoading(false)
                .showImageOnLoading(null).cacheInMemory(true).cacheOnDisk(true)
                .displayer(commonBitmapDisplayer)
                .build();
    }

    private static BitmapDisplayer commonBitmapDisplayer = new BitmapDisplayer() {
        @Override
        public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
            if (imageAware == null || imageAware.getWrappedView() == null) {
                return;
            }
            String url = null;
            if (imageAware.getWrappedView().getTag() != null) {
                url = imageAware.getWrappedView().getTag().toString();
            }
            imageAware.setImageBitmap(bitmap);
        }
    };

    /**
     * 缓存配置
     */
    public static LruMemoryCache getMemoryCache(Context context) {
        int memClass = ((ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE))
                .getMemoryClass();
        int availableSize = memClass / 8;
        int cacheSize = 1024 * 1024 * (availableSize > 0 ? availableSize : 4);
        Log.i(Constants.LOG_TAG, "getMemoryCache---memClass:" + memClass
                + "----availableSize:" + availableSize);
        return new LruMemoryCache(cacheSize);
    }

    /**
     * 缓存目录生成，
     * 目的地：/czfrobot/.image
     * 命名：MD5加密
     */
    public static DiskCache getDiskCache() {
        final File dir = new File(IMAGE_CACHE_PATH);

        //超时时间：7天
        return new LimitedAgeDiscCache(dir, 7 * 24 * 60 * 60);//秒;
    }



}
