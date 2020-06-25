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
import com.navercorp.pinpoint.common.hbase.HbaseTable;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.codec.metric.CustomMetricCodec;
import com.navercorp.pinpoint.common.server.bo.codec.metric.CustomMetricEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricMessage;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricValue;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricValueList;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.metric.IntCounterMetricValueList;
import com.navercorp.pinpoint.common.server.bo.metric.LongCounterMetricValueList;
import com.navercorp.pinpoint.common.server.bo.serializer.metric.CustomMetricSerializer;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;
import com.navercorp.pinpoint.rpc.util.ListUtils;

import org.apache.hadoop.hbase.TableName;
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
    public List<AgentCustomMetricBo> map(AgentCustomMetricMessage agentCustomMetricMessage) {
        MultiValueMap<Long, CustomMetricValue> multiValueMap = new LinkedMultiValueMap();

        for (FieldDescriptor fieldDescriptor : fieldDescriptorList) {
            Class<? extends CustomMetric> type = fieldDescriptor.getType();
            if (type == IntCounter.class) {
                String name = fieldDescriptor.getName();
                final IntCounterMetricValueList intCounterMetricValueList = agentCustomMetricMessage.getIntCounterMetricValueList(name);
                addMetricBo(multiValueMap, intCounterMetricValueList);
            } else if (type == LongCounter.class) {
                String name = fieldDescriptor.getName();
                final LongCounterMetricValueList longCounterMetricValueList = agentCustomMetricMessage.getLongCounterMetricValueList(name);
                addMetricBo(multiValueMap, longCounterMetricValueList);
            }
        }

        final String agentId = agentCustomMetricMessage.getAgentId();
        final long startTimestamp = agentCustomMetricMessage.getStartTimestamp();

        List<AgentCustomMetricBo> result = create(agentId, startTimestamp, multiValueMap);
        return result;
    }

    private void addMetricBo(MultiValueMap<Long, CustomMetricValue> multiValueMap, CustomMetricValueList customMetricValueList) {
        final List<CustomMetricValue> valueList = customMetricValueList.getList();
        for (CustomMetricValue value : valueList) {
            multiValueMap.add(value.getTimestamp(), value);
        }
    }

    private List<AgentCustomMetricBo> create(String agentId, long startTimestamp, MultiValueMap<Long, CustomMetricValue> multiValueMap) {
        List<AgentCustomMetricBo> result = new ArrayList<>(multiValueMap.size());

        for (List<CustomMetricValue> values : multiValueMap.values()) {
            AgentCustomMetricBo agentCustomMetricBo = new AgentCustomMetricBo(AgentStatType.CUSTOM_TEST);

            CustomMetricValue first = ListUtils.getFirst(values);
            if (first == null) {
                continue;
            }

            agentCustomMetricBo.setAgentId(agentId);
            agentCustomMetricBo.setStartTimestamp(startTimestamp);
            agentCustomMetricBo.setTimestamp(first.getTimestamp());

            for (CustomMetricValue value : values) {
                String metricName = value.getMetricName();
                agentCustomMetricBo.put(metricName, value);
            }

            result.add(agentCustomMetricBo);
        }

        return result;
    }

    @Override
    public void save(String agentId, List<AgentCustomMetricBo> agentCustomMetricBoList) {
        Objects.requireNonNull(agentId, "agentId");
        // Assert agentId
        CollectorUtils.checkAgentId(agentId);

        AgentCustomMetricBo first = ListUtils.getFirst(agentCustomMetricBoList);
        if (first == null) {
            return;
        }

        List<Put> puts = this.agentStatHbaseOperationFactory.createPuts(agentId, first.getAgentStatType(), agentCustomMetricBoList, serializer);
        if (!puts.isEmpty()) {
            TableName agentStatTableName = tableNameProvider.getTableName(HbaseTable.AGENT_STAT_VER2);
            this.hbaseTemplate.asyncPut(agentStatTableName, puts);
        }
    }

}


