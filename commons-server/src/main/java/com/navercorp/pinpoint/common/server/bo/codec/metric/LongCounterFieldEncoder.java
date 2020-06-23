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

import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.StrategyAnalyzer;
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedLongEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.LongCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.SimpleCustomMetricBo;

import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class LongCounterFieldEncoder implements CustomMetricFieldEncoder<Long> {

    private final int index;
    private final String metricName;
    private final UnsignedLongEncodingStrategy.Analyzer.Builder analyzerBuilder;

    public LongCounterFieldEncoder(int index, String metricName) {
        this.index = index;
        this.metricName = Objects.requireNonNull(metricName, "metricName");
        this.analyzerBuilder = new UnsignedLongEncodingStrategy.Analyzer.Builder();
    }

    @Override
    public void addValue(SimpleCustomMetricBo agentStatDataPoint) {
        CustomMetricBo customMetricBo = agentStatDataPoint.get(metricName);

        System.out.println("metricName:" + metricName + " ==== " + customMetricBo);

        if (!(customMetricBo instanceof LongCountMetricBo)) {
            throw new IllegalArgumentException(metricName + " must be LongCountMetricBo clazz");
        }

        LongCountMetricBo longCountMetricBo = (LongCountMetricBo) customMetricBo;
        Long value = longCountMetricBo.getValue();

        analyzerBuilder.addValue(value);
    }

    @Override
    public StrategyAnalyzer<Long> getAnalyzer() {
        StrategyAnalyzer<Long> strategyAnalyzer = analyzerBuilder.build();
        return strategyAnalyzer;
    }

}

