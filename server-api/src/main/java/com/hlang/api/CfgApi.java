package com.hlang.api;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.common.Term;
import com.xiaoping.base.BaseController;
import com.xiaoping.base.impl.BaseBizController;
import com.xiaoping.pojo.Rs;
import com.xiaoping.utils.DataRow;
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

    @GetMapping("/echo/app-name")
    public String echoAppName(){
        //使用 LoadBalanceClient 和 RestTemolate 结合的方式来访问
        ServiceInstance serviceInstance = loadBalancerClient.choose("server-nacos");
        String url = String.format("http://%s:%s/echo/%s",serviceInstance.getHost(),serviceInstance.getPort(),name);
        System.out.println("request url:"+url);
        return restTemplate.getForObject(url,String.class);
    }

    @GetMapping("/hanlp")
    public Rs call() {
        String text = this.getStringParam("text");
        List<Term> segment = HanLP.segment(text);
        List<DataRow> list = new ArrayList<>();
        logger.info("[size]" + segment.size());
        List<String> list2 = HanLP.extractKeyword(text, 1);


        segment.forEach(T->{
            DataRow data = new DataRow();
            data.set("word", T.word);
            data.set("nature", String.valueOf(T.nature));
            data.set("offset", T.offset);
            list.add(data);
        });
        return Rs.ok(list);
    }

}
