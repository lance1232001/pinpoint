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

import com.navercorp.pinpoint.common.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class SampledIntCountMetricList implements SampledAgentCustomMetricDataPoint {

    private final String metricName;

    private final List<SampledIntCountMetric> sampledIntCountMetricList = new ArrayList<SampledIntCountMetric>();

    public SampledIntCountMetricList(String metricName) {
        this.metricName = Assert.requireNonNull(metricName, "metricName");
    }

    public String getMetricName() {
        return metricName;
    }

    public void addSampledIntCountMetric(SampledIntCountMetric sampledIntCountMetric) {
        sampledIntCountMetricList.add(sampledIntCountMetric);
    }

    public List<SampledIntCountMetric> getSampledIntCountMetricList() {
        return sampledIntCountMetricList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SampledIntCountMetricList that = (SampledIntCountMetricList) o;
        return Objects.equals(sampledIntCountMetricList, that.sampledIntCountMetricList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sampledIntCountMetricList);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SampledIntCountMetricList{");
        sb.append("sampledIntCountMetricList=").append(sampledIntCountMetricList);
        sb.append('}');
        return sb.toString();
    }

}

