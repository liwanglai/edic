package com.ochess.edict.util;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.ochess.edict.R;

public class ScreenUtil {
    public static void setFullScreen(Activity activity) {
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_FULLSCREEN ;
        activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            activity.getWindow().getAttributes().layoutInDisplayCutoutMode =
                    WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;

            activity.getWindow().getDecorView().setBackgroundColor(activity.getResources().getColor(R.color.bg));
        }
//        setStatusBarFullTransparent(activity.getWindow());
//        setFull(activity);

    }

    public static void setFull(Activity act){
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            act.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        } else {
            act.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
        //act.requestWindowFeature(Window.FEATURE_NO_TITLE);


    }
    public static void afterFull(Activity act){
        View decorView = act.getWindow().getDecorView();

        // 隐藏状态栏和导航栏
        int options = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        decorView.setSystemUiVisibility(options);
    }
    public static void setStatusBarFullTransparent(Window window) {
//        Window window = getWindow();
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }
    public static final int FULL_WIDTH = 370;
    public static final int LAND_LINE_HEIGHT = 4;
    public static final int POTRIT_LINE_HEIGHT = 50;
    public static final int POTRIT_LINE_WIDTH = 4;
    public static final Bitmap getBitmapLine(int width,int height) {
        //int w = UiUtil.dip2px(64), h = UiUtil.dip2px(72);
        final Bitmap mbmpTest = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
        Canvas canvas = new Canvas(mbmpTest);
        canvas.drawColor(Color.BLACK);
//        Paint p = new Paint();
//        String familyName = "宋体";
//        Typeface font = Typeface.create(familyName, Typeface.BOLD);
//        p.setColor(Color.RED);
//        p.setTypeface(font);
//        p.setTextSize(22);
//        canvas.drawText(mstrTitle, 0, 100, p);
//
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(), R.mipmap.public_back);
//        Bitmap bitmap2 = BitmapFactory.decodeResource(getResources(), R.mipmap.public_audio_introduce3);
//        Bitmap bitmap3 = BitmapFactory.decodeResource(getResources(), R.mipmap.public_praise_s);
//        canvas.drawBitmap(bitmap1, 0, 0, p);
//        canvas.drawBitmap(bitmap2, 30, 30, p);
//        canvas.drawBitmap(bitmap3, 60, 60, p);
//        bitmap1.recycle();
//        bitmap2.recycle();
//        bitmap3.recycle();
//        bitmap1=null;
//        bitmap2=null;
//        bitmap3=null;
        return mbmpTest;
    }
}
