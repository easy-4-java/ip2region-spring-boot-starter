package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import io.github.easy4j.ip2region.spring.boot.util.IpUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.core.io.DefaultResourceLoader;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** 双栈真实数据、旧配置及异常边界回归。 */
class DualStackTest {
    private static final String V4 = "8.8.8.8";
    private static final String V6 = "2001:4860:4860::8888";
    private static final DefaultResourceLoader LOADER = new DefaultResourceLoader();
    private static XdbSearcher searcher;
    private static IP2regionTemplate template;

    @BeforeAll
    static void initialize() throws Exception {
        searcher = new XdbSearcher(LOADER);
        template = new IP2regionTemplate(searcher);
    }

    @AfterAll
    static void close() throws Exception {
        template.destroy();
    }

    @Test
    void realIpv6ResultsIncludeCountryIspAndMainland() throws Exception {
        assertThat(searcher.memorySearch(V6)).endsWith("|Google LLC|US");
        assertThat(template.getCountryByIp(V6)).isEqualTo("United States");
        assertThat(template.getRegionAddress(V6).getISP()).isEqualTo("Google LLC");
        assertThat(template.getRegionByIp(V6)).isEqualTo(RegionEnum.US);
        assertThat(template.isMainlandIp(V6)).isFalse();
        assertThat(template.getRegionByIp("240e:3b7:3272:d8d0:db09:c067:8d59:539e")).isEqualTo(RegionEnum.CN);
        assertThat(template.isMainlandIp("240e:3b7:3272:d8d0:db09:c067:8d59:539e")).isTrue();
    }

    @Test
    void ipv4LongCompatibilityAndBoundaries() throws Exception {
        assertThat(searcher.memorySearch(0x08080808L)).isEqualTo(searcher.memorySearch(V4));
        assertThat(searcher.memorySearch(0)).isEqualTo(searcher.memorySearch("0.0.0.0"));
        assertThat(searcher.memorySearch(0xffffffffL)).isEqualTo(searcher.memorySearch("255.255.255.255"));
        assertThatThrownBy(() -> searcher.memorySearch(-1)).isInstanceOf(IOException.class);
        assertThatThrownBy(() -> searcher.memorySearch(0x100000000L)).isInstanceOf(IOException.class);
    }

    @Test
    void invalidInputNeverBecomesIpv4ZeroOrHostnameLookup() {
        for (String ip : new String[]{null, "", " ", "localhost", "example.com", "1.2.3", "256.1.1.1",
                "-1.2.3.4", "2001:::1", "[::1]", "fe80::1%eth0"}) {
            assertThatThrownBy(() -> searcher.memorySearch(ip)).isInstanceOf(IOException.class);
            assertThat(template.getRegion(ip)).isEqualTo(XdbSearcher.NOT_MATCH);
            assertThat(template.getRegionByIp(ip)).isEqualTo(RegionEnum.UK);
            assertThat(template.getCountryByIp(ip)).isEqualTo(RegionEnum.UK.getCname());
            assertThat(template.getRegionAddress(ip)).isSameAs(XdbSearcher.NOT_MATCH_REGION_ADDRESS);
        }
    }

    @Test
    void compressedIpv6ValidationAndMappedIpv4() throws Exception {
        assertThat(IpUtils.isIpv6("::1")).isTrue();
        assertThat(IpUtils.isIpv6("::")).isTrue();
        assertThat(IpUtils.isIpv6(null)).isFalse();
        assertThat(IpUtils.isIpv4(null)).isFalse();
        assertThat(IpUtils.isIpv6("2001:::1")).isFalse();
        assertThat(searcher.memorySearch("::ffff:8.8.8.8")).isEqualTo(searcher.memorySearch(V4));
    }

    @Test
    void disabledIpv6DoesNotAffectIpv4AndCloseIsIdempotent() throws Exception {
        XdbSearcher v4Only = new XdbSearcher(LOADER, XdbSearcher.DEFAULT_LOCATION, false, "missing");
        assertThat(v4Only.memorySearch(V4)).endsWith("|US");
        assertThat(v4Only.memorySearch(V6)).isEqualTo(XdbSearcher.NOT_MATCH);
        v4Only.destroy();
        v4Only.destroy();
        assertThat(v4Only.memorySearch(V4)).isEqualTo(XdbSearcher.NOT_MATCH);
    }

    @Test
    void missingOrWrongFamilyIpv6DegradesIndependently() throws Exception {
        for (String location : new String[]{"classpath:missing-v6.xdb", XdbSearcher.DEFAULT_LOCATION, " "}) {
            XdbSearcher partial = new XdbSearcher(LOADER, XdbSearcher.DEFAULT_LOCATION, true, location);
            try {
                assertThat(partial.memorySearch(V4)).endsWith("|US");
                assertThat(partial.memorySearch(V6)).isEqualTo(XdbSearcher.NOT_MATCH);
            } finally {
                partial.destroy();
            }
        }
    }

    @Test
    void ipv4FailureDoesNotDisableIpv6() throws Exception {
        XdbSearcher partial = new XdbSearcher(LOADER, "classpath:missing-v4.xdb");
        try {
            assertThat(partial.memorySearch(V4)).isEqualTo(XdbSearcher.NOT_MATCH);
            assertThat(partial.memorySearch(V6)).endsWith("|US");
        } finally {
            partial.destroy();
        }
    }

    @Test
    void oldExternalFileConfigurationAndNewIpv6FileConfiguration(@TempDir Path directory) throws Exception {
        Path v4 = copyResource("ip2region/ip2region.xdb", directory.resolve("v4.xdb"));
        Path v6 = copyResource("ip2region/ip2region_v6.xdb", directory.resolve("v6.xdb"));
        runner().withPropertyValues("ip2region.external=true", "ip2region.location=" + v4,
                "ip2region.ipv6-location=" + v6.toUri()).run(context -> {
            assertThat(context).hasNotFailed();
            IP2regionTemplate bean = context.getBean(IP2regionTemplate.class);
            assertThat(bean.getRegionByIp(V4)).isEqualTo(RegionEnum.US);
            assertThat(bean.getRegionByIp(V6)).isEqualTo(RegionEnum.US);
        });
    }

    @Test
    void legacyExternalFalseIgnoresLocationAndIpv6CanBeDisabled() {
        runner().withPropertyValues("ip2region.location=classpath:missing.xdb", "ip2region.ipv6-enabled=false")
                .run(context -> {
                    assertThat(context).hasNotFailed();
                    IP2regionProperties props = context.getBean(IP2regionProperties.class);
                    assertThat(props.isIpv6Enabled()).isFalse();
                    assertThat(props.getIpv6Location()).isEqualTo(XdbSearcher.DEFAULT_IPV6_LOCATION);
                    IP2regionTemplate bean = context.getBean(IP2regionTemplate.class);
                    assertThat(bean.getRegionByIp(V4)).isEqualTo(RegionEnum.US);
                    assertThat(bean.getRegion(V6)).isEqualTo(XdbSearcher.NOT_MATCH);
                });
    }

    @Test
    void defaultAutoConfigurationQueriesBothFamilies() {
        runner().run(context -> {
            assertThat(context).hasSingleBean(XdbSearcher.class).hasSingleBean(IP2regionTemplate.class);
            assertThat(context.getBean(IP2regionProperties.class).isIpv6Enabled()).isTrue();
            assertThat(context.getBean(IP2regionTemplate.class).getRegionByIp(V6)).isEqualTo(RegionEnum.US);
        });
    }

    @Test
    void concurrentMixedQueriesMatchSingleThreadResults() throws Exception {
        String ipv4Result = searcher.memorySearch(V4);
        String ipv6Result = searcher.memorySearch(V6);
        try (var executor = Executors.newFixedThreadPool(8)) {
            List<Callable<Boolean>> tasks = new ArrayList<>();
            for (int i = 0; i < 128; i++) {
                tasks.add(() -> ipv4Result.equals(searcher.memorySearch(V4))
                        && ipv6Result.equals(searcher.memorySearch(V6)));
            }
            for (var result : executor.invokeAll(tasks)) {
                assertThat(result.get()).isTrue();
            }
        }
    }

    @Test
    void legacyStructureTwoIpv4FileAndBufferApiRemainUsable(@TempDir Path directory) throws Exception {
        byte[] data;
        try (InputStream input = LOADER.getResource(XdbSearcher.DEFAULT_LOCATION).getInputStream()) {
            data = input.readAllBytes();
        }
        // 旧结构 2.0 的 IPv4 索引布局不变，地址族由结构版本隐含。
        data[0] = 2;
        data[1] = 0;
        data[16] = 0;
        data[17] = 0;
        data[18] = 0;
        data[19] = 0;
        Path legacy = directory.resolve("legacy-v4.xdb");
        Files.write(legacy, data);
        XdbSearcher old = new XdbSearcher(LOADER, legacy.toString(), false, "unused");
        try {
            assertThat(old.memorySearch(V4)).isEqualTo(searcher.memorySearch(V4));
            var reused = old.loadWithBuffer("not-read-when-buffer-exists");
            try {
                assertThat(reused.search(V4)).isEqualTo(searcher.memorySearch(V4));
            } finally {
                reused.close();
            }
        } finally {
            old.destroy();
        }
    }

    @Test
    void corruptXdbAndWrongIpv4FamilyCannotProduceMisleadingResults(@TempDir Path directory) throws Exception {
        Path corrupt = directory.resolve("corrupt.xdb");
        Files.write(corrupt, new byte[256]);
        for (String location : new String[]{corrupt.toString(), XdbSearcher.DEFAULT_IPV6_LOCATION}) {
            XdbSearcher partial = new XdbSearcher(LOADER, location);
            try {
                assertThat(partial.memorySearch(V4)).isEqualTo(XdbSearcher.NOT_MATCH);
                assertThat(partial.memorySearch(V6)).endsWith("|US");
            } finally {
                partial.destroy();
            }
        }
    }

    private static ApplicationContextRunner runner() {
        return new ApplicationContextRunner().withConfiguration(AutoConfigurations.of(IP2regionAutoConfiguration.class));
    }

    private static Path copyResource(String name, Path destination) throws IOException {
        try (InputStream input = LOADER.getResource("classpath:" + name).getInputStream()) {
            Files.copy(input, destination);
        }
        return destination;
    }
}
