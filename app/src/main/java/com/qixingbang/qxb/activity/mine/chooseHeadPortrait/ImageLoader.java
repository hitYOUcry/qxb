package com.qixingbang.qxb.activity.mine.chooseHeadPortrait;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import static android.graphics.BitmapFactory.Options;

/**
 * 图片加载类
 * Created by Z.H. on 2015/9/7 13:31.
 */
public class ImageLoader {
    /**
     * ImageLoader类的单例
     */
    private static ImageLoader mInstance;
    /**
     * 图片缓存的核心对象
     */
    private LruCache<String, Bitmap> mLruCache;
    /**
     * 线程池
     */
    private ExecutorService mThreadPool;
    /**
     * 默认线程数
     */
    private static final int DEFAULT_THREAD_COUNT = 1;
    /**
     * 后台轮询线程
     */
    private Thread mPoolThread;
    private Handler mPoolThreadHandler;
    /**
     * UI线程的Handler  用于回调显示Bitmap
     */
    private Handler mUIHandler;

    /**
     * 队列的调度方式
     */
    private Type mType = Type.LIFO;
    /**
     * 任务队列
     */
    private LinkedList<Runnable> mTaskQueue;

    /**
     * 信号量
     */
    private Semaphore mSemaphorePoolThreadHandler = new Semaphore(0);
    private Semaphore mSemaphoreThreadPool;
    public enum Type{
        FIFO,LIFO;
    }

    private ImageLoader(int mThreadCount, Type type) {
        init(mThreadCount, type);
    }

    private void init(int mThreadCount, Type type) {
        //后台轮询线程
        mPoolThread = new Thread(){
            @Override
            public void run() {
                Looper.prepare();
                mPoolThreadHandler = new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        //线程池取出一个任务进行执行
                        mThreadPool.execute(getTask());
                        try {
                            mSemaphoreThreadPool.acquire();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };
                mSemaphorePoolThreadHandler.release();
                Looper.loop();
            }
        };
        mPoolThread.start();

        int maxMemory = (int) Runtime.getRuntime().maxMemory();
        Log.e("maxMemory", String.valueOf(maxMemory));
        //TODO:视情况再改变  一般为1/8
        int cacheMemory = maxMemory / 12;
        mLruCache = new LruCache<String, Bitmap>(cacheMemory){
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                //                return bitmap.getRowBytes() * bitmap.getHeight();
                return bitmap.getByteCount();
            }
        };


        //创建线程池
        mThreadPool = Executors.newFixedThreadPool(mThreadCount);
        mTaskQueue = new LinkedList<Runnable>();
        mType = type;

        mSemaphoreThreadPool = new Semaphore(mThreadCount);
    }


    /**
     * 获取ImageLoader实例
     * @return
     */
    public static ImageLoader getInstance(int threadCount, Type type){
        if (mInstance == null){
            //可能有几个线程同时到达  防止new多个实例
            //synchronized  只能有单线程执行
            synchronized (ImageLoader.class){
                if (mInstance == null){
                    mInstance = new ImageLoader(threadCount, type);
                }
            }
        }
        return mInstance;
    }

    public void loadImage(final String path, final ImageView imageView){
        if (mUIHandler == null){
            mUIHandler = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    //加载图片
                    ImgBeanHolder holder = (ImgBeanHolder) msg.obj;
                    Bitmap bm = holder.bitmap;
                    ImageView imageView = holder.imageView;
                    String path = holder.path;
                    if (imageView.getTag().toString().equals(path)){
                        imageView.setImageBitmap(bm);
                    }
                }
            };
        }
        imageView.setTag(path);
        //从缓存中获取bitmap
        Bitmap bm = getBitmapFromLruCache(path);
        if (bm != null){
            //发送消息
            refreshBitmap(path, imageView, bm);
        }else {
            //task获取bitmap 放到 LruCache中
            addTask(new Runnable() {
                @Override
                public void run() {
                    //加载图片
                    //图片压缩
                    //1.获得图片需要显示的大小
                    ImageSize imageSize = getImageViewSize(imageView);
                    //压缩图片
                    Bitmap bm = decodeSampledBitmapFromPath(path, imageSize.width, imageSize.height);
                    //Bitmap bm = decodeSampledBitmapFromPath(path, 225, 225);
                    //图片加入到缓存
                    addBitmapToLruCache(path, bm);

                    refreshBitmap(path, imageView, bm);

                    mSemaphoreThreadPool.release();
                }
            });
        }
    }

    /**
     * @param path
     * @param imageView
     * @param bm
     */
    private void refreshBitmap(String path, ImageView imageView, Bitmap bm) {
        ImgBeanHolder holder = new ImgBeanHolder();
        //TODO: 整理代码
        int degree = 0;
        ExifInterface exif = null;
        try{
            exif = new ExifInterface(path);
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
        if (exif != null){
            //读取图片中相机方向信息
            int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
            switch (ori){
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
                default:
                    break;
            }
        }

        //进行图片旋转
        if (degree != 0) {
            // 旋转图片
            Matrix m = new Matrix();
            m.postRotate(degree);
            bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                    bm.getHeight(), m, true);
        }

        holder.bitmap = bm;
        holder.imageView = imageView;
        holder.path = path;
        Message msg = Message.obtain();
        msg.obj = holder;
        mUIHandler.sendMessage(msg);
    }

    private void addBitmapToLruCache(String path, Bitmap bm) {
        if (getBitmapFromLruCache(path) == null){
            if (bm != null){
                mLruCache.put(path, bm);
            }
        }
    }

    /**
     * 根据图片需要显示的宽高进行压缩
     * @param path
     * @param width
     * @param height
     * @return
     */
    private Bitmap decodeSampledBitmapFromPath(String path, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        options.inSampleSize = calculateInSampleSize(options, width, height);
        options.inJustDecodeBounds = false;
        Bitmap bm = BitmapFactory.decodeFile(path, options);
        return bm;
    }

    private int calculateInSampleSize(Options options, int reWidth, int reHeight) {
        int width = options.outWidth;
        int height = options.outHeight;
        int inSampleSize = 1;
        if (width > reWidth || height > reHeight){
            //需要压缩
            int widthRadio = Math.round(width * 1.0f / reWidth);
            int heightRadio = Math.round(height * 1.0f / reHeight);

            inSampleSize = Math.max(widthRadio, heightRadio);
        }
        return inSampleSize;
    }


    /**
     * 获取适当的宽和高
     * @param imageView
     * @return
     */
    @SuppressLint("NewApi")
    private ImageSize getImageViewSize(ImageView imageView) {
        ImageSize imageSize = new ImageSize();
        int width = imageView.getWidth();
        ViewGroup.LayoutParams params = imageView.getLayoutParams();

        final DisplayMetrics displayMetrics = imageView.getContext().getResources().getDisplayMetrics();
        if (width <= 0){
            width = params.width;
        }
        if (width <= 0){
            width = imageView.getMaxWidth();
        }
        if (width <= 0){
            //屏幕宽度
            width = displayMetrics.widthPixels;
        }

        int height = imageView.getHeight();
        if (height <= 0){
            height = params.height;
        }
        if (height <= 0){
            height = imageView.getMaxHeight();
        }
        if (height <= 0){
            //屏幕高度
            height = displayMetrics.heightPixels;
        }

        imageSize.width = width;
        imageSize.height = height;

        return imageSize;
    }

    private Runnable getTask() {
        if (mType == Type.FIFO){
            return mTaskQueue.removeFirst();
        }else if (mType == Type.LIFO){
            return mTaskQueue.removeLast();
        }
        return null;
    }

    private void addTask(Runnable runnable){
        mTaskQueue.add(runnable);
        try {
            if (mPoolThreadHandler == null){
                mSemaphorePoolThreadHandler.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mPoolThreadHandler.sendEmptyMessage(0x110);
    }

    private Bitmap getBitmapFromLruCache(String path) {
        return mLruCache.get(path);
    }

    private class ImageSize{
        int width;
        int height;
    }

    private class ImgBeanHolder{
        private Bitmap bitmap;
        private ImageView imageView;
        private String path;
    }
}
