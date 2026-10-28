package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.util.IpUtils;
import io.github.easy4j.ip2region.spring.boot.util.Util;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** Checks address validation and byte conversions used by search. */
public class IpConversionTest {

    @Test
    public void validatesAddresses() {
        assertThat(IpUtils.isIpv4("127.0.0.1")).isTrue();
        assertThat(IpUtils.isIpv4("999.0.0.1")).isFalse();
        assertThat(IpUtils.isIpv6("fe80:1295:8030:1fc6:57fa:0000:0000:8030")).isTrue();
        assertThat(IpUtils.isIpv6("127.0.0.1")).isFalse();
        assertThat(Util.isIpAddress("8.8.8.8")).isTrue();
        assertThat(Util.isIpAddress("1.2.3")).isFalse();
        assertThat(Util.isIpAddress("1234.2.3.4")).isFalse();
        assertThat(Util.isIpAddress("256.2.3.4")).isFalse();
    }

    @Test
    public void convertsIpv4AndBytes() {
        assertThat(Util.long2ip(Util.ip2long("8.8.8.8"))).isEqualTo("8.8.8.8");
        assertThat(Util.ip2long("invalid")).isZero();
        byte[] bytes = new byte[4];
        Util.write(bytes, 0, 0x12345678L, 4);
        assertThat(Util.getIntLong(bytes, 0)).isEqualTo(0x12345678L);
        Util.writeIntLong(bytes, 0, 0x01020304L);
        assertThat(Util.getIntLong(bytes, 0)).isEqualTo(0x01020304L);
        assertThat(Util.getInt1(bytes, 0)).isEqualTo(4);
    }
}
