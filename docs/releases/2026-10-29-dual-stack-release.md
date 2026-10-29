# IPv4 / IPv6 双栈维护线发布记录

日期：2026-10-29（Asia/Shanghai）。仓库：easy-4-java/ip2region-spring-boot-starter。

十条维护版本线已完成整合、完整构建验证、远端 CI、阿里云 Maven 私服发布和隔离缓存消费验证。main/master、历史 RELEASE 分支未纳入本次整合。未创建 worktree。

## 发布矩阵

坐标统一为 `io.github.easy4j:ip2region-spring-boot-starter:<版本线>.20260630-SNAPSHOT`；查询依赖为 `org.lionsoul:ip2region:3.3.7`。

| 版本线 | Spring Boot parent | JDK | 发布源码 commit | 测试数 / 行覆盖率 | 已验证时间戳构件 | 隔离消费 | CI |
|---|---|---|---|---|---|---|---|
| 2.3.x | 2.3.12.RELEASE | 8 | `63e903c1d3f8` | 86 / 95.56% | `2.3.x.20260630-20261029.065909-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833988394) |
| 2.7.x | 2.7.18 | 8 | `5b6b87873db0` | 86 / 95.56% | `2.7.x.20260630-20261029.070334-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833989946) |
| 3.0.x | 3.0.13 | 17 | `fa196afab608` | 86 / 95.56% | `3.0.x.20260630-20261029.070501-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833988612) |
| 3.1.x | 3.1.12 | 17 | `725ddcbb18dc` | 86 / 95.56% | `3.1.x.20260630-20261029.070601-2` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833990454) |
| 3.2.x | 3.2.12 | 17 | `42036b371ad8` | 86 / 95.52% | `3.2.x.20260630-20261029.070645-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833988987) |
| 3.3.x | 3.3.13 | 17 | `7e9ef023688c` | 86 / 95.52% | `3.3.x.20260630-20261029.070742-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833986904) |
| 3.4.x | 3.4.13 | 17 | `90ee78614c72` | 86 / 95.52% | `3.4.x.20260630-20261029.070909-3` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833987537) |
| 3.5.x | 3.5.16 | 17 | `2a5abd03b10b` | 86 / 95.52% | `3.5.x.20260630-20261029.071002-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833989642) |
| 4.0.x | 4.0.9 | 21 | `26388e6567ce` | 86 / 95.52% | `4.0.x.20260630-20261029.071035-2` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833987313) |
| 4.1.x | 4.1.2 | 21 | `5f3a2e7687ef` | 86 / 95.52% | `4.1.x.20260630-20261029.071107-1` | passed | [passed](https://github.com/easy-4-java/ip2region-spring-boot-starter/actions/runs/40833988690) |

## 一致性与兼容性

- 十条发布源码的生产 Java 文件及两份 XDB 资源经 Git blob 指纹核对完全相同。
- 每条线均执行实际 JDK 下的 `clean verify`，再通过 `clean deploy` 发布；未跳过测试或覆盖率检查。每条线 86 个测试，无失败、错误或跳过；原 90% 行覆盖率门禁保留。
- 2.x 使用 JDK8，3.x 使用 JDK17，4.x 使用 JDK21；4.1.x 使用 Maven4，其余版本线使用 Maven3.9.16。发布 JAR 字节码主版本分别为 52/61/65。
- Boot2 示例使用 javax，Boot3/4 示例使用 jakarta；测试采用 JDK8 兼容语法。各分支坐标和 Boot/JDK 契约独立。
- 整合期间其他工作更新的 3.4.13、3.5.16、4.0.9 parent 已保留，并对受影响版本线重新验证。
- 保留旧 external/location 语义及 starter 查询接口；新增 IPv6 配置、字面量校验、独立资源降级和并发关闭保护。所有 Java 包统一为 io.github.easy4j。
- 2.x 的旧 Sonatype distributionManagement 改为既有阿里云私服；本次未向 Maven Central 发布。

## 私服与下游证据

发布 server ID 为 `2624322-snapshot-3EoOv3`，使用现有 Maven settings 凭证。每条线核对了远端 POM、主 JAR、sources JAR、javadoc JAR 及其 SHA-1 校验文件，主包与本次本地构建的 SHA-256 一致。时间戳、文件名和摘要见同目录 JSON 记录。

每条线从全新独立 Maven 缓存解析依赖，没有复用生产者本地仓库；下载的 starter JAR 与已核验私服摘要一致。消费者使用对应 JDK 编译、启动 Spring 自动装配，验证 IPv4、压缩/完整 IPv6、IPv6 中国大陆判断，且 XDB 确实从 JAR 读取。示例结果：

```text
8.8.8.8 => United States|California|0|Google LLC|US
2001:4860:4860::8888 => United States|Florida|Miami|Google LLC|US
```

原 4.1.x CI 的 Maven3/POM4.1 不兼容已通过版本线工具链选择修复；表中链接为最终分支提交的实际成功记录（发布源码之后仅追加 README 文档更新），未削弱验证。

## 证据位置和范围

完整构建、发布及消费者日志保存在 `/var/folders/_s/9_xnnkz141920t1yv5zhvd1r0000gn/T/ip2region-release-20261029-wb9turti/logs/`，远端构件核验副本在 `results/<版本线>/remote/`，隔离仓库在 `consumers/<版本线>/repository/`。

原 feature 分支未提交的 Wrapper/POM 改动由 stash 保护，最终回到该分支恢复；未混入发布构件。总体发布规格和完成记录以 `feature/REQ-20261028-wandl-ip2region-issues` 分支的 OpenSpec 归档为事实源，版本线保留其整合时规格快照。

本结论证明 Maven 构件发布和下游组件运行；未执行业务服务部署或生产流量验收。
