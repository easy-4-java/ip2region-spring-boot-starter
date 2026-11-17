package io.github.easy4j.ip2region.spring.boot;

import io.github.easy4j.ip2region.spring.boot.ext.RegionAddress;
import io.github.easy4j.ip2region.spring.boot.ext.RegionEnum;
import io.github.easy4j.ip2region.spring.boot.ext.XdbSearcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.DefaultResourceLoader;

import static org.assertj.core.api.Assertions.assertThat;

public class IP2RegionTemplate_Test {

	IP2regionTemplate template = null;

	@BeforeEach
	public void setUp()  throws Exception {
		XdbSearcher xdbSearcher = new XdbSearcher(new DefaultResourceLoader(), XdbSearcher.DEFAULT_LOCATION);
		template = new IP2regionTemplate(xdbSearcher);
	}

	@Test
	public void templateTest() throws Exception {
		assertThat(template.getRegion("8.8.8.8")).isEqualTo("美国|0|0|0|Level3");

		System.out.println(template.memorySearch("127.0.0.1"));
		System.out.println(template.memorySearch("114.124.146.103"));
		System.out.println(template.memorySearch("115.193.169.84"));

		System.out.println(template.getRegion("127.0.0.1"));
		System.out.println(template.getRegion("114.124.146.103"));

		RegionAddress adress1 = template.getRegionAddress("113.210.53.80");
		System.out.println(adress1);
		System.out.println(RegionEnum.getByRegionAddress(adress1));

		RegionAddress adress2 = template.getRegionAddress("127.0.0.1");
		System.out.println(adress2);
		System.out.println(RegionEnum.getByRegionAddress(adress2));

		RegionEnum regionEnum1 = template.getRegionByIp("127.0.0.1");
		System.out.println(regionEnum1);
		RegionEnum regionEnum2 = template.getRegionByIp("102.42.140.162");
		System.out.println(regionEnum2);

		System.out.println(template.getCountryByIp("127.0.0.1"));
		System.out.println(template.getCountryByIp("114.124.146.103"));

		System.out.println(template.isMainlandIp("127.0.0.1"));
		System.out.println(template.isMainlandIp("114.124.146.103"));

	}

}
