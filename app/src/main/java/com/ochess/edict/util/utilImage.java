package com.ochess.edict.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.util.Log;

import androidx.compose.ui.graphics.ImageBitmap;

import com.ochess.edict.MainActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileOutputStream;

public class utilImage {

    /**
     *
     * @param bitMap
     * @param with  宽度
     * @return
     */
    public static Bitmap thumbnail(Bitmap bitMap,int with) {
        try {
            float ss = Float.valueOf(with)/bitMap.getWidth();
            Matrix matrix = new Matrix();
            //matrix.postRotate(rd);      //旋转
            matrix.postScale(ss,ss);
            bitMap = Bitmap.createBitmap(bitMap, 0,0, bitMap.getWidth(),bitMap.getHeight(), matrix, true);
            return bitMap;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static Bitmap cut(Bitmap bitMap,int with,int x,int y,int w,int h) {
        try {
            float ss = Float.valueOf(with)/bitMap.getWidth();
            Matrix matrix = new Matrix();
            //matrix.postRotate(rd);      //旋转
            matrix.postScale(ss,ss);
            bitMap = Bitmap.createBitmap(bitMap, x,y,w,h, matrix, true);
            return bitMap;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }
    public static Bitmap fixSerialImg(Bitmap bitMap,float ss) {
        try {
            ss = ss/bitMap.getWidth();
            Matrix matrix = new Matrix();
            //matrix.postRotate(rd);      //旋转
            matrix.postScale(ss,ss);

            int x=0,y=0, w = bitMap.getWidth(),h=bitMap.getHeight();
            Bitmap dimg = Bitmap.createBitmap(w*2,h*2,Bitmap.Config.RGB_565);

            int[] pixels = new int[w*h];
            bitMap.getPixels(pixels,0,w,0,0,w,h);
            dimg.setPixels(pixels,0,w, x,y,w, 50);
            dimg.setPixels(pixels,0,w, w,y,w, 50);
            y=50;
            dimg.setPixels(pixels,0,w, x,y,w,h);
            dimg.setPixels(pixels,0,w, w,y,w,h);

            x = (int) (268/ss);   //276
            bitMap = Bitmap.createBitmap(dimg, x, 0, bitMap.getWidth(),bitMap.getHeight()+50, matrix, true);
            return bitMap;
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static void bitmap2jpg(Bitmap bimg, String newFile)
    {
        if(bimg == null) return;
        File file = new File(newFile);
        File fDir=file.getParentFile();
        if(!fDir.exists()) {
            fDir.mkdirs();
        }
        try {
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);
            bimg.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            bimg.recycle();
        }catch (Exception e){
            e.printStackTrace();
            Log.e("kdebug", "bitmap2jpg: "+e.getMessage());
        }
    }

    public static String rootDir(){
        String _rootDir="";
            Context context = ActivityRun.context;
            File root = context.getExternalFilesDir(null);
            _rootDir = root.getPath();
        return _rootDir;
    }
}
