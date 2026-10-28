package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionAddress;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Checks XDB field ordering and mutable address fields. */
public class RegionAddressTest {

    @Test
    public void parsesOfficialV4AndLegacyLayouts() {
        RegionAddress official = new RegionAddress(new String[]{"United States", "California", "0", "Google LLC", "US"});
        assertThat(official.getProvince()).isEqualTo("California");
        assertThat(official.getISP()).isEqualTo("Google LLC");
        RegionAddress legacy = new RegionAddress(new String[]{"中国", "华东", "江苏省", "南京市", "电信"});
        assertThat(legacy.getProvince()).isEqualTo("江苏省");
        assertThat(legacy.getCity()).isEqualTo("南京市");
    }

    @Test
    public void mapsXdbFields() {
        RegionAddress address = new RegionAddress(new String[]{"中国", "华东", "江苏省", "南京市", "电信"});
        assertThat(address.getCountry()).isEqualTo("中国");
        assertThat(address.getProvince()).isEqualTo("江苏省");
        assertThat(address.getCity()).isEqualTo("南京市");
        assertThat(address.getArea()).isEqualTo("华东");
        assertThat(address.getISP()).isEqualTo("电信");
        assertThat(address.toString()).contains("南京市", "电信");
    }

    @Test
    public void supportsCustomAddress() {
        RegionAddress address = new RegionAddress();
        address.setCountry("美国");
        address.setProvince("加州");
        address.setCity("山景城");
        address.setArea("西部");
        address.setISP("测试运营商");
        assertThat(address.getCountry()).isEqualTo("美国");
        assertThat(address.getProvince()).isEqualTo("加州");
        assertThat(address.getCity()).isEqualTo("山景城");
        assertThat(address.getArea()).isEqualTo("西部");
        assertThat(address.getISP()).isEqualTo("测试运营商");
        assertThat(new RegionAddress("中国", "江苏", "南京", "华东", "电信").getCity())
                .isEqualTo("南京");
    }
}
