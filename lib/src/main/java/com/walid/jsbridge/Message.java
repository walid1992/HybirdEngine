package com.walid.jsbridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Author : walid
 * Data : 2017-06-01  15:05
 * Describe :
 */
class Message {

    private String callbackId; // callbackId
    private String responseId; // responseId
    private String data; // data
    private String handlerName; // 事件名称
    private String params; // 事件参数
    private int code = 0; // 状态码
    private String msg; // 消息

    private final static String CALLBACK_ID = "callbackId";
    private final static String RESPONSE_ID = "responseId";
    private final static String DATA = "data";
    private final static String PARAMS = "params";
    private final static String HANDLER_NAME = "handlerName";
    private final static String CODE = "code";
    private final static String MSG = "msg";

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getHandlerName() {
        return handlerName;
    }

    public void setHandlerName(String handlerName) {
        this.handlerName = handlerName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CALLBACK_ID, callbackId);
            jsonObject.put(PARAMS, params);
            jsonObject.put(HANDLER_NAME, handlerName);
            jsonObject.put(DATA, data);
            jsonObject.put(CODE, code);
            jsonObject.put(MSG, msg);
            jsonObject.put(RESPONSE_ID, responseId);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

//    static Message toMessage(JSCallData callData) {
//        Message m = new Message();
//        m.setData(callData.getData());
//        m.setCode(callData.getCode());
//        m.setMsg(callData.getMsg());
//        return m;
//    }

    static Message toMessage(String jsonData) {
        Message m = new Message();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);
            m.setHandlerName(jsonObject.has(HANDLER_NAME) ? jsonObject.getString(HANDLER_NAME) : null);
            m.setCallbackId(jsonObject.has(CALLBACK_ID) ? jsonObject.getString(CALLBACK_ID) : null);
            m.setData(jsonObject.has(DATA) ? jsonObject.getString(DATA) : null);
            m.setResponseId(jsonObject.has(RESPONSE_ID) ? jsonObject.getString(RESPONSE_ID) : null);
            m.setParams(jsonObject.has(PARAMS) ? jsonObject.getString(PARAMS) : null);
            return m;
        } catch (JSONException e) {
            e.printStackTrace();
            return m;
        }
    }

    static List<Message> toMessageList(String jsonData) {
        List<Message> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                Message m = new Message();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                m.setHandlerName(jsonObject.has(HANDLER_NAME) ? jsonObject.getString(HANDLER_NAME) : null);
                m.setCallbackId(jsonObject.has(CALLBACK_ID) ? jsonObject.getString(CALLBACK_ID) : null);
                m.setData(jsonObject.has(DATA) ? jsonObject.getString(DATA) : null);
                m.setResponseId(jsonObject.has(RESPONSE_ID) ? jsonObject.getString(RESPONSE_ID) : null);
                m.setParams(jsonObject.has(PARAMS) ? jsonObject.getString(PARAMS) : null);
                list.add(m);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
}
