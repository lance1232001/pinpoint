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

package com.navercorp.pinpoint.web.vo.metric.chart.agent;

import com.navercorp.pinpoint.common.annotations.VisibleForTesting;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.chart.TimeSeriesChartBuilder;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetric;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetricList;
import com.navercorp.pinpoint.web.vo.metric.chart.CustomMetricChart;
import com.navercorp.pinpoint.web.vo.metric.chart.CustomMetricChartGroup;
import com.navercorp.pinpoint.web.vo.stat.SampledDataSource;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;
import com.navercorp.pinpoint.web.vo.stat.chart.agent.AgentStatPoint;

import com.google.common.collect.ImmutableMap;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * @author Taejin Koo
 */
public class IntCountMetricChart implements CustomMetricChart {

    private final IntCountMetricChartGroup intCountMetricChartGroup;

    public IntCountMetricChart(TimeWindow timeWindow, SampledIntCountMetricList sampledIntCountMetricList) {
        this.intCountMetricChartGroup = newIntCountMetricChartGroup(timeWindow, sampledIntCountMetricList);
    }

    @Override
    public StatChartGroup getCharts() {
        return intCountMetricChartGroup;
    }


    public String getGroupName() {
        return intCountMetricChartGroup.getGroupName();
    }

    public String getMetricName() {
        return intCountMetricChartGroup.getMetricName();
    }

    public String getLabelName() {
        return intCountMetricChartGroup.getLabelName();
    }

    @VisibleForTesting
    static IntCountMetricChartGroup newIntCountMetricChartGroup(TimeWindow timeWindow, SampledIntCountMetricList sampledIntCountMetricList) {
        Objects.requireNonNull(timeWindow, "timeWindow");

        Map<StatChartGroup.ChartType, Chart<? extends Point>> chartTypeChartMap = newIntCountMetricChart(timeWindow, sampledIntCountMetricList);
        return new IntCountMetricChartGroup(timeWindow, chartTypeChartMap, sampledIntCountMetricList.getMetricName());
    }

    @VisibleForTesting
    static Map<StatChartGroup.ChartType, Chart<? extends Point>> newIntCountMetricChart(TimeWindow timeWindow, SampledIntCountMetricList sampledIntCountMetricList) {
        Chart<AgentStatPoint<Integer>> intCountValueChart = newChart(timeWindow, sampledIntCountMetricList.getSampledIntCountMetricList(), SampledIntCountMetric::getIntCountMetric);
        return ImmutableMap.of(IntCountMetricChartGroup.IntCountMetricChartType.VALUE, intCountValueChart);
    }

    @VisibleForTesting
    static Chart<AgentStatPoint<Integer>> newChart(TimeWindow timeWindow, List<SampledIntCountMetric> sampledIntCountMetricList, Function<SampledIntCountMetric, AgentStatPoint<Integer>> filter) {
        TimeSeriesChartBuilder<AgentStatPoint<Integer>> builder = new TimeSeriesChartBuilder<>(timeWindow, SampledDataSource.UNCOLLECTED_POINT_CREATOR);
        return builder.build(sampledIntCountMetricList, filter);
    }

    public static class IntCountMetricChartGroup implements CustomMetricChartGroup {

        private final TimeWindow timeWindow;

        private final Map<StatChartGroup.ChartType, Chart<? extends Point>> intCountMetricCharts;

        private final String groupName;
        private final String metricName;
        private final String labelName;

        public enum IntCountMetricChartType implements StatChartGroup.AgentChartType {
            VALUE,
        }

        public IntCountMetricChartGroup(TimeWindow timeWindow, Map<StatChartGroup.ChartType, Chart<? extends Point>> intCountMetricCharts, String metricName) {
            this.timeWindow = Objects.requireNonNull(timeWindow, "timeWindow");
            this.intCountMetricCharts = intCountMetricCharts;

            Objects.requireNonNull(metricName, "metricName");

            String[] splittedMetricName = metricName.split("/");
            if (splittedMetricName.length != 3) {
                throw new IllegalArgumentException("metricName must must be configured like '{groupName}/{metricName}/{labelName}.");
            }

            Assert.isTrue(!splittedMetricName[0].isEmpty(), "groupName may not be empty");
            this.groupName = splittedMetricName[0];

            Assert.isTrue(!splittedMetricName[1].isEmpty(), "metricName may not be empty");
            this.metricName = splittedMetricName[1];

            Assert.isTrue(!splittedMetricName[2].isEmpty(), "labelName may not be empty");
            this.labelName = splittedMetricName[2];
        }

        @Override
        public TimeWindow getTimeWindow() {
            return timeWindow;
        }

        @Override
        public Map<StatChartGroup.ChartType, Chart<? extends Point>> getCharts() {
            return intCountMetricCharts;
        }

        public String getGroupName() {
            return groupName;
        }

        public String getMetricName() {
            return metricName;
        }

        public String getLabelName() {
            return labelName;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("IntCountMetricChartGroup{");
            sb.append("timeWindow=").append(timeWindow);
            sb.append(", intCountMetricCharts=").append(intCountMetricCharts);
            sb.append(", groupName='").append(groupName).append('\'');
            sb.append(", metricName='").append(metricName).append('\'');
            sb.append(", labelName='").append(labelName).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IntCountMetricChart{");
        sb.append("intCountMetricChartGroup=").append(intCountMetricChartGroup);
        sb.append('}');
        return sb.toString();
    }

}
