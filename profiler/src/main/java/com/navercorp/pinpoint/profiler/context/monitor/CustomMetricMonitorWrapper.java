package com.navercorp.pinpoint.profiler.context.monitor;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.CustomMetricMonitor;
import com.navercorp.pinpoint.common.util.Assert;

import java.lang.ref.WeakReference;

public class CustomMetricMonitorWrapper<V extends Number> implements PluginMonitorWrapper, CustomMetricMonitor<V> {

    private final int id;
    private final WeakReference<CustomMetricMonitor<V>> monitorReference;

    public CustomMetricMonitorWrapper(int id, CustomMetricMonitor<V> customMetricMonitor) {
        this.id = id;
        Assert.requireNonNull(customMetricMonitor, "customMetricMonitor");
        this.monitorReference = new WeakReference<CustomMetricMonitor<V>>(customMetricMonitor);

    }

    private CustomMetricMonitor<V> getInstance() {
        return monitorReference.get();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        CustomMetricMonitor<V> instance = getInstance();
        if (instance == null) {
            return null;
        }
        return instance.getName();
    }

    @Override
    public V getValue() {
        CustomMetricMonitor<V> instance = getInstance();
        if (instance == null) {
            return null;
        }

        return instance.getValue();
    }

    @Override
    public boolean isDisabled() {
        CustomMetricMonitor<V> instance = getInstance();
        if (instance == null) {
            return true;
        }

        return instance.isDisabled();
    }

    @Override
    public boolean equalsWithUnwrap(Object object) {
        if (object == null) {
            return false;
        }

        CustomMetricMonitor instance = getInstance();
        if (instance == null) {
            return false;
        }

        return instance == object;
    }

    @Override
    public String toString() {
        return "CustomMetricMonitorWrapper{" +
            "id=" + id +
            '}';
    }

}
