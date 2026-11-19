# ip2region #10 的 Java 8 安全修复评审

## 原始报告复现

Safer 在 [e9e0c81](https://github.com/easy-4-java/ip2region-spring-boot-starter/commit/e9e0c81e64b20e65f5411f16d0fd2c009db58cf2) 标记三个直接依赖的建议版本。报告中的 `ip2region:2.7.0` 保持原版本。[原始报告](https://gist.github.com/safer-bot/6c560bf22708a806c6a1524f08daab5a) · [Issue #10](https://github.com/easy-4-java/ip2region-spring-boot-starter/issues/10)

| 依赖 | 原始版本 | Safer 建议 |
| --- | --- | --- |
| `spring-boot-starter` | 2.6.0 | 3.3.12 |
| `spring-boot-starter-test` | 2.6.0 | 3.0.8 |
| `spring-boot-autoconfigure` | 2.6.0 | 3.3.12 |

在独立目录检出报告的原始提交，只给上述三个直接依赖添加建议版本，保留原父 POM。以 Corretto 8u504 和 Maven 3.9.16 执行 `mvn -B -ntp -nsu -Djava.version=1.8 clean test`。编译在读取 `spring-boot-autoconfigure-3.3.12.jar` 的 `ConditionalOnClass` 时失败。该类的字节码版本为 61，Java 8 编译器需要不高于 52 的版本。运行未到达测试阶段。

## 保留 Java 8 所需的修复版本

默认分支 [699d419](https://github.com/easy-4-java/ip2region-spring-boot-starter/commit/699d41922f67473d717c6242f821af963caea17e) 仍以 Spring Boot 2.7.18 和 Spring Framework 5.3.39 构建。以下是同一默认分支 OSV 扫描列出的 11 条公告在各自 2.7 或 5.3 版本线上的官方修复版本。[默认分支扫描记录](https://github.com/easy-4-java/ip2region-spring-boot-starter/issues/10#issuecomment-6962707676)

| 组件 | 公告 | 对应维护线修复版本 |
| --- | --- | --- |
| Spring Boot | [CVE-2025-22235](https://spring.io/security/cve-2025-22235/) | 2.7.25 |
| Spring Boot | [CVE-2026-40973](https://spring.io/security/cve-2026-40973/) | 2.7.33 |
| Spring Boot Autoconfigure | [CVE-2026-41001](https://spring.io/security/cve-2026-41001/) | 2.7.34 |
| Spring Context | [CVE-2024-38820](https://spring.io/security/cve-2024-38820/) | 5.3.41 |
| Spring Context | [CVE-2025-22233](https://spring.io/security/cve-2025-22233/) | 5.3.43 |
| Spring Core | [CVE-2025-41249](https://spring.io/security/cve-2025-41249/) | 5.3.45 |
| Spring Core | [CVE-2026-41848](https://spring.io/security/cve-2026-41848/) | 5.3.49 |
| Spring Expression | [CVE-2026-41849](https://spring.io/security/cve-2026-41849/) | 5.3.49 |
| Spring Expression | [CVE-2026-41850](https://spring.io/security/cve-2026-41850/) | 5.3.49 |
| Spring Expression | [CVE-2026-41851](https://spring.io/security/cve-2026-41851/) | 5.3.49 |
| Spring Expression | [CVE-2026-41852](https://spring.io/security/cve-2026-41852/) | 5.3.49 |

对这 11 条公告，维护 Java 8 的版本下限是企业版 Boot **2.7.34**、Framework **5.3.49**。Maven Central 对两个精确 POM 均返回 HTTP 404，当前 Maven 配置也未能解析。此路径的依赖图、应用启动和回归测试待取得有授权的制品后验证。版本下限仅覆盖上表列出的公告。

## 已验证的公开版本线

[Boot 3.5](https://github.com/easy-4-java/ip2region-spring-boot-starter/pull/15) 和 [Boot 4.1](https://github.com/easy-4-java/ip2region-spring-boot-starter/pull/14) 已合入各自维护分支，合并后分别执行 88 项测试。此前的零发现扫描对应提交 `26811a3` 和 `9044992`。从这些扫描提交到各自当前的 `dce32f9` 和 `c2bc1b8`，GitHub 比对显示变更仅涉及兼容接口、回归测试和 CI，POM 字节内容与扫描基线相同。[Boot 3.5 比对](https://github.com/easy-4-java/ip2region-spring-boot-starter/compare/26811a38ff84f9e9709cfa7b0b37b1442c2d395d...dce32f9c56a71913af4155e7821ea7233156f45b) · [Boot 4.1 比对](https://github.com/easy-4-java/ip2region-spring-boot-starter/compare/9044992c142fc60d0fb99232aab8f8f7dce775e3...c2bc1b8926e0edc5f6c4e9598c99c5fb798aeaa3)

默认线的处置还取决于是否继续承诺 Java 8，以及企业版制品能否取得。迁移到公开 Boot 3.5 版本线要求 Java 17。Issue #10 保持开放。
