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
package com.alibaba.csp.sentinel.datasource.redis;

import com.alibaba.csp.sentinel.datasource.Converter;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
import com.alibaba.csp.sentinel.datasource.redis.config.RedisConnectionConfig;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.pubsub.StatefulRedisPubSubConnection;
import io.lettuce.core.pubsub.api.sync.RedisPubSubCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link WritableDataSource} based on redis.
 * Redis 数据源写入操作
 *
 * @param <T> data type
 * @author wangduo
 * @since 0.2.0
 */
public class RedisWritableDataSource<T> implements WritableDataSource<T> {

    private static Logger logger = LoggerFactory.getLogger(RedisWritableDataSource.class);
    private final Converter<T, String> configEncoder;
    // 写入的 redis key
    private String ruleKey = null;
    private String channel = null;

    private RedisConnectionConfig config;
    RedisClient client = null;

    public RedisWritableDataSource(RedisConnectionConfig config, String ruleKey, String channel, Converter<T, String> configEncoder) {
        this.config = config;
        this.ruleKey = ruleKey;
        this.channel = channel;
        this.configEncoder = configEncoder;
    }

    @Override
    public void write(T value) throws Exception {
        String convertResult = configEncoder.convert(value);
        StatefulRedisPubSubConnection<String, String> connection = null;
        try {

            client = RedisClient.create(
                    RedisURI.builder()
                            .withHost(config.getHost())
                            .withPort(config.getPort())
                            .withDatabase(config.getDatabase())
                            .withPassword(config.getPassword()).build());
            connection = client.connectPubSub();
            RedisPubSubCommands<String, String> subCommands = connection.sync();
            subCommands.set(ruleKey, convertResult);
            connection.close();
        } catch (Exception e) {
            logger.info("redis 写入错误{}", e.getMessage());
            throw new Exception(e.getMessage());
        } finally {
            if (connection != null) {
                connection.close();
            }
        }
    }

    @Override
    public void close() throws Exception {
    }
}
