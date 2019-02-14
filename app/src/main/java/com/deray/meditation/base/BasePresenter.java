package com.deray.meditation.base;

/**
 * Created by Deray on 2017/9/1.
 */

public abstract class BasePresenter {

    // 1. 所有的 presenter 都必须实现 网络请求统一管理的方法，页面销毁其也订阅销毁
    // presenter 统一处理方法
    //public abstract void subscribe();

    // 页面销毁调用次方法 解除 api 订阅
    public abstract void unSubscribe();

}
