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
import com.navercorp.pinpoint.bootstrap.plugin.monitor.RequestsStatMonitor;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.monitor.storage.AgentStatStorage;
import com.navercorp.pinpoint.profiler.monitor.storage.request.AsyncQueueingRequestStatStorage;
import com.navercorp.pinpoint.profiler.monitor.vo.RequestsStatInfo;
import com.navercorp.pinpoint.profiler.sender.DataSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taejin Koo
 */
public class DefaultRequestsStatMonitor<T> implements RequestsStatMonitor<T> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final UrlMappingExtractor<T> urlMappingExtractor;

    private final AgentStatStorage<RequestsStatInfo> agentStatStorage;

    public DefaultRequestsStatMonitor(DataSender dataSender, UrlMappingExtractor<T> urlMappingExtractor) {
        Assert.requireNonNull(dataSender, "dataSender");
        this.urlMappingExtractor = Assert.requireNonNull(urlMappingExtractor, "urlMappingExtractor");
        agentStatStorage = new AsyncQueueingRequestStatStorage(dataSender, 8192, "DefaultRequestsStatMonitor");
    }

    @Override
    public void store(T request, String rawUrl, int status, long startTime, long endTime) {
        String url = urlMappingExtractor.getUrl(request, rawUrl);
        if (url == null) {
            logger.warn("can not extract url. request:{}, rawUrl:{}", request, rawUrl);
        }

        RequestsStatInfo requestsStatInfo = new RequestsStatInfo(url, status, startTime, endTime - startTime);

        if (logger.isDebugEnabled()) {
            logger.debug("store() started. requestsStatInfo:{}", requestsStatInfo);
        }

        agentStatStorage.store(requestsStatInfo);
    }

}
