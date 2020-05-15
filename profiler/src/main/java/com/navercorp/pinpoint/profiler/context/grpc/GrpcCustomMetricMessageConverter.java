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

package com.navercorp.pinpoint.profiler.context.grpc;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.grpc.trace.PCustomMetric;
import com.navercorp.pinpoint.grpc.trace.PCustomMetricMessage;
import com.navercorp.pinpoint.grpc.trace.PIntCountMetric;
import com.navercorp.pinpoint.profiler.context.thrift.MessageConverter;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshotBatch;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntCountMetricVo;

import com.google.protobuf.Int32Value;
import com.google.protobuf.Int32ValueOrBuilder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Taejin Koo
 */
public class GrpcCustomMetricMessageConverter implements MessageConverter<PCustomMetricMessage> {

    @Override
    public PCustomMetricMessage toMessage(Object message) {
        if (message instanceof AgentCustomMetricSnapshotBatch) {
            AgentCustomMetricSnapshotBatch agentCustomMetricSnapshotBatch = (AgentCustomMetricSnapshotBatch) message;
            List<AgentCustomMetricSnapshot> agentCustomMetricSnapshotList = agentCustomMetricSnapshotBatch.getAgentCustomMetricSnapshotList();

            Set<String> metricNameSet = new HashSet<String>();
            for (AgentCustomMetricSnapshot agentCustomMetricSnapshot : agentCustomMetricSnapshotList) {
                metricNameSet.addAll(agentCustomMetricSnapshot.getMetricNameSet());
            }

            for (String metricName : metricNameSet) {
                
            }
            
            for (int i = 0; i < agentCustomMetricSnapshotList.size(); i++) {
                AgentCustomMetricSnapshot agentCustomMetricSnapshot = agentCustomMetricSnapshotList.get(i);

//                agentCustomMetricSnapshot.g

            }

            throw new IllegalArgumentException("invalid message type. message=" + message);
        }


        return null;
    }

    private void addMetricValue(String metricName, List<AgentCustomMetricSnapshot> agentCustomMetricSnapshotList, int maxSize) {
        CustomMetricVo[] customMetricVos = new CustomMetricVo[maxSize];

        for (int i = 0; i < agentCustomMetricSnapshotList.size(); i++) {
            AgentCustomMetricSnapshot agentCustomMetricSnapshot = agentCustomMetricSnapshotList.get(i);

            CustomMetricVo customMetricVo = agentCustomMetricSnapshot.get(metricName);
            // supports to insert null
            customMetricVos[i] = customMetricVo;
        }

        create(metricName, customMetricVos);
    }

    private <T extends CustomMetricVo> com.navercorp.pinpoint.grpc.trace.PCustomMetric create(Class<T> clazz, CustomMetricVo[] customMetricVos) {
        if (clazz == IntCountMetricVo.class) {
            PIntCountMetric.Builder builder = PIntCountMetric.newBuilder();
            for (CustomMetricVo customMetricVo : customMetricVos) {
                if (customMetricVo instanceof IntCountMetricVo) {
                    builder.addValues(Int32Value.of(((IntCountMetricVo) customMetricVo).getValue()));
                } else if (customMetricVo == null) {
                    // null 이 되는지 테스트 필요
                    builder.addValues(Int32Value.getDefaultInstance());
                } else {
                    return null;
                }
            }
            PCustomMetric.Builder builder1 = PCustomMetric.newBuilder();
            return builder1.setIntCountMetric(builder).build();
        }


        return null;
    }


    private static class PCustomMetricFactory {

        CustomMetricVo[] customMetricVos;

        public PCustomMetricFactory(CustomMetricVo[] customMetricVos) {
            this.customMetricVos = Assert.requireNonNull(customMetricVos, "customMetricVos");
        }

        public com.navercorp.pinpoint.grpc.trace.PCustomMetric create() {

        }

        private boolean checkAllTypeAreSame() {
            CustomMetricVo firstCustomMetricVo = null;
            for (CustomMetricVo customMetricVo : customMetricVos) {
                if (firstCustomMetricVo == null && customMetricVo != null) {
                    firstCustomMetricVo = customMetricVo;
                    continue;
                }

                if (customMetricVo == null) {
                    continue;
                }

                if (customMetricVo.getClass() != firstCustomMetricVo.getClass()) {
                    return false;
                }
            }


        }

    }
    
    
}