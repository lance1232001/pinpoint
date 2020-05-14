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
import com.navercorp.pinpoint.common.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Taejin Koo
 */
public class DefaultCustomMetricRegistryService implements CustomMetricRegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomMetricRegistryService.class);

    private final CustomMetricWrapperFactory customMetricWrapperFactory = new CustomMetricWrapperFactory();
    private final ConcurrentHashMap<Class, CustomMetricWrapperListHolder> customMetricHolderRepository = new ConcurrentHashMap<Class, CustomMetricWrapperListHolder>();

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
        return addCustomMetricWrapper(customMetricWrapper, customMetricHolderRepository);
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
        return addCustomMetricWrapper(customMetricWrapper, customMetricHolderRepository);
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
        return addCustomMetricWrapper(customMetricWrapper, customMetricHolderRepository);
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
        return addCustomMetricWrapper(customMetricWrapper, customMetricHolderRepository);
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
        return addCustomMetricWrapper(customMetricWrapper, customMetricHolderRepository);
    }

    private static <T extends CustomMetricWrapper> boolean addCustomMetricWrapper(T customMetricWrapper, ConcurrentHashMap<Class, CustomMetricWrapperListHolder> customMetricHolderRepository) {
        if (customMetricWrapper == null) {
            return false;
        }

        CustomMetricWrapperListHolder customMetricWrapperListHolder = getCustomMetricWrapperListHolder(customMetricWrapper.getClass(), customMetricHolderRepository);
        return customMetricWrapperListHolder.add(customMetricWrapper);
    }

    private static <T extends CustomMetricWrapper> CustomMetricWrapperListHolder<T> getCustomMetricWrapperListHolder(Class<T> customMetricWrapperClazz, ConcurrentHashMap<Class, CustomMetricWrapperListHolder> customMetricHolderRepository) {
        Assert.requireNonNull(customMetricWrapperClazz, "customMetricWrapperClazz");

        CustomMetricWrapperListHolder<T> customMetricWrapperListHolder = customMetricHolderRepository.get(customMetricWrapperClazz);
        if (customMetricWrapperListHolder != null) {
            return customMetricWrapperListHolder;
        }

        customMetricHolderRepository.putIfAbsent(customMetricWrapperClazz, new CustomMetricWrapperListHolder<T>(customMetricWrapperClazz));
        return customMetricHolderRepository.get(customMetricWrapperClazz);
    }


    @Override
    public boolean unregister(IntCountMetric customMetric) {
        Class<IntCountMetricWrapper> mappingClazz = customMetricWrapperFactory.getMappingClazz(customMetric);
        CustomMetricWrapperListHolder<IntCountMetricWrapper> customMetricWrapperListHolder = getCustomMetricWrapperListHolder(mappingClazz, customMetricHolderRepository);
        return customMetricWrapperListHolder.remove(customMetric);
    }

    @Override
    public boolean unregister(LongCountMetric customMetric) {
        Class<LongCountMetricWrapper> mappingClazz = customMetricWrapperFactory.getMappingClazz(customMetric);
        CustomMetricWrapperListHolder<LongCountMetricWrapper> customMetricWrapperListHolder = getCustomMetricWrapperListHolder(mappingClazz, customMetricHolderRepository);
        return customMetricWrapperListHolder.remove(customMetric);
    }

    @Override
    public boolean unregister(IntGaugeMetric customMetric) {
        Class<IntGaugeMetricWrapper> mappingClazz = customMetricWrapperFactory.getMappingClazz(customMetric);
        CustomMetricWrapperListHolder<IntGaugeMetricWrapper> customMetricWrapperListHolder = getCustomMetricWrapperListHolder(mappingClazz, customMetricHolderRepository);
        return customMetricWrapperListHolder.remove(customMetric);
    }

    @Override
    public boolean unregister(LongGaugeMetric customMetric) {
        Class<LongGaugeMetricWrapper> mappingClazz = customMetricWrapperFactory.getMappingClazz(customMetric);
        CustomMetricWrapperListHolder<LongGaugeMetricWrapper> customMetricWrapperListHolder = getCustomMetricWrapperListHolder(mappingClazz, customMetricHolderRepository);
        return customMetricWrapperListHolder.remove(customMetric);
    }

    @Override
    public boolean unregister(DoubleGaugeMetric customMetric) {
        Class<DoubleGaugeMetricWrapper> mappingClazz = customMetricWrapperFactory.getMappingClazz(customMetric);
        CustomMetricWrapperListHolder<DoubleGaugeMetricWrapper> customMetricWrapperListHolder = getCustomMetricWrapperListHolder(mappingClazz, customMetricHolderRepository);
        return customMetricWrapperListHolder.remove(customMetric);
    }


    @Override
    public List<IntCountMetricWrapper> getIntCountMetricWrapperList() {
        return getList(IntCountMetricWrapper.class, customMetricHolderRepository);
    }

    @Override
    public List<LongCountMetricWrapper> getLongCountMetricWrapperList() {
        return getList(LongCountMetricWrapper.class, customMetricHolderRepository);
    }

    @Override
    public List<IntGaugeMetricWrapper> getIntGaugeMetricWrapperList() {
        return getList(IntGaugeMetricWrapper.class, customMetricHolderRepository);
    }

    @Override
    public List<LongGaugeMetricWrapper> getLongGaugeMetricWrapperList() {
        return getList(LongGaugeMetricWrapper.class, customMetricHolderRepository);
    }

    @Override
    public List<DoubleGaugeMetricWrapper> getDoubleGaugeMetricWrapperList() {
        return getList(DoubleGaugeMetricWrapper.class, customMetricHolderRepository);
    }

    private static <T extends CustomMetricWrapper> List<T> getList(Class<T> clazz, ConcurrentHashMap<Class, CustomMetricWrapperListHolder> customMetricHolderRepository) {
        CustomMetricWrapperListHolder customMetricWrapperListHolder = customMetricHolderRepository.get(clazz);
        if (customMetricWrapperListHolder == null) {
            return Collections.emptyList();
        }
        return customMetricWrapperListHolder.getList();
    }

}

