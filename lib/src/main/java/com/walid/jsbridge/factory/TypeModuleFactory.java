package com.walid.jsbridge.factory;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.walid.jsbridge.BridgeWebView;

import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeModuleFactory<T extends BridgeModule> {

    public static final String TAG = "TypeModuleFactory";
    private BridgeModule module;
    private BridgeWebView bridgeWebView;
    private Map<String, Invoker> methodMap;
    private Map<String, Boolean> syncMap;

    public TypeModuleFactory(BridgeModule module, final BridgeWebView bridgeWebView) {
        this.module = module;
        this.bridgeWebView = bridgeWebView;
    }

    /**
     * is valid JSON
     */
    private static boolean isJson(String json) {
        try {
            new JSONObject(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void register() {
        String[] methods = getMethods();
        for (String jsMethod : methods) {
            Log.d("BridgeModuleManager", "register" + jsMethod);
            BridgeModuleManager.putAPI(jsMethod);
            if (syncMap.get(jsMethod)) {
                BridgeModuleManager.put(jsMethod, this);
                continue;
            }
            bridgeWebView.register(jsMethod, (data, function) -> {
                if (!isJson(data)) {
                    function.onCallBack(new JSCallData(-101, "json parse failed!!!", ""));
                    return;
                }
                Map<String, Object> map = new Gson().fromJson(data, new TypeToken<HashMap<String, Object>>() {
                }.getType());
                try {
                    Log.d("BridgeModuleManager", "register" + jsMethod);
                    getMethodInvoker(jsMethod).invoke(module, bridgeWebView, map, function);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private void generateMethodMap() {
        HashMap<String, Invoker> methodMap = new HashMap<>();
        HashMap<String, Boolean> syncMap = new HashMap<>();
        try {
            JSMoudle jsMoudle = this.module.getClass().getAnnotation(JSMoudle.class);
            Method[] methods = this.module.getClass().getMethods();
            for (Method method : methods) {
                JSMethod jsMethod = method.getAnnotation(JSMethod.class);
                if (jsMethod != null) {
                    String name = "action_" + jsMoudle.name() + "_" + ("_".equals(jsMethod.alias()) ? method.getName() : jsMethod.alias());
                    syncMap.put(name, jsMethod.sync());
                    methodMap.put(name, new MethodInvoker(method));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        this.methodMap = methodMap;
        this.syncMap = syncMap;
    }

    public BridgeModule getModule() {
        return module;
    }

    public String[] getMethods() {
        if (this.methodMap == null) {
            this.generateMethodMap();
        }
        Set keys = this.methodMap.keySet();
        return (String[]) keys.toArray(new String[keys.size()]);
    }

    public Invoker getMethodInvoker(String name) {
        if (this.methodMap == null) {
            this.generateMethodMap();
        }
        return this.methodMap.get(name);
    }

    public boolean hasMethod(String methodName) {
        return methodMap != null && methodMap.containsKey(methodName);
    }

}
