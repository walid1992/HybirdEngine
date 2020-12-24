package com.walid.cache;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.webkit.URLUtil;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;

import com.walid.cache.config.CacheExtensionConfig;
import com.walid.cache.utils.FileUtil;
import com.walid.cache.utils.MimeTypeMapUtils;
import com.walid.cache.utils.NetUtils;
import com.walid.cache.utils.OKHttpFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Dns;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Created by yale on 2018/7/13.
 */
public class WebViewCacheInterceptor implements WebViewRequestInterceptor {

    private File mCacheFile;
    private long mCacheSize;
    private long mConnectTimeout;
    private long mReadTimeout;
    private CacheExtensionConfig cacheExtensionConfig;
    private Context context;
    private boolean debug;
    private int cacheType;
    private String assetsDir;
    private boolean trustAllHostname;
    private SSLSocketFactory sSLSocketFactory;
    private X509TrustManager x509TrustManager;
    private Dns dns;
    private ResourceInterceptor resourceInterceptor;
    private boolean isSuffixMod;

    //==============
    private OkHttpClient httpClient = null;
    private String mOrigin = "";
    private String mReferer = "";
    private String mUserAgent = "";
    public static final String KEY_CACHE = "WebResourceInterceptor-Key-Cache";


    public WebViewCacheInterceptor(Builder builder) {

        this.cacheExtensionConfig = builder.mCacheExtensionConfig;
        this.mCacheFile = builder.cacheFile;
        this.mCacheSize = builder.mCacheSize;
        this.cacheType = builder.cacheType;
        this.mConnectTimeout = builder.mConnectTimeout;
        this.mReadTimeout = builder.mReadTimeout;
        this.context = builder.mContext;
        this.debug = builder.mDebug;
        this.assetsDir = builder.mAssetsDir;
        this.x509TrustManager = builder.x509TrustManager;
        this.sSLSocketFactory = builder.sSLSocketFactory;
        this.trustAllHostname = builder.trustAllHostname;
        this.resourceInterceptor = builder.resourceInterceptor;
        this.isSuffixMod = builder.isSuffixMod;
        this.dns = builder.dns;

        initHttpClient();
        if (isEnableAssets()) {
            initAssetsLoader();
        }
    }

    private boolean isEnableAssets() {
        return assetsDir != null;
    }

    private void initAssetsLoader() {
        AssetsLoader.getInstance().init(context).setDir(assetsDir).isAssetsSuffixMod(isSuffixMod);
    }

    private void initHttpClient() {

        final Cache cache = new Cache(mCacheFile, mCacheSize);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .cache(cache)
                .connectTimeout(mConnectTimeout, TimeUnit.SECONDS)
                .readTimeout(mReadTimeout, TimeUnit.SECONDS)
                .addNetworkInterceptor(new HttpCacheInterceptor());
        if (trustAllHostname) {
            builder.hostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });
        }
        if (sSLSocketFactory != null && x509TrustManager != null) {
            builder.sslSocketFactory(sSLSocketFactory, x509TrustManager);
        }
        if (dns != null) {
            builder.dns(dns);
        }
        httpClient = builder.build();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public WebResourceResponse interceptRequest(WebResourceRequest request) {
        return interceptRequest(request.getUrl().toString(), request.getRequestHeaders());
    }

    private Map<String, String> buildHeaders() {

        Map<String, String> headers = new HashMap<String, String>();
        if (!TextUtils.isEmpty(mOrigin)) {
            headers.put("Origin", mOrigin);
        }
        if (!TextUtils.isEmpty(mReferer)) {
            headers.put("Referer", mReferer);
        }
        if (!TextUtils.isEmpty(mUserAgent)) {
            headers.put("User-Agent", mUserAgent);
        }
        return headers;
    }

    @Override
    public WebResourceResponse interceptRequest(String url) {
        return interceptRequest(url, buildHeaders());
    }

    private boolean checkUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return false;
        }
        // okhttp only deal with http[s]
        if (!url.startsWith("http")) {
            return false;
        }

        if (resourceInterceptor != null && !resourceInterceptor.interceptor(url)) {
            return false;
        }

        String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);

        // TODO: 空不需要排除
//        if (TextUtils.isEmpty(extension)) {
//            return false;
//        }

        if (cacheExtensionConfig.isMedia(extension)) {
            return false;
        }

        if (!cacheExtensionConfig.canCache(extension)) {
            return false;
        }

        return true;
    }

    @Override
    public void loadUrl(WebView webView, String url) {
        if (!isValidUrl(url)) {
            return;
        }
        webView.loadUrl(url);
        mReferer = webView.getUrl();
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = webView.getSettings().getUserAgentString();
    }

    @Override
    public void loadUrl(String url, String userAgent) {
        if (!isValidUrl(url)) {
            return;
        }
        mReferer = url;
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = userAgent;
    }

    @Override
    public void loadUrl(String url, Map<String, String> additionalHttpHeaders, String userAgent) {
        if (!isValidUrl(url)) {
            return;
        }
        mReferer = url;
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = userAgent;
    }

    @Override
    public void loadUrl(WebView webView, String url, Map<String, String> additionalHttpHeaders) {
        if (!isValidUrl(url)) {
            return;
        }
        webView.loadUrl(url, additionalHttpHeaders);
        mReferer = webView.getUrl();
        mOrigin = NetUtils.getOriginUrl(mReferer);
        mUserAgent = webView.getSettings().getUserAgentString();
    }

    @Override
    public void clearCache() {
        FileUtil.deleteDirs(mCacheFile.getAbsolutePath(), false);
        AssetsLoader.getInstance().clear();

    }

    @Override
    public void enableForce(boolean force) {
        if (force) {
            cacheType = CacheType.FORCE;
        } else {
            cacheType = CacheType.NORMAL;
        }
    }

    @Override
    public InputStream getCacheFile(String url) {
        return OKHttpFile.getCacheFile(mCacheFile, url);
    }

    @Override
    public void initAssetsData() {
        AssetsLoader.getInstance().initData();
    }


    @Override
    public File getCachePath() {
        return mCacheFile;
    }

    public void addHeader(Request.Builder reqBuilder, Map<String, String> headers) {

        if (headers == null) {
            return;
        }
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            reqBuilder.addHeader(entry.getKey(), entry.getValue());
        }
    }

    private WebResourceResponse interceptRequest(String url, Map<String, String> headers) {

        if (cacheType == CacheType.NORMAL) {
            return null;
        }
        if (!checkUrl(url)) {
            return null;
        }

        url = url.split("#")[0];

        if (isEnableAssets()) {
            InputStream inputStream = AssetsLoader.getInstance().getResByUrl(url);
            if (inputStream != null) {
                CacheWebViewLog.d(String.format("from assets: %s", url), debug);
                String mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
                return new WebResourceResponse(mimeType, "", inputStream);
            }
        }

        try {

            Request.Builder reqBuilder = new Request.Builder()
                    .url(url);

            String extension = MimeTypeMapUtils.getFileExtensionFromUrl(url);

            if (cacheExtensionConfig.isHtml(extension)) {
                headers.put(KEY_CACHE, String.valueOf(cacheType));
            }
            addHeader(reqBuilder, headers);

            if (!NetUtils.isConnected(context)) {
                reqBuilder.cacheControl(CacheControl.FORCE_CACHE);
            }
            Request request = reqBuilder.get().build();
            Response response = httpClient.newCall(request).execute();
            Response cacheRes = response.cacheResponse();
            if (cacheRes != null) {
                CacheWebViewLog.d(String.format("from cache: %s", url), debug);
            } else {
                CacheWebViewLog.d(String.format("from server: %s", url), debug);
            }

            // TODO： 入口默认 html，兼容字符乱码问题
            String mimeType;
            if (!TextUtils.isEmpty(response.header("Content-Type"))) {
                mimeType = response.header("Content-Type").split(";")[0];
            } else if (TextUtils.isEmpty(MimeTypeMapUtils.getFileExtensionFromUrl(url))) {
                mimeType = MimeTypeMapUtils.getMimeTypeFromExtension("html");
            } else {
                mimeType = MimeTypeMapUtils.getMimeTypeFromUrl(url);
            }

            WebResourceResponse webResourceResponse = new WebResourceResponse(mimeType, "", response.body().byteStream());
            if (response.code() == 504 && !NetUtils.isConnected(context)) {
                return null;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String message = response.message();
                if (TextUtils.isEmpty(message)) {
                    message = "OK";
                }
                try {
                    webResourceResponse.setStatusCodeAndReasonPhrase(response.code(), message);
                } catch (Exception e) {
                    return null;
                }
                webResourceResponse.setResponseHeaders(NetUtils.multimapToSingle(response.headers().toMultimap()));
            }
            return webResourceResponse;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class Builder {

        private File cacheFile;
        private long mCacheSize = 100 * 1024 * 1024;
        private long mConnectTimeout = 20;
        private long mReadTimeout = 20;
        private CacheExtensionConfig mCacheExtensionConfig;
        private Context mContext;
        private boolean mDebug = true;
        private int cacheType = CacheType.FORCE;

        private boolean trustAllHostname = false;
        private SSLSocketFactory sSLSocketFactory = null;
        private X509TrustManager x509TrustManager = null;
        private ResourceInterceptor resourceInterceptor;

        private String mAssetsDir = null;
        private boolean isSuffixMod = false;
        private Dns dns = null;

        public Builder(Context context) {
            mContext = context;
            cacheFile = new File(context.getExternalCacheDir().toString(), "webviewcache");
            mCacheExtensionConfig = new CacheExtensionConfig();
        }

        public void setResourceInterceptor(ResourceInterceptor resourceInterceptor) {
            resourceInterceptor = resourceInterceptor;
        }

        public Builder setTrustAllHostname() {
            trustAllHostname = true;
            return this;
        }

        public Builder setSSLSocketFactory(SSLSocketFactory sslSocketFactory, X509TrustManager trustManager) {
            if (sslSocketFactory != null && trustManager != null) {
                sSLSocketFactory = sslSocketFactory;
                x509TrustManager = trustManager;
            }
            return this;
        }

        public Builder setCachePath(File file) {
            if (file != null) {
                cacheFile = file;
            }
            return this;
        }

        public Builder setCacheSize(long cacheSize) {
            if (cacheSize > 1024) {
                mCacheSize = cacheSize;
            }
            return this;
        }

        public Builder setReadTimeoutSecond(long time) {
            if (time >= 0) {
                mReadTimeout = time;
            }
            return this;
        }

        public Builder setConnectTimeoutSecond(long time) {
            if (time >= 0) {
                mConnectTimeout = time;
            }

            return this;
        }

        public Builder setCacheExtensionConfig(CacheExtensionConfig config) {
            if (config != null) {
                mCacheExtensionConfig = config;
            }
            return this;
        }

        public Builder setDebug(boolean debug) {
            mDebug = debug;
            return this;
        }

        public Builder setCacheType(@CacheType int cacheType) {
            this.cacheType = cacheType;
            return this;
        }

        public Builder isAssetsSuffixMod(boolean suffixMod) {
            this.isSuffixMod = suffixMod;
            return this;
        }

        public Builder setAssetsDir(String dir) {
            if (dir != null) {
                mAssetsDir = dir;
            }
            return this;
        }

        public void setDns(Dns dns) {
            this.dns = dns;
        }

        public WebViewRequestInterceptor build() {
            return new WebViewCacheInterceptor(this);
        }

    }

    boolean isValidUrl(String url) {
        return URLUtil.isValidUrl(url);
    }
}
