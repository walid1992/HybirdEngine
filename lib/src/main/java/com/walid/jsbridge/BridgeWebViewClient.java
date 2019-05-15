package com.walid.jsbridge;

import android.graphics.Bitmap;

import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Author : walid
 * Data : 2017-06-01  15:05
 * Describe :
 */
public class BridgeWebViewClient extends WebViewClient {

    private BridgeWebView webView;

    public BridgeWebViewClient(BridgeWebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // if return data
        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) {
            webView.handleJsMessageData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) {
            webView.queryJsMessageQueue();
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.LOCAL_JSFile);
        if (webView.getStartupMsgs() != null) {
            for (Message m : webView.getStartupMsgs()) {
                webView.dispatchMessage(m);
            }
            webView.setStartupMsgs(null);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }
}