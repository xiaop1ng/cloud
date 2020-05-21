package com.hlang.api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 注解 `@RefreshScope` 表识动态获取配置
@RefreshScope
@RestController
public class CfgApi {

    @Value("${user.name}")
    private String config;

    @RequestMapping("/cfg")
    public String get() {
        return config;
    }

    @GetMapping(value = "/echo/{string}")
    public String echo(@PathVariable String string) {
        return "Hello Nacos Discovery " + string;
    }


}
