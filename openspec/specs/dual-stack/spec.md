# dual-stack Specification

## Purpose
在保持现有 starter 配置及查询接口兼容的前提下，提供使用独立 XDB 的 IPv4、IPv6 离线归属地查询，明确输入、资源错误和并发行为。

## Requirements

### Requirement: 双栈查询
系统 SHALL 默认使用独立 IPv4/IPv6 数据库，并通过现有字符串 API 按地址族查询。
#### Scenario: IPv6 真实查询
- **WHEN** 使用压缩或完整 IPv6 查询已知美国地址
- **THEN** 原始记录和结构化地址及国家枚举匹配 IPv6 数据库

### Requirement: 向后兼容
系统 SHALL 保留旧配置、构造器、loadWithBuffer 和查询方法签名及模板兜底行为。
#### Scenario: 旧外部 IPv4 配置
- **WHEN** external=true 且 location 为自有 IPv4 XDB
- **THEN** IPv4 使用该资源且 IPv6 继续独立查询
#### Scenario: long 接口
- **WHEN** 使用有效无符号 IPv4 long 查询
- **THEN** 与对应字符串查询结果一致

### Requirement: 输入与资源安全
系统 SHALL 拒绝非 IP 字面量和越界数值，校验 XDB 地址族，并支持关闭 IPv6。
#### Scenario: 非法输入
- **WHEN** 输入域名、空值或非法 IP
- **THEN** 底层抛 IOException，模板返回既有兜底结果且不进行 DNS 查询
#### Scenario: IPv6 关闭或加载失败
- **WHEN** IPv6 被关闭或资源不可用
- **THEN** IPv4 继续工作且 IPv6 返回 NOT_MATCH

### Requirement: 并发查询
系统 SHALL 支持多个线程使用全内存查询器查询两种地址。
#### Scenario: 混合并发
- **WHEN** 多线程同时查询已知 IPv4 和 IPv6
- **THEN** 结果与单线程一致
