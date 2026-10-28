package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies automatic bean registration in Spring Boot 3. */
public class IP2regionAutoConfigurationTest {

    @Test
    public void registersSearchAndTemplateBeans() {
        new ApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(IP2regionAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).hasSingleBean(XdbSearcher.class);
                    assertThat(context).hasSingleBean(IP2regionTemplate.class);
                    assertThat(context.getBean(IP2regionTemplate.class).memorySearch("8.8.8.8"))
                            .isNotEqualTo(XdbSearcher.NOT_MATCH);
                });
    }
}
