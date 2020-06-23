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
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.codec.strategy.EncodingStrategy;

import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class LongCounterFieldDecoder implements CustomMetricFieldDecoder<Long> {

    private final int index;
    private final String metricName;

    private EncodingStrategy<Long> encodingStrategy;
    private List<Long> decodedValue;

    public LongCounterFieldDecoder(int index, String metricName) {
        this.index = index;
        this.metricName = Objects.requireNonNull(metricName, "metricName");
    }

    @Override
    public void setEncodingStrategy(int code) {
        this.encodingStrategy = UnsignedLongEncodingStrategy.getFromCode(code);
    }

    @Override
    public void decodeValues(AgentStatDataPointCodec codec, Buffer valueBuffer, int valueSize) {
        this.decodedValue = codec.decodeValues(valueBuffer, encodingStrategy, valueSize);
    }

    @Override
    public List<Long> getDecodedValue() {
        return decodedValue;
    }


}

