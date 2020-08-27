package com.hlang.constant;

/**
 * Redis 常量类
 */
public class RedisConstants {
    /**
     * 描述： 构造方法
     *
     */
    private RedisConstants() {}

    /**
     * 前缀
     */
    public final static String REDISPERKEY = "com.hlang:voice:";

    /**
     * 言论
     */
    public static final String VOICE_HASH = REDISPERKEY + "voice_hash";

    /**
     * 言论的分词
     */
    public static final String VOICE_WORD_HASH = REDISPERKEY + "voice_word_hash";

    /**
     * 统计词频
     */
    public static final String WORD_COUNT_HASH = REDISPERKEY + "word_count_hash";

    /**
     * 言论对应的情绪
     */
    public static final String VOICE_SENTIMENT_HASH = REDISPERKEY + "voice_sentiment_hash";

    /**
     * 言论分类集合 flag: 0:负向，1:中性，2:正向
     */
    public static final String VOICE_SENTIMENT_FLAG_SET = REDISPERKEY + "voice_sentiment_flag_set:";

    /**
     * 言论消极的概率
     */
    public static final String VOICE_NEGATIVE_PROB_HASH = REDISPERKEY + "voice_negative_prob_hash";

    /**
     * 分词对应的言论ID word_voice_set + 分词
     */
    public static final String WORD_VOICE_SET = REDISPERKEY + "word_voice_set:";

}
