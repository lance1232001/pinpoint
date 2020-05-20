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
import com.navercorp.pinpoint.common.server.bo.metric.DoubleGaugeMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntGaugeMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongGaugeMetricBo;
import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.trace.PCustomMetric;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.grpc.trace.PDouleGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PLongCountMetric;
import com.navercorp.pinpoint.grpc.trace.PLongGaugeMetric;

import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Taejin Koo
 */
@Component
public class GrpcAgentCustomMetricMapper {

    private final GrpcIntCountMetricMapper intCountMetricMapper = new GrpcIntCountMetricMapper();
    private final GrpcLongCountMetricMapper longCountMetricMapper = new GrpcLongCountMetricMapper();

    private final GrpcIntGaugeMetricMapper intGaugeMetricMapper = new GrpcIntGaugeMetricMapper();
    private final GrpcLongGaugeMetricMapper longGaugeMetricMapper = new GrpcLongGaugeMetricMapper();
    private final GrpcDoubleGaugeMetricMapper doubleGaugeMetricMapper = new GrpcDoubleGaugeMetricMapper();

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
                IntCountMetricBo intCountMetricBo = createIntCountMetric(timestampList, pCustomMetric);
                if (intCountMetricBo != null) {
                    agentCustomMetricBo.addIntCountMetricBo(intCountMetricBo);
                }
            } else if (pCustomMetric.hasLongCountMetric()) {
                LongCountMetricBo longCountMetricBo = createLongCountMetric(timestampList, pCustomMetric);
                if (longCountMetricBo != null) {
                    agentCustomMetricBo.addLongCountMetricBoList(longCountMetricBo);
                }
            } else if (pCustomMetric.hasIntGaugeMetric()) {
                IntGaugeMetricBo intGaugeMetricBo = createIntGaugeMetric(timestampList, pCustomMetric);
                if (intGaugeMetricBo != null) {
                    agentCustomMetricBo.addIntGaugeMetricBoList(intGaugeMetricBo);
                }
            } else if (pCustomMetric.hasLongGaugeMetric()) {
                LongGaugeMetricBo longGaugeMetricBo = createLongGaugeMetric(timestampList, pCustomMetric);
                if (longGaugeMetricBo != null) {
                    agentCustomMetricBo.addLongGaugeMetricBoList(longGaugeMetricBo);
                }
            } else if (pCustomMetric.hasDoubleGaugeMetric()) {
                DoubleGaugeMetricBo doubleGaugeMetricBo = createDoubleGaugeMetric(timestampList, pCustomMetric);
                if (doubleGaugeMetricBo != null) {
                    agentCustomMetricBo.addDoubleGaugeMetricBoList(doubleGaugeMetricBo);
                }
            } else {
                continue;
            }
        }

        return agentCustomMetricBo;
    }

    private DoubleGaugeMetricBo createDoubleGaugeMetric(List<Long> timestampList, PCustomMetric pCustomMetric) {
        PDouleGaugeMetric doubleGaugeMetric = pCustomMetric.getDoubleGaugeMetric();
        return doubleGaugeMetricMapper.map(doubleGaugeMetric, timestampList);
    }

    private LongGaugeMetricBo createLongGaugeMetric(List<Long> timestampList, PCustomMetric pCustomMetric) {
        PLongGaugeMetric longGaugeMetric = pCustomMetric.getLongGaugeMetric();
        return longGaugeMetricMapper.map(longGaugeMetric, timestampList);
    }

    private IntGaugeMetricBo createIntGaugeMetric(List<Long> timestampList, PCustomMetric pCustomMetric) {
        PIntGaugeMetric intGaugeMetric = pCustomMetric.getIntGaugeMetric();
        return intGaugeMetricMapper.map(intGaugeMetric, timestampList);
    }

    private LongCountMetricBo createLongCountMetric(List<Long> timestampList, PCustomMetric pCustomMetric) {
        PLongCountMetric longCountMetric = pCustomMetric.getLongCountMetric();
        return longCountMetricMapper.map(longCountMetric, timestampList);
    }

    private IntCountMetricBo createIntCountMetric(List<Long> timestampList, PCustomMetric pCustomMetric) {
        PIntCountMetric intCountMetric = pCustomMetric.getIntCountMetric();
        return intCountMetricMapper.map(intCountMetric, timestampList);
    }

}
