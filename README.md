# AndroidJsBridge

-----

Android and JS communication ~

## Usage

```
dependencies {
    compile 'com.walid:jsbridge:0.0.3'
}
```

## USE in Java

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
@JSMoudle(name = "test")
public class TestModule extends BridgeModule {

    @JSMethod(alias = "doTest")
    public void oauth(BridgeWebView webView, HashMap<String, Object> map, ICallBackFunction function) {
        Log.d("OauthUtils", map.toString());
        String platform = (String) map.get("platform");
        Log.d("TestModule", platform);
        function.onCallBack(new JSCallData(0, "ok", "请求成功！"));
    }

}
    
BridgeModuleManager.registerModule(webView, TestModule.class);
```

## USE in JS

### dispatch

```
    window.AEJSBridge.dispatch({
      handlerName: 'action_test_doTest',
      params: {'platform': 'wechat'},
      callback: function (respData) {
        document.getElementById("show").innerHTML = "send get responseData from java, params = " + JSON.stringify(respData)
      }
    });
```

## addEventListener

```
    window.AEJSBridge.addEventListener({
      handlerName: 'event_test_netChange',
      callback: func
    });
```

## removeEventListener

```
    window.AEJSBridge.removeEventListener({
      handlerName: 'event_test_netChange',
      execFunc: func
    });
```

## License

This project is licensed under the terms of the MIT license.
