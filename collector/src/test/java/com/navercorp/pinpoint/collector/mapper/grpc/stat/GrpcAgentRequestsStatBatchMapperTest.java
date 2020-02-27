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

package com.navercorp.pinpoint.collector.mapper.grpc.stat;

import com.navercorp.pinpoint.bootstrap.util.jdk.ThreadLocalRandom;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatBo;
import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.StartAndElapsedTime;
import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.server.ServerContext;
import com.navercorp.pinpoint.grpc.trace.PAgentRequestsStatBatch;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatData;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatUrlMetadata;

import io.grpc.Context;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;

/**
 * @author Taejin Koo
 */
public class GrpcAgentRequestsStatBatchMapperTest {

    private int urlMetadataIndex = 0;

    @Test
    public void mapTest() {
        final Context currentContext = Context.current();
        Header header = new Header("agent", "application", System.currentTimeMillis(), 2);

        Context newContext = currentContext.withValue(ServerContext.getAgentInfoKey(), header);
        newContext.attach();

        assertEmpty();

        PAgentRequestsStatBatch.Builder batchBuilder = PAgentRequestsStatBatch.newBuilder();
        batchBuilder.addRequestsStatUrlMetadata(createMetadata("/index.html"));
        batchBuilder.addRequestsStatUrlMetadata(createMetadata("/index.jsp"));
        batchBuilder.addRequestsStatUrlMetadata(createMetadata("/main.pinpoint"));

        int testCount = 31;
        for (int i = 0; i < testCount; i++) {
            batchBuilder.addRequestsStatData(createRequestsStatData());
        }

        GrpcAgentRequestsStatBatchMapper grpcAgentRequestsStatBatchMapper = new GrpcAgentRequestsStatBatchMapper();
        AgentRequestsStatBo requestsStatBo = grpcAgentRequestsStatBatchMapper.map(batchBuilder.build());

        Assert.assertEquals(testCount, getRequestCount(requestsStatBo));
        assertTimestmap(requestsStatBo);
    }

    private void assertEmpty() {
        PAgentRequestsStatBatch emptyData = PAgentRequestsStatBatch.newBuilder().build();

        GrpcAgentRequestsStatBatchMapper grpcAgentRequestsStatBatchMapper = new GrpcAgentRequestsStatBatchMapper();
        AgentRequestsStatBo expectedNull = grpcAgentRequestsStatBatchMapper.map(emptyData);
        Assert.assertNull(expectedNull);
    }

    private PRequestsStatUrlMetadata createMetadata(String url) {
        PRequestsStatUrlMetadata.Builder builder = PRequestsStatUrlMetadata.newBuilder();
        builder.setId(++urlMetadataIndex).setUrl(url);
        return builder.build();
    }

    private PRequestsStatData createRequestsStatData() {
        PRequestsStatData.Builder statDataBuilder = PRequestsStatData.newBuilder();
        int index = ThreadLocalRandom.current().nextInt(1, urlMetadataIndex + 1);

        int elapsedTime = ThreadLocalRandom.current().nextInt(3000);
        statDataBuilder.setUrlId(index).setStatus(200).setStartTime(System.currentTimeMillis()).setElapsedTime(elapsedTime);
        return statDataBuilder.build();
    }

    private int getRequestCount(AgentRequestsStatBo data) {
        int requestCount = 0;

        Collection<String> urlList = data.getUrlList();
        for (String url : urlList) {
            AgentRequestsStatDataPoint requestsStatDataPoint = data.getAgentRequestsStatDataPointList(url);
            Collection<Integer> statuses = requestsStatDataPoint.getStatuses();
            for (Integer status : statuses) {
                Collection<StartAndElapsedTime> requestList = requestsStatDataPoint.getRequestList(status);
                requestCount += requestList.size();
            }
        }
        return requestCount;
    }

    private void assertTimestmap(AgentRequestsStatBo data) {
        long fastestEndTime = data.getTimestamp();

        Collection<String> urlList = data.getUrlList();
        for (String url : urlList) {
            AgentRequestsStatDataPoint requestsStatDataPoint = data.getAgentRequestsStatDataPointList(url);
            Collection<Integer> statuses = requestsStatDataPoint.getStatuses();
            for (Integer status : statuses) {
                Collection<StartAndElapsedTime> requestList = requestsStatDataPoint.getRequestList(status);
                for (StartAndElapsedTime startAndElapsedTime : requestList) {
                    long endTime = startAndElapsedTime.getStartTime() + startAndElapsedTime.getElapsedTime();
                    if (fastestEndTime > endTime) {
                        Assert.fail();
                    }
                }
            }
        }
    }

}
