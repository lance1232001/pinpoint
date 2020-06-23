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

package com.navercorp.pinpoint.web.dao.hbase.metric;

import com.navercorp.pinpoint.common.server.bo.codec.metric.IntCountMetricDecoder;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.web.dao.hbase.stat.v2.HbaseAgentStatDaoOperationsV2;
import com.navercorp.pinpoint.web.dao.metric.SampledIntCountMetricDao;
import com.navercorp.pinpoint.web.mapper.metric.SampledIntCountMetricResultExtractor;
import com.navercorp.pinpoint.web.mapper.metric.sampling.sampler.IntCountMetricSampler;
import com.navercorp.pinpoint.web.mapper.stat.AgentStatMapperV2;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetricList;

import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Repository("sampledIntCountMetricDao ")
public class HbaseSampledIntCountMetricDao implements SampledIntCountMetricDao {

    private final HbaseAgentStatDaoOperationsV2 operations;

    private final IntCountMetricDecoder intCountMetricDecoder;
    private final IntCountMetricSampler intCountMetricSampler;

    public HbaseSampledIntCountMetricDao(HbaseAgentStatDaoOperationsV2 operations, IntCountMetricDecoder intCountMetricDecoder, IntCountMetricSampler intCountMetricSampler) {
        this.operations = Objects.requireNonNull(operations, "operations");
        this.intCountMetricDecoder = Objects.requireNonNull(intCountMetricDecoder, "intCountMetricDecoder");
        this.intCountMetricSampler = Objects.requireNonNull(intCountMetricSampler, "intCountMetricSampler");
    }

    @Override
    public List<SampledIntCountMetricList> getSampledAgentStatList(String agentId, TimeWindow timeWindow) {
        long scanFrom = timeWindow.getWindowRange().getFrom();
        long scanTo = timeWindow.getWindowRange().getTo() + timeWindow.getWindowSlotSize();
        Range range = new Range(scanFrom, scanTo);

        AgentStatMapperV2<IntCountMetricListBo> rowMapper = operations.createRowMapper(intCountMetricDecoder, range);
        SampledIntCountMetricResultExtractor resultExtractor = new SampledIntCountMetricResultExtractor(timeWindow, rowMapper, intCountMetricSampler);

        List<SampledIntCountMetricList> sampledAgentStatList = operations.getSampledAgentStatList(AgentStatType.CUSTOM_INT_COUNT, resultExtractor, agentId, range);
        return sampledAgentStatList;
    }

}
