/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.context.provider.metric;

import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.module.StatDataSender;
import com.navercorp.pinpoint.profiler.context.monitor.metric.CustomMetricRegistryService;
import com.navercorp.pinpoint.profiler.monitor.CustomMetricMonitor;
import com.navercorp.pinpoint.profiler.monitor.DefaultCustomMetricMonitor;
import com.navercorp.pinpoint.profiler.monitor.DisabledCustomMetricMonitor;
import com.navercorp.pinpoint.profiler.monitor.collector.AgentCustomMetricCollector;
import com.navercorp.pinpoint.profiler.sender.DataSender;

import com.google.inject.Inject;
import com.google.inject.Provider;

/**
 * @author Taejin Koo
 */
public class CustomMetricMonitorProvider implements Provider<CustomMetricMonitor> {

    private final DataSender dataSender;
    private final CustomMetricRegistryService customMetricRegistryService;
    private final ProfilerConfig profilerConfig;

    @Inject
    public CustomMetricMonitorProvider(@StatDataSender DataSender dataSender,
                                       CustomMetricRegistryService customMetricRegistryService,
                                       ProfilerConfig profilerConfig) {
        this.dataSender = Assert.requireNonNull(dataSender, "dataSender");
        this.customMetricRegistryService = Assert.requireNonNull(customMetricRegistryService, "customMetricRegistryService");
        this.profilerConfig = Assert.requireNonNull(profilerConfig, "profilerConfig");
    }

    @Override
    public CustomMetricMonitor get() {
        if (profilerConfig.isCustomMetricEnable()) {
            return new DefaultCustomMetricMonitor(dataSender, customMetricRegistryService, profilerConfig);
        } else {
            return new DisabledCustomMetricMonitor();
        }
    }

}

