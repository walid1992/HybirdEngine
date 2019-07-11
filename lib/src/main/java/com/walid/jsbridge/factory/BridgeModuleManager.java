package com.walid.jsbridge.factory;

import com.walid.jsbridge.BridgeWebView;

import java.util.HashMap;
import java.util.Map;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:28
 * Describe :
 */
public class BridgeModuleManager {

    public static Map<String, TypeModuleFactory> syncMaps = new HashMap<>();

    public static Map<String, TypeModuleFactory> getSyncMaps() {
        return syncMaps;
    }

    public static void clear() {
        syncMaps.clear();
    }

    public static void put(String key, TypeModuleFactory typeModuleFactory) {
        syncMaps.put(key, typeModuleFactory);
    }

    public static <T extends BridgeModule> void registerModule(final BridgeWebView bridgeWebView, final Class<T> wxModuleClass) {
        TypeModuleFactory typeModuleFactory = new TypeModuleFactory<>(wxModuleClass, bridgeWebView);
        typeModuleFactory.register();
    }

}
