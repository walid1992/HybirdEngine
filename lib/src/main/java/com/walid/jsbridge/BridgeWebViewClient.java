package com.walid.jsbridge;

import android.graphics.Bitmap;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Author : walid
 * Data : 2017-06-01  15:05
 * Describe :
 */
public class BridgeWebViewClient extends WebViewClient {

    private BridgeWebView webView;

    public BridgeWebViewClient(BridgeWebView webView) {
        this.webView = webView;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest webResourceRequest) {
        return super.shouldOverrideUrlLoading(webView, webResourceRequest);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        try {
            url = URLDecoder.decode(url, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        // if return data
        if (url.startsWith(BridgeUtil.YY_RETURN_DATA)) {
            webView.handleJsMessageData(url);
            return true;
        } else if (url.startsWith(BridgeUtil.YY_OVERRIDE_SCHEMA)) {
            webView.queryJsMessageQueue();
            return true;
        } else {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        // 使用新的API替换老版本
        webView.evaluateJavascript("(function () {\n" +
                "  if (window.AEJSBridge) {\n" +
                "    return;\n" +
                "  }\n" +
                "\n" +
                "  var bridgeIframe;\n" +
                "  var untreatedDispatchMsgs = [];\n" +
                "  var eventCallbacks = {};\n" +
                "  var dispatchCallbacks = {};\n" +
                "  var CUSTOM_PROTOCOL_SCHEME = 'yy';\n" +
                "  var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';\n" +
                "  var uniqueId = 1;\n" +
                "\n" +
                "  function _createQueueReadyIframe(doc) {\n" +
                "    bridgeIframe = doc.createElement('iframe');\n" +
                "    bridgeIframe.style.display = 'none';\n" +
                "    doc.documentElement.appendChild(bridgeIframe);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * 事件注册\n" +
                "   * @param req.handlerName 事件名称\n" +
                "   * @param req.exec 监听方法\n" +
                "   * @param req.callback 监听方法\n" +
                "   */\n" +
                "  function addEventListener(req) {\n" +
                "\n" +
                "    if (!req.exec && req.callback) {\n" +
                "      req.exec = req.callback;\n" +
                "      req.callback = null;\n" +
                "    }\n" +
                "\n" +
                "    if (!req || !req.handlerName || !req.exec) {\n" +
                "      return;\n" +
                "    }\n" +
                "    if (!eventCallbacks[req.handlerName]) {\n" +
                "      eventCallbacks[req.handlerName] = [];\n" +
                "    }\n" +
                "    eventCallbacks[req.handlerName].push(req.exec);\n" +
                "    if(req.callback) {\n" +
                "        req.callback({\n" +
                "          msg: 'add success ~',\n" +
                "          code: 0\n" +
                "        });\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * 取消事件注册\n" +
                "   * @param req.handlerName 事件名称\n" +
                "   * @param req.exec 事件名称\n" +
                "   * @param req.callback 回调函数\n" +
                "   */\n" +
                "  function removeEventListener(req) {\n" +
                "    if (!req || !req.handlerName || !req.exec) {\n" +
                "      return;\n" +
                "    }\n" +
                "    var callArray = eventCallbacks[req.handlerName];\n" +
                "    var delSuccess = false;\n" +
                "    for (var index = 0; index < callArray.length; index++) {\n" +
                "      if (callArray[index] === req.exec) {\n" +
                "        callArray.splice(index, 1);\n" +
                "        delSuccess = true;\n" +
                "        break;\n" +
                "      }\n" +
                "    }\n" +
                "    if(req.callback) {\n" +
                "        req.callback({\n" +
                "          msg: delSuccess ? 'delete success ~' : 'delete failed ~',\n" +
                "          code: delSuccess ? 0 : -1\n" +
                "        });\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * 事件分发\n" +
                "   * @param req.handlerName 事件类型 String\n" +
                "   * @param req.params 事件数据 (String | JsonString 可以为null)\n" +
                "   * @param req.callback 回调函数 (function)\n" +
                "   */\n" +
                "  function dispatch(req) {\n" +
                "    console.log(\"dispatch：\" + JSON.stringify(req));\n" +
                "    if (!req || !req.handlerName) {\n" +
                "      return;\n" +
                "    }\n" +
                "    if (req.callback) {\n" +
                "      var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();\n" +
                "      dispatchCallbacks[callbackId] = req.callback;\n" +
                "      req.callbackId = callbackId;\n" +
                "    }\n" +
                "    untreatedDispatchMsgs.push(req);\n" +
                "    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * 事件分发同步\n" +
                "   * @param req.handlerName 事件类型 String\n" +
                "   * @param req.params 事件数据 (String | JsonString 可以为null)\n" +
                "   */\n" +
                "  function dispatchSync(req) {\n" +
                "    if (!req || !req.handlerName) {\n" +
                "      return;\n" +
                "    }\n" +
                "    console.log(JSON.stringify(req.params));\n" +
                "    var res = (AEJSBridgeSync.dispatchSync(req.handlerName, JSON.stringify(req.params)));\n" +
                "    console.log(res);\n" +
                "    try {\n" +
                "      return JSON.parse(res);\n" +
                "    } catch (e) {\n" +
                "      return res;\n" +
                "    }\n" +
                "  }\n" +
                "\n" +
                "  // 提供给native调用,该函数作用:获取untreatedDispatchMsgs返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容\n" +
                "  function _fetchQueue() {\n" +
                "    var messageQueueString = JSON.stringify(untreatedDispatchMsgs);\n" +
                "    console.log(\"_fetchQueue：\" + messageQueueString);\n" +
                "    untreatedDispatchMsgs = [];\n" +
                "    // android can't read directly the return data, so we can reload iframe src to communicate with java\n" +
                "    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);\n" +
                "  }\n" +
                "\n" +
                "  /**\n" +
                "   * 提供给native使用，处理 native 主动或被动传给 JS 的事件\n" +
                "   * @param messageJSON\n" +
                "   * {\n" +
                "   *   handlerName: '',// TODO JS 主动分发使用\n" +
                "   *   callbackId: '',// TODO JS 主动分发使用\n" +
                "   *   responseId: '',// TODO native 主动分发使用\n" +
                "   *   data: '',// TODO native 主动分发使用\n" +
                "   *   params: '',\n" +
                "   *   msg: '',\n" +
                "   *   code: ''\n" +
                "   * }\n" +
                "   * @private\n" +
                "   */\n" +
                "  function _dispatchMessageFromNative(messageJSON) {\n" +
                "    setTimeout(function () {\n" +
                "      var msg = JSON.parse(messageJSON);\n" +
                "      var responseCallback;\n" +
                "      msg.data = (typeof msg.data == 'string') ? decodeURIComponent(msg.data) : msg.data;\n" +
                "      var data;\n" +
                "      try {\n" +
                "        data = JSON.parse(msg.data);\n" +
                "      } catch (e) {\n" +
                "        data = msg.data;\n" +
                "      }\n" +
                "\n" +
                "      // Js dispatch callback\n" +
                "      if (msg.responseId) {\n" +
                "        responseCallback = dispatchCallbacks[msg.responseId];\n" +
                "        if (!responseCallback) {\n" +
                "          return;\n" +
                "        }\n" +
                "        responseCallback({\n" +
                "          data: data,\n" +
                "          msg: msg.msg,\n" +
                "          code: msg.code\n" +
                "        });\n" +
                "        delete dispatchCallbacks[msg.responseId];\n" +
                "        // Js addEventListener callback\n" +
                "      } else {\n" +
                "\n" +
                "        // 直接发送\n" +
                "        if (msg.callbackId) {\n" +
                "          var callbackResponseId = msg.callbackId;\n" +
                "          responseCallback = function (res) {\n" +
                "            untreatedDispatchMsgs.push({\n" +
                "              responseId: callbackResponseId,\n" +
                "              data: res\n" +
                "            });\n" +
                "            bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;\n" +
                "          };\n" +
                "        }\n" +
                "\n" +
                "        var callbacks;\n" +
                "        if (!msg.handlerName) {\n" +
                "          console.log(\"handlerName is empty ...\");\n" +
                "          return;\n" +
                "        }\n" +
                "        callbacks = eventCallbacks[msg.handlerName];\n" +
                "        if (!callbacks) {\n" +
                "          responseCallback({\n" +
                "            msg: \"事件暂未注册~\",\n" +
                "            code: \"-1\"\n" +
                "          });\n" +
                "          console.log(\"handlerName callbacks is empty ...\");\n" +
                "          return;\n" +
                "        }\n" +
                "        callbacks.forEach(function (item) {\n" +
                "          item({\n" +
                "            data: data,\n" +
                "            msg: msg.msg,\n" +
                "            code: msg.code\n" +
                "          }, responseCallback);\n" +
                "        })\n" +
                "      }\n" +
                "    });\n" +
                "  }\n" +
                "\n" +
                "  // 提供给native调用,receiveMsgs 在会在页面加载完后赋值为null\n" +
                "  function _handleMessageFromNative(messageJSON) {\n" +
                "    _dispatchMessageFromNative(messageJSON);\n" +
                "  }\n" +
                "\n" +
                "  var AEJSBridge = window.AEJSBridge = {\n" +
                "    addEventListener: addEventListener,\n" +
                "    removeEventListener: removeEventListener,\n" +
                "    dispatch: dispatch,\n" +
                "    dispatchSync: dispatchSync,\n" +
                "    _fetchQueue: _fetchQueue,\n" +
                "    _handleMessageFromNative: _handleMessageFromNative\n" +
                "  };\n" +
                "\n" +
                "  var doc = document;\n" +
                "  _createQueueReadyIframe(doc);\n" +
                "  var readyEvent = doc.createEvent('Events');\n" +
                "  readyEvent.initEvent('AEJSBridgeReady');\n" +
                "  readyEvent.bridge = AEJSBridge;\n" +
                "  doc.dispatchEvent(readyEvent);\n" +
                "})();\n", null);

        // 删除旧的桥接加载库
//        BridgeUtil.webViewLoadLocalJs(view, BridgeWebView.LOCAL_JSFile);

        List<Message> messageList = webView.getStartupMsgs();
        webView.setStartupMsgs(null);
        if (messageList == null || messageList.size() <= 0) return;
        List<Message> copy = new ArrayList<>(messageList);
        for (Message m : copy) {
            webView.dispatchMessage(m);
        }

    }

    @Override
    public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
    }

}