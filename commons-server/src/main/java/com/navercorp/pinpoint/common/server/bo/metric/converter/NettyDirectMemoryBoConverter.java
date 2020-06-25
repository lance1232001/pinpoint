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

package com.navercorp.pinpoint.common.server.bo.metric.converter;

import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricValue;
import com.navercorp.pinpoint.common.server.bo.metric.NettyDirectMemoryBo;

public class NettyDirectMemoryBoConverter implements CustomMetricBoConverter<NettyDirectMemoryBo> {

    private final String metricName = "tomcat34/request/count";
    private final String metricName2 = "tomcat45/request/count";


    @Override
    public NettyDirectMemoryBo convert(AgentCustomMetricBo agentCustomMetricBo) {
        NettyDirectMemoryBo nettyDirectMemoryBo = new NettyDirectMemoryBo();
        nettyDirectMemoryBo.setAgentId(agentCustomMetricBo.getAgentId());
        nettyDirectMemoryBo.setStartTimestamp(agentCustomMetricBo.getStartTimestamp());
        nettyDirectMemoryBo.setTimestamp(agentCustomMetricBo.getTimestamp());

        final CustomMetricValue value = agentCustomMetricBo.get(metricName);
        if (value != null) {
            nettyDirectMemoryBo.setUsedMemory(value.getValue().longValue());
        }

        return nettyDirectMemoryBo;
    }


}
