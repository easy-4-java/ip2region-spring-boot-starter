## Decisions
同一工作目录顺序切换，无 worktree。改造发生在 feature/REQ-20261029-wandl-ip2region-版本线 分支，验证后合入对应维护线。生产源码、双栈 XDB 和测试来自共同基线，保持字节级一致；仅 POM、README 和自动配置注册按版本线适配。旧包名统一为 io.github.easy4j。
测试将 var/readAllBytes/ExecutorService AutoCloseable 改为 JDK8 等价写法，不改变断言。每条线使用实际 JDK 和兼容 Maven，完整 clean verify；发布通过 Maven deploy，附源码和 Javadoc。私服使用现有 settings.xml server，不输出凭证。
SNAPSHOT 按线串行发布，记录时间戳、摘要及新隔离缓存的下游解析/自动配置/双栈真实查询。首个发布失败即暂停后续，诊断后按远端状态安全恢复。
不合并不同维护线的 Boot/JDK 配置，不改 main/master 或历史发布分支。不运行 Docker：本仓库是库，不是容器应用。
