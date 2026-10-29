## Why
将已验证双栈能力整合到十条维护版本线并发布阿里云 Maven 私服，保证跨 JDK 功能一致。
## What Changes
- 统一 io.github.easy4j 生产代码、双栈数据库和测试；测试兼容 JDK 8。
- 各维护线保留自己的 Spring Boot、JDK、版本坐标；4.1.x 延续已批准的 4.1.2/Maven4 实现。
- 2.x 发布目标对齐既有阿里云私服，不向 Central 发布。
- 在各线 feature 分支实施并验证后合入版本线；不修改 main/master 和历史 RELEASE 分支。
- 保护并最终恢复原 feature 上未提交 Wrapper/POM。
## Capabilities
### New Capabilities
- `maintenance-release`: 十条维护线双栈一致性和私服消费验收。
## Impact
2.3.x、2.7.x、3.0.x 至 3.5.x、4.0.x、4.1.x。JDK8/17/21，私服 SNAPSHOT 发布。
