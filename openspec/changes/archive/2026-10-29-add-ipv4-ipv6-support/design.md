## Context
已有 Spring Boot 4.1 starter，全内存 IPv4 查询。依赖源码确认 3.3.7 使用 Version 和 LongByteArray。
## Decisions
保留旧字段和公开签名；IPv4 旧位置语义不变，新增独立 IPv6 位置及开关。使用两个全内存 Searcher，严格字面量验证后转换 byte[]，避免上游域名解析。long 仅允许 0..0xffffffff。内置数据固定上游 v3.18.0。
加载失败保留历史日志和 NOT_MATCH 降级；校验数据库头和地址族，避免错用 XDB。无热更新。上游全内存查询只读，查询器生命周期锁协调关闭和查询。
## Compatibility
旧 external=false 忽略 location；external=true 使用 location。旧构造器默认增加内置 IPv6。memorySearch 保留 IOException；模板继续返回既有兜底值。受保护 byte[] xdbBuff 保留，新 IPv6 缓冲使用 LongByteArray。IPv4-mapped IPv6 按 IPv4 查询，拒绝 zone ID 和方括号。
## Validation
先编写真实 IPv6 行为失败测试，再实现；全量 verify 含原覆盖率门禁。测试涵盖压缩/完整 IPv6、真实国家和 ISP、long 边界、非法输入、类路径及文件加载、地址族错配、并发、关闭。
