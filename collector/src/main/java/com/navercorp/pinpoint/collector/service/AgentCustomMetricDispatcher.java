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

package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCounter;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCounter;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricMessage;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
@Service
public class AgentCustomMetricDispatcher {

    private final Logger logger = LoggerFactory.getLogger(HBaseAgentStatService.class.getName());


    @Autowired
    private HbaseOperations2 hbaseTemplate;

    @Autowired
    private TableNameProvider tableNameProvider;

    @Autowired
    private AgentStatHbaseOperationFactory agentStatHbaseOperationFactory;


    private final List<AgentCustomMetricService> agentCustomMetricServiceList = new ArrayList<>();

    @PostConstruct
    public void setup() {

        System.out.println("~~~~~~~~~~~~~~~~~~setup");
        System.out.println(hbaseTemplate);
        System.out.println(tableNameProvider);
        System.out.println(agentStatHbaseOperationFactory);

        CustomMetricServiceBuilder builder = new CustomMetricServiceBuilder();
        FieldDescriptor fieldDescriptor = new FieldDescriptor(0, "tomcat34/request/count", IntCounter.class);
        builder.addFieldDescriptor(fieldDescriptor);

        builder.setHbaseTemplate(hbaseTemplate);
        builder.setTableNameProvider(tableNameProvider);
        builder.setAgentStatHbaseOperationFactory(agentStatHbaseOperationFactory);

        AgentCustomMetricService build1 = builder.build();
        agentCustomMetricServiceList.add(build1);

        builder = new CustomMetricServiceBuilder();
        fieldDescriptor = new FieldDescriptor(0, "tomcat45/request/count", LongCounter.class);
        builder.addFieldDescriptor(fieldDescriptor);

        builder.setHbaseTemplate(hbaseTemplate);
        builder.setTableNameProvider(tableNameProvider);
        builder.setAgentStatHbaseOperationFactory(agentStatHbaseOperationFactory);

        AgentCustomMetricService build2 = builder.build();
        agentCustomMetricServiceList.add(build2);
    }

    public void save(AgentCustomMetricMessage agentCustomMetricMessage) {
        String agentId = agentCustomMetricMessage.getAgentId();

        for (AgentCustomMetricService agentCustomMetricService : agentCustomMetricServiceList) {
            List<AgentCustomMetricBo> agentCustomMetricBoList = agentCustomMetricService.map(agentCustomMetricMessage);
            agentCustomMetricService.save(agentId, agentCustomMetricBoList);

        }


//        List<IntCountMetricListBo> intCountMetricBoList = agentCustomMetricBo.getIntCountMetricBoList();
//        insert(agentId, intCountMetricBoList);
    }

//    private void insert(String agentId, List<IntCountMetricListBo> intCountMetricListBos) {
//        try {
//            intCountMetricDao.insert(agentId, intCountMetricListBos);
//        } catch (Exception e) {
//            logger.warn("Failed to insert intCountMetric. message:{}", e.getMessage(), e);
//        }
//    }


}
