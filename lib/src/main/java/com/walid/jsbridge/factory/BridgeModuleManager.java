package com.walid.jsbridge.factory;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walid.jsbridge.BridgeWebView;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:28
 * Describe :
 */
public class BridgeModuleManager {

    /**
     * is valid JSON
     * @param json
     * @return
     */
    private static boolean isJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static <T extends BridgeModule> void registerModule(final BridgeWebView bridgeWebView, final Class<T> wxModuleClass) {
        TypeModuleFactory typeModuleFactory = new TypeModuleFactory<>(wxModuleClass);
        String[] methodNames = typeModuleFactory.getMethods();
        for (String methodName : methodNames) {
            Log.d("BridgeModuleManager", "register" + methodName);
            bridgeWebView.register(methodName, (data, function) -> {
                if (!isJson(data)) {
                    function.onCallBack(new JSCallData(-101, "json parse failed!!!", ""));
                    return;
                }
                Map<String, Object> map = new Gson().fromJson(data, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                try {
                    Log.d("BridgeModuleManager", "register" + methodName);
                    typeModuleFactory.getMethodInvoker(methodName).invoke(wxModuleClass.newInstance(), bridgeWebView, map, function);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

}
