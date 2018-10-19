package com.taobao.csp.sentinel.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * @Auther: wangduo
 * @Date: 2018/10/16 15:43
 * @Description:
 */
@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String host = null;
    @Value("${spring.redis.port}")
    private int port;
    @Value("${spring.redis.flowRuleKey}")
    private String flowRuleKey;
    @Value("${spring.redis.flowChannel}")
    private String flowChannel;
    @Value("${spring.redis.degradeRuleKey}")
    private String degradeRuleKey;
    @Value("${spring.redis.degradeChannel}")
    private String degradeChannel;
    @Value("${spring.redis.systemRuleKey}")
    private String systemRuleKey;
    @Value("${spring.redis.systemChannel}")
    private String systemChannel;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        System.out.println("host ............"+host);
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getFlowRuleKey() {
        return flowRuleKey;
    }

    public void setFlowRuleKey(String flowRuleKey) {
        this.flowRuleKey = flowRuleKey;
    }

    public String getFlowChannel() {
        return flowChannel;
    }

    public void setFlowChannel(String flowChannel) {
        this.flowChannel = flowChannel;
    }

    public String getDegradeRuleKey() {
        return degradeRuleKey;
    }

    public void setDegradeRuleKey(String degradeRuleKey) {
        this.degradeRuleKey = degradeRuleKey;
    }

    public String getDegradeChannel() {
        return degradeChannel;
    }

    public void setDegradeChannel(String degradeChannel) {
        this.degradeChannel = degradeChannel;
    }

    public String getSystemRuleKey() {
        return systemRuleKey;
    }

    public void setSystemRuleKey(String systemRuleKey) {
        this.systemRuleKey = systemRuleKey;
    }

    public String getSystemChannel() {
        return systemChannel;
    }

    public void setSystemChannel(String systemChannel) {
        this.systemChannel = systemChannel;
    }
}
