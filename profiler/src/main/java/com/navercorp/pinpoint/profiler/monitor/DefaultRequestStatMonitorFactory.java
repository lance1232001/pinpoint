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

package com.navercorp.pinpoint.profiler.monitor;

import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractor;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.RequestStatMonitorFactory;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.RequestsStatMonitor;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.sender.DataSender;

/**
 * @author Taejin Koo
 */
public class DefaultRequestStatMonitorFactory implements RequestStatMonitorFactory {

    private final DataSender dataSender;

    public DefaultRequestStatMonitorFactory(DataSender dataSender) {
        this.dataSender = Assert.requireNonNull(dataSender, "dataSender");
    }

    public <T> RequestsStatMonitor<T> create(UrlMappingExtractor<T> urlMappingExtractor) {
        Assert.requireNonNull(urlMappingExtractor, "urlMappingExtractor");
        return new DefaultRequestsStatMonitor<T>(dataSender, urlMappingExtractor);
    }

}
