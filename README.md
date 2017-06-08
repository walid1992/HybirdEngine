#JsBridge

-----

inspired and modified from [this](https://github.com/jacin1/JsBridge) and wechat jsBridge file, with some bugs fix and feature enhancement.

This project make a bridge between Java and JavaScript.

It provides safe and convenient way to call Java code from js and call js code from java.

## Usage

```
dependencies {
    compile 'com.github.lzyzsd:jsbridge:1.0.4'
}
```

## Use it in Java

add com.github.lzyzsd.jsbridge.BridgeWebView to your layout, it is inherited from WebView.

### Register a Java handler function so that js can call

```java

        webView.register("submitFromWeb", new IBridgeHandler() {
            @Override
            public void handler(String data, ICallBackFunction function) {
                Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
                function.onCallBack("submitFromWeb exe, response data 中文 from Java");
            }
        });

```

js can call this Java handler method "submitFromWeb" through:

```javascript

    window.AEJSBridge.dispatch({
      eventName: 'submitFromWeb',
      params: {'param': '中文测试'},
      callback: function (responseData) {
        document.getElementById("show").innerHTML = "send get responseData from java, params = " + responseData
      }
    });

```

You can set a default handler in Java, so that js can send message to Java without assigned handlerName

```java

    webView.setDefaultHandler(new DefaultHandler());

```

```javascript

    window.AEJSBridge.dispatch({
      params: {'param': '中文测试'},
      callback: function (responseData) {
        document.getElementById("show").innerHTML = "send get responseData from java, params = " + responseData
      }
    });

```

### Register a JavaScript handler function so that Java can call

```javascript

    window.AEJSBridge.register("functionInJs", function (params, responseCallback) {
      document.getElementById("show").innerHTML = "params from Java: = " + params;
      let responseData = "Javascript Says Right back aka!";
      responseCallback(responseData);
    });

```

Java can call this js handler function "functionInJs" through:

```java

    webView.dispatch("functionInJs", new Gson().toJson(user), new ICallBackFunction() {
        @Override
        public void onCallBack(String data) {
            Log.d(TAG, "handler = functionInJs, data from web = " + data);
        }
    });

```
You can also define a default handler use init method, so that Java can send message to js without assigned handlerName

for example:

```javascript

  window.AEJSBridge.init(function (message, responseCallback) {
    console.log('JS got a message', message);
    let params = {
      'Javascript Responds': '测试中文!'
    };
    console.log('JS responding with', params);
    responseCallback(params);
  });

```

```java
    webView.dispatch("hello");
```

will print 'JS got a message hello' and 'JS responding with' in webview console.

## License

This project is licensed under the terms of the MIT license.
