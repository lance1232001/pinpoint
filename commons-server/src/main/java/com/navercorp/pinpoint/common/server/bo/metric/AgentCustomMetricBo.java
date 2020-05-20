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

    private List<IntCountMetricBo> intCountMetricBoList = new ArrayList<>();
    private List<LongCountMetricBo> longCountMetricBoList = new ArrayList<>();

    private List<IntGaugeMetricBo> intGaugeMetricBoList = new ArrayList<>();
    private List<LongGaugeMetricBo> longGaugeMetricBoList = new ArrayList<>();
    private List<DoubleGaugeMetricBo> doubleGaugeMetricBoList = new ArrayList<>();

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

    public List<IntCountMetricBo> getIntCountMetricBoList() {
        return intCountMetricBoList;
    }

    public void addIntCountMetricBo(IntCountMetricBo intCountMetricBo) {
        this.intCountMetricBoList.add(intCountMetricBo);
    }

    public List<LongCountMetricBo> getLongCountMetricBoList() {
        return longCountMetricBoList;
    }

    public void addLongCountMetricBoList(LongCountMetricBo longCountMetricBo) {
        this.longCountMetricBoList.add(longCountMetricBo);
    }

    public List<IntGaugeMetricBo> getIntGaugeMetricBoList() {
        return intGaugeMetricBoList;
    }

    public void addIntGaugeMetricBoList(IntGaugeMetricBo intGaugeMetricBo) {
        this.intGaugeMetricBoList.add(intGaugeMetricBo);
    }

    public List<LongGaugeMetricBo> getLongGaugeMetricBoList() {
        return longGaugeMetricBoList;
    }

    public void addLongGaugeMetricBoList(LongGaugeMetricBo longGaugeMetricBo) {
        this.longGaugeMetricBoList.add(longGaugeMetricBo);
    }

    public List<DoubleGaugeMetricBo> getDoubleGaugeMetricBoList() {
        return doubleGaugeMetricBoList;
    }

    public void addDoubleGaugeMetricBoList(DoubleGaugeMetricBo doubleGaugeMetricBo) {
        this.doubleGaugeMetricBoList.add(doubleGaugeMetricBo);
    }

    @Override
    public AgentStatType getAgentStatType() {
        return null;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentCustomMetricBo{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", timestamp=").append(timestamp);
        sb.append(", intCountMetricBoList=").append(intCountMetricBoList);
        sb.append(", longCountMetricBoList=").append(longCountMetricBoList);
        sb.append(", intGaugeMetricBoList=").append(intGaugeMetricBoList);
        sb.append(", longGaugeMetricBoList=").append(longGaugeMetricBoList);
        sb.append(", doubleGaugeMetricBoList=").append(doubleGaugeMetricBoList);
        sb.append('}');
        return sb.toString();
    }
}
