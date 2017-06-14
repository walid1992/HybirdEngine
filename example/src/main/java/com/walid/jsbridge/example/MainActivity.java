package com.walid.jsbridge.example;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.google.gson.Gson;
import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IBridgeHandler;
import com.walid.jsbridge.ICallBackFunction;

public class MainActivity extends Activity {

    private final String TAG = "MainActivity";
    private BridgeWebView webView;

    private static class Location {
        String address;
    }

    private static class User {
        String name;
        Location location;
        String testStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = (BridgeWebView) findViewById(R.id.webView);
        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.dispatch("functionInJs", "data from Java", new ICallBackFunction() {
                    @Override
                    public void onCallBack(String data) {
                        Log.i(TAG, "reponse data from js " + data);
                    }
                });
            }
        });

        webView.loadUrl("file:///android_asset/demo.html");

        webView.register("submitFromWeb", new IBridgeHandler() {
            @Override
            public void handler(String data, ICallBackFunction function) {
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }
        });

        User user = new User();
        Location location = new Location();
        location.address = "SDU";
        user.location = location;
        user.name = "大头鬼";
        webView.dispatch("functionInJs", new Gson().toJson(user), new ICallBackFunction() {
            @Override
            public void onCallBack(String data) {
                Log.d(TAG, "handler = functionInJs, data from web = " + data);
            }
        });
    }

}
