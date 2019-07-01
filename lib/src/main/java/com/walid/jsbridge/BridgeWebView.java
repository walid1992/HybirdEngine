package com.walid.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.smtt.sdk.WebView;
import com.walid.jsbridge.factory.JSCallData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Author : walid
 * Data : 2017-06-01  15:05
 * Describe :
 */
@SuppressLint("SetJavaScriptEnabled")
public class BridgeWebView extends WebView implements IWebViewJsBridge {

    public static final String LOCAL_JSFile = "JsBridge.js";
    private static final int APP_CACHE_MAX_SIZE = 1024 * 1024 * 8;
    private Map<String, IDispatchCallBack> dispatchCallbacks = new HashMap<>();
    private Map<String, IBridgeHandler> registerHandlers = new HashMap<>();
    private List<Message> startupMsgs = new ArrayList<>();
    private long uniqueId = 0;

    public List<Message> getStartupMsgs() {
        return startupMsgs;
    }

    public void setStartupMsgs(List<Message> startupMsgs) {
        this.startupMsgs = startupMsgs;
    }

    public BridgeWebView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BridgeWebView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public BridgeWebView(Context context) {
        super(context);
        init();
    }

    private void init() {
        this.setVerticalScrollBarEnabled(false);
        this.setHorizontalScrollBarEnabled(false);
        this.getSettings().setJavaScriptEnabled(true);

        //localstorage  sj add 20170612
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setAppCacheMaxSize(APP_CACHE_MAX_SIZE);
        String appCachePath = getContext().getCacheDir().getAbsolutePath();
        this.getSettings().setAppCachePath(appCachePath);
        this.getSettings().setAllowFileAccess(true);
        this.getSettings().setAppCacheEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
        this.setWebViewClient(genBridgeWebViewClient());
    }

    public BridgeWebViewClient genBridgeWebViewClient() {
        return new BridgeWebViewClient(this);
    }

    /**
     * register handler,so that javascript can call it
     */
    @Override
    public void register(String eventName, IBridgeHandler handler) {
        if (handler != null) {
            registerHandlers.put(eventName, handler);
        }
    }

    /**
     * call javascript registered handler
     */
    @Override
    public void dispatch(String handlerName, String data, IDispatchCallBack callBack) {
        Message m = new Message();
        if (!TextUtils.isEmpty(data)) {
            m.setData(data);
        }
        if (callBack != null) {
            String callbackStr = String.format(BridgeUtil.CALLBACK_ID_FORMAT, ++uniqueId + (BridgeUtil.UNDERLINE_STR + SystemClock.currentThreadTimeMillis()));
            dispatchCallbacks.put(callbackStr, callBack);
            m.setCallbackId(callbackStr);
        }
        if (!TextUtils.isEmpty(handlerName)) {
            m.setHandlerName(handlerName);
        }
        dispatchMessage(m);
    }

    void handleJsMessageData(String url) {
        String functionName = BridgeUtil.getFunctionFromReturnUrl(url);
        IDispatchCallBack f = dispatchCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(new JSCallData(0, "ok", data));
            dispatchCallbacks.remove(functionName);
        }
    }

    void dispatchMessage(Message m) {
        // no init success
        if (startupMsgs != null) {
            startupMsgs.add(m);
        } else {
            String messageJson = m.toJson();
            // escape special characters for json string
            messageJson = messageJson.replaceAll("(\\\\)([^utrn])", "\\\\\\\\$1$2");
            messageJson = messageJson.replaceAll("(?<=[^\\\\])(\")", "\\\\\"");
            String javascriptCommand = String.format(BridgeUtil.JS_HANDLE_MESSAGE_FROM_JAVA, messageJson);
            if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
                this.loadUrl(javascriptCommand);
            }
        }
    }

    void queryJsMessageQueue() {
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            loadUrl(BridgeUtil.JS_FETCH_QUEUE_FROM_JAVA, callData -> {
                // deserializeMessage
                List<Message> messageList = Message.toMessageList(callData.getData());
                for (Message m : messageList) {
                    String responseId = m.getResponseId();
                    if (!TextUtils.isEmpty(responseId)) {
                        // dispatch callback
                        IDispatchCallBack function = dispatchCallbacks.get(responseId);
                        if (function != null) {
                            // 读取真正数据
                            try {
                                JSCallData realCall = new Gson().fromJson(m.getData(), new TypeToken<JSCallData>() {
                                }.getType());
                                function.onCallBack(realCall);
                            } catch (Exception e) {
                                function.onCallBack(new JSCallData(0, "ok", m.getData()));
                            }
                            dispatchCallbacks.remove(responseId);
                        }
                    } else {
                        // register callBack
                        IDispatchCallBack responseFunc;
                        final String callbackId = m.getCallbackId();
                        responseFunc = respCallData -> {
                            // none callbackId dont dispatch
                            if (TextUtils.isEmpty(callbackId) || respCallData == null) {
                                return;
                            }
                            Message responseMsg = new Message();
                            responseMsg.setResponseId(callbackId);
                            responseMsg.setMsg(respCallData.getMsg());
                            responseMsg.setCode(respCallData.getCode());
                            responseMsg.setData(TextUtils.isEmpty(respCallData.getData()) ? "" : respCallData.getData().replaceAll("\n", ""));
                            dispatchMessage(responseMsg);
                        };
                        IBridgeHandler handler;
                        if (!TextUtils.isEmpty(m.getHandlerName())) {
                            handler = registerHandlers.get(m.getHandlerName());
                            if (handler != null) {
                                handler.handler(m.getParams(), responseFunc);
                            }
                        }
                    }
                }
            });
        }
    }

    public void loadUrl(String jsUrl, IDispatchCallBack returnCallback) {
        this.loadUrl(jsUrl);
        dispatchCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

}
