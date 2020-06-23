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
import com.navercorp.pinpoint.common.server.bo.codec.metric.strategy.DuplicateLongListAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedIntegerEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.strategy.EncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.server.bo.serializer.stat.AgentStatDecodingContext;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.util.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    //  제일 나이스한건
    //  일반적으로 저 덩어리는 항상 같은것으로 나옴  중간에 데이터가 없어지는 경우가 없다고 판단할때
    //  그럴 경우 지금처럼 개별로 저장하는 것이 아니라 timestamp, starttimestap를 함게 압축해 버리는게 나을수 있음
    // size  0 0 size starttimestamp, size  timestamp metricName data, metricName data
    // size 1 0 size  timestamp metricName starttimestamp data, metricName starttimestamp data
    // size 1 1  metricName size  starttimestamp, size timestamp, data, metricName starttimestamp, timestamp data

    @Override
    public void encodeValues(Buffer valueBuffer, List<IntCountMetricListBo> intCountMetricListBos) {
        if (CollectionUtils.isEmpty(intCountMetricListBos)) {
            throw new IllegalArgumentException("intCountMetricListBos must not be empty");
        }

        final int numValues = intCountMetricListBos.size();
        valueBuffer.putVInt(numValues);

        DuplicateLongListAnalyzer startTimestampsAnalyzer = new DuplicateLongListAnalyzer();
        DuplicateLongListAnalyzer timestampsAnalyzer = new DuplicateLongListAnalyzer();

        for (IntCountMetricListBo intCountMetricListBo : intCountMetricListBos) {
            insertAnalyzerData(intCountMetricListBo, startTimestampsAnalyzer, timestampsAnalyzer);
        }

        DuplicateLongListAnalyzer.AnalyzeResult startTimestampsAnalyzeResult = startTimestampsAnalyzer.analyze();
        DuplicateLongListAnalyzer.AnalyzeResult timestampsAnalyzeResult = timestampsAnalyzer.analyze();
        encodeHeader(valueBuffer, startTimestampsAnalyzeResult, timestampsAnalyzeResult);

        boolean skipStartTimestamps = startTimestampsAnalyzeResult.getType() == DuplicateLongListAnalyzer.Type.ALL_SAME;
        boolean skipTimestamps = timestampsAnalyzeResult.getType() == DuplicateLongListAnalyzer.Type.ALL_SAME;

        for (IntCountMetricListBo intCountMetricListBo : intCountMetricListBos) {
            encodeIntCountMetricListBo(valueBuffer, intCountMetricListBo, skipStartTimestamps, skipTimestamps);
        }
    }

    private void insertAnalyzerData(IntCountMetricListBo intCountMetricListBo, DuplicateLongListAnalyzer startTimestampsAnalyzer, DuplicateLongListAnalyzer timestampsAnalyzer) {
        List<IntCountMetricBo> intCountMetricBoList = getValidIntCountMetricBoList(intCountMetricListBo.getList());

        List<Long> startTimestampList = new ArrayList<>(intCountMetricBoList.size());
        List<Long> timestampList = new ArrayList<>(intCountMetricBoList.size());

        for (IntCountMetricBo intCountMetricBo : intCountMetricBoList) {
            startTimestampList.add(intCountMetricBo.getStartTimestamp());
            timestampList.add(intCountMetricBo.getTimestamp());
        }

        startTimestampsAnalyzer.addValue(startTimestampList);
        timestampsAnalyzer.addValue(timestampList);
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

    private void encodeHeader(Buffer valueBuffer, DuplicateLongListAnalyzer.AnalyzeResult startTimestampsAnalyzeResult, DuplicateLongListAnalyzer.AnalyzeResult timestampsAnalyzeResult) {
        DuplicateLongListAnalyzer.Type startTimestampsAnalyzeResultType = startTimestampsAnalyzeResult.getType();
        valueBuffer.putByte(startTimestampsAnalyzeResultType.getCode());
        if (startTimestampsAnalyzeResultType == DuplicateLongListAnalyzer.Type.ALL_SAME) {
            List<Long> values = startTimestampsAnalyzeResult.getValues();
            valueBuffer.putVInt(values.size());
            this.codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, values);
        }

        DuplicateLongListAnalyzer.Type timestampsAnalyzeResultType = timestampsAnalyzeResult.getType();
        valueBuffer.putByte(timestampsAnalyzeResultType.getCode());
        if (timestampsAnalyzeResultType == DuplicateLongListAnalyzer.Type.ALL_SAME) {
            List<Long> values = timestampsAnalyzeResult.getValues();
            valueBuffer.putVInt(values.size());
            this.codec.encodeTimestamps(valueBuffer, values);
        }
    }

    private void encodeIntCountMetricListBo(Buffer valueBuffer, IntCountMetricListBo intCountMetricListBo, boolean skipStartTimestamps, boolean skipTimestamps) {
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
            startTimestamps.add(intCountMetricBo.getStartTimestamp());
            timestamps.add(intCountMetricBo.getTimestamp());

            Integer value = intCountMetricBo.getValue();
            int newValue = prevValue + value;
            intValueAnalyzer.addValue(newValue);
            prevValue = newValue;
        }

        if (!skipStartTimestamps) {
            this.codec.encodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, startTimestamps);
        }

        if (!skipTimestamps) {
            this.codec.encodeTimestamps(valueBuffer, timestamps);
        }

        StrategyAnalyzer<Integer> intStrategyAnalyzer = intValueAnalyzer.build();

        // encode header
        AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();
        headerEncoder.addCode(intStrategyAnalyzer.getBestStrategy().getCode());

        final byte[] header = headerEncoder.getHeader();
        valueBuffer.putPrefixedBytes(header);

        // encode values
        this.codec.encodeValues(valueBuffer, intStrategyAnalyzer.getBestStrategy(), intStrategyAnalyzer.getValues());
    }

    @Override
    public List<IntCountMetricListBo> decodeValues(Buffer valueBuffer, AgentStatDecodingContext decodingContext) {
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numValues = valueBuffer.readVInt();

        byte startTimestampsEncodeCode = valueBuffer.readByte();
        List<Long> startTimestamps = null;
        if (DuplicateLongListAnalyzer.Type.ALL_SAME.getCode() == startTimestampsEncodeCode) {
            int size = valueBuffer.readVInt();
            startTimestamps = this.codec.decodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, size);
        }

        byte timestampsEncodeCode = valueBuffer.readByte();
        List<Long> timestamps = null;
        if (DuplicateLongListAnalyzer.Type.ALL_SAME.getCode() == timestampsEncodeCode) {
            int size = valueBuffer.readVInt();
            timestamps = this.codec.decodeTimestamps(initialTimestamp, valueBuffer, size);
        }

        List<IntCountMetricListBo> intCountMetricListBos = new ArrayList<IntCountMetricListBo>(numValues);
        for (int i = 0; i < numValues; i++) {
            IntCountMetricListBo decode = decode(valueBuffer, decodingContext, startTimestamps, timestamps);
            intCountMetricListBos.add(decode);
        }
        return intCountMetricListBos;
    }

    private IntCountMetricListBo decode(Buffer valueBuffer, AgentStatDecodingContext decodingContext, List<Long> startTimestamps, List<Long> timestamps) {
        final String agentId = decodingContext.getAgentId();
        final long baseTimestamp = decodingContext.getBaseTimestamp();
        final long timestampDelta = decodingContext.getTimestampDelta();
        final long initialTimestamp = baseTimestamp + timestampDelta;

        int numSize = valueBuffer.readVInt();

        String metricName = valueBuffer.readPrefixedString();

        System.out.println(metricName);

        if (startTimestamps == null) {

            System.out.println("~~~~~~~~~");

            startTimestamps = this.codec.decodeValues(valueBuffer, UnsignedLongEncodingStrategy.REPEAT_COUNT, numSize);

        System.out.println(startTimestamps);

        }



        if (timestamps == null) {

            System.out.println("@@@@@@@@@@@");

            timestamps = this.codec.decodeTimestamps(initialTimestamp, valueBuffer, numSize);
        }

        final byte[] header = valueBuffer.readPrefixedBytes();
        AgentStatHeaderDecoder headerDecoder = new BitCountingHeaderDecoder(header);

        EncodingStrategy<Integer> intEncodingStrategy = UnsignedIntegerEncodingStrategy.getFromCode(headerDecoder.getCode());
        List<Integer> integers = this.codec.decodeValues(valueBuffer, intEncodingStrategy, numSize);

        IntCountMetricListBo intCountMetricListBo = new IntCountMetricListBo();
        intCountMetricListBo.setName(metricName);

        for (int i = 0; i < numSize; i++) {
            if (i == 0) {
                setBaseData(intCountMetricListBo, agentId, startTimestamps.get(i), timestamps.get(i));
            }
            IntCountMetricBo intCountMetricBo = new IntCountMetricBo();
            setBaseData(intCountMetricBo, agentId, startTimestamps.get(i), timestamps.get(i));
            intCountMetricBo.setValue(integers.get(i));

            intCountMetricListBo.add(intCountMetricBo);
        }

        return intCountMetricListBo;
    }

    private void setBaseData(AgentStatDataPoint agentStatDataPoint, String agentId, long startTimestamp, long timestamp) {
        agentStatDataPoint.setAgentId(agentId);
        agentStatDataPoint.setStartTimestamp(startTimestamp);
        agentStatDataPoint.setTimestamp(timestamp);
    }

}
