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

> **Current Version**：`4.1.x.20260630-SNAPSHOT`<br>
> **JDK Baseline**：`21`<br>
> **Group ID**：`io.github.easy4j`<br>
> **Artifact ID**：`ip2region-spring-boot-starter`<br>
> **License**：Apache License 2.0<br>

## 1. Positioning

**ip2region-spring-boot-starter** is a Spring Boot starter that integrates **ip2region** for applications using ip2region. It provides auto-configuration, property binding, and ready-to-use beans so that applications can consume ip2region capabilities with minimal setup.

| Dimension | Description |
|---|---|
| Type | Spring Boot Starter |
| Consumers | Spring Boot applications using ip2region |
| Core Capabilities | auto-configuration, property binding, ready-to-use beans for ip2region |
| JDK | `21` |
| Coordinates | `io.github.easy4j:ip2region-spring-boot-starter:4.1.x.20260630-SNAPSHOT` |
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
| JDK | `21` | `pom.xml` |
| Spring Boot | `4.1.2` | `pom.xml` parent |
| Maven | `4.0.0-rc-5` | `pom.xml` |

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
    <version>4.1.x.20260630-SNAPSHOT</version>
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

## 7. Configuration Reference

### 7.1 Config Prefix

`ip2region`

### 7.2 Configuration Items

| Property | Type | Default | Required | Description | Sensitive |
|---|---|---|:---:|---|:---:|
| `ip2region.external` | boolean | `false` | No | Load a custom XDB file | No |
| `ip2region.location` | string | `classpath:ip2region/ip2region.xdb` | No | XDB resource location | No |

## 8. Version Lines and Compatibility

| Branch | JDK | Spring Boot | Component Version | Status |
|---|---:|---:|---|:---:|
| `2.3.x` / `2.7.x` | `8+` | 2.3.x / 2.7.x | `1.0.x` | Maintenance |
| `3.0.x` ~ `3.5.x` | `17` | 3.x | `2.0.x` | Maintenance |
| `4.0.x` / `4.1.x` | 4.1.x: `21` | 4.x | 4.1.x: `2.7.0` | Active |

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
