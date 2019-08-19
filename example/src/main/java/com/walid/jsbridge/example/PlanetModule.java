package com.walid.jsbridge.example;

import android.util.Log;

import com.walid.jsbridge.BridgeWebView;
import com.walid.jsbridge.IDispatchCallBack;
import com.walid.jsbridge.factory.BridgeModule;
import com.walid.jsbridge.factory.JSCallData;
import com.walid.jsbridge.factory.JSMethod;
import com.walid.jsbridge.factory.JSMoudle;

import java.util.Map;

/**
 * Author   : walid
 * Date     : 2019-08-16  16:24
 * Describe :
 */
@JSMoudle(name = "planet")
public class PlanetModule extends BridgeModule {

    @JSMethod(alias = "onClick")
    public void onClick(BridgeWebView webView, Map<String, Object> map, IDispatchCallBack function) {
        Log.e("planet", map.toString());
        function.onCallBack(new JSCallData(0, "", "{\n" +
                "      \"userIdEcpt\": \"WVllUUczSFhISUxWVlgvZUh1NEExdz09\",\n" +
                "      \"count\": 1,\n" +
                "      \"invisible\": false,\n" +
                "      \"matchValue\": 0.93,\n" +
                "      \"time\": 1565753072172,\n" +
                "      \"user\": {\n" +
                "        \"userId\": 41874674,\n" +
                "        \"signature\": \"Avant Laube\",\n" +
                "        \"avatarName\": \"1558045574804\",\n" +
                "        \"avatarColor\": \"HeaderColor_Default\",\n" +
                "        \"background\": \"/image/2019-08-13/a0b544cc-0d2f-4cdd-a242-1fa2f5d2d2cc-1565673677111.png\",\n" +
                "        \"planet\": \"来自灵性艺术家星球\",\n" +
                "        \"cityId\": 131,\n" +
                "        \"gender\": 1,\n" +
                "        \"birthday\": 752515200000,\n" +
                "        \"constellation\": 9,\n" +
                "        \"deviceName\": \"EVA-TL00\",\n" +
                "        \"deleteTime\": null,\n" +
                "        \"createTime\": 1553086498000,\n" +
                "        \"modifyTime\": 1566162222000,\n" +
                "        \"countryName\": \"中国\",\n" +
                "        \"countryCode\": \"cn\",\n" +
                "        \"cityCode\": \"110000\",\n" +
                "        \"personalSignature\": \"Avant L'aube\",\n" +
                "        \"personalBackground\": \"/image/2019-08-19/ee9c8a15-f589-47cf-90f8-70f03668a2c9-1566162219195.png\",\n" +
                "        \"signatrue\": \"Avant Laube\"\n" +
                "      },\n" +
                "      \"openPush\": false\n" +
                "    }"));
    }

}
