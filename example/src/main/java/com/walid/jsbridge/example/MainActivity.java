package com.walid.jsbridge.example;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.BridgeWebViewClient;
import com.walid.jsbridge.factory.BridgeModuleManager;

public class MainActivity extends AppCompatActivity {

    public static boolean has = false;

    private final String TAG = "MainActivity";
    private BridgeWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermisionUtils.verifyStoragePermissions(this);
        webView = findViewById(R.id.webView);

        Button button = findViewById(R.id.button);


        button.setOnClickListener(v -> {
            webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
            webView.dispatch("action_page_onNavigationRightClick", "data from Java", callData -> {

            });
        });

//        button.setOnClickListener(v -> {
//            webView.loadUrl("http://172.29.23.164:7456");
//        });

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
        webView.setWebViewClient(new BridgeWebViewClient(webView) {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

//        [{
//            "birthday":0, "description":"", "gender":"FEMALE", "inExposure":false, "isBubble":
//            false, "matchvalue":0.9319956, "signature":"倚楼听风雨，淡看江湖路", "userId":"-1", "userIdEcpt":
//            "VkZ0R3NKZElJRVBWVlgvZUh1NEExdz09"
//        }]

//        webView.loadUrl("http://172.29.23.164:7456");

//        webView.dispatch("planet_set_data", "[{\n" +
//                "            \"birthday\":0, \"description\":\"\", \"gender\":\"FEMALE\", \"inExposure\":false, \"isBubble\":\n" +
//                "            \"VkZ0R3NKZElJRVBWVlgvZUh1NEExdz09\"\n" +
//                "        }]", null);

//        webView.loadUrl("https://www.baidu.com");

        webView.loadUrl("file:///android_asset/demo.html");

//        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
        webView.dispatch("action_page_onNavigationRightClick", "data from Java", null);
//
//        webView.postDelayed(() -> webView.dispatch("action_page_onNavigationRightClick", "data from Java", null), 200);

//        webView.postDelayed(() -> webView.dispatch("action_page_onNavigationRightClick", "data from Java", null), 200);

//        webView.loadUrl("file:///android_asset/web-mobile/index.html?userId=1165302&roomId=1000022");

//        if (new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/browser/web-mobile/web-mobile/index.html").exists()) {
//
//        }
//
//        webView.loadUrl("file:///" + Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/browser/web-mobile/web-mobile/index.html");

//        if (!has) {
//            webView.loadUrl("http://172.29.22.144:8081/#/coin/bonus?debug=true");
//            has = true;
//        }
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
