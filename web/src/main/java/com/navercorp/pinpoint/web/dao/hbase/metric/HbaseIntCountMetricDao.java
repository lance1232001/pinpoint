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

import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.web.dao.hbase.stat.v2.HbaseAgentStatDaoOperationsV2;
import com.navercorp.pinpoint.web.dao.metric.IntCountMetricDao;
import com.navercorp.pinpoint.web.vo.Range;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Repository("intCountMetricDao")
public class HbaseIntCountMetricDao implements IntCountMetricDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final HbaseAgentStatDaoOperationsV2 operations;

    public HbaseIntCountMetricDao(HbaseAgentStatDaoOperationsV2 operations) {
        this.operations = Objects.requireNonNull(operations, "operations");
    }

    @Override
    public List<IntCountMetricListBo> getAgentStatList(String agentId, Range range) {
        return null;
    }

    @Override
    public boolean agentStatExists(String agentId, Range range) {
        return false;
    }

}
