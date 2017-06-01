(function () {
  if (window.AEJSBridge) {
    return;
  }

  var messagingIframe;
  var dispatchMessages = [];
  var receiveMessages = [];
  var registerCallbacks = {};
  var dispatchCallbacks = {};
  var CUSTOM_PROTOCOL_SCHEME = 'yy';
  var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';
  var uniqueId = 1;

  function _createQueueReadyIframe(doc) {
    messagingIframe = doc.createElement('iframe');
    messagingIframe.style.display = 'none';
    doc.documentElement.appendChild(messagingIframe);
  }

  function init(messageHandler) {
    if (AEJSBridge._messageHandler) {
      throw new Error('AEJSBridge.init called twice');
    }
    AEJSBridge._messageHandler = messageHandler;
    var receivedMessages = receiveMessages;
    receiveMessages = null;
    for (var i = 0; i < receivedMessages.length; i++) {
      _dispatchMessageFromNative(receivedMessages[i]);
    }
  }

  function register(eventName, handler) {
    registerCallbacks[eventName] = handler;
  }

  /**
   * @param disMessage.eventType 事件类型 String
   * @param disMessage.data 事件数据 (String | JsonString 可以为null)
   */
  function dispatch(disMessage) {
    if(!disMessage) {
      return
    }
    if (disMessage.callback) {
      var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
      dispatchCallbacks[callbackId] = disMessage.callback;
      disMessage.callbackId = callbackId;
    }
    dispatchMessages.push(disMessage);
    messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
  }

  // 提供给native调用,该函数作用:获取dispatchMessages返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容
  function _fetchQueue() {
    var messageQueueString = JSON.stringify(dispatchMessages);
    dispatchMessages = [];
    // android can't read directly the return data, so we can reload iframe src to communicate with java
    messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
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

  // 提供给native调用,receiveMessages 在会在页面加载完后赋值为null
  function _handleMessageFromNative(messageJSON) {
    console.log(messageJSON);
    if (receiveMessages && receiveMessages.length > 0) {
      receiveMessages.push(messageJSON);
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
