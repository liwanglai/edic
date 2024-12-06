package com.ochess.edict.print;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.ochess.edict.R;

import java.io.IOException;
import java.io.InputStream;

public class PrintUtils {

    private LinearLayout container;

    public static View createHeadView(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.view_print_head_layout, null);
    }

    public static LinearLayout createContainerView(Context context) {
        return (LinearLayout) LayoutInflater.from(context).inflate(R.layout.view_print_container, null);
    }

    public static View createWordItem(Context context, String word, String ch) {
        return LayoutInflater.from(context).inflate(R.layout.view_print_unit_layout, null);
    }

    public static View createDivideItem(Context context) {
        return LayoutInflater.from(context).inflate(R.layout.view_print_unit_divider_layout, null);
    }

    public static View layoutView(View v, int width, int height) {
        // 整个View的大小 参数是左上角 和右下角的坐标
        v.layout(0, 0, width, height);
        int measuredWidth = View.MeasureSpec.makeMeasureSpec(width, View.MeasureSpec.EXACTLY);
        int measuredHeight = View.MeasureSpec.makeMeasureSpec(10000, View.MeasureSpec.AT_MOST);
        v.measure(measuredWidth, measuredHeight);
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        return v;
    }
    public static int px2dip(Context context,float pxValue) {
            float scale = context.getResources().getDisplayMetrics().density;
            return (int)(pxValue/scale+0.5f);
    }
    public static Bitmap getCustomViewBitmap(View view) {
        // 创建一个和自定义View相同大小的空Bitmap
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        // 将自定义View的内容绘制到Bitmap上
        view.draw(canvas);

//        view.setDrawingCacheEnabled(true);
//        view.buildDrawingCache();
//        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public static Bitmap changeBitmapTo1Pix(Bitmap bitmap) {
        Bitmap bwBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
        float[] hsv = new float[3];
        for (int col = 0; col < bitmap.getWidth(); col++) {
            for (int row = 0; row < bitmap.getHeight(); row++) {
                Color.colorToHSV(bitmap.getPixel(col, row), hsv);
                if (hsv[2] > 0.5f) {
                    bwBitmap.setPixel(col, row, 0xffffffff);
                } else {
                    bwBitmap.setPixel(col, row, 0xff000000);
                }
            }
        }
        return bwBitmap;
    }

    public static byte[] POS_PrintPicture(Bitmap mBitmap, int nWidth, int nMode) {
        int width = (nWidth + 7) / 8 * 8;
        int height = mBitmap.getHeight() * width / mBitmap.getWidth();
        height = (height + 7) / 8 * 8;
        Bitmap rszBitmap = ImageProcessing.resizeImage(mBitmap, width, height);
        Bitmap grayBitmap = ImageProcessing.toGrayscale(rszBitmap);
        byte[] dithered = ImageProcessing.bitmapToBWPix(grayBitmap);
        return ImageProcessing.eachLinePixToCmd(dithered, width, nMode);
    }

    /**
     * 从Assets中读取图片
     */
    public static Bitmap getImageFromAssetsFile(Context context,String fileName) {
        Bitmap image = null;
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;

    }
}
