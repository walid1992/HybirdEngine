package com.walid.webview.cache.adapter;

import android.annotation.TargetApi;
import android.net.Uri;
import android.os.Build;

import java.util.Map;

/**
 * Created by yale on 2018/7/26.
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class WebResourceRequestAdapter implements android.webkit.WebResourceRequest {

    private com.tencent.smtt.export.external.interfaces.WebResourceRequest webResourceRequest;

    private WebResourceRequestAdapter(com.tencent.smtt.export.external.interfaces.WebResourceRequest x5Request) {
        webResourceRequest = x5Request;
    }

    public static WebResourceRequestAdapter adapter(com.tencent.smtt.export.external.interfaces.WebResourceRequest x5Request) {
        return new WebResourceRequestAdapter(x5Request);
    }

    @Override
    public Uri getUrl() {
        return webResourceRequest.getUrl();
    }

    @Override
    public boolean isForMainFrame() {
        return webResourceRequest.isForMainFrame();
    }

    @Override
    public boolean isRedirect() {
        return webResourceRequest.isRedirect();
    }

    @Override
    public boolean hasGesture() {
        return webResourceRequest.hasGesture();
    }

    @Override
    public String getMethod() {
        return webResourceRequest.getMethod();
    }

    @Override
    public Map<String, String> getRequestHeaders() {
        return webResourceRequest.getRequestHeaders();
    }

}
