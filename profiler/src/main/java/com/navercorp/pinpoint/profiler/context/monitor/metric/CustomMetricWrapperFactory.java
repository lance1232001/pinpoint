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

package com.navercorp.pinpoint.profiler.context.monitor.metric;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.DoubleGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongGaugeMetric;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Taejin Koo
 */
public class CustomMetricWrapperFactory {

    CustomMetricWrapperFactory() {
    }

    IntCountMetricWrapper create(int id, IntCountMetric customMetric) {
        return new IntCountMetricWrapper(id, customMetric);
    }

    LongCountMetricWrapper create(int id, LongCountMetric customMetric) {
        return new LongCountMetricWrapper(id, customMetric);
    }

    IntGaugeMetricWrapper create(int id, IntGaugeMetric customMetric) {
        return new IntGaugeMetricWrapper(id, customMetric);
    }

    LongGaugeMetricWrapper create(int id, LongGaugeMetric customMetric) {
        return new LongGaugeMetricWrapper(id, customMetric);
    }

    DoubleGaugeMetricWrapper create(int id, DoubleGaugeMetric customMetric) {
        return new DoubleGaugeMetricWrapper(id, customMetric);
    }

}
