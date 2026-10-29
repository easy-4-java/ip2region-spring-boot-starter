<a id="readme-top"></a>

<div align="center">

# ip2region-spring-boot-starter

**Spring Boot Starter for ip2region**

[![Maven Central](https://img.shields.io/maven-central/v/io.github.easy4j/ip2region-spring-boot-starter)](https://github.com/easy-4-java/ip2region-spring-boot-starter)
[![Java](https://img.shields.io/badge/Java-17-orange)](#3-requirements-and-compatibility)
[![License](https://img.shields.io/badge/license-Apache-2.0-green)](https://www.apache.org/licenses/LICENSE-2.0)

[简体中文](./README.zh-CN.md) | [English](./README.md)

[Positioning](#1-positioning) · [Capabilities](#2-core-capabilities) ·
[Dependency](#5-dependency) · [Quick Start](#6-quick-start) ·
[Configuration](#7-configuration-reference) · [Versions](#9-version-lines-and-compatibility) ·
[Build](#10-build-and-test) · [License](#12-license)

</div>

---

> **Current Version**：`2.3.x.20260630-SNAPSHOT`<br>
> **JDK Baseline**：`8`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`ip2region-spring-boot-starter`<br>
> **License**：Apache License 2.0<br>

## 1. Positioning

**Upstream project:** [ip2region](https://github.com/lionsoul2014/ip2region).

**ip2region-spring-boot-starter** is a Spring Boot starter that integrates **ip2region** for applications using ip2region. It provides auto-configuration, property binding, and ready-to-use beans so that applications can consume ip2region capabilities with minimal setup.

| Dimension | Description |
|---|---|
| Type | Spring Boot Starter |
| Consumers | Spring Boot applications using ip2region |
| Core Capabilities | auto-configuration, property binding, ready-to-use beans for ip2region |
| JDK | `8` |
| Coordinates | `io.github.easy4j:ip2region-spring-boot-starter:2.3.x.20260630-SNAPSHOT` |
| Config Prefix | `ip2region` |

## 2. Core Capabilities

| Capability | Status | Description |
|---|:---:|---|
| Auto-configuration | ✅ Stable | Registers ip2region beans automatically |
| Property Binding | ✅ Stable | Binds `ip2region.*` to `IP2regionProperties` |
| `XdbSearcher` bean | ✅ Stable | Auto-registered via IP2regionAutoConfiguration |

## 3. Requirements and Compatibility

| Dependency | Minimum | Evidence |
|---|---:|---|
| JDK | `8` | `pom.xml` |
| Spring Boot | `2.3.12.RELEASE` | `pom.xml` parent |
| Maven | `3.9.16` | `pom.xml` |

## 4. Auto-configuration

The starter auto-configures the following beans:

| Bean | Condition | Missing Behavior |
|---|---|---|
| `XdbSearcher` | classpath + property | not created |
| `IP2regionTemplate` | classpath + property | not created |

Auto-configuration registration:

- `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (this 4.1.x artifact)
- `META-INF/spring.factories` (retained for legacy registration)

## 5. Dependency

```xml
<dependency>
    <groupId>io.github.easy4j</groupId>
    <artifactId>ip2region-spring-boot-starter</artifactId>
    <version>2.3.x.20260630-SNAPSHOT</version>
</dependency>
```

No additional easy4j component dependencies.

## 6. Quick Start

### 6.1 Add dependency

Add the dependency above to your `pom.xml`.

### 6.2 Configure

No configuration is required for the bundled `classpath:ip2region/ip2region.xdb` database. To use your own XDB file:



```yaml
ip2region:
  external: true
  location: file:/path/to/ip2region.xdb
```

The legacy `ip2region.db` format is not compatible with the XDB searcher.

Bundled IPv4 XDB: [official ip2region v3.18.0](https://github.com/lionsoul2014/ip2region/blob/v3.18.0/data/ip2region_v4.xdb), SHA-256 `8e31bbdccb5bf21028af10592d4312ec975da0bffa108c0c5d862a12190f9ad3`. The current data layout is `country|province|city|ISP|ISO alpha-2`; country names outside China are in English. Legacy XDB records using `country|area|province|city|ISP` remain readable.

### 6.3 Use the bean

```java
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

Then inject the auto-configured bean in your code:

```java
@Autowired
private XdbSearcher xdbSearcher;
```

### IPv4 / IPv6 dual-stack queries

The Java search dependency is `org.lionsoul:ip2region:3.3.7`. Both bundled databases are loaded by default. Existing string APIs on `IP2regionTemplate` and `XdbSearcher` accept IPv4 and IPv6:

```java
String ipv4 = ip2regionTemplate.memorySearch("8.8.8.8");
String ipv6 = ip2regionTemplate.memorySearch("2001:4860:4860::8888");
RegionAddress address = ip2regionTemplate.getRegionAddress("2001:4860:4860::8888");
```

Legacy `external/location` properties retain their IPv4 meaning: `location` is ignored when `external=false`. IPv6 configuration is independent of `external`:

```yaml
ip2region:
  external: true
  location: file:/data/ip2region_v4.xdb
  ipv6-enabled: true
  ipv6-location: file:/data/ip2region_v6.xdb
```

`ipv6-enabled` defaults to `true`; `ipv6-location` defaults to `classpath:ip2region/ip2region_v6.xdb`. Disable IPv6 to skip its memory loading. Both resources accept plain file paths, `file:` and `classpath:` locations. Each database must match its address family.

`memorySearch(long)` remains IPv4-only, with range `0..4294967295`. String queries accept IP literals, including compressed and expanded IPv6; hostnames, brackets and zone IDs are rejected. IPv4-mapped IPv6 uses the IPv4 database. Invalid input throws `IOException` from `memorySearch`; template convenience methods retain their fallback behavior. Database loading errors are logged and return `NOT_MATCH` only for the affected family. Successful application startup does not prove both databases loaded successfully.

Full-memory queries support concurrent access. Queries after shutdown return `NOT_MATCH`; hot reload is not provided. The raw databases total approximately 46 MiB, increasing the JAR and memory footprint. Both existing constructors remain and enable bundled IPv6 by default. Direct consumers of upstream `Searcher` must adapt to its 3.x API; starter query signatures remain unchanged.

The IPv6 database is pinned to [upstream v3.18.0](https://github.com/lionsoul2014/ip2region/blob/v3.18.0/data/ip2region_v6.xdb), SHA-256 `939f6b46bd2b8bec3cf7c5ceb8ba782266ae9b1f35b5ba7916700dec0b7506ed`. Refer to upstream for data licensing.

## 7. Configuration Reference

### 7.1 Config Prefix

`ip2region`

### 7.2 Configuration Items

| Property | Type | Default | Required | Description | Sensitive |
|---|---|---|:---:|---|:---:|
| `ip2region.external` | boolean | `false` | No | Load a custom XDB file | No |
| `ip2region.location` | string | `classpath:ip2region/ip2region.xdb` | No | XDB resource location | No |
| `ip2region.ipv6-enabled` | boolean | `true` | No | Enable IPv6 | No |
| `ip2region.ipv6-location` | string | `classpath:ip2region/ip2region_v6.xdb` | No | IPv6 XDB resource | No |

## 8. Version Lines and Compatibility

| Branch | JDK | Spring Boot | ip2region Version | Status |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `3.3.7` | Maintenance |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `3.3.7` | Maintenance |
| `4.0.x` / `4.1.x` | 4.1.x: `21` | 4.x | `3.3.7` | Active |

## 9. Build and Test

```bash
mvn clean verify
```

## 10. Troubleshooting

| Symptom | Diagnosis | Resolution |
|---|---|---|
| Bean not created | Check auto-configuration report | Verify the ip2region dependency and XDB resource are available |
| `ClassNotFoundException` | Missing dependency | Add the required module |
| Version conflict | `mvn dependency:tree` | Use BOM for version alignment |

## 11. Contribution

1. Fork the repository.
2. Create a feature branch.
3. Run `mvn clean verify` before submitting.
4. Submit a pull request.

## 12. License

This project is licensed under the [Apache License, Version 2.0](https://www.apache.org/licenses/LICENSE-2.0).

---

<div align="center">

[Back to top](#readme-top) · [Issues](https://github.com/easy-4-java/ip2region-spring-boot-starter/issues) · [Repository](https://github.com/easy-4-java/ip2region-spring-boot-starter)

</div>
