package com.hlang.api;

import cn.hutool.extra.tokenizer.TokenizerUtil;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.NLPTokenizer;
import com.hankcs.hanlp.tokenizer.SpeedTokenizer;
import com.hankcs.hanlp.tokenizer.StandardTokenizer;
import com.xiaoping.base.BaseController;
import com.xiaoping.base.impl.BaseBizController;
import com.xiaoping.pojo.Rs;
import com.xiaoping.utils.DataRow;
import com.alibaba.csp.sentinel.annotation.SentinelResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@RestController
public class CfgApi extends BaseBizController {

    private Logger logger = Logger.getLogger("CfgApi");

    @Value("${user.name}")
    private String name;

    @Autowired
    private LoadBalancerClient loadBalancerClient;

    @Autowired
    private RestTemplate restTemplate;


    @RequestMapping("/name")
    public String name(){
        return name;
    }

    @SentinelResource(value = "api")
    @GetMapping("/echo/app-name")
    public String echoAppName(){
        //使用 LoadBalanceClient 和 RestTemolate 结合的方式来访问
        ServiceInstance serviceInstance = loadBalancerClient.choose("server-nacos");
        String url = String.format("http://%s:%s/echo/%s",serviceInstance.getHost(),serviceInstance.getPort(),name);
        System.out.println("request url:"+url);
        return restTemplate.getForObject(url,String.class);
    }

    @GetMapping("/hanlp/v1")
    public Rs hanlp() {
        String text = this.getStringParam("text");
        List<Term> segment = SpeedTokenizer.segment(text);
        List<DataRow> list = new ArrayList<>();
        segment.forEach(T->{
            DataRow data = new DataRow();
            data.set("word", T.word);
            data.set("nature", String.valueOf(T.nature));
            list.add(data);
        });
        return Rs.ok(list);
    }

    @GetMapping("/hanlp")
    public Rs segment() {
        String text = this.getStringParam("text");
        List<Term> segment = HanLP.segment(text.trim());
        List<DataRow> list = new ArrayList<>();
        segment.forEach(T->{
            DataRow data = new DataRow();
            data.set("word", T.word);
            data.set("nature", String.valueOf(T.nature));
            list.add(data);
        });
        return Rs.ok(list);
    }



}
