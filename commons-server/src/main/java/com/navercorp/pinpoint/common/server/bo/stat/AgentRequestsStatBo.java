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

package com.navercorp.pinpoint.common.server.bo.stat;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Taejin Koo
 */
public class AgentRequestsStatBo implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private final Map<String, AgentRequestsStatDataPoint> requestsStatDataPointMap = new HashMap<>();

    public void put(String url, int status, long startTime, long elpasedTime) {
        AgentRequestsStatDataPoint agentRequestsStatDataPoint = requestsStatDataPointMap.get(url);
        if (agentRequestsStatDataPoint == null) {
            agentRequestsStatDataPoint = new AgentRequestsStatDataPoint(url);
            requestsStatDataPointMap.put(url, agentRequestsStatDataPoint);
        }

        agentRequestsStatDataPoint.addEachRequest(status, startTimestamp, elpasedTime);
    }

    public Collection<String> getUrlList() {
        if (requestsStatDataPointMap.size() == 0) {
            return Collections.emptyList();
        }

        return requestsStatDataPointMap.keySet();
    }

    public AgentRequestsStatDataPoint getAgentRequestsStatDataPointList(String url) {
        AgentRequestsStatDataPoint agentRequestsStatDataPoint = requestsStatDataPointMap.get(url);
        return agentRequestsStatDataPoint;
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
        return AgentStatType.REQUESTS;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentRequestsStatBo{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", requestsStatDataPointMap=").append(requestsStatDataPointMap);
        sb.append('}');
        return sb.toString();
    }

}
