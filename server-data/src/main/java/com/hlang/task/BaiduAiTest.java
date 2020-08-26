package com.hlang.task;

import com.baidu.aip.nlp.AipNlp;
import org.json.JSONObject;

import java.util.HashMap;

public class BaiduAiTest {

    public static final String APP_ID = "22271625";
    public static final String API_KEY = "elrzPpGEfCAMbiZRsBgbmPqj";
    public static final String SECRET_KEY = "yvURkUjOmnXMrYGrdBtK6Y6qj5k1DG9L";

    public static void main(String[] args) {
        // 初始化一个AipNlp
        AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
        String text = "你们遇到过组长强制要求代码实现的吗?";
        HashMap<String, Object> options = new HashMap<String, Object>();
        JSONObject res = client.sentimentClassify(text, null);
        System.out.println(res.toString(2));
        /**
         * {
         *   "log_id": 6211481725896222554,
         *   "text": "为什么其他汽车厂商卖车不能像特斯拉一样互联网销售，价格公开透明？",
         *   "items": [{
         *     "positive_prob": 0.0265329, // 积极的概率
         *     "sentiment": 0, // 情感极性分类结果, 0:负向，1:中性，2:正向
         *     "confidence": 0.941038,
         *     "negative_prob": 0.973467 // 消极类别的概率
         *   }]
         * }
         */
    }
}
