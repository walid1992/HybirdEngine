package com.walid.webview.cache.adapter;

import android.os.Build;

import androidx.annotation.RequiresApi;

import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import java.io.InputStream;
import java.util.Map;

/**
 * Created by yale on 2018/7/26.
 */
public class WebResourceResponseAdapter extends WebResourceResponse {

    private android.webkit.WebResourceResponse webResourceResponse;

    private WebResourceResponseAdapter(android.webkit.WebResourceResponse webResourceResponse) {
        this.webResourceResponse = webResourceResponse;
    }

    public static WebResourceResponseAdapter adapter(android.webkit.WebResourceResponse webResourceResponse) {
        if (webResourceResponse == null) {
            return null;
        }
        return new WebResourceResponseAdapter(webResourceResponse);
    }

    @Override
    public String getMimeType() {
        return webResourceResponse.getMimeType();
    }

    @Override
    public InputStream getData() {
        return webResourceResponse.getData();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int getStatusCode() {
        return webResourceResponse.getStatusCode();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public Map<String, String> getResponseHeaders() {
        return webResourceResponse.getResponseHeaders();
    }

    @Override
    public String getEncoding() {
        return webResourceResponse.getEncoding();
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public String getReasonPhrase() {
        return webResourceResponse.getReasonPhrase();
    }
}
