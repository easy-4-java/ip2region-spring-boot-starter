#!/bin/bash
set -euo pipefail
root=$(cd "$(dirname "$0")" && pwd)
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
export GRADLE_USER_HOME="$root/gradle"
options=(--no-daemon --max-workers=4 "-Dmaven.repo.local=$root/maven")
case "${1:-}" in
  framework)
    cd "$root/framework"
    ./gradlew "${options[@]}" -PmainToolchain=8 -PtestToolchain=8 -Pversion=5.3.49-easy4j-review.1 \
      :framework-bom:publishToMavenLocal :spring-aop:publishToMavenLocal :spring-beans:publishToMavenLocal \
      :spring-context:publishToMavenLocal :spring-core:publishToMavenLocal :spring-expression:publishToMavenLocal \
      :spring-jcl:publishToMavenLocal :spring-test:publishToMavenLocal
    ;;
  boot)
    cd "$root/boot"
    ./gradlew "${options[@]}" -Pversion=2.7.18-easy4j-review.1 \
      :spring-boot-project:spring-boot-dependencies:publishToMavenLocal \
      :spring-boot-project:spring-boot-starters:spring-boot-starter-parent:publishToMavenLocal \
      :spring-boot-project:spring-boot-starters:spring-boot-starter:publishToMavenLocal \
      :spring-boot-project:spring-boot-starters:spring-boot-starter-test:publishToMavenLocal \
      :spring-boot-project:spring-boot-starters:spring-boot-starter-logging:publishToMavenLocal \
      :spring-boot-project:spring-boot:publishToMavenLocal \
      :spring-boot-project:spring-boot-autoconfigure:publishToMavenLocal \
      :spring-boot-project:spring-boot-actuator:publishToMavenLocal \
      :spring-boot-project:spring-boot-actuator-autoconfigure:publishToMavenLocal \
      :spring-boot-project:spring-boot-test:publishToMavenLocal \
      :spring-boot-project:spring-boot-test-autoconfigure:publishToMavenLocal \
      :spring-boot-project:spring-boot-tools:spring-boot-configuration-processor:publishToMavenLocal
    ;;
  *)
    exit 2
    ;;
esac
