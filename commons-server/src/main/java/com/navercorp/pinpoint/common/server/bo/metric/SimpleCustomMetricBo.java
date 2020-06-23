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

package com.navercorp.pinpoint.common.server.bo.metric;

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class SimpleCustomMetricBo implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private final AgentStatType agentStatType;

    private final Map<String, CustomMetricBo> customMetricBoMap = new HashMap<>();
//    private final Map<String, LongCountMetricBo> longCountMetricBoMap = new HashMap<>();

    public boolean put(String key, CustomMetricBo value) {
        CustomMetricBo customMetricBo = customMetricBoMap.putIfAbsent(key, value);
        return customMetricBo == null;
    }


    public CustomMetricBo get(String key) {
        return customMetricBoMap.get(key);
    }

    public SimpleCustomMetricBo(AgentStatType agentStatType) {
        this.agentStatType = Objects.requireNonNull(agentStatType, "agentStatType");
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return agentStatType;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SimpleCustomMetricBo{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", agentStatType=").append(agentStatType);
        sb.append(", customMetricBoMap=").append(customMetricBoMap);
        sb.append('}');
        return sb.toString();
    }

}
