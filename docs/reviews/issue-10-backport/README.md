# ip2region Java 8 回移构建

在独立目录准备以下 Git 源码，分别检出指定提交，并用 `git apply` 应用本目录对应的补丁。

| 目录 | 源码 | 提交 | 补丁 |
| --- | --- | --- | --- |
| `framework` | [第三方 Framework 回移树](https://github.com/meimingqi222/spring-framework-5.3.49-security-backport) | `16a56ef8631a2203fdf9a3a6aec35655ed939f25` | [Framework 补充补丁](framework-follow-up.patch) |
| `boot` | [Spring Boot](https://github.com/spring-projects/spring-boot) | `0c8b382d42db22b92efcf47000d0ff9ef4971629` | [Boot 回移补丁](boot-2.7.patch) |

将 `build-maven-artifacts.sh` 放到上述两个目录的父目录。脚本在 macOS 上使用 JDK 17 启动 Gradle。Framework 的编译与测试显式选择 JDK 8。输出写入同一父目录下的 `maven`。

```bash
bash build-maven-artifacts.sh framework
bash build-maven-artifacts.sh boot
```

在此 ip2region 分支上切换到 JDK 8，用 Maven 的隔离仓库路径执行 `mvn -Dmaven.repo.local=/实际路径/maven clean verify`。两套依赖各自使用带 `easy4j-review.1` 后缀的版本。 [本地验证记录](../issue-10-java8-security.md)
