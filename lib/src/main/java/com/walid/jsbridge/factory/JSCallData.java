package com.walid.jsbridge.factory;

/**
 * Author   : walid
 * Date     : 2017-09-17  17:16
 * Describe : JSCallData Data
 */
public class JSCallData {
    private int code;
    private String msg;
    private String data;

    public JSCallData(int code, String msg, String data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public String getMsg() {
        return msg;
    }

    public int getCode() {
        return code;
    }
}
