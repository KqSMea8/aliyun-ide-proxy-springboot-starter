package com.alibaba.dataworks.ide.proxy;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@ConfigurationProperties(prefix="ide.proxy.data.service")
@Data
public class ProxyProperties {

    private String host = "https://dataservice-api.dw.alibaba-inc.com" ;

    private String appKey ;

    private String appSecret ;

    /**
     * 需要代理的API前缀
     */
    private String apiPrefix = "/api/1.0/dsproxy";

    /**
     * 连接超时时间
     */
    private int connectTimeout = 20000 ;

    /**
     * 读超时时间
     */
    private int readTimeout = 20000 ;

    /**
     * 连接读超时时间
     */
    private int connectionRequestTimeout = 20000 ;

}
