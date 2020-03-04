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

import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;
import com.navercorp.pinpoint.common.util.CollectionUtils;
import com.navercorp.pinpoint.rpc.util.ListUtils;
import com.navercorp.pinpoint.web.dao.stat.RequestsStatSummaryDao;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.stat.RequestsStatSummaryResponseBo;
import com.navercorp.pinpoint.web.vo.stat.RequestsStatSummaryResponseBoBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Taejin Koo
 */
@Service
public class RequestsStatSummaryService implements AgentStatService<RequestsStatSummaryResponseBo> {

    private final RequestsStatSummaryDao requestsStatSummaryDao;

    @Autowired
    public RequestsStatSummaryService(@Qualifier("requestsStatSummaryDao") RequestsStatSummaryDao requestsStatSummaryDao) {
        this.requestsStatSummaryDao = requestsStatSummaryDao;
    }

    @Override
    public List<RequestsStatSummaryResponseBo> selectAgentStatList(String agentId, Range range) {
        if (agentId == null) {
            throw new NullPointerException("agentId");
        }
        if (range == null) {
            throw new NullPointerException("range");
        }
        List<RequestsStatSummaryBo> agentStatList = this.requestsStatSummaryDao.getAgentStatList(agentId, range);
        if (CollectionUtils.isEmpty(agentStatList)) {
            return null;
        }

        RequestsStatSummaryBo requestsStatSummaryBo1 = ListUtils.getFirst(agentStatList);

        RequestsStatSummaryResponseBoBuilder requestsStatSummaryResponseBoBuilder = new RequestsStatSummaryResponseBoBuilder();
        for (RequestsStatSummaryBo requestsStatSummaryBo : agentStatList) {
            requestsStatSummaryResponseBoBuilder.add(requestsStatSummaryBo.getUrl(), requestsStatSummaryBo.getStatus(), requestsStatSummaryBo.getCount(), requestsStatSummaryBo.getAvgTime(), requestsStatSummaryBo.getMaxTime());
        }

        RequestsStatSummaryResponseBo requestsStatSummaryResponseBo = requestsStatSummaryResponseBoBuilder.build();

        requestsStatSummaryResponseBo.setAgentId(requestsStatSummaryBo1.getAgentId());
        requestsStatSummaryResponseBo.setStartTimestamp(requestsStatSummaryBo1.getStartTimestamp());
        requestsStatSummaryResponseBo.setTimestamp(requestsStatSummaryBo1.getTimestamp());

        ArrayList<RequestsStatSummaryResponseBo> objects = new ArrayList<>();
        objects.add(requestsStatSummaryResponseBo);

        return objects;
    }
}

