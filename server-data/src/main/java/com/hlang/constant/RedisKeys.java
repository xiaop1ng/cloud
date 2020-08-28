package com.hlang.constant;

import com.xiaoping.utils.StringHelper;

public class RedisKeys {

    /**
     * 前缀
     */
    private String REDISPERKEY = "com.hlang:voice:";

    /**
     * 描述： 构造方法
     *
     */
    public RedisKeys(String dist) {
        this.REDISPERKEY = this.REDISPERKEY + dist + ":";
        this.VOICE_HASH =  this.REDISPERKEY + "voice_hash";
        this.VOICE_WORD_HASH = this.REDISPERKEY + "voice_word_hash";
        this.WORD_COUNT_HASH = this.REDISPERKEY + "word_count_hash";
        this.VOICE_SENTIMENT_HASH = this.REDISPERKEY + "voice_sentiment_hash";
        this.VOICE_SENTIMENT_FLAG_SET = this.REDISPERKEY + "voice_sentiment_flag_set:";
        this.VOICE_NEGATIVE_PROB_HASH = this.REDISPERKEY + "voice_negative_prob_hash";
        this.WORD_VOICE_SET = this.REDISPERKEY + "word_voice_set:";
    }

    public RedisKeys() {}

    public static RedisKeys getInstance(String dist) {
        if (StringHelper.isBlank(dist)) return new RedisKeys();
        return new RedisKeys(dist);
    }


    /**
     * 言论
     */
    public String VOICE_HASH = REDISPERKEY + "voice_hash";

    /**
     * 言论的分词
     */
    public String VOICE_WORD_HASH = REDISPERKEY + "voice_word_hash";

    /**
     * 统计词频
     */
    public String WORD_COUNT_HASH = REDISPERKEY + "word_count_hash";

    /**
     * 言论对应的情绪
     */
    public String VOICE_SENTIMENT_HASH = REDISPERKEY + "voice_sentiment_hash";

    /**
     * 言论分类集合 flag: 0:负向，1:中性，2:正向
     */
    public String VOICE_SENTIMENT_FLAG_SET = REDISPERKEY + "voice_sentiment_flag_set:";

    /**
     * 言论消极的概率
     */
    public String VOICE_NEGATIVE_PROB_HASH = REDISPERKEY + "voice_negative_prob_hash";

    /**
     * 分词对应的言论ID word_voice_set + 分词
     */
    public String WORD_VOICE_SET = REDISPERKEY + "word_voice_set:";

}
