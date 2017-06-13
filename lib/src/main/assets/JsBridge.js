(function () {
  if (window.AEJSBridge) {
    return;
  }

  var bridgeIframe;
  var untreatedDispatchMsgs = [];
  var receiveMsgs = [];
  var registerCallbacks = {};
  var dispatchCallbacks = {};
  var CUSTOM_PROTOCOL_SCHEME = 'yy';
  var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';
  var uniqueId = 1;

  function _createQueueReadyIframe(doc) {
    bridgeIframe = doc.createElement('iframe');
    bridgeIframe.style.display = 'none';
    doc.documentElement.appendChild(bridgeIframe);
  }

  function init(messageHandler) {
    if (AEJSBridge._messageHandler) {
      throw new Error('AEJSBridge.init called twice');
    }
    AEJSBridge._messageHandler = messageHandler;
    var receivedMessages = receiveMsgs;
    receiveMsgs = null;
    for (var i = 0; i < receivedMessages.length; i++) {
      _dispatchMessageFromNative(receivedMessages[i]);
    }
  }

  /**
   * 事件注册
   * @param regMsg.eventName 事件名称
   * @param regMsg.callback 回调函数
   */
  function register(regMsg) {
    registerCallbacks[regMsg.eventName] = regMsg.callback;
  }

  /**
   * 取消事件注册
   * @param unRegMsg.eventName 事件名称
   * @param unRegMsg.callback 回调函数
   */
  function unRegister(unRegMsg) {
    delete registerCallbacks[message.eventName];
  }

  /**
   * 事件分发
   * @param disMsg.eventName 事件类型 String
   * @param disMsg.params 事件数据 (String | JsonString 可以为null)
   * @param disMsg.callback 回调函数 (function)
   */
  function dispatch(disMsg) {
    if(!disMsg) {
      return
    }
    if (disMsg.callback) {
      var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
      dispatchCallbacks[callbackId] = disMsg.callback;
      disMsg.callbackId = callbackId;
    }
    untreatedDispatchMsgs.push(disMsg);
    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
  }

  // 提供给native调用,该函数作用:获取untreatedDispatchMsgs返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容
  function _fetchQueue() {
    var messageQueueString = JSON.stringify(untreatedDispatchMsgs);
    untreatedDispatchMsgs = [];
    // android can't read directly the return data, so we can reload iframe src to communicate with java
    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
  }

  // 提供给native使用
  function _dispatchMessageFromNative(messageJSON) {
    setTimeout(function () {
      var message = JSON.parse(messageJSON);
      var responseCallback;
      //java call finished, now need to call js callback function
      if (message.responseId) {
        responseCallback = dispatchCallbacks[message.responseId];
        if (!responseCallback) {
          return;
        }
        responseCallback(message.responseData);
        delete dispatchCallbacks[message.responseId];
      } else {
        //直接发送
        if (message.callbackId) {
          var callbackResponseId = message.callbackId;
          responseCallback = function (responseData) {
            dispatch({
              responseId: callbackResponseId,
              responseData: responseData
            });
          };
        }

        var handler = AEJSBridge._messageHandler;
        if (message.eventName) {
          handler = registerCallbacks[message.eventName];
        }
        //查找指定handler
        try {
          handler(message.params, responseCallback);
        } catch (exception) {
          console.log("AEJSBridge: WARNING: javascript handler threw.", message, exception);
        }
      }
    });
  }

  // 提供给native调用,receiveMsgs 在会在页面加载完后赋值为null
  function _handleMessageFromNative(messageJSON) {
    console.log(messageJSON);
    if (receiveMsgs && receiveMsgs.length > 0) {
      receiveMsgs.push(messageJSON);
    } else {
      _dispatchMessageFromNative(messageJSON);
    }
  }

  var AEJSBridge = window.AEJSBridge = {
    init: init,
    register: register,
    dispatch: dispatch,
    _fetchQueue: _fetchQueue,
    _handleMessageFromNative: _handleMessageFromNative
  };

  var doc = document;
  _createQueueReadyIframe(doc);
  var readyEvent = doc.createEvent('Events');
  readyEvent.initEvent('AEJSBridgeReady');
  readyEvent.bridge = AEJSBridge;
  doc.dispatchEvent(readyEvent);
})();
