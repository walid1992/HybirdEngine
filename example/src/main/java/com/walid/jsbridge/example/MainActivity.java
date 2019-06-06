package com.walid.jsbridge.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModuleManager;
import com.walid.jsbridge.factory.JSCallData;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private BridgeWebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = findViewById(R.id.webView);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.dispatch("event_test_netChange", "data from Java", new IDispatchCallBack() {
                    @Override
                    public void onCallBack(JSCallData data) {
                        Log.i(TAG, "reponse data from js " + data.getData());
                    }
                });
            }
        });

        BridgeModuleManager.registerModule(webView, TestModule.class);

        webView.loadUrl("file:///android_asset/demo.html");
    }

}
