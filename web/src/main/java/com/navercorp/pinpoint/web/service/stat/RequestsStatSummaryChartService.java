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

package com.navercorp.pinpoint.web.service.stat;

import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.loader.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.rpc.util.ListUtils;
import com.navercorp.pinpoint.web.dao.stat.SampledRequestsStatSummaryDao;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatStatusSummary;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummary;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummaryList;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChart;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.RequestsStatSummaryChart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Service
public class RequestsStatSummaryChartService implements AgentStatChartService {

    private final SampledRequestsStatSummaryDao sampledRequestsStatSummaryDao;

    @Autowired
    private ServiceTypeRegistryService serviceTypeRegistryService;

    @Autowired
    public RequestsStatSummaryChartService(@Qualifier("sampledRequestsStatSummaryDaoFactory") SampledRequestsStatSummaryDao sampledRequestsStatSummaryDao) {
        this.sampledRequestsStatSummaryDao = sampledRequestsStatSummaryDao;
    }

    @Override
    public StatChart selectAgentChart(String agentId, TimeWindow timeWindow) {
        List<StatChart> statCharts = selectAgentChartList(agentId, timeWindow);
        if (CollectionUtils.hasLength(statCharts)) {
            return statCharts.get(0);
        }
        return null;
    }

    @Override
    public List<StatChart> selectAgentChartList(String agentId, TimeWindow timeWindow) {
        Objects.requireNonNull(agentId, "agentId");
        Objects.requireNonNull(timeWindow, "timeWindow");

        List<StatChart> result = new ArrayList<>();

        List<SampledRequestsStatSummaryList> sampledRequestsStatSummaryLists = this.sampledRequestsStatSummaryDao.getSampledAgentStatList(agentId, timeWindow);

        for (SampledRequestsStatSummaryList sampledRequestsStatSummaryList : sampledRequestsStatSummaryLists) {
            if (isEmpty(sampledRequestsStatSummaryList)) {
                continue;
            }

            List<StatChart> statCharts = create(timeWindow, sampledRequestsStatSummaryList);
            result.addAll(statCharts);
        }

        return result;
    }

    private boolean isEmpty(SampledRequestsStatSummaryList sampledRequestsStatSummaryList) {
        if (sampledRequestsStatSummaryList == null) {
            return true;
        }

        if (CollectionUtils.isEmpty(sampledRequestsStatSummaryList.getSampledRequestsStatSummaryList())) {
            return true;
        }

        return false;
    }


    private List<StatChart> create(TimeWindow timeWindow, SampledRequestsStatSummaryList sampledRequestsStatSummaryList) {
        SampledRequestsStatSummary representativeStatSummary = getRepresentativeSummary(sampledRequestsStatSummaryList);
        if (representativeStatSummary == null) {
            return Collections.emptyList();
        }

        String url = representativeStatSummary.getUrl();

        List<StatChart> result = new ArrayList<>();
        Map<Integer, List<SampledRequestsStatStatusSummary>> statStatusSummaryList = divideByStatus(sampledRequestsStatSummaryList);
        for (Map.Entry<Integer, List<SampledRequestsStatStatusSummary>> entry : statStatusSummaryList.entrySet()) {
            StatChart chart = createChart(timeWindow, url, entry.getKey(), entry.getValue());
            result.add(chart);
        }

        return result;
    }

    private SampledRequestsStatSummary getRepresentativeSummary(SampledRequestsStatSummaryList sampledRequestsStatSummaryList) {
        if (isEmpty(sampledRequestsStatSummaryList)) {
            return null;
        }

        SampledRequestsStatSummary representativeStatSummary = ListUtils.getFirst(sampledRequestsStatSummaryList.getSampledRequestsStatSummaryList());
        return representativeStatSummary;
    }


    private Map<Integer, List<SampledRequestsStatStatusSummary>> divideByStatus(SampledRequestsStatSummaryList sampledRequestsStatSummaryList) {
        if (isEmpty(sampledRequestsStatSummaryList)) {
            return Collections.emptyMap();
        }

        Map<Integer, List<SampledRequestsStatStatusSummary>> result = new HashMap<>();

        for (SampledRequestsStatSummary sampledRequestsStatSummary : sampledRequestsStatSummaryList.getSampledRequestsStatSummaryList()) {
            Map<Integer, SampledRequestsStatStatusSummary> statStatusSummaryByStatusMap = sampledRequestsStatSummary.getMap();

            for (Map.Entry<Integer, SampledRequestsStatStatusSummary> entry : statStatusSummaryByStatusMap.entrySet()) {
                List<SampledRequestsStatStatusSummary> sampledRequestsStatStatusSummaries = result.get(entry.getKey());
                if (sampledRequestsStatStatusSummaries == null) {
                    sampledRequestsStatStatusSummaries = new ArrayList<>();
                    result.put(entry.getKey(), sampledRequestsStatStatusSummaries);
                }

                sampledRequestsStatStatusSummaries.add(entry.getValue());
            }
        }

        return result;
    }

    private StatChart createChart(TimeWindow timeWindow, String url, int status, List<SampledRequestsStatStatusSummary> sampledRequestsStatStatusSummaries) {
        return new RequestsStatSummaryChart(timeWindow, url, status, sampledRequestsStatStatusSummaries, serviceTypeRegistryService);
    }

}

