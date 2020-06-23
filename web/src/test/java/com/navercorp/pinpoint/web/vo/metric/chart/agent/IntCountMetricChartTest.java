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

import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.web.mapper.metric.sampling.sampler.IntCountMetricSampler;
import com.navercorp.pinpoint.web.util.TimeWindow;
import com.navercorp.pinpoint.web.util.TimeWindowDownSampler;
import com.navercorp.pinpoint.web.vo.Range;
import com.navercorp.pinpoint.web.vo.chart.Chart;
import com.navercorp.pinpoint.web.vo.chart.Point;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetric;
import com.navercorp.pinpoint.web.vo.metric.SampledIntCountMetricList;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSampler;
import com.navercorp.pinpoint.web.vo.stat.chart.DownSamplers;
import com.navercorp.pinpoint.web.vo.stat.chart.StatChartGroup;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author Taejin Koo
 */
public class IntCountMetricChartTest {

    private static final DownSampler<Integer> INTEGER_DOWN_SAMPLER = DownSamplers.getIntegerDownSampler(SampledIntCountMetric.UNCOLLECTED_VALUE);

    @Test
    public void simpleTest() {
        long to = System.currentTimeMillis();
        long from = System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(30);

        Range range = new Range(from, to);

        final TimeWindow timeWindow = new TimeWindow(range, TimeWindowDownSampler.SAMPLER);
        long windowSlotSize = timeWindow.getWindowSlotSize();
        System.out.println(windowSlotSize);
        long windowRangeCount = timeWindow.getWindowRangeCount();
        System.out.println(windowRangeCount);

        List<Long> xvalList = getXvalList(range, (int) windowRangeCount);

        System.out.println(xvalList);

        List<SampledIntCountMetric> agentStatPoints = new ArrayList<>();
        Iterator<Long> iterator = timeWindow.iterator();

        IntCountMetricSampler intCountMetricSampler = new IntCountMetricSampler();

        int count = 0;
        while (iterator.hasNext()) {
            Long next = iterator.next();

            IntCountMetricBo intCountMetricBo = new IntCountMetricBo();
            intCountMetricBo.setValue(count);

            SampledIntCountMetric sampledIntCountMetric = intCountMetricSampler.sampleDataPoints(count, next, Arrays.asList(intCountMetricBo), null);

            agentStatPoints.add(sampledIntCountMetric);
            count++;
        }

//        intCountMetricSampler.

//        for (Long aLong : xvalList) {
//            AgentStatPoint<Integer> point = createPoint(aLong, Arrays.asList(1));
//            SampledIntCountMetric sampledIntCountMetric = new SampledIntCountMetric();
//            sampledIntCountMetric.setIntCountMetric(point);
//
//            agentStatPoints.add(sampledIntCountMetric);
//        }


//        new AgentStatPoint<Integer>()

        SampledIntCountMetricList sampledIntCountMetricList = new SampledIntCountMetricList("group/metric/label");

        for (SampledIntCountMetric agentStatPoint : agentStatPoints) {
            sampledIntCountMetricList.addSampledIntCountMetric(agentStatPoint);

        }

        IntCountMetricChart intCountMetricChart = new IntCountMetricChart(timeWindow, sampledIntCountMetricList);

        StatChartGroup charts = intCountMetricChart.getCharts();

        Map<StatChartGroup.ChartType, Chart<? extends Point>> charts1 = charts.getCharts();
        System.out.println(intCountMetricChart);

//        System.out.println(charts1.values());

    }

    List<Long> getXvalList(Range range, int splitSize) {
        long from = range.getFrom();
        long to = range.getTo();

        long diff = to - from;

        long interval = diff / splitSize;


        List<Long> timestampsList = new ArrayList<>();
        for (int i = 0; i < splitSize; i++) {
            long a = from + (interval * i);
            timestampsList.add(a);
        }

        return timestampsList;
    }


}
