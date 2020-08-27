package com.hlang.bot;

import cn.hutool.json.JSONUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hlang.constant.RedisConstants;
import com.hlang.util.BaiduAiUtil;
import com.hlang.util.JsoupUtil;
import org.jboss.logging.Logger;
import org.json.JSONObject;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TophubBot {

    @Autowired
    private StringRedisTemplate redis;

    private Logger logger = Logger.getLogger(TophubBot.class);

    public void execute(String type, String node) throws IOException {
        String baseUrl = "https://tophub.today";
        String communityUrl = baseUrl.concat(type); // 社区 => /c/community 分页 ?p=2
        Document doc = JsoupUtil.parse(communityUrl);
        Element v2Ele = doc.getElementById(node);// v2 => node-7
        if (null == v2Ele) return;
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
            Object[] words = segment.stream().map(
                    T -> T.word.replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , ""))
                    .toArray();
            redis.opsForHash().put(RedisConstants.VOICE_WORD_HASH, id, JSONUtil.toJsonStr(words));
            for (Object word : words) {
                redis.opsForHash().increment(RedisConstants.WORD_COUNT_HASH, word, 1);
                redis.opsForSet().add(RedisConstants.WORD_VOICE_SET + String.valueOf(word), id);
            }
            try {
                // 情感分析
                JSONObject sentiment = BaiduAiUtil.getSentiment(tit);
                String flag = sentiment.getJSONArray("items").getJSONObject(0).get("sentiment").toString();
                String negative_prob = sentiment.getJSONArray("items").getJSONObject(0).get("negative_prob").toString();
                redis.opsForHash().put(RedisConstants.VOICE_SENTIMENT_HASH, id, sentiment.toString(2));
                redis.opsForSet().add(RedisConstants.VOICE_SENTIMENT_FLAG_SET + flag, id);
                redis.opsForHash().put(RedisConstants.VOICE_NEGATIVE_PROB_HASH, id, negative_prob);
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                logger.error("[baiduSdk error]", e);
            }
        });
    }
}
