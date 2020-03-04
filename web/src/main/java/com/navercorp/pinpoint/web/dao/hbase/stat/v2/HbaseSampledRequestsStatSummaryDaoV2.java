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

package com.navercorp.pinpoint.web.dao.hbase.stat.v2;

import com.navercorp.pinpoint.common.hbase.HbaseTable;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.codec.stat.RequestsStatSummaryDecoder;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.web.dao.stat.SampledRequestsStatSummaryDao;
import com.navercorp.pinpoint.web.mapper.stat.AgentStatMapperV2;
import com.navercorp.pinpoint.web.mapper.stat.SampledRequestsStatSummaryResultExtractor;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.RequestsStatSummaryTimeSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummaryList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Taejin Koo
 */
@Repository("sampledRequestsStatSummaryDaoV2")
public class HbaseSampledRequestsStatSummaryDaoV2 implements SampledRequestsStatSummaryDao {

    @Autowired
    private RequestsStatSummaryDecoder requestsStatSummaryDecoder;

    @Autowired
    private RequestsStatSummaryTimeSampler requestsStatSummaryTimeSampler;

    @Autowired
    private HbaseAgentStatDaoOperationsV2 operations;

    @Autowired(required = false)
    private TableNameProvider tableNameProvider;

    @Override
    public List<SampledRequestsStatSummaryList> getSampledAgentStatList(String agentId, TimeWindow timeWindow) {
        long scanFrom = timeWindow.getWindowRange().getFrom();
        long scanTo = timeWindow.getWindowRange().getTo() + timeWindow.getWindowSlotSize();
        Range range = new Range(scanFrom, scanTo);
        AgentStatMapperV2<RequestsStatSummaryBo> mapper = operations.createRowMapper(requestsStatSummaryDecoder, range);

        SampledRequestsStatSummaryResultExtractor resultExtractor = new SampledRequestsStatSummaryResultExtractor(timeWindow, mapper, requestsStatSummaryTimeSampler);
        return operations.getSampledAgentStatList(tableNameProvider.getTableName(HbaseTable.AGENT_REQUESTS_STAT_VER2), AgentStatType.REQUESTS_SUMMARY, resultExtractor, agentId, range);
    }

}
