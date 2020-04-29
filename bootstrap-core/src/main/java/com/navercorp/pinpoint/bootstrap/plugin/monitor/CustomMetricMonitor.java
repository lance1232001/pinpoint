package com.navercorp.pinpoint.bootstrap.plugin.monitor;

public interface CustomMetricMonitor<V extends Number> {

    String getName();

    V getValue();

    boolean isDisabled();

}
