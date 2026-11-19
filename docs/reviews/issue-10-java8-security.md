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

## 默认线决定与消费验证

默认分支继续支持 Java 8。取得授权的 Spring 企业版制品后，再在该线验证依赖树、应用启动和安全公告。公开 Boot 3.5 与 4.1 线已分别交付，Java 17 的公开依赖线不用于替换默认分支。

独立 Maven 消费应用分别加载默认分支 `699d419` 和 Boot 3.5 `dce32f9` 构建的 Starter JAR，通过正常 `@EnableAutoConfiguration` 启动。每线各 3 项测试通过，覆盖默认 XDB、显式外部配置和单独设置 `ip2region.location`。在默认线单独配置 location 会选择该资源。Boot 3.5 需要同时设置 `external=true` 才选择自定义 IPv4 资源。用缺失文件验证时，选中该资源的查询按既有约定返回 NOT_MATCH。

## 无 Broadcom 制品时的 Java 8 替代来源

HeroDevs NES 的 Boot 2.7 版本线支持 Java 8。Boot 修复候选下限为 `org.springframework.boot:spring-boot-starter-parent:pom:2.7.18-spring-boot-2.7.40`。发布记录显示，2.7.25、2.7.38 和 2.7.40 分别修复了本 Issue 所列的三条 Boot 公告。Framework 建议评估 `org.springframework:spring-framework-bom:pom:5.3.39-spring-framework-5.3.54`，其发布记录明确列出 CVE-2026-41848 至 CVE-2026-41852。两套候选仍需在实际解析到的完整依赖树上验证。[Boot 发布记录](https://docs.herodevs.com/spring/release-notes/spring-boot-2-7) · [Framework 发布记录](https://docs.herodevs.com/spring/release-notes/spring-framework-5-3)

按官方试用方式实测，公开的 Boot 2.7.21 试用 POM 返回 HTTP 200，发布时间早于本 Issue 所列的 2026 年 Boot 修复。正式 2.7.40 和 2.7.44 POM 在无订阅凭据的请求中均返回 HTTP 401。现有证据只证明试用制品可取。正式验证需取得允许读取候选版本的 Maven 仓库权限，并确保项目 CI 和该公开 Starter 的消费方能解析所选制品。[官方试用说明](https://docs.herodevs.com/spring/spring-boot-2-x-portfolio/spring-boot-2-7/trial)

## SAS 补丁和代码托管发布附件

SAS 的 M3P010 清单列出多个 Spring Framework 5.3.49 运行时 JAR。替代它的 M3P012 清单列出 Framework 5.3.52 的运行时 JAR。两份清单均未列出 Boot 2.7.34 模块或 Maven 父 POM、BOM。SAS 下载页明确要求已有 SAS 软件许可，核查仅使用公开清单。[SAS 下载条款](https://tshf.sas.com/techsup/download/hotfix/HF2/M3P.html) · [M3P010 清单](https://tshf.sas.com/techsup/download/hotfix/HF2/M/M3P/M3P010/xx/prt/M3P010pt_manifest.html) · [M3P012 清单](https://tshf.sas.com/techsup/download/hotfix/HF2/M/M3P/M3P012/xx/prt/M3P012pt_manifest.html)

GitHub 的公开仓库和精确文件名搜索未找到所需的完整发布附件。发现的 Framework 5.3.49 第三方回移仓库只有源码，GitHub API 返回空的 Releases 与 Tags 列表。Gitee 公开项目搜索按两条目标版本名均返回零结果。缺少完整候选制品，本轮未进入 Java 8 构建验收。[GitHub 回移仓库](https://github.com/meimingqi222/spring-framework-5.3.49-security-backport)

## 本地回移候选验证

Spring Framework 从第三方 Apache 2.0 回移树 `16a56ef8631a2203fdf9a3a6aec35655ed939f25` 构建。依照上游 `5c5cf73e11c76ad68fec92619619999fbea2fd3c` 补齐大小写匹配行为。Spring Boot 从公开 `v2.7.18` 的 `0c8b382d42db22b92efcf47000d0ff9ef4971629` 构建。自行构建的制品使用 `-easy4j-review.1` 版本后缀。

| 组件 | 制品版本 | 实际验证 |
| --- | --- | --- |
| Spring Framework | `5.3.49-easy4j-review.1` | Core、Context 和 Expression 的 JDK 8 测试共 7,835 项，其中 65 项跳过、零失败 |
| Spring Boot | `2.7.18-easy4j-review.1` | 原工程三模块的回移回归共 59 项全部通过。独立 JDK 8 验证同一补丁源码的 59 项也全部通过 |
| ip2region | `2.0.x.20260630-SNAPSHOT` | Corretto 8u504 的 `clean install` 通过。独立消费应用通过默认 XDB 查询和仅指定位置时的行为检查 |

从 Maven 实际解析的构建依赖生成 45 组件的 CycloneDX SBOM。OSV 原始结果保留以下三条 Boot 告警。补丁来源和测试已核对，扫描器按版本范围匹配，原始扫描仍未达到零告警。

| OSV 告警 | 实际回移来源 | 回归范围 |
| --- | --- | --- |
| CVE-2025-22235 | [EndpointRequest 补丁](https://github.com/spring-projects/spring-boot/commit/55f67c9a522647039fd3294dee5cb83f4888160a) | Servlet 和 Reactive Actuator |
| CVE-2026-40973 | [ApplicationTemp 补丁](https://github.com/spring-projects/spring-boot/commit/5ceb1a228932e35cc803d1c1fea68f0f984aaa90)及[后续调整](https://github.com/spring-projects/spring-boot/commit/9dc5aa2863f598a15d3dfa116f4b89249daba7e7) | 临时目录权限和目录类型检查 |
| CVE-2026-41001 | [Artemis 补丁](https://github.com/spring-projects/spring-boot/commit/4218bd76e934e5cf9e3fd3997c67b8a6b0d0c111) | 内置 Artemis 的目录配置 |

[复现步骤](issue-10-backport/README.md)保存源码输入、回移补丁和本地 Maven 构建命令。现有制品保存在本地隔离仓库，远端 CI 和下游应用仍需可解析的制品来源。Issue 保持开放。
