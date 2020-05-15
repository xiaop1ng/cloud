package com.hlang.api;

import cn.hutool.crypto.SecureUtil;
import com.xiaoping.base.impl.BaseBizController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class WxApi extends BaseBizController {

    @RequestMapping("/wx")
    public String wx() {
        String signature = this.getStringParam("signature");
        String timestamp = this.getStringParam("timestamp");
        String nonce = this.getStringParam("nonce");

        String echostr = this.getStringParam("echostr");

        String token = "weixin";
        List<String> arr = Arrays.asList(timestamp, nonce, token);
        Object strs[] = arr.toArray();
        Arrays.sort(strs);
        String allInOne = "";
        for (int i = 0; i < strs.length; i++) {
            allInOne += strs[i].toString();
        }
        String v = SecureUtil.sha1(allInOne);
        if (v.equals(signature)) {
            return echostr;
        }

        return "ok";

    }
}
