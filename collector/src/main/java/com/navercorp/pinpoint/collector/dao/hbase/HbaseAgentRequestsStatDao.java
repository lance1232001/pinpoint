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

package com.navercorp.pinpoint.collector.dao.hbase;

import com.navercorp.pinpoint.collector.dao.AgentRequestsStatDao;
import com.navercorp.pinpoint.collector.dao.hbase.stat.HbaseRequestsStatSummaryDao;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatBo;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Taejin Koo
 */
@Repository
public class HbaseAgentRequestsStatDao implements AgentRequestsStatDao {

    @Autowired
    private HbaseRequestsStatSummaryDao hbaseRequestsStatSummaryDao;

    @Override
    public void insert(AgentRequestsStatBo agentRequestsStatBo) {
        Collection<RequestsStatSummaryBo> agentRequestsStatSummaryDataList = agentRequestsStatBo.getAgentRequestsStatSummaryDataList();
        hbaseRequestsStatSummaryDao.insert(agentRequestsStatBo.getAgentId(), new ArrayList<RequestsStatSummaryBo>(agentRequestsStatSummaryDataList));
    }

}
