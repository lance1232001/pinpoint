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

import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.grpc.trace.PIntValue;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class GrpcIntCountMetricMapper implements GrpcCustomMetricMapper<PIntCountMetric, IntCountMetricBo> {

    @Override
    public IntCountMetricBo map(PIntCountMetric pIntCountMetric, List<Long> timestampList) {
        if (pIntCountMetric == null || timestampList == null) {
            return null;
        }

        List<PIntValue> intValuesList = pIntCountMetric.getValuesList();
        int valueSize = intValuesList.size();
        if (valueSize != timestampList.size()) {
            return null;
        }

        String name = pIntCountMetric.getName();
        IntCountMetricBo intCountMetricBo = new IntCountMetricBo(name);
        for (int i = 0; i < valueSize; i++) {
            PIntValue intValue = intValuesList.get(i);
            Long timestamp = timestampList.get(i);
            if (intValue.getIsNotSet()) {
                intCountMetricBo.add(null, timestamp);
            } else {
                intCountMetricBo.add(intValue.getValue(), timestamp);
            }
        }

        return intCountMetricBo;
    }

}
