package com.navercorp.pinpoint.profiler.monitor.metric.custom;

import com.navercorp.pinpoint.common.util.Assert;

public class CustomMetricVoListHolder<T> {

    private final T firstCustomMetricTy
    private final T[] customMetricVos;

    public CustomMetricVoListHolder(T[] customMetricVoList) {
        this.customMetricVos = Assert.requireNonNull(customMetricVoList, "customMetricVoList");
    }


}
