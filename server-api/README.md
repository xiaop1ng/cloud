# server-api

面向用户的接口服务 port: 8770

## Build

离线打包

> mvn clean package

> 注意：如果 build 失败，需要再 cloud 目录下执行 `mvn install`


## Start

>  java -jar server-config-1.0-BUILD.jar &


> http://127.0.0.1:8771/name

当配置中心服务 `server-config` 存活的时候，会读取 `server-api-dev.properties` 中的 name 值

-> xiaop1ng

如果 `server-config` 不幸 dump 掉了，则会读取到 `application.yml` 中的 name 值

-> test

需要**注意**的是：Spring 的 `@Value` 值是一次性注入的。也就是说当配置中心的值被修改过，`@Value` 依旧是读取启动时的配置值

> http://127.0.0.1:8771/echo/app-name

这里启用了两个 server-nacos 应用，所以这里会有两种不同的响应
```
8081: Hello Nacos Discovery xiaop1ng // or
8080: Hello Nacos Discovery xiaop1ng 
```

如果我们其中一个节点应用挂掉了，请求就会都落到另外一个节点，
如果后来我们恢复宕掉的那个节点，nacos 则会重新负载请求到两台节点上。