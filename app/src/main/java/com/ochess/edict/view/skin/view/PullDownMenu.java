package com.ochess.edict.view.skin.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class PullDownMenu extends View {
    public PullDownMenu(Context context) {
        super(context);
    }
    public PullDownMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    interface callBackString{
        void run(String s);
    }
    public static void popMenu(View v,@NotNull ArrayList<String> items, callBackString function) {
        SkinView.popMenu(v,items, (Function1<? super String, Unit>) function);
    }


}
