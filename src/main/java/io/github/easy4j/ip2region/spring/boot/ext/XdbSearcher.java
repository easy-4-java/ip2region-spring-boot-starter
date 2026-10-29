package io.github.easy4j.ip2region.spring.boot.ext;

import io.github.easy4j.ip2region.spring.boot.util.IpUtils;
import io.github.easy4j.ip2region.spring.boot.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.lionsoul.ip2region.xdb.Header;
import org.lionsoul.ip2region.xdb.InetAddressException;
import org.lionsoul.ip2region.xdb.LongByteArray;
import org.lionsoul.ip2region.xdb.Searcher;
import org.lionsoul.ip2region.xdb.Version;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/** 基于独立 IPv4、IPv6 XDB 的全内存查询器，保留原有 IPv4 接口。 */
@Slf4j
public class XdbSearcher implements ResourceLoaderAware, DisposableBean {

    public static final String NOT_MATCH = "0|0|0|内网IP|内网IP";
    public static final RegionAddress NOT_MATCH_REGION_ADDRESS = new RegionAddress(NOT_MATCH.split("\\|"));
    public static final String DEFAULT_LOCATION = "classpath:ip2region/ip2region.xdb";
    public static final String DEFAULT_IPV6_LOCATION = "classpath:ip2region/ip2region_v6.xdb";
    protected ResourceLoader resourceLoader;
    protected byte[] vIndex;
    protected byte[] xdbBuff;
    protected Searcher searcher;
    private Searcher ipv6Searcher;
    private final ReentrantReadWriteLock lifecycleLock = new ReentrantReadWriteLock();
    private boolean closed;

    public XdbSearcher(ResourceLoader resourceLoader) throws IOException {
        this(resourceLoader, DEFAULT_LOCATION);
    }

    public XdbSearcher(ResourceLoader resourceLoader, String location) throws IOException {
        this(resourceLoader, location, true, DEFAULT_IPV6_LOCATION);
    }

    /** 创建双栈查询器；IPv4 沿用 location，IPv6 可独立关闭或指定资源。 */
    public XdbSearcher(ResourceLoader resourceLoader, String location, boolean ipv6Enabled,
                       String ipv6Location) throws IOException {
        this.resourceLoader = resourceLoader;
        this.searcher = loadWithBuffer(location);
        if (ipv6Enabled) {
            this.ipv6Searcher = loadSearcher(ipv6Location, Version.IPv6);
        }
    }

    /** 保留原有 IPv4 缓冲加载接口；已有缓冲时复用，不作为热更新接口。 */
    public synchronized Searcher loadWithBuffer(String location) throws IOException {
        if (Objects.nonNull(xdbBuff)) {
            LongByteArray buffer = new LongByteArray();
            buffer.append(xdbBuff);
            return Searcher.newWithBuffer(Version.IPv4, buffer);
        }
        return loadSearcher(location, Version.IPv4);
    }

    private Searcher loadSearcher(String location, Version version) {
        try {
            if (!StringUtils.hasText(location)) {
                throw new IOException("XDB location must not be blank");
            }
            File file = new File(location);
            try (InputStream input = file.isFile() ? new FileInputStream(file)
                    : resourceLoader.getResource(location).getInputStream()) {
                LongByteArray buffer = Searcher.loadContentFromInputStream(input);
                Header header = Searcher.loadHeaderFromBuffer(buffer);
                Searcher.verify(header, buffer.length());
                if (Version.fromHeader(header).id != version.id) {
                    throw new IOException("XDB address family mismatch: expected " + version.name);
                }
                if (version.id == Version.IPv4.id) {
                    // 保留旧受保护缓冲字段，供既有子类及 loadWithBuffer 使用。
                    xdbBuff = buffer.slice(0, (int) buffer.length());
                    buffer = new LongByteArray();
                    buffer.append(xdbBuff);
                }
                return Searcher.newWithBuffer(version, buffer);
            }
        } catch (Exception e) {
            log.error("Failed to load {} XDB from {}", version.name, location, e);
            return null;
        }
    }

    /** 查询无符号 IPv4 数值（0..4294967295），返回原始归属地或 NOT_MATCH。 */
    public String memorySearch(long ip) throws IOException {
        if (ip < 0 || ip > 0xffffffffL) {
            throw new IOException("IPv4 value out of range: " + ip);
        }
        return memorySearch(Util.long2ip(ip));
    }

    /** 查询 IPv4/IPv6 字面量；非法输入抛 IOException，不进行域名解析。 */
    public String memorySearch(String ip) throws IOException {
        if (!IpUtils.isIpv4(ip) && !IpUtils.isIpv6(ip)) {
            throw new IOException("Invalid IP address: " + ip);
        }
        lifecycleLock.readLock().lock();
        try {
            if (closed) {
                return NOT_MATCH;
            }
            // 已验证为字面量；映射 IPv6 地址由 JDK 规范化成四字节 IPv4。
            byte[] address = org.lionsoul.ip2region.xdb.Util.parseIP(ip);
            Searcher selected = address.length == 4 ? searcher : ipv6Searcher;
            if (Objects.isNull(selected)) {
                return NOT_MATCH;
            }
            String region = selected.search(address);
            return StringUtils.hasText(region) ? region : NOT_MATCH;
        } catch (InetAddressException e) {
            throw new IOException("Invalid IP address: " + ip, e);
        } finally {
            lifecycleLock.readLock().unlock();
        }
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    /** 关闭两种查询器；与正在进行的查询互斥，允许重复关闭。 */
    @Override
    public void destroy() throws Exception {
        lifecycleLock.writeLock().lock();
        try {
            if (!closed) {
                closed = true;
                if (Objects.nonNull(searcher)) {
                    searcher.close();
                }
                if (Objects.nonNull(ipv6Searcher)) {
                    ipv6Searcher.close();
                }
            }
        } finally {
            lifecycleLock.writeLock().unlock();
        }
    }
}
