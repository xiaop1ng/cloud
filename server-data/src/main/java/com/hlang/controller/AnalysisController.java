package com.hlang.controller;

import cn.hutool.core.util.URLUtil;
import cn.hutool.json.JSONUtil;
import com.google.gson.JsonObject;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hlang.constant.RedisConstants;
import com.hlang.util.BaiduAiUtil;
import com.hlang.util.JsoupUtil;
import com.hlang.util.Terms;
import com.xiaoping.pojo.Rs;
import com.xiaoping.utils.DataRow;
import javafx.collections.transformation.SortedList;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.xml.crypto.Data;
import java.beans.IntrospectionException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
public class AnalysisController {

    @Autowired
    private StringRedisTemplate redis;

    @GetMapping("/v1/wordcount")
    public Rs wordRank() {
        Map<Object, Object> wordCount = redis.opsForHash().entries(RedisConstants.WORD_COUNT_HASH);
        List<DataRow> list = new ArrayList<>();
        for (Object key: wordCount.keySet()) {
            Object val = wordCount.get(key);
            list.add(Terms.newInstance()
                .add("word", key)
                .add("count", val));
        }

        list.sort((o1, o2) -> {
            if (null == o1 && null == o2) return 0;
            if (null == o1) return -1;
            if (null == o2) return 1;
            if (o1.getLong("count") == o2.getLong("count")) return 0;
            return o1.getLong("count") > o2.getLong("count") ? -1: 1;
        });
        return Rs.ok(list.stream().filter(item-> item.getLong("count") > 1 && item.getString("word").length() > 1));
    };

    @GetMapping("/v2ex")
    public Rs analysisV2ex(
            @RequestParam(required = false, defaultValue = "/c/community") String type,
            @RequestParam(required = false, defaultValue = "node-7") String node) throws IOException {
        String baseUrl = "https://tophub.today";
        String communityUrl = baseUrl.concat(type); // 社区 => /c/community 分页 ?p=2
        Document doc = JsoupUtil.parse(communityUrl);
        Element v2Ele = doc.getElementById(node);// v2 => node-7
        if (null == v2Ele) return Rs.err(Rs.ERROR_CODE_BAD_REQUEST, "bad request.");
        String v2href = v2Ele.getElementsByClass("cc-cd-is")
                .get(0)
                .getElementsByTag("a")
                .get(0)
                .attr("href");

        Document v2Doc = JsoupUtil.parse(baseUrl.concat(v2href));
        Elements articles = v2Doc.getElementsByTag("tr");
        articles.stream().map(article -> article.getElementsByTag("a")
                .get(0)).filter(item -> null != item && item.hasText()).forEach(item -> {
            String url = item.attr("href");
            String tit = StringUtils.trimAllWhitespace(item.text()); // 去掉所有的空格
            String id = url;
            if ( redis.opsForHash().hasKey(RedisConstants.VOICE_HASH, id) ) return; // 去重
            redis.opsForHash().put(RedisConstants.VOICE_HASH, id, tit);
            List<Term> segment = HanLP.segment(tit);
            Object[] words = segment.stream().map(T -> T.word).toArray();
            redis.opsForHash().put(RedisConstants.VOICE_WORD_HASH, id, JSONUtil.toJsonStr(words));
            for (Object word : words) {
                redis.opsForHash().increment(RedisConstants.WORD_COUNT_HASH, word, 1);
                redis.opsForSet().add(RedisConstants.WORD_VOICE_SET + String.valueOf(word), id);
            }

            JSONObject sentiment = BaiduAiUtil.getSentiment(tit);
            String flag = sentiment.getJSONArray("items").getJSONObject(0).get("sentiment").toString();
            String negative_prob = sentiment.getJSONArray("items").getJSONObject(0).get("negative_prob").toString();
            redis.opsForHash().put(RedisConstants.VOICE_SENTIMENT_HASH, id, sentiment.toString(2));
            redis.opsForSet().add(RedisConstants.VOICE_SENTIMENT_FLAG_SET + flag, id);
            redis.opsForHash().put(RedisConstants.VOICE_NEGATIVE_PROB_HASH, id, negative_prob);
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        return Rs.ok();
    }

}
