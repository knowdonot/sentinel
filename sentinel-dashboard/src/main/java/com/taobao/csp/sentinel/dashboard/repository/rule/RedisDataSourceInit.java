/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.taobao.csp.sentinel.dashboard.repository.rule;

import ai.grakn.redismock.RedisServer;
import com.alibaba.csp.sentinel.datasource.*;
import com.alibaba.csp.sentinel.datasource.redis.RedisDataSource;
import com.alibaba.csp.sentinel.datasource.redis.RedisWritableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import com.alibaba.csp.sentinel.init.InitFunc;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRule;
import com.alibaba.csp.sentinel.slots.block.degrade.DegradeRuleManager;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRule;
import com.alibaba.csp.sentinel.slots.block.flow.FlowRuleManager;
import com.alibaba.csp.sentinel.slots.system.SystemRule;
import com.alibaba.csp.sentinel.slots.system.SystemRuleManager;
import com.alibaba.csp.sentinel.transport.util.WritableDataSourceRegistry;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * Redis 数据源初始化规则数据，目前包含三种规则：FlowRule、DegradeRule、SystemRule
 */
public class RedisDataSourceInit implements InitFunc {

    private static RedisServer server = null;

    private RedisClient client;

    private String host = "172.16.51.36";
    private String password = "ecp";
    private int port = RedisConnectionConfig.DEFAULT_REDIS_PORT;

    private String flowRuleKey = "sentinel.rules.flow.ruleKey";
    private String flowChannel = "sentinel.rules.flow.channel";
    private String degradeRuleKey = "sentinel.rules.degrade.ruleKey";
    private String degradeChannel = "sentinel.rules.degrade.channel";
    private String systemRuleKey = "sentinel.rules.system.ruleKey";
    private String systemChannel = "sentinel.rules.system.channel";

    @Override
    public void init() throws Exception {
        System.out.println("init ............");
        Converter<String, List<FlowRule>> flowConfigParser = buildFlowConfigParser();
        RedisConnectionConfig config = RedisConnectionConfig.builder()
                .withHost(host)
                .withPort(port)
                .withPassword(password)
                .withDatabase(10)
                .build();
        // 读取流控规则
        ReadableDataSource<String, List<FlowRule>> flowRedisDataSource = new RedisDataSource<List<FlowRule>>(config,
                flowRuleKey, flowChannel, flowConfigParser);
        FlowRuleManager.register2Property(flowRedisDataSource.getProperty());
        // 写入流控
        WritableDataSource<List<FlowRule>> frWDS = new RedisWritableDataSource(config, flowRuleKey, flowChannel, this::encodeJson);
        WritableDataSourceRegistry.registerFlowDataSource(frWDS);

        //
        Converter<String, List<DegradeRule>> degradeConfigParser = buildDegradeConfigParser();
        // 读取降级规则
        ReadableDataSource<String, List<DegradeRule>> DegradeRedisDataSource = new RedisDataSource<List<DegradeRule>>(config,
                degradeRuleKey, degradeChannel, degradeConfigParser);
        DegradeRuleManager.register2Property(DegradeRedisDataSource.getProperty());
        // 写入降级规则
        WritableDataSource<List<DegradeRule>> drWDS = new RedisWritableDataSource(config, degradeRuleKey, degradeChannel, this::encodeJson);
        WritableDataSourceRegistry.registerDegradeDataSource(drWDS);


        Converter<String, List<SystemRule>> systemRuleConfigParser = buildSystemConfigParser();
        // 读取系统规则
        ReadableDataSource<String, List<SystemRule>> SystemRedisDataSource = new RedisDataSource<List<SystemRule>>(config,
                systemRuleKey, systemChannel, systemRuleConfigParser);
        SystemRuleManager.register2Property(SystemRedisDataSource.getProperty());
        // 写入系统规则
        WritableDataSource<List<SystemRule>> srWDS = new RedisWritableDataSource(config, systemRuleKey, systemChannel, this::encodeJson);
        WritableDataSourceRegistry.registerSystemDataSource(srWDS);


    }

    private Converter<String, List<FlowRule>> buildFlowConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {
        });
    }

    private Converter<String, List<DegradeRule>> buildDegradeConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {
        });
    }

    private Converter<String, List<SystemRule>> buildSystemConfigParser() {
        return source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {
        });
    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
