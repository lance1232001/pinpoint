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

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.CustomMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.DoubleGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.IntGaugeMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongCountMetric;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.LongGaugeMetric;
import com.navercorp.pinpoint.common.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Taejin Koo
 */
public class DefaultCustomMetricRegistryService implements CustomMetricRegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomMetricRegistryService.class);

    private final CustomMetricWrapperFactory customMetricWrapperFactory = new CustomMetricWrapperFactory();

    private final Map<String, CustomMetricWrapper> customMetricWrapperMap = new ConcurrentHashMap<String, CustomMetricWrapper>();

    private final CustomMetricIdGenerator customMetricIdGenerator;

    public DefaultCustomMetricRegistryService(int limitIdNumber) {
        this.customMetricIdGenerator = new CustomMetricIdGenerator(limitIdNumber);
    }

    @Override
    public boolean register(IntCountMetric customMetric) {
        Assert.requireNonNull(customMetric, "customMetric");

        int id = customMetricIdGenerator.create(customMetric.getName());
        if (id == CustomMetricIdGenerator.NOT_REGISTERED) {
            LOGGER.warn("Failed to create metricId. metric:{}", customMetric);
            return false;
        }

        IntCountMetricWrapper customMetricWrapper = customMetricWrapperFactory.create(id, customMetric);
        return add(customMetricWrapper);
    }

    @Override
    public boolean register(LongCountMetric customMetric) {
        Assert.requireNonNull(customMetric, "customMetric");

        int id = customMetricIdGenerator.create(customMetric.getName());
        if (id == CustomMetricIdGenerator.NOT_REGISTERED) {
            LOGGER.warn("Failed to create metricId. metric:{}", customMetric);
            return false;
        }

        LongCountMetricWrapper customMetricWrapper = customMetricWrapperFactory.create(id, customMetric);
        return add(customMetricWrapper);
    }

    @Override
    public boolean register(IntGaugeMetric customMetric) {
        Assert.requireNonNull(customMetric, "customMetric");

        int id = customMetricIdGenerator.create(customMetric.getName());
        if (id == CustomMetricIdGenerator.NOT_REGISTERED) {
            LOGGER.warn("Failed to create metricId. metric:{}", customMetric);
            return false;
        }

        IntGaugeMetricWrapper customMetricWrapper = customMetricWrapperFactory.create(id, customMetric);
        return add(customMetricWrapper);
    }

    @Override
    public boolean register(LongGaugeMetric customMetric) {
        Assert.requireNonNull(customMetric, "customMetric");

        int id = customMetricIdGenerator.create(customMetric.getName());
        if (id == CustomMetricIdGenerator.NOT_REGISTERED) {
            LOGGER.warn("Failed to create metricId. metric:{}", customMetric);
            return false;
        }

        LongGaugeMetricWrapper customMetricWrapper = customMetricWrapperFactory.create(id, customMetric);
        return add(customMetricWrapper);
    }

    @Override
    public boolean register(DoubleGaugeMetric customMetric) {
        Assert.requireNonNull(customMetric, "customMetric");

        int id = customMetricIdGenerator.create(customMetric.getName());
        if (id == CustomMetricIdGenerator.NOT_REGISTERED) {
            LOGGER.warn("Failed to create metricId. metric:{}", customMetric);
            return false;
        }

        DoubleGaugeMetricWrapper customMetricWrapper = customMetricWrapperFactory.create(id, customMetric);
        return add(customMetricWrapper);
    }

    private boolean add(CustomMetricWrapper customMetricWrapper) {
        CustomMetricWrapper put = customMetricWrapperMap.put(customMetricWrapper.getName(), customMetricWrapper);
        return put == null;
    }

    @Override
    public boolean unregister(IntCountMetric customMetric) {
        return remove(customMetric);
    }

    @Override
    public boolean unregister(LongCountMetric customMetric) {
        return remove(customMetric);
    }

    @Override
    public boolean unregister(IntGaugeMetric customMetric) {
        return remove(customMetric);
    }

    @Override
    public boolean unregister(LongGaugeMetric customMetric) {
        return remove(customMetric);
    }

    @Override
    public boolean unregister(DoubleGaugeMetric customMetric) {
        return remove(customMetric);
    }

    private boolean remove(CustomMetric customMetric) {
        CustomMetricWrapper customMetricWrapper = customMetricWrapperMap.get(customMetric.getName());

        if (customMetricWrapper == null) {
            return false;
        }

        if (customMetricWrapper.equalsWithUnwrap(customMetric)) {
            CustomMetricWrapper remove = customMetricWrapperMap.remove(customMetric.getName());
            return remove != null;
        }
        return false;
    }

    @Override
    public Map<String, CustomMetricWrapper> getCustomMetricMap() {
        return customMetricWrapperMap;
    }

}

