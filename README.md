# Android 与 JS 桥接库

-----

Android and JS communication ，由于 jcenter 目前上传遇到问题，最新版本采用 jitpack 方式~

## Usage

在项目根目录 build.gradle 中加入：

```
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

在工程目录 build.gradle 中加入：

```
dependencies {
    implementation 'com.github.walid1992:JSBridge:v1.0.0.beta'
}
```

## USE in Java

## dispatch

```
    webView.dispatch("event_test_netChange", "data from Java", new ICallBackFunction() {
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
