package com.navercorp.pinpoint.profiler.context.monitor;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.CustomMetricMonitor;
import com.navercorp.pinpoint.common.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class DefaultCustomMetricMonitorRegistryService implements CustomMetricMonitorRegistryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCustomMetricMonitorRegistryService.class);

    private final AtomicInteger idGenerator = new AtomicInteger();

    private final int limitIdNumber;

    private final CopyOnWriteArrayList<CustomMetricMonitorWrapper<Integer>> intRepository = new CopyOnWriteArrayList<CustomMetricMonitorWrapper<Integer>>();

    private final CopyOnWriteArrayList<CustomMetricMonitorWrapper<Long>> longRepository = new CopyOnWriteArrayList<CustomMetricMonitorWrapper<Long>>();

    private final CopyOnWriteArrayList<CustomMetricMonitorWrapper<Double>> doubleRepository = new CopyOnWriteArrayList<CustomMetricMonitorWrapper<Double>>();

    private final DataSourceMonitorWrapperFactory wrapperFactory = new DataSourceMonitorWrapperFactory();

    public DefaultCustomMetricMonitorRegistryService(int limitIdNumber) {
        Assert.isTrue(limitIdNumber > 0, "'limitIdNumber' must be >= 0");
        this.limitIdNumber = limitIdNumber;
    }

    @Override
    public boolean registerIntMetric(CustomMetricMonitor<Integer> customMetricMonitor) {
        if (!checkAvailableId()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("can't register {}. The maximum value of id number has been exceeded.");
            }
            return false;
        }
        return registerMetric(idGenerator, customMetricMonitor, intRepository);
    }

    @Override
    public boolean registerLongMetric(CustomMetricMonitor<Long> customMetricMonitor) {
        if (!checkAvailableId()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("can't register {}. The maximum value of id number has been exceeded.");
            }
            return false;
        }
        return registerMetric(idGenerator, customMetricMonitor, longRepository);
    }

    @Override
    public boolean registerDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor) {
        if (!checkAvailableId()) {
            if (LOGGER.isInfoEnabled()) {
                LOGGER.info("can't register {}. The maximum value of id number has been exceeded.");
            }
            return false;
        }
        return registerMetric(idGenerator, customMetricMonitor, doubleRepository);
    }

    private static <V extends Number> boolean registerMetric(AtomicInteger idGenerator, CustomMetricMonitor<V> customMetricMonitor, List<CustomMetricMonitorWrapper<V>> repository) {
        CustomMetricMonitorWrapper<V> customMetricMonitorWrapper = new CustomMetricMonitorWrapper<V>(idGenerator.incrementAndGet(), customMetricMonitor);
        return repository.add(customMetricMonitorWrapper);
    }

    private boolean checkAvailableId() {
        return getRemainingIdNumber() > 0;
    }



    @Override
    public boolean unregisterIntMetric(CustomMetricMonitor<Integer> customMetricMonitor) {
        return unregisterMetric(customMetricMonitor, intRepository);
    }

    @Override
    public boolean unregisterLongMetric(CustomMetricMonitor<Long> customMetricMonitor) {
        return unregisterMetric(customMetricMonitor, longRepository);
    }

    @Override
    public boolean unregisterDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor) {
        return unregisterMetric(customMetricMonitor, doubleRepository);
    }

    private static <V extends Number> boolean unregisterMetric(CustomMetricMonitor<V> customMetricMonitor, List<CustomMetricMonitorWrapper<V>> repository) {
        for (CustomMetricMonitorWrapper<V> customMetricMonitorWrapper : repository) {
            if (customMetricMonitorWrapper.equalsWithUnwrap(customMetricMonitor)) {
                return repository.remove(customMetricMonitorWrapper);
            }
        }

        return false;
    }

    @Override
    public List<CustomMetricMonitorWrapper<Integer>> getIntMetricMonitorWrapperList() {
        return getEnabledMetricMonitorWrapperList(intRepository);
    }

    @Override
    public List<CustomMetricMonitorWrapper<Long>> getLongMetricMonitorList() {
        return getEnabledMetricMonitorWrapperList(longRepository);
    }

    @Override
    public List<CustomMetricMonitorWrapper<Double>> getDoubleMetricMonitorWrapperList() {
        return getEnabledMetricMonitorWrapperList(doubleRepository);
    }

    private static <V extends Number> List<CustomMetricMonitorWrapper<V>> getEnabledMetricMonitorWrapperList(List<CustomMetricMonitorWrapper<V>> repository) {
        List<CustomMetricMonitorWrapper<V>> metricMonitorList = new ArrayList<CustomMetricMonitorWrapper<V>>(repository.size());
        List<CustomMetricMonitorWrapper<V>> disabledMetricMonitorList = new ArrayList<CustomMetricMonitorWrapper<V>>();

        for (CustomMetricMonitorWrapper<V> customMetricMonitorWrapper : repository) {
            if (customMetricMonitorWrapper.isDisabled()) {
                disabledMetricMonitorList.add(customMetricMonitorWrapper);
            } else {
                metricMonitorList.add(customMetricMonitorWrapper);
            }
        }

        // bulk delete for reduce copy
        if (disabledMetricMonitorList.size() > 0) {
            LOGGER.info("CustomMetricMonitorWrapper was disabled(list:{})", disabledMetricMonitorList);
            repository.removeAll(disabledMetricMonitorList);
        }

        return null;
    }

    @Override
    public int getRemainingIdNumber() {
        return limitIdNumber - idGenerator.get();
    }

}
