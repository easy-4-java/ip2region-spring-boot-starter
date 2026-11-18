package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link IP2regionAutoConfiguration} and {@link IP2regionProperties}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class IP2regionAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(IP2regionAutoConfiguration.class));

    @Test
    public void defaultProperties() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(IP2regionProperties.class);
            IP2regionProperties props = context.getBean(IP2regionProperties.class);
            assertThat(props.isExternal()).isFalse();
            assertThat(props.getLocation()).isEqualTo("classpath:ip2region/ip2region.xdb");
        });
    }

    @Test
    public void customProperties() {
        this.contextRunner
                .withPropertyValues("ip2region.external=true", "ip2region.location=classpath:ip2region/ip2region.xdb")
                .run(context -> {
                    assertThat(context).hasSingleBean(IP2regionProperties.class);
                    IP2regionProperties props = context.getBean(IP2regionProperties.class);
                    assertThat(props.isExternal()).isTrue();
                    assertThat(props.getLocation()).isEqualTo("classpath:ip2region/ip2region.xdb");
                });
    }

    @Test
    public void beansCreated() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(XdbSearcher.class);
            assertThat(context).hasSingleBean(IP2regionTemplate.class);
        });
    }

    @Test
    void preservesApplicationProvidedSearcherAndTemplate() throws Exception {
        XdbSearcher searcher = mock(XdbSearcher.class);
        IP2regionTemplate template = new IP2regionTemplate(searcher);

        this.contextRunner.withBean("customSearcher", XdbSearcher.class, () -> searcher)
                .withBean("customTemplate", IP2regionTemplate.class, () -> template)
                .run(context -> {
                    assertThat(context).hasSingleBean(XdbSearcher.class).hasSingleBean(IP2regionTemplate.class);
                    assertThat(context.getBean(XdbSearcher.class)).isSameAs(searcher);
                    assertThat(context.getBean(IP2regionTemplate.class)).isSameAs(template);
                });
    }

    @Test
    void legacyFactoryUsesTheSuppliedResourceLoaderAndLocation() throws Exception {
        String location = "classpath:legacy-ipv4.xdb";
        ResourceLoader loader = mock(ResourceLoader.class);
        when(loader.getResource(location)).thenReturn(new DefaultResourceLoader().getResource(XdbSearcher.DEFAULT_LOCATION));
        IP2regionProperties properties = new IP2regionProperties();
        properties.setLocation(location);
        properties.setIpv6Enabled(false);

        XdbSearcher searcher = new IP2regionAutoConfiguration().xdbSearcher(properties, loader);
        try {
            verify(loader).getResource(location);
            assertThat(searcher.memorySearch("8.8.8.8")).isNotEqualTo(XdbSearcher.NOT_MATCH);
        } finally {
            searcher.destroy();
        }
    }

    @Test
    public void propertiesSettersWork() {
        IP2regionProperties props = new IP2regionProperties();
        props.setExternal(true);
        props.setLocation("custom-location");
        assertThat(props.isExternal()).isTrue();
        assertThat(props.getLocation()).isEqualTo("custom-location");
    }
}
