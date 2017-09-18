package com.walid.jsbridge;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Looper;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebView;

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
    private Map<String, ICallBackFunction> dispatchCallbacks = new HashMap<>();
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

    protected BridgeWebViewClient genBridgeWebViewClient() {
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
    public void dispatch(String handlerName, String data, ICallBackFunction callBack) {
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
        ICallBackFunction f = dispatchCallbacks.get(functionName);
        String data = BridgeUtil.getDataFromReturnUrl(url);
        if (f != null) {
            f.onCallBack(new JSCallData(0, "ok", data));
            dispatchCallbacks.remove(functionName);
        }
    }

    void dispatchMessage(Message m) {
        // 如果没初始化成功
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
                    // dispatch callback
                    if (!TextUtils.isEmpty(responseId)) {
                        ICallBackFunction function = dispatchCallbacks.get(responseId);
                        function.onCallBack(callData);
                        dispatchCallbacks.remove(responseId);
                        // register callBack
                    } else {
                        ICallBackFunction responseFunc;
                        final String callbackId = m.getCallbackId();
                        responseFunc = respCallData -> {
                            // 没有 callbackId 不负责事件分发
                            if (TextUtils.isEmpty(callbackId)) {
                                return;
                            }
                            Message responseMsg = new Message();
                            responseMsg.setResponseId(callbackId);
                            responseMsg.setMsg(respCallData.getMsg());
                            responseMsg.setCode(respCallData.getCode());
                            responseMsg.setData(respCallData.getData());
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

    public void loadUrl(String jsUrl, ICallBackFunction returnCallback) {
        this.loadUrl(jsUrl);
        dispatchCallbacks.put(BridgeUtil.parseFunctionName(jsUrl), returnCallback);
    }

}
