package io.github.easy4j.ip2region.spring.boot.ext;

/**
 * Basic RegionAddress Info
 * @author 凡梦星尘(elkan1788@gmail.com)
 */
public class RegionAddress {

    private String country;
    private String province;
    private String city;
    private String area;
    private String ISP;

    public RegionAddress() {
    }

    /**
     *
     * Translate this string "中国|华东|江苏省|南京市|电信" or "印度尼西亚|0|大雅加达|雅加达|Telin" to location fields.
     * @param region location region address info array
     */
    public RegionAddress(String[] region) {
        this(region[0], isOfficialV4Format(region) ? region[1] : region[2],
                isOfficialV4Format(region) ? region[2] : region[3],
                isOfficialV4Format(region) ? "0" : region[1],
                isOfficialV4Format(region) ? region[3] : region[4]);
    }

    /** Identifies the current official country|province|city|ISP|ISO2 data layout. */
    public static boolean isOfficialV4Format(String[] region) {
        if (region.length != 5) {
            return false;
        }
        String code = region[4];
        return "Reserved".equals(region[0]) ||
                (code.length() == 2 && code.charAt(0) >= 'A' && code.charAt(0) <= 'Z' &&
                        code.charAt(1) >= 'A' && code.charAt(1) <= 'Z');
    }

    /**
     * Basic constructor method
     * @param country   Country name
     * @param province  province name
     * @param city      city name
     * @param area      area name
     * @param ISP       ISP name
     */
    public RegionAddress(String country, String province, String city, String area, String ISP) {
        this.country = country;
        this.province = province;
        this.city = city;
        this.area = area;
        this.ISP = ISP;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getISP() {
        return ISP;
    }

    public void setISP(String ISP) {
        this.ISP = ISP;
    }

    @Override
    public String toString() {
        return "RegionAddress{" +
                "country='" + country + '\'' +
                ", province='" + province + '\'' +
                ", city='" + city + '\'' +
                ", area='" + area + '\'' +
                ", ISP='" + ISP + '\'' +
                '}';
    }
}
