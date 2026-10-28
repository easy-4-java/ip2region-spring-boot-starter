package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionAddress;
import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Checks country code and region mapping used by the template. */
public class RegionEnumTest {

    @Test
    public void mapsCountryCodesAndNames() {
        RegionEnum china = RegionEnum.CN;
        assertThat(china.getNumber()).isEqualTo("156");
        assertThat(china.getCode2()).isEqualTo("CN");
        assertThat(china.getCode3()).isEqualTo("CHN");
        assertThat(china.getIsoCode()).isEqualTo("ISO 3166-2:CN");
        assertThat(china.getIsoName()).isEqualTo("China");
        assertThat(china.getCname()).isEqualTo("中国");
        assertThat(china.getCurrency()).isNull();
        assertThat(RegionEnum.getByNumber("156")).isEqualTo(china);
        assertThat(RegionEnum.getByCode2("cn")).isEqualTo(china);
        assertThat(RegionEnum.getByCode3("chn")).isEqualTo(china);
        assertThat(RegionEnum.getByIsoCode("ISO 3166-2:CN")).isEqualTo(china);
        assertThat(RegionEnum.getByIsoName("China")).isEqualTo(china);
        assertThat(RegionEnum.getByCnName("中国")).isEqualTo(china);
        assertThat(RegionEnum.getByRegionAddress(new RegionAddress("中国", "江苏省", "南京市", "0", "0")))
                .isEqualTo(china);
        assertThat(RegionEnum.getByCode2("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByCode3("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByNumber("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByIsoCode("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByIsoName("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByCnName("missing")).isEqualTo(RegionEnum.UK);
        assertThat(RegionEnum.getByRegionAddress(new RegionAddress("missing", "", "", "", "")))
                .isEqualTo(RegionEnum.UK);
    }

    @Test
    public void distinguishesChinaAndMainland() {
        assertThat(RegionEnum.isValidRegion(RegionEnum.CN)).isTrue();
        assertThat(RegionEnum.isValidRegion(RegionEnum.UK)).isFalse();
        assertThat(RegionEnum.isChinaRegion(RegionEnum.HK)).isTrue();
        assertThat(RegionEnum.isChinaRegion("HKG")).isTrue();
        assertThat(RegionEnum.isChinaRegion("US")).isFalse();
        assertThat(RegionEnum.isMainland("CN")).isTrue();
        assertThat(RegionEnum.isMainland("HKG")).isFalse();
        assertThat(RegionEnum.CN.isValidRegion()).isTrue();
        assertThat(RegionEnum.TS.isValidRegion()).isFalse();
        assertThat(RegionEnum.HK.isChinaRegion()).isTrue();
        assertThat(RegionEnum.US.isChinaRegion()).isFalse();
        assertThat(RegionEnum.CN.isChinaMainland()).isTrue();
        assertThat(RegionEnum.HK.isChinaMainland()).isFalse();
        assertThat(RegionEnum.CN.equals(RegionEnum.CN)).isTrue();
        assertThat(RegionEnum.CN.equals(RegionEnum.US)).isFalse();
    }
}
