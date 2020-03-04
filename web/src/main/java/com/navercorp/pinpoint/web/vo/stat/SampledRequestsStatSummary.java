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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class SampledRequestsStatSummary implements SampledAgentStatDataPoint {

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

    private final String url;
    private final Map<Integer, SampledRequestsStatStatusSummary> map = new HashMap<>();

    public SampledRequestsStatSummary(final String url) {
        this.url = Objects.requireNonNull(url, "url");
    }

    public void add(int status, SampledRequestsStatStatusSummary sampledRequestsStatStatusSummary) {
//        SampledRequestsStatStatusSummary sampledRequestsStatStatusSummary = new SampledRequestsStatStatusSummary(counts, avgTimes, maxTimes);
        map.put(status, sampledRequestsStatStatusSummary);
    }

    public String getUrl() {
        return url;
    }

    public Map<Integer, SampledRequestsStatStatusSummary> getMap() {
        return map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampledRequestsStatSummary that = (SampledRequestsStatSummary) o;
        return Objects.equals(url, that.url) &&
                Objects.equals(map, that.map);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url, map);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledRequestsStatSummary{");
        sb.append("url='").append(url).append('\'');
        sb.append(", map=").append(map);
        sb.append('}');
        return sb.toString();
    }

}
