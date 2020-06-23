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

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.CustomMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCounter;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCounter;
import com.navercorp.pinpoint.collector.util.CollectorUtils;
import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.codec.metric.CustomMetricCodec;
import com.navercorp.pinpoint.common.server.bo.codec.metric.CustomMetricEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricListBo;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.metric.SimpleCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.serializer.metric.CustomMetricSerializer;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.rpc.util.ListUtils;

import org.apache.hadoop.hbase.client.Put;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class HbaseCustomMetricService implements AgentCustomMetricService {

    private final HbaseOperations2 hbaseTemplate;

    private final TableNameProvider tableNameProvider;

    private final AgentStatHbaseOperationFactory agentStatHbaseOperationFactory;

    private final List<FieldDescriptor> fieldDescriptorList;

    private final CustomMetricSerializer serializer;

    public HbaseCustomMetricService(HbaseOperations2 hbaseTemplate, TableNameProvider tableNameProvider, AgentStatHbaseOperationFactory agentStatHbaseOperationFactory, List<FieldDescriptor> fieldDescriptorList) {
        this.hbaseTemplate = Objects.requireNonNull(hbaseTemplate, "hbaseTemplate");
        this.tableNameProvider = Objects.requireNonNull(tableNameProvider, "tableNameProvider");

        this.agentStatHbaseOperationFactory = Objects.requireNonNull(agentStatHbaseOperationFactory, "agentStatHbaseOperationFactory");

        this.fieldDescriptorList = Objects.requireNonNull(fieldDescriptorList, "fieldDescriptorList");

        CustomMetricCodec customMetricCodec = new CustomMetricCodec(new AgentStatDataPointCodec(), fieldDescriptorList);
        CustomMetricEncoder customMetricEncoder = new CustomMetricEncoder(customMetricCodec);
        CustomMetricSerializer customMetricSerializer = new CustomMetricSerializer(customMetricEncoder);

        this.serializer = customMetricSerializer;
    }


    @Override
    public List<SimpleCustomMetricBo> map(AgentCustomMetricBo agentCustomMetricBo) {
//        AgentCustomMetricBo result = new AgentCustomMetricBo();
//
//        result.setFieldDescriptorList(fieldDescriptorList);
//
//        result.setAgentId(agentCustomMetricBo.getAgentId());
//        result.setStartTimestamp(agentCustomMetricBo.getStartTimestamp());

        List<CustomMetricListBo> params = new ArrayList<>();

//        Map<Long, List<CustomMetricBo>> map = new HashMap<>();

        MultiValueMap<Long, CustomMetricBo> multiValueMap = new LinkedMultiValueMap();

        for (FieldDescriptor fieldDescriptor : fieldDescriptorList) {
            Class<? extends CustomMetric> type = fieldDescriptor.getType();
            if (type == IntCounter.class) {
                String name = fieldDescriptor.getName();
                IntCountMetricListBo intCountMetricBoList = agentCustomMetricBo.getIntCountMetricBoList(name);
                addMetricBo(multiValueMap, intCountMetricBoList);
            } else if (type == LongCounter.class) {
                String name = fieldDescriptor.getName();
                LongCountMetricListBo longCountMetricBoList = agentCustomMetricBo.getLongCountMetricBoList(name);
                addMetricBo(multiValueMap, longCountMetricBoList);
            }
        }

        List<SimpleCustomMetricBo> result = create(multiValueMap);

        return result;
    }

    private void addMetricBo(MultiValueMap<Long, CustomMetricBo> multiValueMap, CustomMetricListBo customMetricListBo) {
        List<CustomMetricBo> list = customMetricListBo.getList();
        for (CustomMetricBo customMetricBo : list) {
            multiValueMap.add(customMetricBo.getTimestamp(), customMetricBo);
        }
    }

    private List<SimpleCustomMetricBo> create(MultiValueMap<Long, CustomMetricBo> multiValueMap) {
        List<SimpleCustomMetricBo> result = new ArrayList<>(multiValueMap.size());

        for (List<CustomMetricBo> values : multiValueMap.values()) {
            SimpleCustomMetricBo simpleCustomMetricBo = new SimpleCustomMetricBo(AgentStatType.CUSTOM_TEST);

            CustomMetricBo first = ListUtils.getFirst(values);
            if (first == null) {
                continue;
            }

            simpleCustomMetricBo.setAgentId(first.getAgentId());
            simpleCustomMetricBo.setStartTimestamp(first.getStartTimestamp());
            simpleCustomMetricBo.setTimestamp(first.getTimestamp());

            for (CustomMetricBo value : values) {
                String name = value.getName();
                simpleCustomMetricBo.put(name, value);
            }

            result.add(simpleCustomMetricBo);
        }

        return result;
    }

    @Override
    public void save(String agentId, List<SimpleCustomMetricBo> simpleCustomMetricBos) {
        Objects.requireNonNull(agentId, "agentId");
        // Assert agentId
        CollectorUtils.checkAgentId(agentId);

        SimpleCustomMetricBo first = ListUtils.getFirst(simpleCustomMetricBos);
        if (first == null) {
            return;
        }

        System.out.println("~~~~~~~~~~~~ SAVE:" + simpleCustomMetricBos);

        List<Put> puts = this.agentStatHbaseOperationFactory.createPuts(agentId, first.getAgentStatType(), simpleCustomMetricBos, serializer);
//        if (!puts.isEmpty()) {
//            TableName agentStatTableName = tableNameProvider.getTableName(HbaseTable.AGENT_STAT_VER2);
//            this.hbaseTemplate.asyncPut(agentStatTableName, puts);
//        }


        System.out.println("~~~~~~~~~~~~~~~~~ Put:" + puts);

    }

}


