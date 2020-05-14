# server-config

配置中心

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