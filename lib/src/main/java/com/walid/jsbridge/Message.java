package com.walid.jsbridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author : walid
 * @Data : 2017-06-01  15:05
 * @Describe :
 */
public class Message {

    private String callbackId; // callbackId
    private String responseId; // responseId
    private String responseData; // responseData
    private String eventName; // 事件名称
    private String params; // 事件参数

    private final static String CALLBACK_ID = "callbackId";
    private final static String RESPONSE_ID = "responseId";
    private final static String RESPONSE_DATA = "responseData";
    private final static String PARAMS = "params";
    private final static String EVENT_NAME = "eventName";

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getResponseData() {
        return responseData;
    }

    public void setResponseData(String responseData) {
        this.responseData = responseData;
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

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String toJson() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(CALLBACK_ID, getCallbackId());
            jsonObject.put(PARAMS, getParams());
            jsonObject.put(EVENT_NAME, getEventName());
            jsonObject.put(RESPONSE_DATA, getResponseData());
            jsonObject.put(RESPONSE_ID, getResponseId());
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Message toObject(String jsonStr) {
        Message m = new Message();
        try {
            JSONObject jsonObject = new JSONObject(jsonStr);
            m.setEventName(jsonObject.has(EVENT_NAME) ? jsonObject.getString(EVENT_NAME) : null);
            m.setCallbackId(jsonObject.has(CALLBACK_ID) ? jsonObject.getString(CALLBACK_ID) : null);
            m.setResponseData(jsonObject.has(RESPONSE_DATA) ? jsonObject.getString(RESPONSE_DATA) : null);
            m.setResponseId(jsonObject.has(RESPONSE_ID) ? jsonObject.getString(RESPONSE_ID) : null);
            m.setParams(jsonObject.has(PARAMS) ? jsonObject.getString(PARAMS) : null);
            return m;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return m;
    }

    public static List<Message> toArrayList(String jsonStr) {
        List<Message> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonStr);
            for (int i = 0; i < jsonArray.length(); i++) {
                Message m = new Message();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                m.setEventName(jsonObject.has(EVENT_NAME) ? jsonObject.getString(EVENT_NAME) : null);
                m.setCallbackId(jsonObject.has(CALLBACK_ID) ? jsonObject.getString(CALLBACK_ID) : null);
                m.setResponseData(jsonObject.has(RESPONSE_DATA) ? jsonObject.getString(RESPONSE_DATA) : null);
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
