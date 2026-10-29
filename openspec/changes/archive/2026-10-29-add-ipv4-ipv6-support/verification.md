# 验证记录

日期：2026-10-29。当前 feature/REQ-20261028-wandl-ip2region-issues 分支，未创建分支或 worktree。

## Completeness
全部 SHALL 场景有实现和测试。旧配置 external/location 保持 IPv4 语义；新增 IPv6 独立位置和开关。原查询、构造、缓冲加载签名保留。两个数据库固定来源 v3.18.0。

## Correctness
先运行新增 IPv6 测试：旧实现返回 Reserved|Reserved|Reserved|0|0，目标行为失败。升级 3.3.7 后，Maven 4.0.0-rc-5 + JDK 21 执行 clean verify，86 tests，0 failures/errors/skipped。JaCoCo 512/536 行，95.5224%，原 90% 门禁通过。
覆盖真实 IPv4/IPv6、压缩及完整 IPv6、中国大陆判断、映射 IPv6、非法字面量、long 边界、旧 external=false 语义、外部普通路径/file URI、类路径、关闭 IPv6、资源缺失/损坏/地址族错配、旧结构 2.0（以现有 IPv4 数据头转换模拟）及旧缓冲 API、128 个并发混合任务和重复关闭。
从最终 JAR（不含 target/classes）加载数据库进行查询：8.8.8.8 => United States|California|0|Google LLC|US；2001:4860:4860::8888 => United States|Florida|Miami|Google LLC|US。

## Coherence
实现保持全内存模式，XdbSearcher 读写锁协调查询与关闭；字面量检查阻止域名解析；IPv4/IPv6 分别校验 XDB 头。修正同一自动配置的历史错误包名元数据。

## Findings
CRITICAL: 无。
WARNING: 默认增加约 35.5 MiB IPv6 原始数据和相应运行缓冲；可使用 ipv6-enabled=false 关闭加载。旧上游 Searcher API 在 3.x 有变化，直接调用上游 API 的消费者需要适配，starter 自身公开签名保留。
保留旧加载失败日志+NOT_MATCH 降级策略，未新增热更新。
未运行远端 CI、未发布或部署；未执行在线业务服务验收。

## Sources
- Maven Central https://repo.maven.apache.org/maven2/org/lionsoul/ip2region/maven-metadata.xml：latest/release 3.3.7。
- https://github.com/lionsoul2014/ip2region/blob/master/binding/java/README.md：双栈、全内存并发支持说明；实际实现以 3.3.7 sources JAR 核实。
- IPv6 数据 https://github.com/lionsoul2014/ip2region/blob/v3.18.0/data/ip2region_v6.xdb；SHA-256 939f6b46bd2b8bec3cf7c5ceb8ba782266ae9b1f35b5ba7916700dec0b7506ed。
