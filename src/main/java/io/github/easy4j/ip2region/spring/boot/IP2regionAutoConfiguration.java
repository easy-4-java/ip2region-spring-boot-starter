package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.IOException;

/**
 * ip 解析
 * @author <a href="https://github.com/loong10k">Loong Wan</a>
 * @since 1.0.0
 */
@Configuration
@ConditionalOnClass(org.lionsoul.ip2region.xdb.Searcher.class)
@EnableConfigurationProperties({ IP2regionProperties.class })
public class IP2regionAutoConfiguration implements ResourceLoaderAware {

	protected ResourceLoader resourceLoader;
	/**
	 * <p>Xdb searcher.</p>
	 * @param properties the properties
	 * @return the xdb searcher
	 * @throws IOException if an error occurs
	 */

	@Bean
	@ConditionalOnMissingBean
	public XdbSearcher xdbSearcher(IP2regionProperties properties) throws IOException {
        String ipv4Location = properties.isExternal() ? properties.getLocation() : XdbSearcher.DEFAULT_LOCATION;
        return new XdbSearcher(resourceLoader, ipv4Location, properties.isIpv6Enabled(),
                properties.getIpv6Location());
	}
    /**
     * Creates a searcher for callers using the original factory signature.
     * The supplied loader and IPv4 location take precedence for this direct call.
     *
     * @param properties IPv4 and IPv6 resource configuration
     * @param loader resource loader supplied by the caller
     * @return a searcher for the configured resources
     * @throws IOException if the searcher cannot be initialized
     */
    public XdbSearcher xdbSearcher(IP2regionProperties properties, ResourceLoader loader) throws IOException {
        return new XdbSearcher(loader, properties.getLocation(), properties.isIpv6Enabled(),
                properties.getIpv6Location());
    }

	/**
	 * <p>Ip2region template.</p>
	 * @param xdbSearcher the xdb searcher
	 * @return the i p2region template
	 * @throws IOException if an error occurs
	 */

	@Bean
	@ConditionalOnMissingBean
	public IP2regionTemplate ip2regionTemplate(XdbSearcher xdbSearcher) throws IOException {
		return new IP2regionTemplate(xdbSearcher);
	}
	/** Sets the resource loader. */

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}


}
