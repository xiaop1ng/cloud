package com.hlang.util;

import com.hlang.constant.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class JsoupUtil {

    public static Document parse(String url) throws IOException {
        return Jsoup.connect(url)
                .userAgent(Constants.USER_AGENT)
                .ignoreHttpErrors(true)
                .get();
    }

}
