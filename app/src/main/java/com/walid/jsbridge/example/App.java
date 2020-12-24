package com.walid.jsbridge.example;

import android.app.Application;

import com.walid.cache.WebViewCacheInterceptor;
import com.walid.cache.WebViewCacheInterceptorInst;

/**
 * Author   : walid
 * Date     : 2020-12-07  17:26
 * Describe :
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        WebViewCacheInterceptorInst.getInstance().init(new WebViewCacheInterceptor.Builder(this));
    }

}
