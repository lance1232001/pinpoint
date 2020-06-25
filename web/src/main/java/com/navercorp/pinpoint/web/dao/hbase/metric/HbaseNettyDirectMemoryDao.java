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

package com.navercorp.pinpoint.web.dao.hbase.metric;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCounter;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.metric.NettyDirectMemoryBo;
import com.navercorp.pinpoint.common.server.bo.metric.converter.NettyDirectMemoryBoConverter;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.web.dao.hbase.stat.v2.HbaseAgentStatDaoOperationsV2;
import com.navercorp.pinpoint.web.dao.metric.AgentCustomMetricDao;
import com.navercorp.pinpoint.web.dao.metric.NettyDirectMemoryDao;
import com.navercorp.pinpoint.web.vo.Range;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository("nettyDirectMemoryDao")
public class HbaseNettyDirectMemoryDao implements NettyDirectMemoryDao {

    private final AgentCustomMetricDao agentCustomMetricDao;

    private final NettyDirectMemoryBoConverter nettyDirectMemoryBoConverter = new NettyDirectMemoryBoConverter();

    public HbaseNettyDirectMemoryDao(HbaseAgentStatDaoOperationsV2 operations) {
        Objects.requireNonNull(operations, "operations");

        FieldDescriptor fieldDescriptor = new FieldDescriptor(0, "tomcat34/request/count", IntCounter.class);

        List<FieldDescriptor> fieldDescriptorList = new ArrayList<>();
        fieldDescriptorList.add(fieldDescriptor);

        this.agentCustomMetricDao = new HbaseAgentCustomMetricDao(operations, AgentStatType.CUSTOM_TEST, fieldDescriptorList);
    }


    @Override
    public List<NettyDirectMemoryBo> getAgentStatList(String agentId, Range range) {
        final List<AgentCustomMetricBo> agentStatList = agentCustomMetricDao.getAgentStatList(agentId, range);

        System.out.println("=============getAgentStatList:" + agentStatList);

        List<NettyDirectMemoryBo> result = new ArrayList<>();

        for (AgentCustomMetricBo agentCustomMetricBo :
                agentStatList) {
            final NettyDirectMemoryBo convert = nettyDirectMemoryBoConverter.convert(agentCustomMetricBo);
            result.add(convert);
        }

        System.out.println("=============result:" + result);

        return result;
    }

    @Override
    public boolean agentStatExists(String agentId, Range range) {
        return agentCustomMetricDao.agentStatExists(agentId, range);
    }

}
