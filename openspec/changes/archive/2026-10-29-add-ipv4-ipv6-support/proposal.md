## Why
当前依赖 2.7.0 和 IPv4 long 转换链无法查询 IPv6，需保留既有配置和接口并增加双栈能力。
## What Changes
- 将 org.lionsoul:ip2region 升级为 Maven Central 最新稳定版本 3.3.7。
- 内置 IPv6 XDB，新增 ipv6-enabled（默认 true）、ipv6-location，旧 external/location 继续仅控制 IPv4。
- 保留查询和构造接口，字符串支持 IPv4/IPv6 字面量，long 保持无符号 IPv4。
- 增加真实数据库、配置兼容、错误和并发测试。
## Capabilities
### New Capabilities
- `dual-stack`: 兼容既有 API 的 IPv4/IPv6 离线查询。
## Impact
查询器、配置、数据资源、测试、README。无需部署、分支切换或安装全局工具。
