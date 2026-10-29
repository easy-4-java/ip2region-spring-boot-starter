<a id="readme-top"></a>

<div align="center">

# ip2region-spring-boot-starter

**Spring Boot Starter，集成 ip2region**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/ip2region-spring-boot-starter)](https://github.com/easy-4-java/ip2region-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17-orange)](#3-运行要求与兼容性)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[English](./README.md) | [简体中文](./README.zh-CN.md)

[项目定位](#1-项目定位) · [核心能力](#2-核心能力) ·
[引入依赖](#5-引入依赖) · [快速开始](#6-快速开始) ·
[配置参考](#7-配置参考) · [版本线](#8-版本线与兼容性) ·
[构建测试](#9-构建与测试) · [许可证](#12-许可证)

</div>

---

> **当前版本**：`3.2.x.20260630-SNAPSHOT`<br>
> **JDK 基线**：`17`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`ip2region-spring-boot-starter`<br>
> **许可证**：Apache License 2.0<br>

## 1. 项目定位

**上游项目：** [ip2region](https://github.com/lionsoul2014/ip2region)。

**ip2region-spring-boot-starter** 是一个面向 使用 ip2region 的应用 的 Spring Boot Starter，用于将 **ip2region** 集成到 Spring Boot 应用中。它提供自动装配、属性绑定与开箱即用的 Bean，使应用以最小配置即可使用 ip2region 的全部能力。

| 维度 | 说明 |
|---|---|
| 类型 | Spring Boot Starter |
| 消费方 | 使用 ip2region 的 Spring Boot 应用 |
| 核心能力 | 自动装配、属性绑定、开箱即用的 ip2region Bean |
| JDK | `17` |
| 坐标 | `io.github.easy4j:ip2region-spring-boot-starter:3.2.x.20260630-SNAPSHOT` |
| 配置前缀 | `ip2region` |

## 2. 核心能力

| 能力 | 状态 | 说明 |
|---|:---:|---|
| 自动装配 | ✅ 稳定 | 自动注册 ip2region 相关 Bean |
| 属性绑定 | ✅ 稳定 | 绑定 `ip2region.*` 到 `IP2regionProperties` |
| `XdbSearcher` Bean | ✅ 稳定 | 通过 IP2regionAutoConfiguration 自动注册 |

## 3. 运行要求与兼容性

| 依赖 | 最低版本 | 证据来源 |
|---|---:|---|
| JDK | `17` | `pom.xml` |
| Spring Boot | `3.2.12` | `pom.xml` parent |
| Maven | `3.9.16` | `pom.xml` |

## 4. 自动装配

Starter 自动装配以下 Bean：

| Bean | 条件 | 缺失时行为 |
|---|---|---|
| `XdbSearcher` | classpath + property | 不创建 |
| `IP2regionTemplate` | classpath + property | 不创建 |

自动装配注册：

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`（当前 4.1.x 构件）
- `META-INF/spring.factories`（保留作旧版注册）

## 5. 引入依赖

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>ip2region-spring-boot-starter</artifactId>
    <version>3.2.x.20260630-SNAPSHOT</version>
</dependency>
```

无其他 easy4j 组件依赖。

## 6. 快速开始

### 6.1 引入依赖

在 `pom.xml` 中添加上述依赖。

### 6.2 配置

默认使用内置的 `classpath:ip2region/ip2region.xdb`，无需配置。使用自有 XDB 文件时：



```yaml
ip2region:
  external: true
  location: file:/path/to/ip2region.xdb
```

旧版 `ip2region.db` 与当前 XDB 查询器不兼容。

内置 IPv4 XDB 来自 [ip2region 官方 v3.18.0](https://github.com/lionsoul2014/ip2region/blob/v3.18.0/data/ip2region_v4.xdb)，SHA-256：`8e31bbdccb5bf21028af10592d4312ec975da0bffa108c0c5d862a12190f9ad3`。新版字段顺序为 `国家|省份|城市|运营商|ISO 两位国家代码`；中国以外的国家名称为英文。旧版 XDB 的 `国家|地区|省份|城市|运营商` 记录仍可解析。

### 6.3 使用 Bean

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

在业务代码中注入自动装配的 Bean：

```java
@Autowired
private XdbSearcher xdbSearcher;
```

### IPv4 / IPv6 双栈查询

Java 查询依赖为 `org.lionsoul:ip2region:3.3.7`。默认同时加载内置 IPv4 与 IPv6 数据，现有 `IP2regionTemplate` 和 `XdbSearcher` 的字符串查询接口均支持两种地址：

```java
String ipv4 = ip2regionTemplate.memorySearch("8.8.8.8");
String ipv6 = ip2regionTemplate.memorySearch("2001:4860:4860::8888");
RegionAddress address = ip2regionTemplate.getRegionAddress("2001:4860:4860::8888");
```

旧 `external/location` 配置保持原语义：仅控制 IPv4，且 `external=false` 时忽略 `location`。IPv6 使用独立配置，不受 `external` 影响：

```yaml
ip2region:
  external: true
  location: file:/data/ip2region_v4.xdb
  ipv6-enabled: true
  ipv6-location: file:/data/ip2region_v6.xdb
```

`ipv6-enabled` 默认 `true`；`ipv6-location` 默认 `classpath:ip2region/ip2region_v6.xdb`。设为 `false` 可关闭 IPv6 查询及其内存加载。两种资源都支持普通文件路径、`file:` 和 `classpath:`。每种地址族必须使用对应 XDB，不能互换。

`memorySearch(long)` 保持 IPv4 接口，范围为 `0..4294967295`。字符串接口只接受 IP 字面量，支持压缩和完整 IPv6，不接受域名、方括号或 zone ID；IPv4-mapped IPv6 按 IPv4 查询。非法输入在 `memorySearch` 抛出 `IOException`，模板 `getRegion*` 等便利接口继续使用既有兜底结果。数据加载失败记录错误日志并仅使对应地址族返回 `NOT_MATCH`，不会禁用另一地址族；启动成功不代表两份数据均加载成功。

全内存查询支持并发；关闭后返回 `NOT_MATCH`，无文件热更新。默认数据库原始大小合计约 46 MiB，JAR 和运行内存均会增加。两个旧构造器保留，默认增加内置 IPv6；依赖上游 `Searcher` 的调用方须适配 3.x API，starter 自身公开查询签名不变。

IPv6 数据固定来自 [上游 v3.18.0](https://github.com/lionsoul2014/ip2region/blob/v3.18.0/data/ip2region_v6.xdb)，SHA-256：`939f6b46bd2b8bec3cf7c5ceb8ba782266ae9b1f35b5ba7916700dec0b7506ed`。数据许可沿用上游，详见其仓库说明。

## 7. 配置参考

### 7.1 配置前缀

`ip2region`

### 7.2 配置项

| 属性 | 类型 | 默认值 | 必填 | 说明 | 敏感 |
|---|---|---|:---:|---|:---:|
| `ip2region.external` | boolean | `false` | 否 | 是否加载自有 XDB 文件 | 否 |
| `ip2region.location` | string | `classpath:ip2region/ip2region.xdb` | 否 | XDB 资源位置 | 否 |
| `ip2region.ipv6-enabled` | boolean | `true` | 否 | 启用 IPv6 | 否 |
| `ip2region.ipv6-location` | string | `classpath:ip2region/ip2region_v6.xdb` | 否 | IPv6 XDB 资源位置 | 否 |

## 8. 版本线与兼容性

| 分支 | JDK | Spring Boot | 组件版本 | 状态 |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | 维护中 |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | 维护中 |
| `4.0.x` / `4.1.x` | 4.1.x: `21` | 4.x | 4.1.x: `3.3.7` | 活跃开发 |

## 9. 构建与测试

```bash
mvn clean verify
```

## 10. 排障

| 症状 | 诊断 | 解决 |
|---|---|---|
| Bean 未创建 | 查看自动装配报告 | 确认 ip2region 依赖和 XDB 资源可用 |
| `ClassNotFoundException` | 缺少依赖 | 引入对应模块 |
| 版本冲突 | `mvn dependency:tree` | 使用 BOM 统一版本 |

## 11. 贡献

1. Fork 本仓库。
2. 创建特性分支。
3. 提交前运行 `mvn clean verify`。
4. 提交 Pull Request。

## 12. 许可证

本项目采用 [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0) 许可证。

---

<div align="center">

[返回顶部](#readme-top) · [问题反馈](https://github.com/easy-4-java/ip2region-spring-boot-starter/issues) · [仓库地址](https://github.com/easy-4-java/ip2region-spring-boot-starter)

</div>
