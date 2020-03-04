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

package com.navercorp.pinpoint.web.mapper.stat.sampling.sampler;

import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.web.vo.stat.SampledDataSource;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatStatusSummary;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummary;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSampler;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSamplers;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Taejin Koo
 */
@Component
public class RequestsStatSummaryTimeSampler implements AgentStatSampler<RequestsStatSummaryBo, SampledRequestsStatSummary> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final DownSampler<Integer> INTEGER_DOWN_SAMPLER = DownSamplers.getIntegerDownSampler(SampledDataSource.UNCOLLECTED_VALUE);
    private static final DownSampler<Long> LONG_DOWN_SAMPLER = DownSamplers.getLongDownSampler(SampledRequestsStatSummary.UNCOLLECTED_LONG_COUNT);

    @Override
    public SampledRequestsStatSummary sampleDataPoints(int index, long timestamp, List<RequestsStatSummaryBo> dataPoints, RequestsStatSummaryBo previousDataPoint) {
        if (CollectionUtils.isEmpty(dataPoints)) {
            return null;
        }

        RequestsStatSummaryBo representativeStatSummary = dataPoints.get(0);
        String url = representativeStatSummary.getUrl();

        SampledRequestsStatSummary result = new SampledRequestsStatSummary(url);

        Map<Integer, List<RequestsStatSummaryBo>> summaryBoListMap = divideByStatus(dataPoints);
        for (Map.Entry<Integer, List<RequestsStatSummaryBo>> entry : summaryBoListMap.entrySet()) {
            int status = entry.getKey();

            List<RequestsStatSummaryBo> summaryBoList = entry.getValue();
            SampledRequestsStatStatusSummary sampledRequestsStatStatusSummary = createSampledRequestsStatStatusSummary(timestamp, summaryBoList);

            result.add(status, sampledRequestsStatStatusSummary);
        }

        return result;
    }

    private Map<Integer, List<RequestsStatSummaryBo>> divideByStatus(List<RequestsStatSummaryBo> dataPoints) {
        Map<Integer, List<RequestsStatSummaryBo>> result = new HashMap<>();

        for (RequestsStatSummaryBo dataPoint : dataPoints) {
            int status = dataPoint.getStatus();
            List<RequestsStatSummaryBo> requestsStatSummaryBoList = result.get(status);
            if (requestsStatSummaryBoList == null) {
                requestsStatSummaryBoList = new ArrayList<>();
                result.put(status, requestsStatSummaryBoList);
            }

            requestsStatSummaryBoList.add(dataPoint);
        }

        return result;
    }

    public SampledRequestsStatStatusSummary createSampledRequestsStatStatusSummary(long timestamp, List<RequestsStatSummaryBo> dataPoints) {
        if (CollectionUtils.isEmpty(dataPoints)) {
            return null;
        }

        List<Integer> counts = new ArrayList<>();
        List<Long> avgTimes = new ArrayList<>();
        List<Long> maxTimes = new ArrayList<>();

        long totalCount = 0;
        for (RequestsStatSummaryBo dataPoint : dataPoints) {
            int count = dataPoint.getCount();
            long avgTime = dataPoint.getAvgTime();
            long maxTime = dataPoint.getMaxTime();

            totalCount += count;
            counts.add(count);
            avgTimes.add(avgTime);
            maxTimes.add(maxTime);
        }

        return new SampledRequestsStatStatusSummary(totalCount, createIntegerPoint(timestamp, counts), createLongPoint(timestamp, avgTimes), createLongPoint(timestamp, maxTimes));
    }

    private AgentStatPoint<Integer> createIntegerPoint(long timestamp, List<Integer> values) {
        if (values.isEmpty()) {
            return SampledRequestsStatSummary.UNCOLLECTED_INT_POINT_CREATOR.createUnCollectedPoint(timestamp);
        } else {
            return new AgentStatPoint<>(
                    timestamp,
                    INTEGER_DOWN_SAMPLER.sampleMin(values),
                    INTEGER_DOWN_SAMPLER.sampleMax(values),
                    INTEGER_DOWN_SAMPLER.sampleAvg(values, 3),
                    INTEGER_DOWN_SAMPLER.sampleSum(values));
        }
    }

    private AgentStatPoint<Long> createLongPoint(long timestamp, List<Long> values) {
        if (values.isEmpty()) {
            return SampledRequestsStatSummary.UNCOLLECTED_LONG_POINT_CREATOR.createUnCollectedPoint(timestamp);
        }

        return new AgentStatPoint<>(
                timestamp,
                LONG_DOWN_SAMPLER.sampleMin(values),
                LONG_DOWN_SAMPLER.sampleMax(values),
                LONG_DOWN_SAMPLER.sampleAvg(values),
                LONG_DOWN_SAMPLER.sampleSum(values));

    }

}
