package com.ochess.edict.util.media;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;

import com.ochess.edict.util.ActivityRun;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;

/**
 * 音频
 * @link https://blog.51cto.com/u_16213334/7302622
 */
public class Audio {
    public static MediaPlayer play(String file,MediaPlayer.OnCompletionListener over){
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            File fp = new File(file);
            FileInputStream fis = new FileInputStream(fp);
            FileDescriptor fd = fis.getFD();
            return play(fd,over);
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static MediaPlayer play(@Nullable FileDescriptor fd,MediaPlayer.OnCompletionListener over) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(fd);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(over);
            mediaPlayer.start();
            return mediaPlayer;
        }catch (Exception e){
            e.printStackTrace();
            over.onCompletion(null);
            return null;
        }
        //        mediaPlayer.release();
        //        mediaPlayer = null;
    }

    public static void play(@Nullable Uri uri) {
        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(ActivityRun.context, uri);
            mediaPlayer.prepare();
            mediaPlayer.start();
//            mediaPlayer.setOnCompletionListener()
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void play(int rid) {
        Activity act = (Activity) ActivityRun.context;
        Uri uri = Uri.parse(("android.resource://" + act.getPackageName()) + "/" + rid);
        play(uri);
    }
}
