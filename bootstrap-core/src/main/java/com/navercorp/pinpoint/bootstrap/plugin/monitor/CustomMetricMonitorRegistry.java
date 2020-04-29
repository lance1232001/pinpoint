package com.navercorp.pinpoint.bootstrap.plugin.monitor;

public interface CustomMetricMonitorRegistry {

    boolean registerIntMetric(CustomMetricMonitor<Integer> customMetricMonitor);

    boolean registerLongMetric(CustomMetricMonitor<Long> customMetricMonitor);

    boolean registerDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor);


    boolean unregisterIntMetric(CustomMetricMonitor<Integer> customMetricMonitor);

    boolean unregisterLongMetric(CustomMetricMonitor<Long> customMetricMonitor);

    boolean unregisterDoubleMetric(CustomMetricMonitor<Double> customMetricMonitor);

}
