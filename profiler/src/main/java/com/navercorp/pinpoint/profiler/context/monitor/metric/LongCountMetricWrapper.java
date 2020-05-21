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

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCountMetric;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.LongCountMetricVo;

/**
 * @author Taejin Koo
 */
public class LongCountMetricWrapper extends AbstractCustomMetricWrapper<LongCountMetric> implements LongCountMetric {

    public LongCountMetricWrapper(int id, LongCountMetric customMetric) {
        super(id, customMetric);
    }

    @Override
    public long getValue() {
        return customMetric.getValue();
    }

    @Override
    public CustomMetricVo snapshot() {
        return new LongCountMetricVo(this);
    }

}