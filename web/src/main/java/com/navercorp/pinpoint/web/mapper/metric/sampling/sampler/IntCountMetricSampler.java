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

package com.navercorp.pinpoint.web.mapper.metric.sampling.sampler;

import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetric;
import com.navercorp.pinpoint.web.vo.stat.SampledDataSource;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSampler;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSamplers;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
@Component
public class IntCountMetricSampler implements AgentCustomMetricSampler<IntCountMetricBo, SampledIntCountMetric> {

    private static final DownSampler<Integer> INTEGER_DOWN_SAMPLER = DownSamplers.getIntegerDownSampler(SampledDataSource.UNCOLLECTED_VALUE);

    @Override
    public SampledIntCountMetric sampleDataPoints(int index, long timestamp, List<IntCountMetricBo> intCountMetricBoList, IntCountMetricBo previousIntCountMetricBo) {
        if (CollectionUtils.isEmpty(intCountMetricBoList)) {
            return null;
        }

        final List<Integer> intCountMetricValueList = new ArrayList<>(intCountMetricBoList.size());

        for (IntCountMetricBo intCountMetricBo : intCountMetricBoList) {
            Integer value = intCountMetricBo.getValue();
            intCountMetricValueList.add(value);
        }

        AgentStatPoint<Integer> point = createPoint(timestamp, intCountMetricValueList);

        SampledIntCountMetric sampledIntCountMetric = new SampledIntCountMetric();
        sampledIntCountMetric.setIntCountMetric(point);

        return sampledIntCountMetric;
    }

    private AgentStatPoint<Integer> createPoint(long timestamp, List<Integer> values) {
        if (values.isEmpty()) {
            return SampledDataSource.UNCOLLECTED_POINT_CREATOR.createUnCollectedPoint(timestamp);
        } else {
            return new AgentStatPoint<>(
                    timestamp,
                    INTEGER_DOWN_SAMPLER.sampleMin(values),
                    INTEGER_DOWN_SAMPLER.sampleMax(values),
                    INTEGER_DOWN_SAMPLER.sampleAvg(values, 3),
                    INTEGER_DOWN_SAMPLER.sampleSum(values));
        }
    }


}
