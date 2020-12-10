package com.walid.jsbridge.example;

import android.content.Intent;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.util.HashMap;

/**
 * Author   : walid
 * Date     : 2017-09-18  16:06
 * Describe :
 */
@JSMoudle(name = "router")
public class RouterModule extends BridgeModule {

    @JSMethod(alias = "navigateTo", sync = false)
    public String navigateTo(BridgeWebView webView, HashMap<String, Object> map, IDispatchCallBack function) {
        Intent intent = new Intent(webView.getContext(), MainActivity.class);
        webView.getContext().startActivity(intent);
        return "3.10.20";
    }

}
