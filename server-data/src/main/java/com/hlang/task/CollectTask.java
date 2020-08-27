package com.hlang.task;

import cn.hutool.core.util.URLUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hlang.bot.TophubBot;
import com.hlang.constant.Constants;
import com.hlang.constant.RedisConstants;
import com.hlang.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisConnectionUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

/**
 * 定时任务：采集数据
 */
@Component
public class CollectTask {

    private Logger logger = LoggerFactory.getLogger(CollectTask.class);

    @Autowired
    private TophubBot bot;

    /**
     * 朝九晚九内 每半小时
     */
    @Scheduled(cron = "0 0/30 9-21 * * ?")
    public void excute() {
        logger.info("test" + System.currentTimeMillis());
        // execute -> 微博、澎湃、头条、网易新闻
        String[][] cfg= { {"/c/news", "node-1"}, // 微博热搜
            {"/c/news", "node-51"}, // 澎湃
            {"/c/news?p=2", "node-3608"}, // 今日头条
            {"/c/news?p=3", "node-237"} // 网易新闻
        };
        for (int i = 0; i < cfg.length; i++) {
            try {
                bot.execute(cfg[i][0], cfg[i][1]);
            } catch (IOException e) {
                logger.error("[×]执行任务出错" + i, e);
            }
        }

    }



}
