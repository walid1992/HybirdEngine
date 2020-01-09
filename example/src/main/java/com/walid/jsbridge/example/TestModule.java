package com.walid.jsbridge.example;

import android.util.Base64;
import android.util.Log;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSCallData;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Author   : walid
 * Date     : 2017-09-18  16:06
 * Describe :
 */
@JSMoudle(name = "app")
public class TestModule extends BridgeModule {

    /**
     * 本地图片转换成base64字符串
     *
     * @param imgFile 图片本地路径
     * @return
     * @author ZHANGJL
     * @dateTime 2018-02-23 14:40:46
     */
    public static String ImageToBase64ByLocal(String imgFile) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in;
        byte[] data = null; // 读取图片字节数组
        try {
            in = new FileInputStream(imgFile);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        } // 对字节数组Base64编码
        return Base64.encodeToString(data, Base64.DEFAULT);
        // 返回Base64编码过的字节数组字符串
    }

    @JSMethod(alias = "doTest", sync = false)
    public void oauth(BridgeWebView webView, Map<String, Object> map, IDispatchCallBack function) {
        webView.post(() -> {
            Log.d("TestModule", map.toString());
            double platform = (double) map.get("platform");
            Log.d("TestModule", platform + "");

//            String base64 = ImageToBase64ByLocal(Environment.getExternalStorageDirectory() + "/Soul.jpg");
//            Log.d("TestModule", base64);
//            function.onCallBack(new JSCallData(0, "ok", base64.replaceAll("\n", "")));

//            String data = URLEncoder.encode("Avant L'aube");
//            data.replace("\\+", "%20");
//            String ces = URLDecoder.decode(data);
//            Log.e("TAG", data);

            String a = "%%%";
            function.onCallBack(new JSCallData(0, "", a));
        });
    }

    @JSMethod(alias = "getVersion", sync = true)
    public String testSync(BridgeWebView webView, HashMap<String, Object> map) {
        return "3.10.20";
    }

}
