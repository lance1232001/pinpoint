/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.service.metric;

import com.navercorp.pinpoint.web.dao.metric.NettyDirectMemoryDao;
import com.navercorp.pinpoint.web.service.stat.AgentStatChartService;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChart;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
public class NettyDirectMemoryChartService implements AgentStatChartService {

//    private final SampledTotalThreadCountDao sampledTotalThreadCountDao;

    private final NettyDirectMemoryDao nettyDirectMemoryDao;

    public NettyDirectMemoryChartService(NettyDirectMemoryDao nettyDirectMemoryDao) {
        this.nettyDirectMemoryDao = Objects.requireNonNull(nettyDirectMemoryDao, "nettyDirectMemoryDao");
    }

//    List<SampledActiveTrace> sampledActiveTraces = this.sampledActiveTraceDao.getSampledAgentStatList(agentId, timeWindow);
//        return new ActiveTraceChart(timeWindow, sampledActiveTraces);
//}
//
//    @Override
//    public List<StatChart> selectAgentChartList(String agentId, TimeWindow timeWindow) {
//        StatChart agentStatChart = selectAgentChart(agentId, timeWindow);
//
//        List<StatChart> result = new ArrayList<>(1);
//        result.add(agentStatChart);
//
//        return result;


    @Override
    public StatChart selectAgentChart(String agentId, TimeWindow timeWindow) {
        Objects.requireNonNull(agentId, "agentId");
        Objects.requireNonNull(timeWindow, "timeWindow");

//        nettyDirectMemoryDao.getAgentStatList(agentId, timeWindow);

        System.out.println("======================== selectAgentChart");
        return null;
    }

    @Override
    public List<StatChart> selectAgentChartList(String agentId, TimeWindow timeWindow) {
        System.out.println("======================== selectAgentChartList");
        return Collections.emptyList();
    }

}

