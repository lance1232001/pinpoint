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

package com.navercorp.pinpoint.web.vo.stat;

import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class SampledRequestsStatStatusSummary implements SampledAgentStatDataPoint {

    public static final int UNCOLLECTED_INT_COUNT = -1;
    public static final Point.UncollectedPointCreator<AgentStatPoint<Integer>> UNCOLLECTED_INT_POINT_CREATOR = new Point.UncollectedPointCreator<AgentStatPoint<Integer>>() {
        @Override
        public AgentStatPoint<Integer> createUnCollectedPoint(long xVal) {
            return new AgentStatPoint<>(xVal, UNCOLLECTED_INT_COUNT);
        }
    };
    public static final long UNCOLLECTED_LONG_COUNT = -1;
    public static final Point.UncollectedPointCreator<AgentStatPoint<Long>> UNCOLLECTED_LONG_POINT_CREATOR = new Point.UncollectedPointCreator<AgentStatPoint<Long>>() {
        @Override
        public AgentStatPoint<Long> createUnCollectedPoint(long xVal) {
            return new AgentStatPoint<>(xVal, UNCOLLECTED_LONG_COUNT);
        }
    };

    private final long count;
    private final AgentStatPoint<Integer> counts;
    private final AgentStatPoint<Long> avgTimes;
    private final AgentStatPoint<Long> maxTimes;

    public SampledRequestsStatStatusSummary(long count, AgentStatPoint<Integer> counts, AgentStatPoint<Long> avgTimes, AgentStatPoint<Long> maxTimes) {
        this.count = count;
        this.counts = Objects.requireNonNull(counts, "counts");
        this.avgTimes = Objects.requireNonNull(avgTimes, "avgTimes");
        this.maxTimes = Objects.requireNonNull(maxTimes, "maxTimes");
    }

    public long getCount() {
        return count;
    }

    public AgentStatPoint<Integer> getCounts() {
        return counts;
    }

    public AgentStatPoint<Long> getAvgTimes() {
        return avgTimes;
    }

    public AgentStatPoint<Long> getMaxTimes() {
        return maxTimes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampledRequestsStatStatusSummary that = (SampledRequestsStatStatusSummary) o;
        return Objects.equals(counts, that.counts) &&
                Objects.equals(avgTimes, that.avgTimes) &&
                Objects.equals(maxTimes, that.maxTimes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(counts, avgTimes, maxTimes);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledRequestsStatStatusSummary{");
        sb.append("counts=").append(counts);
        sb.append(", avgTimes=").append(avgTimes);
        sb.append(", maxTimes=").append(maxTimes);
        sb.append('}');
        return sb.toString();
    }

}
