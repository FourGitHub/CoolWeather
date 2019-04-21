package com.fourweather.learn.View;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.fourweather.learn.entity.CityWeaInfo;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.Callable;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

/**
 * Create on 2019/04/17
 *
 * @author Four
 * @description
 */
public abstract class PagerFrag extends Fragment {

    abstract CardView getCard();

    public static CityWeaInfo mCityWeaInfo;

    public static PagerFrag getInstance(int pos) {
        PagerFrag frag = null;
        switch (pos) {
            case 0:
                frag = new PagerLeftFrag();
                break;
            case 1:
                frag = new PagerRightFrg();
                break;
        }
        return frag;
    }

    static Bitmap getViewBitmap(View view) {
        view.clearFocus();
        view.setPressed(false);
        boolean willNotCache = view.willNotCacheDrawing();
        view.setWillNotCacheDrawing(false);
        int color = view.getDrawingCacheBackgroundColor();
        view.setDrawingCacheBackgroundColor(0);
        if (color != 0) {
            view.destroyDrawingCache();
        }
        view.buildDrawingCache();
        Bitmap cacheBitmap = view.getDrawingCache();

        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);
        view.destroyDrawingCache();
        view.setWillNotCacheDrawing(willNotCache);
        view.setDrawingCacheBackgroundColor(color);
        return bitmap;
    }

    static void saveBitmapToSDCard(onSavedToSDCardListener onSavedToSDCardListener, Bitmap bitmap, String path, String bitmapFilename) {
        // 耗时I/O操作创建新线程执行
        new Thread(new SavaPicToSDCardTask(onSavedToSDCardListener, bitmap, path, bitmapFilename)).start();
    }

    private static boolean checkSDCardAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /* 封装一个保存图片至SD卡的任务*/
    static class SavaPicToSDCardTask implements Runnable {
        private onSavedToSDCardListener onSavedToSDCardListener;
        private Bitmap bitmap;
        private String path;
        private String bitmapFilename;

        SavaPicToSDCardTask(onSavedToSDCardListener onSavedToSDCardListener,Bitmap bitmap, String path, String bitmapFilename) {
            this.onSavedToSDCardListener = onSavedToSDCardListener;
            this.bitmap = bitmap;
            this.path = path;
            this.bitmapFilename = bitmapFilename;
        }

        @Override
        public void run() {

            if ((bitmap == null || path == null || bitmapFilename == null) && onSavedToSDCardListener != null) {
                onSavedToSDCardListener.onFailed();
            }

            File bitmapFile;
            if (checkSDCardAvailable()) {
                File dir = new File(path);

                if (!dir.exists()) {
                    dir.mkdirs();
                }

                bitmapFile = new File(path, bitmapFilename);
                try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(bitmapFile))) {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
                    bos.flush();
                } catch (FileNotFoundException e) {
                    bitmapFile.delete();
                    e.printStackTrace();
                } catch (IOException e) {
                    bitmapFile.delete();
                    e.printStackTrace();
                }
            }
            if (onSavedToSDCardListener != null) {
                onSavedToSDCardListener.onSuccess();
            }
        }
    }

    interface onSavedToSDCardListener{
        void onSuccess();
        void onFailed();
    }

}

