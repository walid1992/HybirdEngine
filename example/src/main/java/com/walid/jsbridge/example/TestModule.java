package com.walid.jsbridge.example;

import android.util.Log;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSCallData;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.util.HashMap;

/**
 * Author   : walid
 * Date     : 2017-09-18  16:06
 * Describe :
 */
@JSMoudle(name = "test")
public class TestModule extends BridgeModule {

    @JSMethod(alias = "doTest")
    public void oauth(BridgeWebView webView, HashMap<String, Object> map, IDispatchCallBack function) {
        Log.d("OauthUtils", map.toString());
        String platform = (String) map.get("platform");
        Log.d("TestModule", platform);
        function.onCallBack(new JSCallData(0, "ok", "请求成功！\n"));
    }

}
