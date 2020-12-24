package com.walid.cache;

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

    private int day30 = 3600 * 24 * 30;

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response originResponse = chain.proceed(request);
        String cache = request.header(WebViewCacheInterceptor.KEY_CACHE);
        if (!TextUtils.isEmpty(cache) && cache.equals(String.valueOf(CacheType.NORMAL))) {
            return originResponse;
        }

        // text/html;charset=utf-8
        int maxAge = 300;
        String contentType = originResponse.header("Content-Type");
        if (TextUtils.isEmpty(contentType) || "text/html".equals(contentType.split(";")[0])) {
            maxAge = day30;
        }

        CacheControl cacheControl = originResponse.cacheControl();
        int maxAgeSeconds = cacheControl.maxAgeSeconds();
        if (maxAgeSeconds <= maxAge) {
            cacheControl = new CacheControl.Builder()
                    .maxAge(maxAge, TimeUnit.SECONDS)
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
