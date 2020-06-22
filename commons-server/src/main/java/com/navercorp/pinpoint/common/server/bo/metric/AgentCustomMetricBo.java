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

import com.navercorp.pinpoint.common.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Taejin Koo
 */
public class AgentCustomMetricBo {

    private String agentId;

    private long startTimestamp;

    private Map<String, IntCountMetricListBo> intCountMetricListBoMap = new HashMap<>();

    private Map<String, LongCountMetricListBo> longCountMetricListBoMap = new HashMap<>();


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

    public boolean addIntCountMetricBo(IntCountMetricListBo intCountMetricBo) {
        String name = intCountMetricBo.getName();

        if (StringUtils.isEmpty(name)) {
            return false;
        }

        IntCountMetricListBo oldValue = intCountMetricListBoMap.putIfAbsent(name, intCountMetricBo);
        return oldValue == null;
    }

    public IntCountMetricListBo getIntCountMetricBoList(String name) {
        return intCountMetricListBoMap.get(name);
    }

    public boolean addLongCountMetricBo(LongCountMetricListBo longCountMetricBo) {
        String name = longCountMetricBo.getName();

        if (StringUtils.isEmpty(name)) {
            return false;
        }

        LongCountMetricListBo oldValue = longCountMetricListBoMap.putIfAbsent(name, longCountMetricBo);
        return oldValue == null;
    }


    public LongCountMetricListBo getLongCountMetricBoList(String name) {
        return longCountMetricListBoMap.get(name);
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentCustomMetricBo{");
        sb.append("agentId='").append(agentId).append('\'');
        sb.append(", startTimestamp=").append(startTimestamp);
        sb.append(", intCountMetricListBoMap=").append(intCountMetricListBoMap);
        sb.append(", longCountMetricListBoMap=").append(longCountMetricListBoMap);
        sb.append('}');
        return sb.toString();
    }

}
