# AndroidJsBridge

-----

android and js ~

## Usage

```
dependencies {
    compile 'com.github.lzyzsd:jsbridge:1.0.4'
}
```

## use in java

## dispatch

```
    webView.dispatch("functionInJs", "data from Java", new ICallBackFunction() {
        @Override
        public void onCallBack(String data) {
            Log.i(TAG, "reponse data from js " + data);
        }
    });

```

## register

```
    webView.register("submitFromWeb", new IBridgeHandler() {
        @Override
        public void handler(String data, ICallBackFunction function) {
            Log.i(TAG, "handler = submitFromWeb, data from web = " + data);
            function.onCallBack("submitFromWeb exe, response data 中文 from Java");
        }
    });
```

## use in js

### dispatch

```
    window.AEJSBridge.dispatch({
      handlerName: 'submitFromWeb',
      params: {'param': '中文测试'},
      callback: function (responseData) {
        document.getElementById("show").innerHTML = "send get responseData from java, params = " + responseData
      }
    });

```

## addEventListener

```
    window.AEJSBridge.addEventListener({
      handlerName: 'functionInJs',
      callback: func
    });
```

## removeEventListener

```
    window.AEJSBridge.removeEventListener({
      handlerName: 'functionInJs',
      execFunc: func
    });
```

## License

This project is licensed under the terms of the MIT license.
