package com.navercorp.pinpoint.web.dao.hbase.stat.v2;

import com.navercorp.pinpoint.common.hbase.HbaseTable;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.codec.stat.RequestsStatSummaryDecoder;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.web.dao.stat.RequestsStatSummaryDao;
import com.navercorp.pinpoint.web.mapper.stat.AgentStatMapperV2;
import com.navercorp.pinpoint.web.vo.Range;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("requestsStatSummaryDaoV2")
public class HbaseRequestsStatSummaryDaoV2 implements RequestsStatSummaryDao {

    @Autowired
    private RequestsStatSummaryDecoder requestsStatSummaryDecoder;

    @Autowired
    private HbaseAgentStatDaoOperationsV2 operations;

    @Autowired
    private TableNameProvider tableNameProvider;

    @Override
    public List<RequestsStatSummaryBo> getAgentStatList(String agentId, Range range) {
        AgentStatMapperV2<RequestsStatSummaryBo> mapper = operations.createRowMapper(requestsStatSummaryDecoder, range);
        return operations.getAgentStatList(tableNameProvider.getTableName(HbaseTable.AGENT_REQUESTS_STAT_VER2), AgentStatType.REQUESTS_SUMMARY, mapper, agentId, range);
    }

    @Override
    public boolean agentStatExists(String agentId, Range range) {
        AgentStatMapperV2<RequestsStatSummaryBo> mapper = operations.createRowMapper(requestsStatSummaryDecoder, range);
        return operations.agentStatExists(AgentStatType.REQUESTS_SUMMARY, mapper, agentId, range);
    }

}

