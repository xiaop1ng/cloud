package com.hlang.controller;

import com.hlang.constant.RedisKeys;
import com.hlang.util.Terms;
import com.xiaoping.pojo.Rs;
import com.xiaoping.utils.DataRow;
import com.xiaoping.utils.DateHelper;
import com.xiaoping.utils.StringHelper;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class AnalysisController {

    @Autowired
    private StringRedisTemplate redis;

    private String day = DateHelper.formatDate(new Date(), "yyyyMMdd");

    @GetMapping("/v1/wordcount/{word}")
    public Rs getWordVoices(@PathVariable(required = true) String word) {
        return getWordVoices(null, null, word);
    }

    /**
     * 获取今日词条对应言论数据
     * @param word
     * @param dist
     * @return
     */
    @GetMapping("/v2/wordcount/{word}")
    public Rs getTodayWordVoices(@PathVariable(required = true) String word,
                       @RequestParam(required = false) String dist) {
        return getWordVoices(dist, day, word);
    }

    /**
     * 获取今日的热点数据
     * @param dist
     * @return
     */
    @GetMapping("/v2/wordcount")
    public Rs todayWordRank(@RequestParam(required = false) String dist,
                            @RequestParam(required = false) Integer filter) {
        return getWordRank(dist, day, filter);
    }

    @GetMapping("/v1/wordcount")
    public Rs wordRank( @RequestParam(required = false) Integer filter ) {
        return getWordRank(null,null, filter);
    };

    @GetMapping("/v2/feel/{dist}")
    public Rs feel(@PathVariable(required = true) String dist) {
        RedisKeys redisKeys = RedisKeys.getInstance(dist);
        Long neSize = redis.opsForSet().size(redisKeys.VOICE_SENTIMENT_FLAG_SET + "0"); //  0:负向，1:中性，2:正向
        Long noSize = redis.opsForSet().size(redisKeys.VOICE_SENTIMENT_FLAG_SET + "1"); //  0:负向，1:中性，2:正向
        Long poSize = redis.opsForSet().size(redisKeys.VOICE_SENTIMENT_FLAG_SET + "2"); //  0:负向，1:中性，2:正向
        return Rs.ok(Terms.newInstance()
                .add("negative", neSize) // 消极
                .add("normal", noSize) // 平常
                .add("positive", poSize) // 积极
        );
    }

    /**
     * 获取关键词的排行
     * @param time
     * @return
     */
    private Rs getWordRank (String dist, String time, Integer filter) {
        List<DataRow> list = new ArrayList<>();
        RedisKeys redisKeys = RedisKeys.getInstance(dist);
        String wordCountKey = redisKeys.WORD_COUNT_HASH;
        if (StringHelper.isNotBlank(time)) {
            wordCountKey = redisKeys.WORD_COUNT_HASH + time;
        }
        Map<Object, Object> wordCount = redis.opsForHash().entries(wordCountKey);
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
        if (null == filter || filter < 1) filter = 1;
        int f = filter.intValue();
        return Rs.ok(list.stream().filter(item-> item.getLong("count") > f && item.getString("word").length() > 1));
    }

    /**
     * 获取关键词对应的言论
     * @param dist
     * @param time
     * @param word
     * @return
     */
    private Rs getWordVoices(String dist, String time, String word) {
        List<DataRow> list = new ArrayList<>();
        RedisKeys redisKeys = RedisKeys.getInstance(dist);
        Set<String> members = redis.opsForSet().members(redisKeys.WORD_VOICE_SET + word);
        members.forEach(T->{
            DataRow data = Terms.newInstance()
                    .add("url", T)
                    .add("tit", redis.opsForHash().get(redisKeys.VOICE_HASH, T));
            Object sentiment = redis.opsForHash().get(redisKeys.VOICE_SENTIMENT_HASH, T);
            String json = null == sentiment ? "" : sentiment.toString();
            if (StringHelper.isNotBlank(json)) {
                JSONObject ret = new JSONObject(json);
                JSONObject sentimentObj = ret.getJSONArray("items").getJSONObject(0);
                if(null != sentimentObj) {
                    for (String key: sentimentObj.keySet() ) {
                        data.set(key, String.valueOf(sentimentObj.get(key)));
                    }
                }
            }
            if (StringHelper.isNotBlank(time)) // 没有时间限制
                list.add(data);
            else if ( redis.opsForHash().hasKey(redisKeys.VOICE_HASH + day, T) ) // 只要今天的
                list.add(data);
        });
        return Rs.ok(list);
    }

}
