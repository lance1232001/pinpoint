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

package com.navercorp.pinpoint.web.mapper.stat;

import com.navercorp.pinpoint.common.hbase.ResultsExtractor;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.web.mapper.stat.sampling.AgentStatSamplingHandler;
import com.navercorp.pinpoint.web.mapper.stat.sampling.EagerSamplingHandler;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.AgentStatSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummary;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummaryList;

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
public class SampledRequestsStatSummaryResultExtractor implements ResultsExtractor<List<SampledRequestsStatSummaryList>> {

    private final TimeWindow timeWindow;
    private final AgentStatMapper<RequestsStatSummaryBo> rowMapper;
    private final AgentStatSampler<RequestsStatSummaryBo, SampledRequestsStatSummary> sampler;

    public SampledRequestsStatSummaryResultExtractor(TimeWindow timeWindow, AgentStatMapper<RequestsStatSummaryBo> rowMapper, AgentStatSampler<RequestsStatSummaryBo, SampledRequestsStatSummary> sampler) {
        if (timeWindow.getWindowRangeCount() > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("range yields too many timeslots");
        }
        this.timeWindow = timeWindow;
        this.rowMapper = rowMapper;
        this.sampler = sampler;
    }

    @Override
    public List<SampledRequestsStatSummaryList> extractData(ResultScanner hbaseResults) throws Exception {
        // divide by dataSource id

        List<RequestsStatSummaryBo> decodedList = decode(hbaseResults);
        Map<String, List<RequestsStatSummaryBo>> requestsStatSummaryBoListMap = divideByUrl(decodedList);

        List<SampledRequestsStatSummaryList> result = new ArrayList<>();
        for (List<RequestsStatSummaryBo> value : requestsStatSummaryBoListMap.values()) {
            List<SampledRequestsStatSummary> sampleData = getSampleData(value);

            SampledRequestsStatSummaryList sampledRequestsStatSummaryList = new SampledRequestsStatSummaryList();
            sampledRequestsStatSummaryList.addAll(sampleData);
            result.add(sampledRequestsStatSummaryList);
        }

        return result;
    }

    private List<RequestsStatSummaryBo> decode(ResultScanner hbaseResults) throws Exception {
        int rowNum = 0;

        List<RequestsStatSummaryBo> result = new ArrayList<>();
        for (Result hbaseResult : hbaseResults) {
            List<RequestsStatSummaryBo> summaryBoList = rowMapper.mapRow(hbaseResult, rowNum++);
            result.addAll(summaryBoList);
        }
        return result;

    }

    private Map<String, List<RequestsStatSummaryBo>> divideByUrl(List<RequestsStatSummaryBo> summaryBoList) throws Exception {
        Map<String, List<RequestsStatSummaryBo>> result = new HashMap<>();

        for (RequestsStatSummaryBo summaryBo : summaryBoList) {
            String url = summaryBo.getUrl();

            List<RequestsStatSummaryBo> dividedList = result.get(url);
            if (dividedList == null) {
                dividedList = new ArrayList<>();
                result.put(url, dividedList);
            }

            dividedList.add(summaryBo);
        }

        return result;
    }

    private List<SampledRequestsStatSummary> getSampleData(List<RequestsStatSummaryBo> requestsStatSummaryBoList) {
        requestsStatSummaryBoList.sort(new Comparator<RequestsStatSummaryBo>() {
            @Override
            public int compare(RequestsStatSummaryBo o1, RequestsStatSummaryBo o2) {
                return Long.compare(o2.getTimestamp(), o1.getTimestamp());
            }
        });

        AgentStatSamplingHandler<RequestsStatSummaryBo, SampledRequestsStatSummary> samplingHandler = new EagerSamplingHandler<>(timeWindow, sampler);
        for (RequestsStatSummaryBo requestsStatSummaryBo : requestsStatSummaryBoList) {
            samplingHandler.addDataPoint(requestsStatSummaryBo);
        }
        List<SampledRequestsStatSummary> sampledDataSourceList = samplingHandler.getSampledDataPoints();

        return sampledDataSourceList;
    }

}

