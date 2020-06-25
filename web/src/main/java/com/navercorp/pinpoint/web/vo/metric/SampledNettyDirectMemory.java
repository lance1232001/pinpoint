/*
 * Copyright 2020 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.web.vo.metric;

import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.stat.SampledAgentStatDataPoint;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import java.util.Objects;

public class SampledNettyDirectMemory implements SampledAgentStatDataPoint {

    public static final Long UNCOLLECTED_VALUE = -1L;
    public static final Point.UncollectedPointCreator<AgentStatPoint<Long>> UNCOLLECTED_POINT_CREATOR = new Point.UncollectedPointCreator<AgentStatPoint<Long>>() {
        @Override
        public AgentStatPoint<Long> createUnCollectedPoint(long xVal) {
            return new AgentStatPoint<>(xVal, UNCOLLECTED_VALUE);
        }
    };

    private final AgentStatPoint<Long> used;
    private final AgentStatPoint<Long> max;

    public SampledNettyDirectMemory(AgentStatPoint<Long> used, AgentStatPoint<Long> max) {
        this.used = Objects.requireNonNull(used, "used");
        this.max = Objects.requireNonNull(max, "max");
    }

    public AgentStatPoint<Long> getUsed() {
        return used;
    }

    public AgentStatPoint<Long> getMax() {
        return max;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SampledNettyDirectMemory that = (SampledNettyDirectMemory) o;

        if (used != null ? !used.equals(that.used) : that.used != null) return false;
        return max != null ? max.equals(that.max) : that.max == null;
    }

    @Override
    public int hashCode() {
        int result = used != null ? used.hashCode() : 0;
        result = 31 * result + (max != null ? max.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SampledNettyDirectMemory{" +
                "used=" + used +
                ", max=" + max +
                '}';
    }

}
