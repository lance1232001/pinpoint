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

import com.navercorp.pinpoint.common.server.bo.metric.DoubleGaugeMetricBo;
import com.navercorp.pinpoint.grpc.trace.PDoubleValue;
import com.navercorp.pinpoint.grpc.trace.PDouleGaugeMetric;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class GrpcDoubleGaugeMetricMapper implements GrpcCustomMetricMapper<PDouleGaugeMetric, DoubleGaugeMetricBo> {

    @Override
    public DoubleGaugeMetricBo map(PDouleGaugeMetric pDouleGaugeMetric, List<Long> timestampList) {
        if (pDouleGaugeMetric == null || timestampList == null) {
            return null;
        }

        List<PDoubleValue> doubleValueList = pDouleGaugeMetric.getValuesList();
        int valueSize = doubleValueList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        String name = pDouleGaugeMetric.getName();
        DoubleGaugeMetricBo doubleGaugeMetricBo = new DoubleGaugeMetricBo(name);
        for (int i = 0; i < valueSize; i++) {
            PDoubleValue doubleValue = doubleValueList.get(i);
            Long timestamp = timestampList.get(i);
            if (doubleValue.getIsNotSet()) {
                doubleGaugeMetricBo.add(null, timestamp);
            } else {
                doubleGaugeMetricBo.add(doubleValue.getValue(), timestamp);
            }
        }

        return doubleGaugeMetricBo;
    }

}
