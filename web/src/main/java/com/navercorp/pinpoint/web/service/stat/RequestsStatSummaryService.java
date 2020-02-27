package com.navercorp.pinpoint.web.service.stat;

import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.web.dao.stat.RequestsStatSummaryDao;
import com.navercorp.pinpoint.web.vo.Range;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RequestsStatSummaryService implements AgentStatService<RequestsStatSummaryBo> {

    private final RequestsStatSummaryDao requestsStatSummaryDao;

    @Autowired
    public RequestsStatSummaryService(@Qualifier("requestsStatSummaryDao") RequestsStatSummaryDao requestsStatSummaryDao) {
        this.requestsStatSummaryDao = requestsStatSummaryDao;
    }

    @Override
    public List<RequestsStatSummaryBo> selectAgentStatList(String agentId, Range range) {
        if (agentId == null) {
            throw new NullPointerException("agentId");
        }
        if (range == null) {
            throw new NullPointerException("range");
        }
        return this.requestsStatSummaryDao.getAgentStatList(agentId, range);
    }
}

