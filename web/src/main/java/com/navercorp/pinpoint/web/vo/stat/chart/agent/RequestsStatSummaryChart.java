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

package com.navercorp.pinpoint.web.vo.stat.chart.agent;

import com.navercorp.pinpoint.common.annotations.VisibleForTesting;
import com.navercorp.pinpoint.loader.service.ServiceTypeRegistryService;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.chart.TimeSeriesChartBuilder;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatStatusSummary;
import com.navercorp.pinpoint.web.vo.stat.SampledRequestsStatSummary;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChart;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Taejin Koo
 */
public class RequestsStatSummaryChart implements StatChart {

    private final RequestsStatSummaryChartGroup requestsStatSummaryChartGroup;

    public RequestsStatSummaryChart(TimeWindow timeWindow, String url, int status, List<SampledRequestsStatStatusSummary> sampledRequestsStatSummaries, ServiceTypeRegistryService serviceTypeRegistryService) {
        this.requestsStatSummaryChartGroup = newRequestsStatSummaryChartGroup(timeWindow, url, status, sampledRequestsStatSummaries, serviceTypeRegistryService);
    }

    @VisibleForTesting
    static RequestsStatSummaryChartGroup newRequestsStatSummaryChartGroup(TimeWindow timeWindow, String url, int status, List<SampledRequestsStatStatusSummary> sampledRequestsStatSummaries, ServiceTypeRegistryService serviceTypeRegistryService) {
        Objects.requireNonNull(timeWindow, "timeWindow");

        long totalCount = 0;
        for (SampledRequestsStatStatusSummary sampledRequestsStatSummary : sampledRequestsStatSummaries) {
            long count = sampledRequestsStatSummary.getCount();
            totalCount += count;
        }

        Map<StatChartGroup.ChartType, Chart<? extends Point>> chartTypeChartMap = newChart(timeWindow, sampledRequestsStatSummaries);
        if (CollectionUtils.isNotEmpty(sampledRequestsStatSummaries)) {
            return new RequestsStatSummaryChartGroup(timeWindow, chartTypeChartMap, url, status, totalCount);
        } else {
            return null;
        }
    }

    @Override
    public StatChartGroup getCharts() {
        return requestsStatSummaryChartGroup;
    }

    public int getStatus() {
        return requestsStatSummaryChartGroup.getStatus();
    }

    public String getUrl() {
        return requestsStatSummaryChartGroup.getUrl();
    }

    public long getCount() {
        return requestsStatSummaryChartGroup.getCount();
    }

    @VisibleForTesting
    static Map<StatChartGroup.ChartType, Chart<? extends Point>> newChart(TimeWindow timeWindow, List<SampledRequestsStatStatusSummary> sampledRequestsStatStatusSummaries) {
        Chart<AgentStatPoint<Integer>> countsChart = newIntegerChart(timeWindow, sampledRequestsStatStatusSummaries, SampledRequestsStatStatusSummary::getCounts);
        Chart<AgentStatPoint<Long>> avgTimesChart = newLongChart(timeWindow, sampledRequestsStatStatusSummaries, SampledRequestsStatStatusSummary::getAvgTimes);
        Chart<AgentStatPoint<Long>> maxTimesChart = newLongChart(timeWindow, sampledRequestsStatStatusSummaries, SampledRequestsStatStatusSummary::getMaxTimes);

        return ImmutableMap.of(RequestsStatSummaryChartGroup.RequestsStatSummaryType.COUNTS, countsChart,
                RequestsStatSummaryChartGroup.RequestsStatSummaryType.AVG_TIMES, avgTimesChart,
                RequestsStatSummaryChartGroup.RequestsStatSummaryType.MAX_TIMES, maxTimesChart);
    }

    @VisibleForTesting
    static Chart<AgentStatPoint<Integer>> newIntegerChart(TimeWindow timeWindow, List<SampledRequestsStatStatusSummary> sampledList, Function<SampledRequestsStatStatusSummary, AgentStatPoint<Integer>> filter) {
        TimeSeriesChartBuilder<AgentStatPoint<Integer>> builder = new TimeSeriesChartBuilder<>(timeWindow, SampledRequestsStatSummary.UNCOLLECTED_INT_POINT_CREATOR);
        return builder.build(sampledList, filter);
    }

    @VisibleForTesting
    static Chart<AgentStatPoint<Long>> newLongChart(TimeWindow timeWindow, List<SampledRequestsStatStatusSummary> sampledList, Function<SampledRequestsStatStatusSummary, AgentStatPoint<Long>> filter) {
        TimeSeriesChartBuilder<AgentStatPoint<Long>> builder = new TimeSeriesChartBuilder<>(timeWindow, SampledRequestsStatSummary.UNCOLLECTED_LONG_POINT_CREATOR);
        return builder.build(sampledList, filter);
    }

    public static class RequestsStatSummaryChartGroup implements StatChartGroup {

        private final TimeWindow timeWindow;

        private final Map<ChartType, Chart<? extends Point>> dataSourceCharts;

        private final String url;
        private final int status;
        private final long count;

        public enum RequestsStatSummaryType implements AgentChartType {
            COUNTS,
            AVG_TIMES,
            MAX_TIMES;
        }

        public RequestsStatSummaryChartGroup(TimeWindow timeWindow, Map<ChartType, Chart<? extends Point>> dataSourceCharts, String url, int status, long count) {
            this.timeWindow = Objects.requireNonNull(timeWindow, "timeWindow");
            this.dataSourceCharts = dataSourceCharts;
            this.url = url;
            this.status = status;
            this.count = count;
        }


        @Override
        public TimeWindow getTimeWindow() {
            return timeWindow;
        }

        @Override
        public Map<ChartType, Chart<? extends Point>> getCharts() {
            return dataSourceCharts;
        }

        public String getUrl() {
            return url;
        }

        public int getStatus() {
            return status;
        }

        public long getCount() {
            return count;
        }
    }

}

