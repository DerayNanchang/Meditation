package com.deray.meditation.manage;

import android.content.Context;
import android.content.Intent;

import com.deray.meditation.service.music.MusicService;
import com.deray.meditation.utils.ServiceUtils;

public class ServiceManager {

    private ServiceManager() {

    }

    private static class getInStance{
         private static ServiceManager manager = new ServiceManager();
    }

    public static ServiceManager get(){
        return getInStance.manager;
    }

    public interface ServiceNames{
        String MUSIC_SERVICE = "com.deray.meditation.service.music.MusicService";
    }

    public boolean isServiceExisted(Context context,String clazzName){
       return ServiceUtils.isServiceExisted(context,clazzName);
    }

    public void startService(Context context){
        context.startService(new Intent(context,MusicService.class));
    }

}
