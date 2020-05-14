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

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.CustomMetricRegistry;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.DoubleGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongGaugeMetric;
import com.navercorp.pinpoint.common.util.Assert;

/**
 * @author Taejin Koo
 */
public class CustomMetricRegistryAdaptor implements CustomMetricRegistry {

    private final CustomMetricRegistryService delegate;

    public CustomMetricRegistryAdaptor(CustomMetricRegistryService delegate) {
        this.delegate = Assert.requireNonNull(delegate, "delegate");
    }

    @Override
    public boolean register(IntCountMetric customMetric) {
        return delegate.register(customMetric);
    }

    @Override
    public boolean register(LongCountMetric customMetric) {
        return delegate.register(customMetric);
    }

    @Override
    public boolean register(IntGaugeMetric customMetric) {
        return delegate.register(customMetric);
    }

    @Override
    public boolean register(LongGaugeMetric customMetric) {
        return delegate.register(customMetric);
    }

    @Override
    public boolean register(DoubleGaugeMetric customMetric) {
        return delegate.register(customMetric);
    }

    @Override
    public boolean unregister(IntCountMetric customMetric) {
        return delegate.unregister(customMetric);
    }

    @Override
    public boolean unregister(LongCountMetric customMetric) {
        return delegate.unregister(customMetric);
    }

    @Override
    public boolean unregister(IntGaugeMetric customMetric) {
        return delegate.unregister(customMetric);
    }

    @Override
    public boolean unregister(LongGaugeMetric customMetric) {
        return delegate.unregister(customMetric);
    }

    @Override
    public boolean unregister(DoubleGaugeMetric customMetric) {
        return delegate.unregister(customMetric);
    }
    
}
