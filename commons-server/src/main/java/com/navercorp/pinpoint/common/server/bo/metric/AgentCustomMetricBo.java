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
public class AgentCustomMetricBo {

    private String agentId;

    private long startTimestamp;

    private List<IntCountMetricListBo> intCountMetricBoList = new ArrayList<>();

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public long getStartTimestamp() {
        return startTimestamp;
    }

    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    public List<IntCountMetricListBo> getIntCountMetricBoList() {
        return intCountMetricBoList;
    }

    public void addIntCountMetricBo(IntCountMetricListBo intCountMetricBo) {
        this.intCountMetricBoList.add(intCountMetricBo);
    }

    @Override
    public String toString() {
        return "AgentCustomMetricBo{" +
            "agentId='" + agentId + '\'' +
            ", startTimestamp=" + startTimestamp +
            ", intCountMetricBoList=" + intCountMetricBoList +
            '}';
    }
}
