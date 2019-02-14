package com.deray.meditation.utils.glide;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.SizeReadyCallback;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.target.ViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.deray.meditation.R;

/**
 * Created by Administrator on 2018/9/20.
 */

public class GlideHelp {


    public interface OnAsBitmap {
        void asBitmap(Bitmap bitmap);
    }

    public void asBitMap(final ImageView imageView, final String url, final OnAsBitmap onAsBitmap) {
        System.out.println("url : "+url);
        Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        onAsBitmap.asBitmap(resource);
                    }
                });
        /*Glide.with(imageView.getContext())
                .asBitmap()
                .load(url)
                .into(new Target<Bitmap>() {
                    @Override
                    public void onLoadStarted(@Nullable Drawable placeholder) {
                        *//*BitmapDrawable drawable = (BitmapDrawable) placeholder;
                        if (drawable != null){
                            if (drawable.getBitmap() != null){
                                onAsBitmap.asBitmap(drawable.getBitmap());
                            }
                        }*//*
                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        Bitmap bitmap = BitmapFactory.decodeResource(imageView.getResources(), R.mipmap.icon_default);
                        onAsBitmap.asBitmap(bitmap);
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        onAsBitmap.asBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        System.out.println("onLoadCleared");
                    }

                    @Override
                    public void getSize(@NonNull SizeReadyCallback cb) {
                    }

                    @Override
                    public void removeCallback(@NonNull SizeReadyCallback cb) {
                    }

                    @Override
                    public void setRequest(@Nullable Request request) {
                    }

                    @Nullable
                    @Override
                    public Request getRequest() {
                        return null;
                    }

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onStop() {
                    }

                    @Override
                    public void onDestroy() {
                    }
                });*/
    }
}
