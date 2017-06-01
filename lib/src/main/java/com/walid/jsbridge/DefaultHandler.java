package com.walid.jsbridge;

/**
 * @Author : walid
 * @Data : 2017-06-01  15:05
 * @Describe :
 */
public class DefaultHandler implements IBridgeHandler {

    @Override
    public void handler(String data, ICallBackFunction function) {
        if (function != null) {
            function.onCallBack("DefaultHandler response data");
        }
    }

}
