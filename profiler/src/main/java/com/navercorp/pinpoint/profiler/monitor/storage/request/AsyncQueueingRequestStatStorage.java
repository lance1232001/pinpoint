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

package com.navercorp.pinpoint.profiler.monitor.storage.request;

import com.navercorp.pinpoint.common.profiler.concurrent.PinpointThreadFactory;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.grpc.trace.PAgentRequestsStatBatch;
import com.navercorp.pinpoint.profiler.monitor.storage.AgentStatStorage;
import com.navercorp.pinpoint.profiler.monitor.vo.RequestsStatInfo;
import com.navercorp.pinpoint.profiler.sender.DataSender;
import com.navercorp.pinpoint.profiler.sender.UnsafeArrayCollection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Taejin Koo
 */
public class AsyncQueueingRequestStatStorage implements Runnable, AgentStatStorage<RequestsStatInfo>, Closeable {

    private static final long DEFAULT_FLUSH_INTERVAL_MS = 1000 * 60;
    private static final int DEFAULT_MAX_WATER_MARK_SIZE = 65535;

    private final Logger logger;
    private final boolean isWarn;

    private final long flushIntervalMs;


    private final LinkedBlockingQueue<RequestsStatInfo> queue;
    private final AtomicBoolean isRun = new AtomicBoolean(true);
    private final Thread executeThread;
    private final String executorName;

    private final int maxDrainSize;
    // Caution. single thread only. this Collection is simpler than ArrayList.
    private final Collection<RequestsStatInfo> drain;

    private RequestsStatInnerStorage requestsStatStorage = new RequestsStatInnerStorage(DEFAULT_MAX_WATER_MARK_SIZE);

    private final DataSender dataSender;

    private long lastFlushedTime = System.currentTimeMillis();

    public AsyncQueueingRequestStatStorage(DataSender dataSender, int queueSize, String executorName) {
        Assert.requireNonNull(executorName, "executorName");

        this.dataSender = Assert.requireNonNull(dataSender, "dataSender");

        this.logger = LoggerFactory.getLogger(this.getClass().getName() + "@" + executorName);
        this.isWarn = logger.isWarnEnabled();

        // BEFORE executeThread start
        this.maxDrainSize = 10;
        this.drain = new UnsafeArrayCollection<RequestsStatInfo>(maxDrainSize);
        this.queue = new LinkedBlockingQueue<RequestsStatInfo>(queueSize);

        this.executeThread = this.createExecuteThread(executorName);
        this.executorName = executeThread.getName();

        this.flushIntervalMs = DEFAULT_FLUSH_INTERVAL_MS;
    }

    private Thread createExecuteThread(String executorName) {
        final ThreadFactory threadFactory = new PinpointThreadFactory(executorName, true);
        Thread thread = threadFactory.newThread(this);
        thread.start();
        return thread;
    }

    @Override
    public void run() {
        logger.info("{} started.", executorName);
        doExecute();
    }

    private void doExecute() {
        drainStartEntry:
        while (isRun()) {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("execute again()");
                }
                final Collection<RequestsStatInfo> dtoList = getDrainQueue();
                final int drainSize = takeN(dtoList, this.maxDrainSize);
                if (drainSize > 0) {
                    store0(dtoList);
                    continue;
                }

                while (isRun()) {
                    final RequestsStatInfo dto = takeOne();
                    if (dto != null) {
                        store0(dto);
                        continue drainStartEntry;
                    } else {
                        flush();
                    }
                }
            } catch (Throwable th) {
                logger.warn("{} doExecute(). Unexpected Error. Cause:{}", executorName, th.getMessage(), th);
            }
        }
        flushQueue();
    }

    private void store0(Collection<RequestsStatInfo> dtoList) {
        Object[] requestsStatInfos = dtoList.toArray();

        for (Object requestsStatInfo : requestsStatInfos) {
            requestsStatStorage.store((RequestsStatInfo) requestsStatInfo);
        }

        if (requestsStatStorage.needsFlush()) {
            PAgentRequestsStatBatch requestsStatBatch = requestsStatStorage.createAndClear();
            send(requestsStatBatch);
        }
    }

    private void store0(RequestsStatInfo requestsStatInfo) {
        requestsStatStorage.store(requestsStatInfo);
        if (requestsStatStorage.needsFlush()) {
            PAgentRequestsStatBatch requestsStatBatch = requestsStatStorage.createAndClear();
            send(requestsStatBatch);
        }
    }

    private void flush() {
        boolean hasData = requestsStatStorage.hasData();
        if (hasData) {
            PAgentRequestsStatBatch requestsStatBatch = requestsStatStorage.createAndClear();
            send(requestsStatBatch);
        }
        this.lastFlushedTime = System.currentTimeMillis();
    }

    private void send(PAgentRequestsStatBatch requestsStatBatch) {
        logger.debug("send. data:{}", requestsStatBatch);
        dataSender.send(requestsStatBatch);
        this.lastFlushedTime = System.currentTimeMillis();
    }

    private RequestsStatInfo takeOne() {
        try {
            long waitTime = getWaitTime();
            if (waitTime > 0) {
                return queue.poll(getWaitTime(), TimeUnit.MILLISECONDS);
            }
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    private long getWaitTime() {
        long waitTime = lastFlushedTime + flushIntervalMs - System.currentTimeMillis();
        if (waitTime > 0) {
            return waitTime;
        } else {
            return 0;
        }
    }

    private int takeN(Collection<RequestsStatInfo> drain, int maxDrainSize) {
        return queue.drainTo(drain, maxDrainSize);
    }

    private void flushQueue() {
        boolean debugEnabled = logger.isDebugEnabled();
        if (debugEnabled) {
            logger.debug("Loop is stop.");
        }
        while (true) {
            final Collection<RequestsStatInfo> dtoList = getDrainQueue();
            int drainSize = takeN(dtoList, this.maxDrainSize);
            if (drainSize == 0) {
                break;
            }
            if (debugEnabled) {
                logger.debug("flushData size {}", drainSize);
            }
            store0(dtoList);
        }
    }

    @Override
    public boolean store(RequestsStatInfo requestsStatInfo) {
        if (requestsStatInfo == null) {
            if (isWarn) {
                logger.warn("execute(). requestsStatInfo is null");
            }
            return false;
        }
        if (!isRun.get()) {
            if (isWarn) {
                logger.warn("{} is shutdown. discard data:{}", executorName, requestsStatInfo);
            }
            return false;
        }
        boolean offer = queue.offer(requestsStatInfo);
        if (!offer) {
            if (isWarn) {
                logger.warn("{} Drop data. queue is full. size:{}", executorName, queue.size());
            }
        }
        return offer;
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

    public boolean isRun() {
        return isRun.get();
    }

    @Override
    public void close() {
        isRun.set(false);

        if (!isEmpty()) {
            logger.info("Wait 5 seconds. Flushing queued data.");
        }
        executeThread.interrupt();
        try {
            executeThread.join(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.warn("{} stopped incompletely.", executorName);
        }

        logger.info("{} stopped.", executorName);
    }

    Collection<RequestsStatInfo> getDrainQueue() {
        this.drain.clear();
        return drain;
    }

}
