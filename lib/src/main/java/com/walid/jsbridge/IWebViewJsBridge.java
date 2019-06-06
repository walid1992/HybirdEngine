package com.walid.jsbridge;

/**
 * Author : walid
 * Data : 2017-06-01  15:05
 * Describe :
 */
interface IWebViewJsBridge {

    void register(String eventName, IBridgeHandler handler);

    void dispatch(String eventName, String data, IDispatchCallBack callBack);

}
