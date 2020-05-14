# server-config

配置中心 port:8770

## 由于 `eureka` 添加了 `security` 组件

首先我们需要将 `WebSecurityConfig` csrf 配置忽略掉 `/eureka`
```java
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().ignoringAntMatchers("/eureka/**").ignoringAntMatchers("/eureka");
        super.configure(http);
    }

}
```

然后在 `server-config` 中配置 `defaultZone` 使用 form 方式登录至注册中心

```yml
eureka:
  client:
    serviceUrl:
      defaultZone: http://admin:admin@localhost:8761/eureka # eureka 服务中心地址
```

## Start

> http://127.0.0.1:8770/cfg/dev

该请求会访问 https://github.com/xiaop1ng/cloud 仓库的 /config/cfg-dev.properties

Resp:

```json
{
  "name": "cfg",
  "profiles": [
    "dev"
  ],
  "label": null,
  "version": "f2b97c0e846b00b2c078e303d92204be4a2615d7",
  "state": null,
  "propertySources": [
    {
      "name": "https://github.com/xiaop1ng/cloud/config/cfg-dev.properties",
      "source": {
        "name": "test"
      }
    }
  ]
}
```

## 访问配置文件

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```