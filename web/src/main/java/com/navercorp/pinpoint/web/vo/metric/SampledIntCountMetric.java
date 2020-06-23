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

package com.navercorp.pinpoint.web.vo.metric;

import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class SampledIntCountMetric implements SampledAgentCustomMetricDataPoint {

    public static final Integer UNCOLLECTED_VALUE = -1;
    public static final Point.UncollectedPointCreator<AgentStatPoint<Integer>> UNCOLLECTED_POINT_CREATOR = new Point.UncollectedPointCreator<AgentStatPoint<Integer>>() {
        @Override
        public AgentStatPoint<Integer> createUnCollectedPoint(long xVal) {
            return new AgentStatPoint<>(xVal, UNCOLLECTED_VALUE);
        }
    };

    private AgentStatPoint<Integer> intCountMetric;

    public AgentStatPoint<Integer> getIntCountMetric() {
        return intCountMetric;
    }

    public void setIntCountMetric(AgentStatPoint<Integer> intCountMetric) {
        this.intCountMetric = intCountMetric;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampledIntCountMetric that = (SampledIntCountMetric) o;
        return Objects.equals(intCountMetric, that.intCountMetric);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intCountMetric);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledIntCountMetric{");
        sb.append("intCountMetric=").append(intCountMetric);
        sb.append('}');
        return sb.toString();
    }

}
