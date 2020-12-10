package cn.walid.android.webviewperformancemonitor;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Author   : walid
 * Date     : 2020-12-01  17:08
 * Describe :
 */
public class WebViewMonitor {

    private AndroidObject androidObject;
    private IWebmonitor iWebmonitor;

    /**
     * WebView不支持修改Timeout , 这里自定义
     */
    private int mTimeOut = 3000;
    private int mJsTimeout = 500;

    private Timer mTimer = new Timer();

    /**
     * 避免重复执行mWebsiteLoadTimeoutTask
     */
    private boolean isWebTimeoutTaskScheduling = false;

    /**
     * 避免重复执行mJsInjectTimeoutTask
     */
    private boolean isJsTimeoutTaskScheduling = false;

    public WebViewMonitor(AndroidObject androidObject, IWebmonitor webmonitor) {
        this.androidObject = androidObject;
        this.iWebmonitor = webmonitor;
    }

    /**
     * 判断网页加载是否完成
     */
    public AtomicBoolean isWebLoadFinished = new AtomicBoolean(false);

    private TimerTask mWebsiteLoadTimeoutTask = new TimerTask() {
        @Override
        public void run() {
            if (!isWebLoadFinished.get()) {
                sendWebsiteLoadTimeoutMsg();
            }
        }
    };

    private TimerTask mJsInjectTimeoutTask = new TimerTask() {
        @Override
        public void run() {
            if (androidObject != null) {
                if (!androidObject.isDataReturn()) {
                    sendJsInjectTimeoutMsg();
                } else {
                    sendDestroyMsg();
                }
            }
        }
    };

    final Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerMessage.MSG_DESTROY: {
                    destroyWebView();
                    break;
                }
                case HandlerMessage.MSG_WEBSITE_LOAD_TIMEOUT: {
                    Logger.d("网页加载超时 , WebView进度:" + iWebmonitor.getProgress() + " ,  url:" + iWebmonitor.getUrl());
                    if (iWebmonitor.getProgress() < 100) {
                        androidObject.handleError("LoadUrlTimeout");
                        destroyWebView();
                    }
                    break;
                }
                case HandlerMessage.MSG_JS_INJECT_TIMEOUT: {
                    if (androidObject != null) {
                        if (!androidObject.isDataReturn()) {
                            Logger.d("JS注入脚本执行超时");
                            String format = "ExecuteJsTimeout(%dms)";
                            androidObject.handleError(String.format(format, mJsTimeout));
                            destroyWebView();
                        }
                    }
                    break;
                }
                default:
                    super.handleMessage(msg);
            }
        }
    };

    public void handleError(String msg) {
        isWebLoadFinished.set(true);
        sendDestroyMsg();
        if (androidObject != null) {
            androidObject.handleError(msg);
        }
    }

    public int getTimeOut() {
        return mTimeOut;
    }

    public void setTimeOut(int timeOut) {
        mTimeOut = timeOut;
    }

    /**
     * 网页加载计时
     */
    public void setupWebLoadTimeout() {
        if (!isWebTimeoutTaskScheduling) {
            isWebTimeoutTaskScheduling = true;
            mTimer.schedule(mWebsiteLoadTimeoutTask, mTimeOut);
        }
    }

    /**
     * 注入js脚本执行计时
     * <p>
     * 注入js之后等待一段时间，如果这段时间内js不回调AndroidObject.handleResource()，则再销毁WebView
     * 过早销毁WebView，js不回调AndroidObject.handleResource()
     */
    public void setupJsInjectTimeout() {
        if (!isJsTimeoutTaskScheduling) {
            isJsTimeoutTaskScheduling = true;
            if (androidObject != null) {
                androidObject.setStartTime(System.currentTimeMillis());
            }
            mTimer.schedule(mJsInjectTimeoutTask, mJsTimeout);
        }
    }

    private void sendDestroyMsg() {
        handler.sendEmptyMessage(HandlerMessage.MSG_DESTROY);
    }

    private void sendWebsiteLoadTimeoutMsg() {
        handler.sendEmptyMessage(HandlerMessage.MSG_WEBSITE_LOAD_TIMEOUT);
    }

    private void sendJsInjectTimeoutMsg() {
        handler.sendEmptyMessage(HandlerMessage.MSG_JS_INJECT_TIMEOUT);
    }

    private void destroyWebView() {
//        if (mWebView != null) {
//            mWebView.clearCache(true);
//            mWebView.clearHistory();
//            mWebView.destroy();
//            mWebView = null;
//            Logger.d("成功销毁WebView");
//        } else {
//            Logger.d("销毁失败，WebView为空");
//        }
    }

}
