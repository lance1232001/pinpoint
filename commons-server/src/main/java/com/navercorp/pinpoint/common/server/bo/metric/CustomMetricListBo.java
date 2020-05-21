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

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPointList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public abstract class CustomMetricListBo<T extends CustomMetricBo> implements AgentStatDataPointList<T> {

    private String name;
    private final List<T> customMetricBoList = new ArrayList<>();

    private String agentId;
    private long startTimestamp;
    private long timestamp;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean add(T element) {
        return customMetricBoList.add(element);
    }

    @Override
    public boolean remove(T element) {
        return customMetricBoList.remove(element);
    }

    @Override
    public int size() {
        if (customMetricBoList == null) {
            return 0;
        }

        return customMetricBoList.size();
    }

    @Override
    public List<T> getList() {
        return new ArrayList<>(customMetricBoList);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomMetricListBo<?> that = (CustomMetricListBo<?>) o;
        return startTimestamp == that.startTimestamp &&
            timestamp == that.timestamp &&
            Objects.equals(customMetricBoList, that.customMetricBoList) &&
            Objects.equals(agentId, that.agentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customMetricBoList, agentId, startTimestamp, timestamp);
    }

    @Override
    public String toString() {
        return "CustomMetricListBo{" +
            "customMetricBoList=" + customMetricBoList +
            ", agentId='" + agentId + '\'' +
            ", startTimestamp=" + startTimestamp +
            ", timestamp=" + timestamp +
            '}';
    }

}
