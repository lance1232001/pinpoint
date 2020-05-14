package com.navercorp.pinpoint.profiler.monitor.collector;

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.monitor.metric.CustomMetricRegistryService;
import com.navercorp.pinpoint.profiler.context.monitor.metric.DoubleGaugeMetricWrapper;
import com.navercorp.pinpoint.profiler.context.monitor.metric.IntCountMetricWrapper;
import com.navercorp.pinpoint.profiler.context.monitor.metric.IntGaugeMetricWrapper;
import com.navercorp.pinpoint.profiler.context.monitor.metric.LongCountMetricWrapper;
import com.navercorp.pinpoint.profiler.context.monitor.metric.LongGaugeMetricWrapper;
import com.navercorp.pinpoint.profiler.monitor.metric.AgentCustomMetricSnapshot;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.CustomMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.DoubleGaugeMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntCountMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.IntGaugeMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.LongCountMetricVo;
import com.navercorp.pinpoint.profiler.monitor.metric.custom.LongGaugeMetricVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
public class AgentCustomMetricCollector {

    private final CustomMetricRegistryService customMetricRegistryService;

    public AgentCustomMetricCollector(CustomMetricRegistryService customMetricRegistryService) {
        this.customMetricRegistryService = Assert.requireNonNull(customMetricRegistryService, "customMetricRegistryService");
    }

    public AgentCustomMetricSnapshot collect() {
        List<CustomMetricVo> customMetricVoList = new ArrayList<CustomMetricVo>();

        addIntCountMetric(customMetricVoList);
        addLongCountMetric(customMetricVoList);

        addIntGaugeMetric(customMetricVoList);
        addLongGaugeMetric(customMetricVoList);
        addDoubleGaugeMetric(customMetricVoList);

        if (customMetricVoList.isEmpty()) {
            return null;
        }

        return new AgentCustomMetricSnapshot(customMetricVoList);
    }

    private void addIntCountMetric(List<CustomMetricVo> customMetricVoList) {
        List<IntCountMetricWrapper> intCountMetricWrapperList = customMetricRegistryService.getIntCountMetricWrapperList();
        for (IntCountMetricWrapper intCountMetricWrapper : intCountMetricWrapperList) {
            customMetricVoList.add(new IntCountMetricVo(intCountMetricWrapper));
        }
    }

    private void addLongCountMetric(List<CustomMetricVo> customMetricVoList) {
        List<LongCountMetricWrapper> longCountMetricWrapperList = customMetricRegistryService.getLongCountMetricWrapperList();
        for (LongCountMetricWrapper longCountMetricWrapper : longCountMetricWrapperList) {
            customMetricVoList.add(new LongCountMetricVo(longCountMetricWrapper));
        }
    }

    private void addIntGaugeMetric(List<CustomMetricVo> customMetricVoList) {
        List<IntGaugeMetricWrapper> intGaugeMetricWrapperList = customMetricRegistryService.getIntGaugeMetricWrapperList();
        for (IntGaugeMetricWrapper intGaugeMetricWrapper : intGaugeMetricWrapperList) {
            customMetricVoList.add(new IntGaugeMetricVo(intGaugeMetricWrapper));
        }
    }

    private void addLongGaugeMetric(List<CustomMetricVo> customMetricVoList) {
        List<LongGaugeMetricWrapper> longGaugeMetricWrapperList = customMetricRegistryService.getLongGaugeMetricWrapperList();
        for (LongGaugeMetricWrapper longGaugeMetricWrapper : longGaugeMetricWrapperList) {
            customMetricVoList.add(new LongGaugeMetricVo(longGaugeMetricWrapper));
        }
    }

    private void addDoubleGaugeMetric(List<CustomMetricVo> customMetricVoList) {
        List<DoubleGaugeMetricWrapper> doubleGaugeMetricWrapperList = customMetricRegistryService.getDoubleGaugeMetricWrapperList();
        for (DoubleGaugeMetricWrapper doubleGaugeMetricWrapper : doubleGaugeMetricWrapperList) {
            customMetricVoList.add(new DoubleGaugeMetricVo(doubleGaugeMetricWrapper));
        }
    }

}
