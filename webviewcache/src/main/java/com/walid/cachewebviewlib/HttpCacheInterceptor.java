package com.walid.cachewebviewlib;

import android.text.TextUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by yale on 2018/7/13.
 */
class HttpCacheInterceptor implements Interceptor {

    private int min15 = 60 * 15;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);
        String cache = request.header(WebViewCacheInterceptor.KEY_CACHE);
        if (!TextUtils.isEmpty(cache) && cache.equals(String.valueOf(CacheType.NORMAL))) {
            return originResponse;
        }

        CacheControl cacheControl = originResponse.cacheControl();
        int maxAgeSeconds = cacheControl.maxAgeSeconds();
        if (maxAgeSeconds <= min15) {
            cacheControl = new CacheControl.Builder()
                    .maxAge(min15, TimeUnit.SECONDS)
                    .build();
        }

        CacheWebViewLog.d(cacheControl.toString());

        return originResponse.newBuilder()
                .removeHeader("pragma")
                .removeHeader("Cache-Control")
                // cache for 30 days 3600 * 24 * 30
                .header("Cache-Control", cacheControl.toString())
                .build();
    }

}
