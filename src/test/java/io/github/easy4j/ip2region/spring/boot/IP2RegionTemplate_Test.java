package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionAddress;
import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for {@link IP2regionTemplate}.
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class IP2RegionTemplate_Test {

    IP2regionTemplate template;

    @BeforeEach
    public void setUp() throws Exception {
        ResourceLoader resourceLoader = new ResourceLoader() {
            @Override
            public Resource getResource(String location) {
                return new DefaultResourceLoader().getResource(location);
            }
            @Override
            public ClassLoader getClassLoader() {
                return getClass().getClassLoader();
            }
        };
        XdbSearcher xdbSearcher = new XdbSearcher(resourceLoader);
        template = new IP2regionTemplate(xdbSearcher);
    }

    @Test
    public void ipv6CompressedAndExpandedResolveUnitedStates() throws Exception {
        String compressed = "2001:4860:4860::8888";
        String expanded = "2001:4860:4860:0000:0000:0000:0000:8888";
        assertThat(template.memorySearch(compressed)).endsWith("|US");
        assertThat(template.memorySearch(expanded)).isEqualTo(template.memorySearch(compressed));
        assertThat(template.getRegionAddress(compressed).getCountry()).isEqualTo("United States");
        assertThat(template.getRegionByIp(compressed)).isEqualTo(RegionEnum.US);
    }

    @Test
    public void memorySearchByString() throws Exception {
        String result = template.memorySearch("127.0.0.1");
        assertThat(result).isNotNull();
    }

    @Test
    public void memorySearchByLong() throws Exception {
        String result = template.memorySearch(2130706433L); // 127.0.0.1
        assertThat(result).isNotNull();
    }

    @Test
    public void getRegionReturnsString() {
        String region = template.getRegion("127.0.0.1");
        assertThat(region).isNotNull();
    }

    @Test
    public void getRegionAddressReturnsAddress() {
        RegionAddress address = template.getRegionAddress("127.0.0.1");
        assertThat(address).isNotNull();
        assertThat(address.getCountry()).isNotNull();
    }

    @Test
    public void getRegionByIpReturnsEnum() {
        RegionEnum regionEnum = template.getRegionByIp("127.0.0.1");
        assertThat(regionEnum).isNotNull();
    }

    @Test
    public void bundledOfficialDatabaseResolvesUsAndChina() {
        assertThat(template.getRegion("8.8.8.8")).isEqualTo("United States|California|0|Google LLC|US");
        assertThat(template.getRegionAddress("8.8.8.8").getISP()).isEqualTo("Google LLC");
        assertThat(template.getRegionByIp("8.8.8.8")).isEqualTo(RegionEnum.US);
        assertThat(template.isMainlandIp("114.114.114.114")).isTrue();
    }

    @Test
    public void getCountryByIpReturnsString() {
        String country = template.getCountryByIp("127.0.0.1");
        assertThat(country).isNotNull();
    }

    @Test
    public void isMainlandIpReturnsBoolean() {
        boolean result = template.isMainlandIp("127.0.0.1");
        assertThat(result).isFalse();
    }

    @Test
    public void destroyCleansUp() throws Exception {
        template.destroy();
        // should not throw
    }
}
