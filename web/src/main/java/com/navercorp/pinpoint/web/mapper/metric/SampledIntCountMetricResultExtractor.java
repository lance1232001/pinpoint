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

package com.navercorp.pinpoint.web.mapper.metric;

import com.navercorp.pinpoint.common.hbase.ResultsExtractor;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.web.mapper.stat.AgentStatMapper;
import com.navercorp.pinpoint.web.mapper.stat.sampling.AgentStatSamplingHandler;
import com.navercorp.pinpoint.web.mapper.stat.sampling.EagerSamplingHandler;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.AgentStatSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetric;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetricList;

import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Taejin Koo
 */
public class SampledIntCountMetricResultExtractor implements ResultsExtractor<List<SampledIntCountMetricList>> {

    private final TimeWindow timeWindow;
    private final AgentStatMapper<IntCountMetricListBo> rowMapper;
    private final AgentStatSampler<IntCountMetricBo, SampledIntCountMetric> sampler;

    public SampledIntCountMetricResultExtractor(TimeWindow timeWindow, AgentStatMapper<IntCountMetricListBo> rowMapper, AgentStatSampler<IntCountMetricBo, SampledIntCountMetric> sampler) {
        if (timeWindow.getWindowRangeCount() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("range yields too many timeslots");
        }
        this.timeWindow = timeWindow;
        this.rowMapper = rowMapper;
        this.sampler = sampler;
    }

    @Override
    public List<SampledIntCountMetricList> extractData(ResultScanner results) throws Exception {
        List<IntCountMetricListBo> intCountMetricListBoList = getIntCountMetricListBoList(results);
        Map<String, IntCountMetricListBo> stringIntCountMetricListBoMap = divideByMetricName(intCountMetricListBoList);

        List<SampledIntCountMetricList> result = new ArrayList<>();
        for (IntCountMetricListBo intCountMetricListBo : stringIntCountMetricListBoMap.values()) {
            SampledIntCountMetricList sampledIntCountMetricList = toSampledData(intCountMetricListBo);
            result.add(sampledIntCountMetricList);
        }

        return result;
    }

    private Map<String, IntCountMetricListBo> divideByMetricName(List<IntCountMetricListBo> intCountMetricListBoList) {

        Map<String, IntCountMetricListBo> stringIntCountMetricListBoMap = new HashMap<>();

        for (IntCountMetricListBo intCountMetricListBo : intCountMetricListBoList) {
            String metricName = intCountMetricListBo.getName();

            IntCountMetricListBo representative = stringIntCountMetricListBoMap.putIfAbsent(metricName, intCountMetricListBo);
            if (representative != null) {
                for (IntCountMetricBo intCountMetricBo : intCountMetricListBo.getList()) {
                    representative.add(intCountMetricBo);
                }
            }
        }

        return stringIntCountMetricListBoMap;
    }

    private List<IntCountMetricListBo> getIntCountMetricListBoList(ResultScanner results) throws Exception {
        List<IntCountMetricListBo> intCountMetricListBoList = new ArrayList<>();

        int rowNum = 0;
        for (Result result : results) {
            for (IntCountMetricListBo intCountMetricListBo : this.rowMapper.mapRow(result, rowNum++)) {
                if (intCountMetricListBo.size() == 0) {
                    continue;
                }

                intCountMetricListBoList.add(intCountMetricListBo);
            }
        }

        return intCountMetricListBoList;
    }

    private SampledIntCountMetricList toSampledData(IntCountMetricListBo intCountMetricListBo) {
        AgentStatSamplingHandler<IntCountMetricBo, SampledIntCountMetric> samplingHandler = new EagerSamplingHandler<>(timeWindow, sampler);

        List<IntCountMetricBo> list = intCountMetricListBo.getList();
        list.sort(new Comparator<IntCountMetricBo>() {
            @Override
            public int compare(IntCountMetricBo o1, IntCountMetricBo o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
            }
        });

        for (IntCountMetricBo intCountMetricBo : list) {
            samplingHandler.addDataPoint(intCountMetricBo);
        }
        List<SampledIntCountMetric> sampledIntCountMetricList = samplingHandler.getSampledDataPoints();

        SampledIntCountMetricList result = new SampledIntCountMetricList(intCountMetricListBo.getName());
        for (SampledIntCountMetric sampledIntCountMetric : sampledIntCountMetricList) {
            result.addSampledIntCountMetric(sampledIntCountMetric);
        }

        return result;
    }

}

