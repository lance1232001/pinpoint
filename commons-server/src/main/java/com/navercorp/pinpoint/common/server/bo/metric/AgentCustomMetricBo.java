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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
public class AgentCustomMetricBo implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;
    private List<CustomMetricBo> customMetricBoList = new ArrayList<>();

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

    public void addCustomMetricBo(CustomMetricBo customMetricBo) {
        customMetricBoList.add(customMetricBo);
    }

    public List<CustomMetricBo> getCustomMetricBoList() {
        return customMetricBoList;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return null;
    }


}
