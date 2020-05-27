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

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedIntegerEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatDecodingContext;
import com.navercorp.pinpoint.common.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Component("intCountMetricCodec")
public class IntCountMetricCodec implements AgentCustomMetricCodec<IntCountMetricListBo> {

    private static final byte VERSION = 1;

    private final AgentStatDataPointCodec codec;

    @Autowired
    public IntCountMetricCodec(AgentStatDataPointCodec codec) {
        this.codec = Objects.requireNonNull(codec, "codec");
    }

    @Override
    public byte getVersion() {
        return VERSION;
    }

    @Override
    public void encodeValues(Buffer valueBuffer, List<IntCountMetricListBo> intCountMetricListBos) {
        if (CollectionUtils.isEmpty(intCountMetricListBos)) {
            throw new IllegalArgumentException("dataSourceListBos must not be empty");
        }

        final int numValues = intCountMetricListBos.size();
        valueBuffer.putVInt(numValues);

        for (IntCountMetricListBo intCountMetricListBo : intCountMetricListBos) {
            encodeIntCountMetricListBo(valueBuffer, intCountMetricListBo);
        }
    }

    private void encodeIntCountMetricListBo(Buffer valueBuffer, IntCountMetricListBo intCountMetricListBo) {
        List<IntCountMetricBo> intCountMetricBoList = getValidIntCountMetricBoList(intCountMetricListBo.getList());
        final int numValues = intCountMetricBoList.size();
        valueBuffer.putVInt(numValues);
        if (numValues == 0) {
            return;
        }

        String metricName = intCountMetricListBo.getName();
        valueBuffer.putPrefixedString(metricName);

        List<Long> startTimestamps = new ArrayList<Long>(numValues);
        List<Long> timestamps = new ArrayList<Long>(numValues);
        UnsignedIntegerEncodingStrategy.Analyzer.Builder intValueAnalyzer = new UnsignedIntegerEncodingStrategy.Analyzer.Builder();

        int prevValue = 0;
        for (IntCountMetricBo intCountMetricBo : intCountMetricBoList) {
            Integer value = intCountMetricBo.getValue();
            startTimestamps.add(intCountMetricBo.getStartTimestamp());
            timestamps.add(intCountMetricBo.getTimestamp());
            int newValue = prevValue + value;
            intValueAnalyzer.addValue(newValue);
            prevValue = newValue;
        }

        this.codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, startTimestamps);
        this.codec.encodeTimestamps(valueBuffer, timestamps);

        StrategyAnalyzer<Integer> intStrategyAnalyzer = intValueAnalyzer.build();

        // encode header
        AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();
        headerEncoder.addCode(intStrategyAnalyzer.getBestStrategy().getCode());

        final byte[] header = headerEncoder.getHeader();
        valueBuffer.putPrefixedBytes(header);

        // encode values
        this.codec.encodeValues(valueBuffer, intStrategyAnalyzer.getBestStrategy(), intStrategyAnalyzer.getValues());
    }

    private List<IntCountMetricBo> getValidIntCountMetricBoList(List<IntCountMetricBo> intCountMetricBoList) {
        List<IntCountMetricBo> result = new ArrayList<>();

        for (IntCountMetricBo intCountMetricBo : intCountMetricBoList) {
            Integer value = intCountMetricBo.getValue();
            if (value != null) {
                result.add(intCountMetricBo);
            }
        }

        return result;
    }

    @Override
    public List<IntCountMetricListBo> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {

        System.out.println("HHHHHHHHELO DECODE");

        return Collections.emptyList();
    }

}
