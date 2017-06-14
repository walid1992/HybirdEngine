(function () {
  if (window.AEJSBridge) {
    return;
  }

  var bridgeIframe;
  var untreatedDispatchMsgs = [];
  var eventCallbacks = {};
  var dispatchCallbacks = {};
  var CUSTOM_PROTOCOL_SCHEME = 'yy';
  var QUEUE_HAS_MESSAGE = '__QUEUE_MESSAGE__/';
  var uniqueId = 1;

  function _createQueueReadyIframe(doc) {
    bridgeIframe = doc.createElement('iframe');
    bridgeIframe.style.display = 'none';
    doc.documentElement.appendChild(bridgeIframe);
  }

  /**
   * 事件注册
   * @param req.handlerName 事件名称
   * @param req.callback 回调函数
   */
  function addEventListener(req) {
    if (!req || !req.handlerName || !req.callback) {
      return;
    }
    if (!eventCallbacks[req.handlerName]) {
      eventCallbacks[req.handlerName] = [];
    }
    eventCallbacks[req.handlerName].push(req.callback);
  }

  /**
   * 取消事件注册
   * @param req.handlerName 事件名称
   * @param req.execFunc 事件名称
   * @param req.callback 回调函数
   */
  function removeEventListener(req) {
    if (!req || !req.handlerName || !req.execFunc) {
      return;
    }
    var callArray = eventCallbacks[req.handlerName];
    var delSuccess = false;
    for (var index = 0; index < callArray.length; index++) {
      if (callArray[index] === req.execFunc) {
        callArray.splice(index, 1);
        delSuccess = true;
        break;
      }
    }
//    req.callback({
//      msg: delSuccess ? 'delete success ~' : 'delete failed ~',
//      code: delSuccess ? 0 : -1
//    });
  }

  /**
   * 事件分发
   * @param req.handlerName 事件类型 String
   * @param req.params 事件数据 (String | JsonString 可以为null)
   * @param req.callback 回调函数 (function)
   */
  function dispatch(req) {
    if (!req || !req.handlerName) {
      return;
    }
    if (req.callback) {
      var callbackId = 'cb_' + (uniqueId++) + '_' + new Date().getTime();
      dispatchCallbacks[callbackId] = req.callback;
      req.callbackId = callbackId;
    }
    untreatedDispatchMsgs.push(req);
    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
  }

  // 提供给native调用,该函数作用:获取untreatedDispatchMsgs返回给native,由于android不能直接获取返回的内容,所以使用url shouldOverrideUrlLoading 的方式返回内容
  function _fetchQueue() {
    var messageQueueString = JSON.stringify(untreatedDispatchMsgs);
    untreatedDispatchMsgs = [];
    // android can't read directly the return data, so we can reload iframe src to communicate with java
    bridgeIframe.src = CUSTOM_PROTOCOL_SCHEME + '://return/_fetchQueue/' + encodeURIComponent(messageQueueString);
  }

  /**
   * 提供给native使用
   * @param messageJSON
   * {
   *   handlerName: '',
   *   callbackId: '',
   *   responseId: '',
   *   responseData: '',
   *   params: '',
   *   msg: '',
   *   code: ''
   * }
   * @private
   */
  function _dispatchMessageFromNative(messageJSON) {
    setTimeout(function () {
      var msg = JSON.parse(messageJSON);
      var responseCallback;
      // Js dispatch callback
      if (msg.responseId) {
        responseCallback = dispatchCallbacks[msg.responseId];
        if (!responseCallback) {
          return;
        }
        responseCallback({
          data: msg.data,
          msg: msg.msg,
          code: msg.code
        });
        delete dispatchCallbacks[msg.responseId];
        // Js addEventListener callback
      } else {
        var callbacks;
        if (!msg.handlerName) {
          console.log("handlerName is empty ...");
          return;
        }
        callbacks = eventCallbacks[msg.handlerName];
        if (!callbacks) {
          console.log("handlerName callbacks is empty ...");
          return;
        }
        callbacks.forEach(function (item) {
          item({
            data: msg.data,
            msg: msg.msg,
            code: msg.code
          });
        })
      }
    });
  }

  // 提供给native调用,receiveMsgs 在会在页面加载完后赋值为null
  function _handleMessageFromNative(messageJSON) {
    _dispatchMessageFromNative(messageJSON);
  }

  var AEJSBridge = window.AEJSBridge = {
    addEventListener: addEventListener,
    removeEventListener: removeEventListener,
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