package com.walid.jsbridge.factory;

import android.util.ArrayMap;

import com.walid.jsbridge.BridgeWebView;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:28
 * Describe :
 */
public class BridgeModuleManager {

    public static ArrayMap<String, TypeModuleFactory> syncMaps = new ArrayMap<>();
    public static ArrayMap<String, Boolean> registerMethods = new ArrayMap<>();

    public static void putAPI(String api) {
        registerMethods.put(api, true);
    }

    public static boolean hasAPI(String api) {
        return registerMethods.containsKey(api);
    }

    public static ArrayMap<String, TypeModuleFactory> getSyncMaps() {
        return syncMaps;
    }

    public static void clear() {
        syncMaps.clear();
    }

    public static void put(String key, TypeModuleFactory typeModuleFactory) {
        syncMaps.put(key, typeModuleFactory);
    }

    public static <T extends BridgeModule> void registerModule(final BridgeWebView bridgeWebView, final BridgeModule module) {
        TypeModuleFactory typeModuleFactory = new TypeModuleFactory<>(module, bridgeWebView);
        typeModuleFactory.register();
    }

}
