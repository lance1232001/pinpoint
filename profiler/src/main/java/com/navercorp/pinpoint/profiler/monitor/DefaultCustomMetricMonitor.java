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

package com.navercorp.pinpoint.profiler.monitor;

import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.common.profiler.concurrent.PinpointThreadFactory;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.monitor.metric.CustomMetricRegistryService;
import com.navercorp.pinpoint.profiler.monitor.collector.AgentCustomMetricCollector;
import com.navercorp.pinpoint.profiler.sender.DataSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author Taejin Koo
 */
public class DefaultCustomMetricMonitor implements CustomMetricMonitor {

    private static final long MIN_COLLECTION_INTERVAL_MS = 1000;
    private static final long MAX_COLLECTION_INTERVAL_MS = 1000 * 5;
    private static final long DEFAULT_COLLECTION_INTERVAL_MS = DefaultProfilerConfig.DEFAULT_AGENT_STAT_COLLECTION_INTERVAL_MS;
    private static final int DEFAULT_NUM_COLLECTIONS_PER_SEND = DefaultProfilerConfig.DEFAULT_NUM_AGENT_STAT_BATCH_SEND;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final long collectionIntervalMs;

    private final ScheduledExecutorService executor = new ScheduledThreadPoolExecutor(1, new PinpointThreadFactory("Pinpoint-stat-monitor", true));

    private final CustomMetricCollectingJob customMetricCollectJob;

    public DefaultCustomMetricMonitor(DataSender dataSender,
                                      CustomMetricRegistryService customMetricRegistryService,
                                      ProfilerConfig profilerConfig) {
        this(dataSender, customMetricRegistryService, profilerConfig.getProfileJvmStatCollectIntervalMs(), profilerConfig.getProfileJvmStatBatchSendCount());
    }

    public DefaultCustomMetricMonitor(DataSender dataSender,
                                      CustomMetricRegistryService customMetricRegistryService,
                                      long collectionIntervalMs, int numCollectionsPerBatch) {
        Assert.requireNonNull(dataSender, "dataSender");
        Assert.requireNonNull(customMetricRegistryService, "customMetricRegistryService");

        if (collectionIntervalMs < MIN_COLLECTION_INTERVAL_MS) {
            collectionIntervalMs = DEFAULT_COLLECTION_INTERVAL_MS;
        }
        if (collectionIntervalMs > MAX_COLLECTION_INTERVAL_MS) {
            collectionIntervalMs = DEFAULT_COLLECTION_INTERVAL_MS;
        }
        if (numCollectionsPerBatch < 1) {
            numCollectionsPerBatch = DEFAULT_NUM_COLLECTIONS_PER_SEND;
        }
        this.collectionIntervalMs = collectionIntervalMs;
        this.customMetricCollectJob = new CustomMetricCollectingJob(dataSender, new AgentCustomMetricCollector(customMetricRegistryService), numCollectionsPerBatch);
    }

    @Override
    public void start() {
        executor.scheduleAtFixedRate(customMetricCollectJob, this.collectionIntervalMs, this.collectionIntervalMs, TimeUnit.MILLISECONDS);
        logger.info("AgentStat monitor started");
    }

    @Override
    public void stop() {
        executor.shutdown();
        try {
            executor.awaitTermination(3000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        logger.info("AgentStat monitor stopped");
    }

}
