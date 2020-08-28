package com.hlang.task;

import com.hlang.bot.TophubBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

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
        String[][] cfg= {
            {"/c/news", "node-1", "news"}, // 微博热搜
            {"/c/news", "node-51", "news"}, // 澎湃
            {"/c/news?p=2", "node-3608", "news"}, // 今日头条
            {"/c/news?p=3", "node-237", "news"}, // 网易新闻
            {"/c/community?p=2", "node-2566", "game"}, // NGA
            {"/c/ent?p=3", "node-203", "game"}, // 游研社
            {"/c/community?p=4", "node-321", "game"} // 虎扑
        };
        for (int i = 0; i < cfg.length; i++) {
            try {
                bot.execute(cfg[i][0], cfg[i][1], cfg[i][2]);
            } catch (IOException e) {
                logger.error("[×]执行任务出错" + i, e);
            }
        }

    }



}
