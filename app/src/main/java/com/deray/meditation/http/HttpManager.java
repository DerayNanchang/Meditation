package com.deray.meditation.http;


import com.deray.meditation.http.fastjson.FastJsonConverterFactory;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

public class HttpManager {

    private HttpManager() {
    }

    private static class Instance{
        private static HttpManager manager = new HttpManager();
    }

    /*public HttpManager get(){
        return Instance.manager;
    }*/

    public interface Keys{
        String ML_WEI_KEY = "523077333";
        String MLWEI_URL = "https://api.mlwei.com";
    }

    public static HttpManager request(){
        return Instance.manager;
    }

    public  <T> T get(Class<T> clazz){
        return getDefaultManager().create(clazz);
    }

    private Retrofit getDefaultManager() {
        return setBaseUrl(Keys.MLWEI_URL)
                .addConverterFactory(FastJsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    private Retrofit.Builder getBaseRetrofitBuilder() {
        return new Retrofit.Builder();
    }

    private Retrofit.Builder setBaseUrl(String baseUrl) {
        return getBaseRetrofitBuilder().baseUrl(baseUrl);
    }

}
