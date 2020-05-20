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

import com.navercorp.pinpoint.common.server.bo.metric.LongGaugeMetricBo;
import com.navercorp.pinpoint.grpc.trace.PLongGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PLongValue;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class GrpcLongGaugeMetricMapper implements GrpcCustomMetricMapper<PLongGaugeMetric, LongGaugeMetricBo> {

    @Override
    public LongGaugeMetricBo map(PLongGaugeMetric pLongGaugeMetric, List<Long> timestampList) {
        if (pLongGaugeMetric == null || timestampList == null) {
            return null;
        }

        List<PLongValue> longValueList = pLongGaugeMetric.getValuesList();
        int valueSize = longValueList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        String name = pLongGaugeMetric.getName();
        LongGaugeMetricBo longGaugeMetricBo = new LongGaugeMetricBo(name);
        for (int i = 0; i < valueSize; i++) {
            PLongValue longValue = longValueList.get(i);
            Long timestamp = timestampList.get(i);
            if (longValue.getIsNotSet()) {
                longGaugeMetricBo.add(null, timestamp);
            } else {
                longGaugeMetricBo.add(longValue.getValue(), timestamp);
            }
        }

        return longGaugeMetricBo;
    }

}
