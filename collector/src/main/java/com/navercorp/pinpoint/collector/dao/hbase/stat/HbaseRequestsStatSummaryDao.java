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
