package com.walid.jsbridge.example;

import android.util.Log;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.util.Map;

/**
 * Author   : walid
 * Date     : 2019-08-16  16:24
 * Describe :
 */
@JSMoudle(name = "planet")
public class PlanetModule extends BridgeModule {

    @JSMethod(alias = "onClick")
    public void onClick(BridgeWebView webView, Map<String, Object> map, IDispatchCallBack function){
        Log.e("planet",map.toString());
    }

}
