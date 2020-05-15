package com.navercorp.pinpoint.profiler.monitor.collector;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.monitor.metric.CustomMetricRegistryService;
import com.navercorp.pinpoint.profiler.context.monitor.metric.CustomMetricWrapper;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;

import java.util.Map;

/**
 * @author Taejin Koo
 */
public class AgentCustomMetricCollector {

    private final CustomMetricRegistryService customMetricRegistryService;

    public AgentCustomMetricCollector(CustomMetricRegistryService customMetricRegistryService) {
        this.customMetricRegistryService = Assert.requireNonNull(customMetricRegistryService, "customMetricRegistryService");
    }

    public AgentCustomMetricSnapshot collect() {
        Map<String, CustomMetricWrapper> customMetricMap = customMetricRegistryService.getCustomMetricMap();
        int size = customMetricMap.size();
        if (size == 0) {
            return null;
        }

        AgentCustomMetricSnapshot agentCustomMetricSnapshot = new AgentCustomMetricSnapshot(size);
        for (CustomMetricWrapper metricWrapper : customMetricMap.values()) {
            agentCustomMetricSnapshot.add(metricWrapper.snapshot());
        }

        return agentCustomMetricSnapshot;
    }

}
