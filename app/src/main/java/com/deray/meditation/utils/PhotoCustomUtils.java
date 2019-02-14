package com.deray.meditation.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.deray.meditation.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PhotoCustomUtils {

    /**
     *  高斯模糊图片
     * @param context
     * @param source
     * @param radius
     * @return
     */
    private static Bitmap rsBlur(Context context, Bitmap source, int radius){

        Bitmap inputBmp = source;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            //(1)
            RenderScript renderScript =  RenderScript.create(context);

            // Allocate memory for Renderscript to work with
            //(2)
            final Allocation input = Allocation.createFromBitmap(renderScript,inputBmp);
            final Allocation output = Allocation.createTyped(renderScript,input.getType());
            //(3)
            // Load up an instance of the specific script that we want to use.
            ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
            //(4)
            scriptIntrinsicBlur.setInput(input);
            //(5)
            // Set the blur radius
            scriptIntrinsicBlur.setRadius(radius);
            //(6)
            // Start the ScriptIntrinisicBlur
            scriptIntrinsicBlur.forEach(output);
            //(7)
            // Copy the output to the blurred bitmap
            output.copyTo(inputBmp);
            //(8)
            renderScript.destroy();

            return inputBmp;
        }else {
            return null;
        }
    }

}
