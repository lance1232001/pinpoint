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

import com.navercorp.pinpoint.common.server.bo.metric.IntGaugeMetricBo;
import com.navercorp.pinpoint.grpc.trace.PIntGaugeMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class GrpcIntGaugeMetricMapper implements GrpcCustomMetricMapper<PIntGaugeMetric, IntGaugeMetricBo> {

    @Override
    public IntGaugeMetricBo map(PIntGaugeMetric pIntGaugeMetric, List<Long> timestampList) {
        if (pIntGaugeMetric == null || timestampList == null) {
            return null;
        }

        List<PIntValue> intValueList = pIntGaugeMetric.getValuesList();
        int valueSize = intValueList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        String name = pIntGaugeMetric.getName();
        IntGaugeMetricBo intGaugeMetricBo = new IntGaugeMetricBo(name);
        for (int i = 0; i < valueSize; i++) {
            PIntValue intValue = intValueList.get(i);
            Long timestamp = timestampList.get(i);
            if (intValue.getIsNotSet()) {
                intGaugeMetricBo.add(null, timestamp);
            } else {
                intGaugeMetricBo.add(intValue.getValue(), timestamp);
            }
        }

        return intGaugeMetricBo;
    }

}
