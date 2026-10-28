package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

/** Verifies the bundled XDB is usable on the Spring Boot 3 line. */
public class IP2RegionTemplate_Test {

    @Test
    public void loadsBundledXdbAndSearchesPublicIp() throws Exception {
        XdbSearcher searcher = new XdbSearcher(new DefaultResourceLoader(), XdbSearcher.DEFAULT_LOCATION);
        try {
            IP2regionTemplate template = new IP2regionTemplate(searcher);
            assertThat(template.memorySearch("8.8.8.8")).isNotEqualTo(XdbSearcher.NOT_MATCH);
            assertThat(template.getRegion("8.8.8.8")).contains("United States|California|0|Google LLC|US");
            assertThat(template.getRegionAddress("8.8.8.8").getCountry()).isEqualTo("United States");
            assertThat(template.getRegionAddress("8.8.8.8").getISP()).isEqualTo("Google LLC");
            assertThat(template.getRegionByIp("8.8.8.8")).isEqualTo(RegionEnum.US);
            assertThat(template.getCountryByIp("8.8.8.8")).isEqualTo("United States");
            assertThat(template.isMainlandIp("8.8.8.8")).isFalse();
            assertThat(template.isMainlandIp("114.114.114.114")).isTrue();
        } finally {
            searcher.destroy();
        }
    }
}
