package com.walid.jsbridge.example;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.BridgeWebViewClient;
import com.walid.jsbridge.SMinaEngine;
import com.walid.jsbridge.factory.BridgeModuleManager;

public class MainActivity extends AppCompatActivity {

    public static boolean has = false;

    private final String TAG = "MainActivity";
    private BridgeWebView webView;

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        PermisionUtils.verifyStoragePermissions(this);
        webView = findViewById(R.id.webView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(v -> {
            webView.loadUrl("http://www.baidu.com");
//            webView.loadUrl("https://w3.soulsmile.cn/activity/#/voice?postIdEcpt=dGJ5eDVEc1dVdWhpZEErQUVyYitWQT09");
//            webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//            webView.dispatch("action_page_onNavigationRightClick", "data from Java", callData -> {
//
//            });
        });

        BridgeModuleManager.registerModule(webView, new TestModule());

//        String scbs = "";
//        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//        ClipData data = cm.getPrimaryClip();
//        if (data != null) {
//            int count = data.getItemCount();
//            for (int i = 0; i < count; i++) {
//                ClipData.Item item = data.getItemAt(i);
//                if (item == null) continue;
//                String text = (String) item.getText();
//                if (!TextUtils.isEmpty(text) && text.startsWith("scbs=")) {
//                    scbs = text.replaceFirst("scbs=", "");
//                    break;
//                }
//            }
//        }
//        webView.loadUrl("http://172.29.90.163:62143/?scbs=" + scbs);
//        webView.getSettings().setJavaScriptEnabled(true);


        // 解决 XMLHttpRequest cannot load file from android asset folder
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setPluginState(WebSettings.PluginState.ON);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setAllowFileAccessFromFileURLs(true);
        ws.setAllowUniversalAccessFromFileURLs(true);
        BridgeModuleManager.registerModule(webView, new PlanetModule());
        BridgeModuleManager.registerModule(webView, new RouterModule());

        Log.e("TAG", SMinaEngine.getArtifactId());
        webView.setWebViewClient(new BridgeWebViewClient(webView) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

//        webView.loadUrl("file:///android_asset/demo.html");
//        webView.loadUrl("https://www.baidu.com");

        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        webView.destroy();
    }

}
