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

package com.navercorp.pinpoint.profiler.monitor.metric;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class AgentCustomMetricSnapshot {

    private long timestamp;
    private long collectInterval;

    private final List<CustomMetricVo> customMetricVoList;

    public AgentCustomMetricSnapshot(List<CustomMetricVo> customMetricVoList) {
        this.customMetricVoList = Assert.requireNonNull(customMetricVoList, "customMetricVoList");
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public long getCollectInterval() {
        return collectInterval;
    }

    public void setCollectInterval(long collectInterval) {
        this.collectInterval = collectInterval;
    }

    public List<CustomMetricVo> getCustomMetricVoList() {
        return customMetricVoList;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentCustomMetricSnapshot{");
        sb.append("timestamp=").append(timestamp);
        sb.append(", collectInterval=").append(collectInterval);
        sb.append(", customMetricVoList=").append(customMetricVoList);
        sb.append('}');
        return sb.toString();
    }

}
