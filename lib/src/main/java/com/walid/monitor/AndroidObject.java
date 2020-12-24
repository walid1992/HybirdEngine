package com.walid.monitor;

import android.webkit.JavascriptInterface;

/**
 * Author   : walid
 * Date     : 2020-12-01  17:00
 * Describe :
 */
public abstract class AndroidObject {

    private volatile boolean dataReturn = false;
    private long startTime;
    private long endTime;

    /**
     * 用于收集Timing信息
     *
     * @param jsonStr
     */
    @JavascriptInterface
    public void sendResource(String jsonStr) {
        dataReturn = true;
        endTime = System.currentTimeMillis();
        Logger.d("js成功执行时间：" + (endTime - startTime));
        handleResource(jsonStr);
    }

    /**
     * 用于收集js的执行错误
     *
     * @param msg
     */
    @JavascriptInterface
    public void sendError(String msg) {
        handleError(msg);
    }

    /**
     * 处理错误信息，可能会被回调多次
     *
     * @param msg
     */
    public abstract void handleError(String msg);

    /**
     * @param jsonStr
     */
    public abstract void handleResource(String jsonStr);

    public boolean isDataReturn() {
        return dataReturn;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

}
