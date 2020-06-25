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
import com.navercorp.pinpoint.common.server.bo.codec.stat.strategy.UnsignedIntegerEncodingStrategy;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.CustomMetricValue;
import com.navercorp.pinpoint.common.server.bo.metric.IntCounterMetricValue;

import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class IntCounterFieldEncoder implements CustomMetricFieldEncoder<Integer> {

    private final int index;
    private final String metricName;
    private final UnsignedIntegerEncodingStrategy.Analyzer.Builder analyzerBuilder;

    private StrategyAnalyzer<Integer> strategyAnalyzer;

    public IntCounterFieldEncoder(int index, String metricName) {
        this.index = index;
        this.metricName = Objects.requireNonNull(metricName, "metricName");
        this.analyzerBuilder = new UnsignedIntegerEncodingStrategy.Analyzer.Builder();
    }

    @Override
    public void addValue(AgentCustomMetricBo agentCustomMetricBo) {
        final CustomMetricValue customMetricValue = agentCustomMetricBo.get(metricName);

        System.out.println("metricName:" + metricName + " ==== " + customMetricValue);

        if (!(customMetricValue instanceof IntCounterMetricValue)) {
            throw new IllegalArgumentException(metricName + " must be IntCounterMetricValue clazz");
        }

        IntCounterMetricValue intCounterMetricValue = (IntCounterMetricValue) customMetricValue;
        Integer value = intCounterMetricValue.getValue();

        analyzerBuilder.addValue(value);
    }

    @Override
    public StrategyAnalyzer<Integer> getAnalyzer() {
        if (strategyAnalyzer != null) {
            return strategyAnalyzer;
        }

        StrategyAnalyzer<Integer> strategyAnalyzer = analyzerBuilder.build();
        this.strategyAnalyzer = strategyAnalyzer;
        return strategyAnalyzer;
    }

}
