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

import com.alibaba.csp.sentinel.datasource.FileRefreshableDataSource;
import com.alibaba.csp.sentinel.datasource.FileWritableDataSource;
import com.alibaba.csp.sentinel.datasource.ReadableDataSource;
import com.alibaba.csp.sentinel.datasource.WritableDataSource;
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

import java.net.URLDecoder;
import java.util.List;

/**
 * <p>
 * A sample showing how to register readable and writable data source via Sentinel init SPI mechanism.
 * </p>
 * <p>
 * To activate this, you can add the class name to `com.alibaba.csp.sentinel.init.InitFunc` file
 * in `META-INF/services/` directory of the resource directory. Then the data source will be automatically
 * registered during the initialization of Sentinel.
 * </p>
 *
 * @author Eric Zhao
 */
public class FileDataSourceInit implements InitFunc {

    @Override
    public void init() throws Exception {
        // A fake path.  D:\temp\rule
        ClassLoader classLoader = getClass().getClassLoader();
        String flowRulePath = "D:\\temp\\rule\\FlowRule.json";
        String degradeRulePath = "D:\\temp\\rule\\DegradeRule.json";
        String systemRulePath = "D:\\temp\\rule\\SystemRule.json";

        ReadableDataSource<String, List<FlowRule>> ds = new FileRefreshableDataSource<>(
            flowRulePath, source -> JSON.parseObject(source, new TypeReference<List<FlowRule>>() {})
        );
        // Register to flow rule manager.
        FlowRuleManager.register2Property(ds.getProperty());

        WritableDataSource<List<FlowRule>> wds = new FileWritableDataSource<>(flowRulePath, this::encodeJson);
        // Register to writable data source registry so that rules can be updated to file
        // when there are rules pushed from the Sentinel Dashboard.
        WritableDataSourceRegistry.registerFlowDataSource(wds);

        ReadableDataSource<String, List<DegradeRule>> drDS = new FileRefreshableDataSource<>(
                degradeRulePath, source -> JSON.parseObject(source, new TypeReference<List<DegradeRule>>() {})
        );
        DegradeRuleManager.register2Property(drDS.getProperty());
        WritableDataSource<List<DegradeRule>> drWDS = new FileWritableDataSource<>(degradeRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerDegradeDataSource(drWDS);

        ReadableDataSource<String, List<SystemRule>> srDS = new FileRefreshableDataSource<>(
                systemRulePath, source -> JSON.parseObject(source, new TypeReference<List<SystemRule>>() {})
        );
        SystemRuleManager.register2Property(srDS.getProperty());
        WritableDataSource<List<SystemRule>> srWDS = new FileWritableDataSource<>(systemRulePath, this::encodeJson);
        WritableDataSourceRegistry.registerSystemDataSource(srWDS);


    }

    private <T> String encodeJson(T t) {
        return JSON.toJSONString(t);
    }
}
