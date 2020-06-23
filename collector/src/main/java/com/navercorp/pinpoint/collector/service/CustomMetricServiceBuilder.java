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

import com.navercorp.pinpoint.common.hbase.HbaseOperations2;
import com.navercorp.pinpoint.common.hbase.TableNameProvider;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatHbaseOperationFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class CustomMetricServiceBuilder {

    private final List<FieldDescriptor> fieldDescriptorList = new ArrayList<>();

    private HbaseOperations2 hbaseTemplate;

    private TableNameProvider tableNameProvider;

    private AgentStatHbaseOperationFactory agentStatHbaseOperationFactory;

    public void setHbaseTemplate(HbaseOperations2 hbaseTemplate) {
        this.hbaseTemplate = hbaseTemplate;
    }

    public void setTableNameProvider(TableNameProvider tableNameProvider) {
        this.tableNameProvider = tableNameProvider;
    }

    public void setAgentStatHbaseOperationFactory(AgentStatHbaseOperationFactory agentStatHbaseOperationFactory) {
        this.agentStatHbaseOperationFactory = agentStatHbaseOperationFactory;
    }

    public CustomMetricServiceBuilder addFieldDescriptor(FieldDescriptor fieldDescriptor) {
        fieldDescriptorList.add(fieldDescriptor);
        return this;
    }

    AgentCustomMetricService build() {
        Objects.requireNonNull(hbaseTemplate, "hbaseTemplate");
        Objects.requireNonNull(tableNameProvider, "tableNameProvider");
        Objects.requireNonNull(agentStatHbaseOperationFactory, "agentStatHbaseOperationFactory");

        return new HbaseCustomMetricService(hbaseTemplate, tableNameProvider, agentStatHbaseOperationFactory, fieldDescriptorList);
    }


}
