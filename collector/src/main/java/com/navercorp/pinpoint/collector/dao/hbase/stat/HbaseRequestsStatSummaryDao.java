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

package com.navercorp.pinpoint.collector.dao.hbase.stat;

import com.navercorp.pinpoint.collector.dao.AgentStatDaoV2;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.HbaseTable;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.RequestsStatSummarySerializer;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Taejin Koo
 */
@Repository
public class HbaseRequestsStatSummaryDao implements AgentStatDaoV2<RequestsStatSummaryBo> {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final boolean isDebug = logger.isDebugEnabled();

    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    private TableNameProvider tableNameProvider;

    @Autowired
    private AgentStatHbaseOperationFactory agentStatHbaseOperationFactory;

    @Autowired
    private RequestsStatSummarySerializer serializer;


    @Override
    public void insert(String agentId, List<RequestsStatSummaryBo> requestsStatSummaryBoList) {
        if (agentId == null) {
            throw new NullPointerException("agentId");
        }
        if (CollectionUtils.isEmpty(requestsStatSummaryBoList)) {
            return;
        }

        if (isDebug) {
            logger.debug("agentId:{}, insertData:{}", agentId, requestsStatSummaryBoList);
        }


        List<Put> puts = this.agentStatHbaseOperationFactory.createPuts(agentId, AgentStatType.REQUESTS_SUMMARY, requestsStatSummaryBoList, this.serializer);
        if (CollectionUtils.isEmpty(puts)) {
            return;
        }

        TableName agentStatTableName = tableNameProvider.getTableName(HbaseTable.AGENT_REQUESTS_STAT_VER2);
        List<Put> rejectedPuts = this.hbaseTemplate.asyncPut(agentStatTableName, puts);
        if (CollectionUtils.hasLength(rejectedPuts)) {
            this.hbaseTemplate.put(agentStatTableName, rejectedPuts);
        }
    }

}
