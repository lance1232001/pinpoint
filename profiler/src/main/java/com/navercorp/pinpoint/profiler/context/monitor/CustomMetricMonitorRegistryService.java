package com.navercorp.pinpoint.profiler.context.monitor;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.CustomMetricMonitor;

import java.util.List;

public interface CustomMetricMonitorRegistryService {

    boolean registerIntMetric(CustomMetricMonitor<Integer> customMetricMonitor);

    boolean registerLongMetric(CustomMetricMonitor<Long> customMetricMonitor);

    boolean registerDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor);


    boolean unregisterIntMetric(CustomMetricMonitor<Integer> customMetricMonitor);

    boolean unregisterLongMetric(CustomMetricMonitor<Long> customMetricMonitor);

    boolean unregisterDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor);


    List<CustomMetricMonitorWrapper<Integer>> getIntMetricMonitorWrapperList();

    List<CustomMetricMonitorWrapper<Long>> getLongMetricMonitorList();

    List<CustomMetricMonitorWrapper<Double>> getDoubleMetricMonitorWrapperList();


    int getRemainingIdNumber();

}
