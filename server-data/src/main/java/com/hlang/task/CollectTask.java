package com.hlang.task;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hlang.constant.Constants;
import com.hlang.util.JsoupUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

/**
 * 定时任务：采集数据
 */
@Component
public class CollectTask {

    private Logger logger = LoggerFactory.getLogger(CollectTask.class);

    /**
     * 朝九晚九内 每半小时
     */
    @Scheduled(cron = "0 0/30 9-21 * * ?")
    public void excute() {
        logger.info("test" + System.currentTimeMillis());
    }

    public static void main(String[] args) throws Exception {
        String baseUrl = "https://tophub.today";
        String communityUrl = baseUrl.concat("/c/community"); // 社区

        Document doc = JsoupUtil.parse(communityUrl);
        Element v2Ele = doc.getElementById("node-7");// v2
        String v2href = v2Ele.getElementsByClass("cc-cd-is")
                .get(0)
                .getElementsByTag("a")
                .get(0)
                .attr("href");

        Document v2Doc = JsoupUtil.parse(baseUrl.concat(v2href));
        Elements articles = v2Doc.getElementsByTag("tr");
        articles.forEach(article -> {
            Element item = article.getElementsByTag("a")
                    .get(0);
            if (null != item && item.hasText()) {
                String url = item.attr("href");
                String tit = item.text();
                System.out.println(url + tit);
                List<Term> segment = HanLP.segment(tit);

            }
        });
    }

}
