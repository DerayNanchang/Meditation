package com.deray.meditation.utils.log;

import android.content.Context;
import android.support.annotation.RawRes;
import android.support.annotation.StringRes;

public class Toast {

    private Context context;

    private Toast() {
    }

    private static class getInstance {
        private static Toast toast = new Toast();
    }

    public static Toast get(){
        return getInstance.toast;
    }

    public void init(Context context){
       this.context = context;
    }

    public void show(String msg){
        android.widget.Toast.makeText(context,msg, android.widget.Toast.LENGTH_SHORT).show();
    }
    public void show(@StringRes int resId){
        android.widget.Toast.makeText(context,resId, android.widget.Toast.LENGTH_SHORT).show();
    }
}
