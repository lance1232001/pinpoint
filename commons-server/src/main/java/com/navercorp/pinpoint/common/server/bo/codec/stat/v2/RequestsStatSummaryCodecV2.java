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

package com.navercorp.pinpoint.common.server.bo.codec.stat.v2;

import com.navercorp.pinpoint.common.buffer.Buffer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatDataPointCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.CodecFactory;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderDecoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.AgentStatHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.header.BitCountingHeaderEncoder;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StringEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedIntegerEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.strategy.EncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.stat.RequestsStatSummaryBo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Component("requestsStatSummaryCodecV2")
public class RequestsStatSummaryCodecV2 extends AgentStatCodecV2<RequestsStatSummaryBo> {

    @Autowired
    public RequestsStatSummaryCodecV2(AgentStatDataPointCodec codec) {
        super(new RequestsStatSummaryCodecV2.RequestsStatSummaryCodecFactory(codec));
    }


    private static class RequestsStatSummaryCodecFactory implements CodecFactory<RequestsStatSummaryBo> {

        private final AgentStatDataPointCodec codec;

        private RequestsStatSummaryCodecFactory(AgentStatDataPointCodec codec) {
            this.codec = Objects.requireNonNull(codec, "codec");
        }

        @Override
        public AgentStatDataPointCodec getCodec() {
            return codec;
        }

        @Override
        public CodecEncoder<RequestsStatSummaryBo> createCodecEncoder() {
            return new RequestsStatSummaryCodecV2.RequestsStatSummaryCodecEncoder(codec);
        }

        @Override
        public CodecDecoder<RequestsStatSummaryBo> createCodecDecoder() {
            return new RequestsStatSummaryCodecV2.RequestsStatSummaryDecoder(codec);
        }
    }

    public static class RequestsStatSummaryCodecEncoder implements CodecEncoder<RequestsStatSummaryBo> {

        private final AgentStatDataPointCodec codec;

        private final StringEncodingStrategy.Analyzer.Builder urlAnalyzerBuilder = new StringEncodingStrategy.Analyzer.Builder();
        private final UnsignedIntegerEncodingStrategy.Analyzer.Builder statusAnalyzerBuilder = new UnsignedIntegerEncodingStrategy.Analyzer.Builder();
        private final UnsignedIntegerEncodingStrategy.Analyzer.Builder countAnalyzerBuilder = new UnsignedIntegerEncodingStrategy.Analyzer.Builder();
        private final UnsignedLongEncodingStrategy.Analyzer.Builder avgAnalyzerBuilder = new UnsignedLongEncodingStrategy.Analyzer.Builder();
        private final UnsignedLongEncodingStrategy.Analyzer.Builder maxAnalyzerBuilder = new UnsignedLongEncodingStrategy.Analyzer.Builder();

        public RequestsStatSummaryCodecEncoder(AgentStatDataPointCodec codec) {
            this.codec = Objects.requireNonNull(codec, "codec");
        }

        @Override
        public void addValue(RequestsStatSummaryBo requestsStatSummaryBo) {
            urlAnalyzerBuilder.addValue(requestsStatSummaryBo.getUrl());
            statusAnalyzerBuilder.addValue(requestsStatSummaryBo.getStatus());
            countAnalyzerBuilder.addValue(requestsStatSummaryBo.getCount());
            avgAnalyzerBuilder.addValue(requestsStatSummaryBo.getAvgTime());
            maxAnalyzerBuilder.addValue(requestsStatSummaryBo.getMaxTime());
        }

        @Override
        public void encode(Buffer valueBuffer) {
            StrategyAnalyzer<String> urlAnalyzer = urlAnalyzerBuilder.build();
            StrategyAnalyzer<Integer> statusAnalyzer = statusAnalyzerBuilder.build();
            StrategyAnalyzer<Integer> countAnalyzer = countAnalyzerBuilder.build();
            StrategyAnalyzer<Long> avgAnalyzer = avgAnalyzerBuilder.build();
            StrategyAnalyzer<Long> maxAnalyzer = maxAnalyzerBuilder.build();

            // encode header
            AgentStatHeaderEncoder headerEncoder = new BitCountingHeaderEncoder();
            headerEncoder.addCode(urlAnalyzer.getBestStrategy().getCode());
            headerEncoder.addCode(statusAnalyzer.getBestStrategy().getCode());
            headerEncoder.addCode(countAnalyzer.getBestStrategy().getCode());
            headerEncoder.addCode(avgAnalyzer.getBestStrategy().getCode());
            headerEncoder.addCode(maxAnalyzer.getBestStrategy().getCode());

            final byte[] header = headerEncoder.getHeader();
            valueBuffer.putPrefixedBytes(header);

            // encode values
            this.codec.encodeValues(valueBuffer, urlAnalyzer.getBestStrategy(), urlAnalyzer.getValues());
            this.codec.encodeValues(valueBuffer, statusAnalyzer.getBestStrategy(), statusAnalyzer.getValues());
            this.codec.encodeValues(valueBuffer, countAnalyzer.getBestStrategy(), countAnalyzer.getValues());
            this.codec.encodeValues(valueBuffer, avgAnalyzer.getBestStrategy(), avgAnalyzer.getValues());
            this.codec.encodeValues(valueBuffer, maxAnalyzer.getBestStrategy(), maxAnalyzer.getValues());
        }

    }

    public static class RequestsStatSummaryDecoder implements CodecDecoder<RequestsStatSummaryBo> {

        private final AgentStatDataPointCodec codec;
        private List<String> urls;
        private List<Integer> statuses;
        private List<Integer> counts;
        private List<Long> avgs;
        private List<Long> maxs;

        public RequestsStatSummaryDecoder(AgentStatDataPointCodec codec) {
            this.codec = Objects.requireNonNull(codec, "codec");
        }

        @Override
        public void decode(Buffer valueBuffer, AgentStatHeaderDecoder headerDecoder, int valueSize) {
            EncodingStrategy<String> urlEncodingStrategy = StringEncodingStrategy.getFromCode(headerDecoder.getCode());
            EncodingStrategy<Integer> statusEncodingStrategy = UnsignedIntegerEncodingStrategy.getFromCode(headerDecoder.getCode());
            EncodingStrategy<Integer> countEncodingStrategy = UnsignedIntegerEncodingStrategy.getFromCode(headerDecoder.getCode());
            EncodingStrategy<Long> avgEncodingStrategy = UnsignedLongEncodingStrategy.getFromCode(headerDecoder.getCode());
            EncodingStrategy<Long> maxEncodingStrategy = UnsignedLongEncodingStrategy.getFromCode(headerDecoder.getCode());

            urls = this.codec.decodeValues(valueBuffer, urlEncodingStrategy, valueSize);
            statuses = this.codec.decodeValues(valueBuffer, statusEncodingStrategy, valueSize);
            counts = this.codec.decodeValues(valueBuffer, countEncodingStrategy, valueSize);
            avgs = this.codec.decodeValues(valueBuffer, avgEncodingStrategy, valueSize);
            maxs = this.codec.decodeValues(valueBuffer, maxEncodingStrategy, valueSize);
        }

        @Override
        public RequestsStatSummaryBo getValue(int index) {
            String url = urls.get(index);
            Integer status = statuses.get(index);
            Integer count = counts.get(index);
            Long avg = avgs.get(index);
            Long max = maxs.get(index);

            return new RequestsStatSummaryBo(url, status, count, avg, max);
        }

    }

}
