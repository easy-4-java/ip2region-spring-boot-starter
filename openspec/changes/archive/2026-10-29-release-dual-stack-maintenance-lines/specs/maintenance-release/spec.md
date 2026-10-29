## ADDED Requirements
### Requirement: 维护线功能一致
十条维护线 SHALL 使用相同 io.github.easy4j 双栈实现及数据，并保留各自工具链和构件版本。
#### Scenario: 分支验证
- **WHEN** 分别在 JDK8/17/21 对应版本线执行完整验证
- **THEN** 相同双栈行为测试通过且原覆盖率门禁通过
### Requirement: 私服可消费
每条版本线 SHALL 通过 Maven deploy 发布到配置的阿里云私服，且在隔离缓存中可解析并运行。
#### Scenario: 发布验收
- **WHEN** 发布该线 SNAPSHOT
- **THEN** 远端 POM、JAR、sources、javadoc 和元数据可验证，隔离消费者自动装配并正确查询 IPv4/IPv6
### Requirement: 工作区保护
流程 SHALL 保留原分支未提交修改，不使用 worktree，不改写历史发布分支。
#### Scenario: 恢复
- **WHEN** 任务结束
- **THEN** 回到原 feature 并恢复保存的 Wrapper/POM 改动
