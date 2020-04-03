package com.walid.jsbridge.example;

import android.text.TextUtils;
import android.util.ArrayMap;

import com.google.gson.internal.LinkedTreeMap;
import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSCallData;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import io.socket.client.IO;
import io.socket.client.Socket;

/**
 * Author   : walid
 * Date     : 2020-03-07  17:06
 * Describe :
 */

@JSMoudle(name = "ws")
public class WSModule extends BridgeModule {

    private static final String SERVER_URL = "url";

    private Map<String, Socket> socketMap = new ArrayMap<>();

    @JSMethod(alias = "connect", sync = false)
    public void connect(BridgeWebView webView, HashMap<String, Object> map, IDispatchCallBack function) {
        try {
            String serverUrl = (String) map.get(SERVER_URL);
            if (TextUtils.isEmpty(serverUrl)) {
                function.onCallBack(new JSCallData(-1, "SERVER_URL is empty~", ""));
                return;
            }
            Socket socket = IO.socket(serverUrl);
            socket.connect();

            socket.on(Socket.EVENT_CONNECT, args -> {
                webView.dispatch("event_ws_onconnect", "", null);
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                webView.dispatch("event_ws_ondisconnect", "", null);
            });
            socket.on(Socket.EVENT_CONNECT_ERROR, args -> {
                webView.dispatch("event_ws_onconnect_error", "", null);
            });
            socket.on(Socket.EVENT_CONNECT_TIMEOUT, args -> {
                webView.dispatch("event_ws_onconnect_timeout", "", null);
            });
            socket.on(Socket.EVENT_ERROR, args -> {
                webView.dispatch("event_ws_onerror", "", null);
            });
            socket.on("new message", args -> {
                webView.dispatch("event_ws_onnewmessage", args[0].toString(), null);
            });
            socketMap.put(serverUrl, socket);

        } catch (URISyntaxException e) {
            function.onCallBack(new JSCallData(-1, e.getReason(), ""));
        }
    }

    @JSMethod(alias = "disconnect", sync = false)
    public void disconnect(BridgeWebView webView, HashMap<String, Object> map, IDispatchCallBack function) {
        String serverUrl = (String) map.get(SERVER_URL);
        if (TextUtils.isEmpty(serverUrl)) {
            function.onCallBack(new JSCallData(-1, "SERVER_URL is empty~", ""));
            return;
        }

        Socket socket = socketMap.get(serverUrl);
        if (socket == null) {
            function.onCallBack(new JSCallData(0, "", ""));
            return;
        }
        socket.disconnect();
        function.onCallBack(new JSCallData(0, "", ""));
    }

    private static byte[] list2bytes(Map<String, Double> list) {
        if (list == null) return null;
        byte[] bytes = new byte[list.size()];
        int i = 0;
        for (double aByte : list.values()) {
            bytes[i] = (byte) aByte;
            i++;
        }
        return bytes;
    }

    @JSMethod(alias = "emit", sync = false)
    public void emit(BridgeWebView webView, HashMap<String, Object> map, IDispatchCallBack function) {
        String serverUrl = (String) map.get(SERVER_URL);

        if (TextUtils.isEmpty(serverUrl)) {
            function.onCallBack(new JSCallData(-1, "SERVER_URL is empty~", ""));
            return;
        }

        Socket socket = socketMap.get(serverUrl);
        if (socket == null) {
            function.onCallBack(new JSCallData(0, "", ""));
            return;
        }

        Object data = map.get("data");
        if (data instanceof LinkedTreeMap) {
            data = list2bytes((Map<String, Double>) data);
        }

        socket.emit("new message", data);

        function.onCallBack(new JSCallData(0, "", ""));
    }

}
