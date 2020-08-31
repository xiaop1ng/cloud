package com.hlang.bot;

import cn.hutool.json.JSONUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hlang.constant.RedisKeys;
import com.hlang.util.BaiduAiUtil;
import com.hlang.util.JsoupUtil;
import com.xiaoping.utils.DateHelper;
import com.xiaoping.utils.StringHelper;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Component
public class TophubBot {

    @Autowired
    private StringRedisTemplate redis;

    private Logger logger = Logger.getLogger(TophubBot.class);

    public void execute(String site_type, String site_node) throws IOException {
        execute(site_type, site_node, null);
    }

    /**
     *
     * @param site_type 对应站点的类型
     * @param site_node 对应站点的节点
     * @param dist 保存数据时的区分划分
     * @throws IOException
     */
    public void execute(String site_type, String site_node, String dist) throws IOException {
        if (StringHelper.isBlank(dist)) dist = "";
        String baseUrl = "https://tophub.today";
        // 今天 20200909
        String day = DateHelper.formatDate(new Date(), "yyyyMMdd");
        RedisKeys redisKeys = RedisKeys.getInstance(dist);
        String communityUrl = baseUrl.concat(site_type); // 社区 => /c/community 分页 ?p=2
        Document doc = JsoupUtil.parse(communityUrl);
        Element v2Ele = doc.getElementById(site_node);// v2 => node-7
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
            if ( redis.opsForHash().hasKey(redisKeys.VOICE_HASH, id) ) return; // 去重
            redis.opsForHash().put(redisKeys.VOICE_HASH, id, tit);

            redis.opsForHash().put(redisKeys.VOICE_HASH + day, id, tit);

            List<Term> segment = HanLP.segment(tit);
            Object[] words = segment.stream().map(
                    T -> T.word.replaceAll( "[\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]" , "").toUpperCase())
                    .toArray();
            redis.opsForHash().put(redisKeys.VOICE_WORD_HASH, id, JSONUtil.toJsonStr(words));
            for (Object word : words) {
                redis.opsForHash().increment(redisKeys.WORD_COUNT_HASH, word, 1);
                redis.opsForHash().increment(redisKeys.WORD_COUNT_HASH + day, word, 1);
                String wd = String.valueOf(word);
                if ( StringHelper.isNotBlank(wd) )
                    redis.opsForSet().add(redisKeys.WORD_VOICE_SET + wd, id);
            }
            try {
                // 情感分析
                JSONObject sentiment = BaiduAiUtil.getSentiment(tit);
                String flag = sentiment.getJSONArray("items").getJSONObject(0).get("sentiment").toString();
                String negative_prob = sentiment.getJSONArray("items").getJSONObject(0).get("negative_prob").toString();
                redis.opsForHash().put(redisKeys.VOICE_SENTIMENT_HASH, id, sentiment.toString(2));
                redis.opsForSet().add(redisKeys.VOICE_SENTIMENT_FLAG_SET + flag, id);
                redis.opsForHash().put(redisKeys.VOICE_NEGATIVE_PROB_HASH, id, negative_prob);
                TimeUnit.SECONDS.sleep(1);
            } catch (Exception e) {
                logger.error("[baiduSdk error]", e);
            }
        });
        // 5 天后过期
        redis.expire(redisKeys.VOICE_HASH + day, 5, TimeUnit.DAYS);
        redis.expire(redisKeys.WORD_COUNT_HASH + day, 5, TimeUnit.DAYS);
    }
}
