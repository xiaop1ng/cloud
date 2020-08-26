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
    public static final String VOICE_HASH = REDISPERKEY + "userinfo:user_id:";
}
