package com.walid.cache;

import android.annotation.TargetApi;
import android.os.Build;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import java.io.File;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by yale on 2018/7/16.
 */
public class WebViewCacheInterceptorInst implements WebViewRequestInterceptor {

    private static volatile WebViewCacheInterceptorInst webViewCacheInterceptorInst;

    private WebViewRequestInterceptor interceptor;

    public void init(WebViewCacheInterceptor.Builder builder) {
        if (builder != null) {
            interceptor = builder.build();
        }
    }

    public static WebViewCacheInterceptorInst getInstance() {
        if (webViewCacheInterceptorInst == null) {
            synchronized (WebViewCacheInterceptorInst.class) {
                if (webViewCacheInterceptorInst == null) {
                    webViewCacheInterceptorInst = new WebViewCacheInterceptorInst();
                }
            }
        }
        return webViewCacheInterceptorInst;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse interceptRequest(WebResourceRequest request) {
        if (interceptor == null) {
            return null;
        }
        return interceptor.interceptRequest(request);
    }

    @Override
    public WebResourceResponse interceptRequest(String url) {
        if (interceptor == null) {
            return null;
        }
        return interceptor.interceptRequest(url);
    }

    @Override
    public void loadUrl(WebView webView, String url) {
        if (interceptor == null) {
            return;
        }
        interceptor.loadUrl(webView, url);
    }

    @Override
    public void loadUrl(String url, String userAgent) {
        if (interceptor == null) {
            return;
        }
        interceptor.loadUrl(url, userAgent);
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders, String userAgent) {
        if (interceptor == null) {
            return;
        }
        interceptor.loadUrl(url, additionalHttpHeaders, userAgent);
    }

    @Override
    public void loadUrl(WebView webView, String url, Map<String, String> additionalHttpHeaders) {
        if (interceptor == null) {
            return;
        }
        interceptor.loadUrl(webView, url, additionalHttpHeaders);
    }

    @Override
    public void clearCache() {
        if (interceptor == null) {
            return;
        }
        interceptor.clearCache();
    }

    @Override
    public void enableForce(boolean force) {
        if (interceptor == null) {
            return;
        }
        interceptor.enableForce(force);
    }

    @Override
    public InputStream getCacheFile(String url) {
        if (interceptor == null) {
            return null;
        }
        return interceptor.getCacheFile(url);
    }

    @Override
    public void initAssetsData() {
        AssetsLoader.getInstance().initData();
    }

    @Override
    public File getCachePath() {
        if (interceptor == null) {
            return null;
        }
        return interceptor.getCachePath();
    }

}
