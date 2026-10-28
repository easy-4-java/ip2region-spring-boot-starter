package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionAddress;
import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

public class OfficialXdbTest {

    @Test
    public void bundledOfficialDataAndLegacyRecords() throws Exception {
        XdbSearcher searcher = new XdbSearcher(new DefaultResourceLoader(), XdbSearcher.DEFAULT_LOCATION);
        try {
            IP2regionTemplate template = new IP2regionTemplate(searcher);
            assertThat(template.getRegion("8.8.8.8")).isEqualTo("United States|California|0|Google LLC|US");
            RegionAddress address = template.getRegionAddress("8.8.8.8");
            assertThat(address.getProvince()).isEqualTo("California");
            assertThat(address.getISP()).isEqualTo("Google LLC");
            assertThat(template.getRegionByIp("8.8.8.8")).isEqualTo(RegionEnum.US);
            assertThat(template.isMainlandIp("114.114.114.114")).isTrue();
            RegionAddress legacy = new RegionAddress(new String[]{"中国", "华东", "江苏省", "南京市", "电信"});
            assertThat(legacy.getProvince()).isEqualTo("江苏省");
            assertThat(legacy.getISP()).isEqualTo("电信");
        } finally {
            searcher.destroy();
        }
    }
}
