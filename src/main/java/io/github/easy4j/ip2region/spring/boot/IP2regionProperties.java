package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(IP2regionProperties.PREFIX)
@Data
/**
 * <p>Configuration properties for I P2region.</p>
 *
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
public class IP2regionProperties {

	public static final String PREFIX = "ip2region";

	/**
	 * 是否使用外部的IP数据文件.
	 */
	private boolean external = false;
	/**
	 * ip2region.xdb 文件路径，默认： classpath:ip2region/ip2region.xdb
	 */
	private String location = XdbSearcher.DEFAULT_LOCATION;

    /** 是否启用 IPv6 查询，默认使用内置 IPv6 数据库。 */
    private boolean ipv6Enabled = true;

    /** IPv6 XDB 资源位置，与旧 external/location 的 IPv4 配置独立。 */
    private String ipv6Location = XdbSearcher.DEFAULT_IPV6_LOCATION;

}
