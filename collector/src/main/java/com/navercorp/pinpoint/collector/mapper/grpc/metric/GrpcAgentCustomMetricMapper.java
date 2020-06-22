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

package com.navercorp.pinpoint.collector.mapper.grpc.metric;

import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.trace.PCustomMetric;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;
import com.navercorp.pinpoint.grpc.trace.PLongCountMetric;
import com.navercorp.pinpoint.grpc.trace.PLongValue;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Taejin Koo
 */
@Component
public class GrpcAgentCustomMetricMapper {

    private final GrpcIntCountMetricMapper intCountMetricMapper = new GrpcIntCountMetricMapper();
    private final GrpcLongCountMetricMapper longCountMetricMapper = new GrpcLongCountMetricMapper();

    public AgentCustomMetricBo map(final PCustomMetricMessage customMetricMessage, final Header header) {
        if (customMetricMessage == null) {
            return null;
        }
        final String agentId = header.getAgentId();
        final long startTimestamp = header.getAgentStartTime();

        List<Long> timestampList = customMetricMessage.getTimestampList();
        List<Long> collectIntervalList = customMetricMessage.getCollectIntervalList();

        if (timestampList.size() != collectIntervalList.size()) {
            return null;
        }

        AgentCustomMetricBo agentCustomMetricBo = new AgentCustomMetricBo();
        agentCustomMetricBo.setAgentId(agentId);
        agentCustomMetricBo.setStartTimestamp(startTimestamp);

        List<PCustomMetric> customMetricsList = customMetricMessage.getCustomMetricsList();
        for (PCustomMetric pCustomMetric : customMetricsList) {
            if (pCustomMetric.hasIntCountMetric()) {
                IntCountMetricListBo intCountMetricBo = createIntCountMetric(pCustomMetric, timestampList, header);
                if (intCountMetricBo != null) {
                    agentCustomMetricBo.addIntCountMetricBo(intCountMetricBo);
                }
            } else if (pCustomMetric.hasLongCountMetric()) {
                LongCountMetricListBo longCountMetric = createLongCountMetric(pCustomMetric, timestampList, header);
                if (longCountMetric != null) {
                    agentCustomMetricBo.addLongCountMetricBo(longCountMetric);
                }
            } else {
                continue;
            }
        }

        return agentCustomMetricBo;
    }

    private IntCountMetricListBo createIntCountMetric(PCustomMetric pCustomMetric, List<Long> timestampList, final Header header) {
        PIntCountMetric intCountMetric = pCustomMetric.getIntCountMetric();

        List<PIntValue> intValuesList = intCountMetric.getValuesList();
        int valueSize = intValuesList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        IntCountMetricListBo intCountMetricListBo = new IntCountMetricListBo();
        for (int i = 0; i < valueSize; i++) {
            PIntValue pIntValue = intValuesList.get(i);
            Long timestmap = timestampList.get(i);

            IntCountMetricBo intCountMetricBo = intCountMetricMapper.map(pIntValue);

            if (intCountMetricBo != null) {
                setBaseData(intCountMetricBo, header.getAgentId(), header.getAgentStartTime(), timestmap);
                intCountMetricListBo.add(intCountMetricBo);
            }
        }

        String metricName = intCountMetric.getName();
        intCountMetricListBo.setName(metricName);
        return intCountMetricListBo;
    }

    private LongCountMetricListBo createLongCountMetric(PCustomMetric pCustomMetric, List<Long> timestampList, final Header header) {
        PLongCountMetric longCountMetric = pCustomMetric.getLongCountMetric();

        List<PLongValue> longValuesList = longCountMetric.getValuesList();
        int valueSize = longValuesList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        LongCountMetricListBo longCountMetricListBo = new LongCountMetricListBo();
        for (int i = 0; i < valueSize; i++) {
            PLongValue pLongValue = longValuesList.get(i);
            Long timestmap = timestampList.get(i);

            LongCountMetricBo longCountMetricBo = longCountMetricMapper.map(pLongValue);

            if (longCountMetricBo != null) {
                setBaseData(longCountMetricBo, header.getAgentId(), header.getAgentStartTime(), timestmap);
                longCountMetricListBo.add(longCountMetricBo);
            }
        }

        String metricName = longCountMetric.getName();
        longCountMetricListBo.setName(metricName);
        return longCountMetricListBo;
    }

    private void setBaseData(AgentStatDataPoint agentStatDataPoint, String agentId, long startTimestamp, long timestamp) {
        agentStatDataPoint.setAgentId(agentId);
        agentStatDataPoint.setStartTimestamp(startTimestamp);
        agentStatDataPoint.setTimestamp(timestamp);
    }

}
