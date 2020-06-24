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

package com.navercorp.pinpoint.common.server.bo.codec.metric;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCounter;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCounter;
import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.CodecFactory;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.v2.AgentStatCodecV2;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.FieldDescriptor;
import com.navercorp.pinpoint.common.server.bo.metric.IntCounterMetricValue;
import com.navercorp.pinpoint.common.server.bo.metric.LongCounterMetricValue;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class CustomMetricCodec extends AgentStatCodecV2<AgentCustomMetricBo> {

    public CustomMetricCodec(AgentStatDataPointCodec codec, List<FieldDescriptor> fieldDescriptorList) {
        super(new CustomMetricCodecFactory(codec, fieldDescriptorList));
    }

    private static class CustomMetricCodecFactory implements CodecFactory<AgentCustomMetricBo> {

        private final AgentStatDataPointCodec codec;
        private final List<FieldDescriptor> fieldDescriptorList;

        private CustomMetricCodecFactory(AgentStatDataPointCodec codec, List<FieldDescriptor> fieldDescriptorList) {
            this.codec = Objects.requireNonNull(codec, "codec");
            this.fieldDescriptorList = Objects.requireNonNull(fieldDescriptorList, "fieldDescriptorList");
        }

        @Override
        public AgentStatDataPointCodec getCodec() {
            return codec;
        }

        @Override
        public CodecEncoder<AgentCustomMetricBo> createCodecEncoder() {
            return new Encoder(codec, fieldDescriptorList);
        }

        @Override
        public CodecDecoder<AgentCustomMetricBo> createCodecDecoder() {
            return new Decoder(codec, fieldDescriptorList);
        }
    }

    public static class Encoder implements AgentStatCodec.CodecEncoder<AgentCustomMetricBo> {

        private final AgentStatDataPointCodec codec;

        private final List<CustomMetricFieldEncoder> customMetricFieldEncoderList = new ArrayList<>();

        public Encoder(AgentStatDataPointCodec codec, List<FieldDescriptor> fieldDescriptorList) {
            this.codec = Objects.requireNonNull(codec, "codec");
            Objects.requireNonNull(fieldDescriptorList, "fieldDescriptorList");

            for (FieldDescriptor fieldDescriptor : fieldDescriptorList) {
                if (fieldDescriptor.getType() == IntCounter.class) {
                    IntCounterFieldEncoder intCounterFieldEncoder = new IntCounterFieldEncoder(fieldDescriptor.getIndex(), fieldDescriptor.getName());
                    customMetricFieldEncoderList.add(intCounterFieldEncoder);
                } else if (fieldDescriptor.getType() == LongCounter.class) {
                    LongCounterFieldEncoder longCounterFieldEncoder = new LongCounterFieldEncoder(fieldDescriptor.getIndex(), fieldDescriptor.getName());
                    customMetricFieldEncoderList.add(longCounterFieldEncoder);
                }
            }
        }

        @Override
        public void addValue(AgentCustomMetricBo agentStatDataPoint) {
            System.out.println("addValue:" + agentStatDataPoint);

            for (CustomMetricFieldEncoder customMetricFieldEncoder : customMetricFieldEncoderList) {
                customMetricFieldEncoder.addValue(agentStatDataPoint);
            }
        }

        @Override
        public void encode(Buffer valueBuffer) {
            // encode header
            AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();

            for (CustomMetricFieldEncoder customMetricFieldEncoder : customMetricFieldEncoderList) {
                StrategyAnalyzer analyzer = customMetricFieldEncoder.getAnalyzer();
                headerEncoder.addCode(analyzer.getBestStrategy().getCode());
            }

            for (CustomMetricFieldEncoder customMetricFieldEncoder : customMetricFieldEncoderList) {
                codec.encodeValues(valueBuffer, customMetricFieldEncoder.getAnalyzer().getBestStrategy(), customMetricFieldEncoder.getAnalyzer().getValues());
            }
        }
    }

    public static class Decoder implements AgentStatCodec.CodecDecoder<AgentCustomMetricBo> {

        private final AgentStatDataPointCodec codec;
        private final List<CustomMetricFieldDecoder> customMetricFieldDecoderList = new ArrayList<>();

        public Decoder(AgentStatDataPointCodec codec, List<FieldDescriptor> fieldDescriptorList) {
            this.codec = Objects.requireNonNull(codec, "codec");

            for (FieldDescriptor fieldDescriptor : fieldDescriptorList) {
                if (fieldDescriptor.getType() == IntCounter.class) {
                    IntCounterFieldDecoder intCounterFieldDecoder = new IntCounterFieldDecoder(fieldDescriptor.getIndex(), fieldDescriptor.getName());
                    customMetricFieldDecoderList.add(intCounterFieldDecoder);
                } else if (fieldDescriptor.getType() == LongCounter.class) {
                    LongCounterFieldDecoder longCounterFieldEncoder = new LongCounterFieldDecoder(fieldDescriptor.getIndex(), fieldDescriptor.getName());
                    customMetricFieldDecoderList.add(longCounterFieldEncoder);
                }
            }
        }

        @Override
        public void decode(Buffer valueBuffer, AgentStatHeaderDecoder headerDecoder, int valueSize) {
            for (CustomMetricFieldDecoder customMetricFieldDecoder : customMetricFieldDecoderList) {
                customMetricFieldDecoder.setEncodingStrategy(headerDecoder.getCode());
            }

            for (CustomMetricFieldDecoder customMetricFieldDecoder : customMetricFieldDecoderList) {
                customMetricFieldDecoder.decodeValues(codec, valueBuffer, valueSize);
            }
        }

        @Override
        public AgentCustomMetricBo getValue(int index) {
            AgentCustomMetricBo agentCustomMetricBo = new AgentCustomMetricBo(AgentStatType.CUSTOM_TEST);


            for (CustomMetricFieldDecoder customMetricFieldDecoder : customMetricFieldDecoderList) {
                String metricName = customMetricFieldDecoder.getMetricName();

                if (customMetricFieldDecoder instanceof IntCounterFieldDecoder) {
                    IntCounterFieldDecoder intCounterFieldDecoder = (IntCounterFieldDecoder) customMetricFieldDecoder;
                    final List<Integer> intList = intCounterFieldDecoder.getDecodedValue();

                    final IntCounterMetricValue intCounterMetricValue = new IntCounterMetricValue();
                    intCounterMetricValue.setMetricName(metricName);
                    intCounterMetricValue.setValue(intList.get(index));

                    agentCustomMetricBo.put(metricName, intCounterMetricValue);
                } else if (customMetricFieldDecoder instanceof LongCounterFieldDecoder) {
                    LongCounterFieldDecoder longCounterFieldDecoder = (LongCounterFieldDecoder) customMetricFieldDecoder;
                    final List<Long> longList = longCounterFieldDecoder.getDecodedValue();

                    final LongCounterMetricValue longCounterMetricValue = new LongCounterMetricValue();
                    longCounterMetricValue.setMetricName(metricName);
                    longCounterMetricValue.setValue(longList.get(index));

                    agentCustomMetricBo.put(metricName, longCounterMetricValue);
                }

            }


            return agentCustomMetricBo;
        }

    }

}

