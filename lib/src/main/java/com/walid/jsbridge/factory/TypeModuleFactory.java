package com.walid.jsbridge.factory;

import android.util.Log;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class TypeModuleFactory<T extends BridgeModule> {

    public static final String TAG = "TypeModuleFactory";
    private Class<T> tClass;
    private Map<String, Invoker> methodMap;

    public TypeModuleFactory(Class<T> clz) {
        this.tClass = clz;
    }

    private void generateMethodMap() {
        HashMap<String, Invoker> methodMap = new HashMap<>();
        try {
            JSMoudle jsMoudle = this.tClass.getAnnotation(JSMoudle.class);
            Method[] methods = this.tClass.getMethods();
            for (Method method : methods) {
                JSMethod jsMethod = method.getAnnotation(JSMethod.class);
                if (jsMethod != null) {
                    String name = "action_" + jsMoudle.name() + "_" + ("_".equals(jsMethod.alias()) ? method.getName() : jsMethod.alias());
                    methodMap.put(name, new MethodInvoker(method));
                }
            }
        } catch (Exception e) {
            Log.e(TAG, String.valueOf(e));
        }
        this.methodMap = methodMap;
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
}
