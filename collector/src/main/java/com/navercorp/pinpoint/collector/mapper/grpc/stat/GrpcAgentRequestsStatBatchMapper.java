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

import com.navercorp.pinpoint.common.server.bo.stat.AgentRequestsStatBo;
import com.navercorp.pinpoint.grpc.Header;
import com.navercorp.pinpoint.grpc.server.ServerContext;
import com.navercorp.pinpoint.grpc.trace.PAgentRequestsStatBatch;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatData;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatUrlMetadata;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Taejin Koo
 */
@Component
public class GrpcAgentRequestsStatBatchMapper {

    public AgentRequestsStatBo map(final PAgentRequestsStatBatch agentRequestsStatBatch) {
        List<PRequestsStatData> requestsStatDataList = agentRequestsStatBatch.getRequestsStatDataList();
        if (requestsStatDataList.size() == 0) {
            return null;
        }

        final Header agentInfo = ServerContext.getAgentInfo();
        final String agentId = agentInfo.getAgentId();
        final long startTimestamp = agentInfo.getAgentStartTime();

        Map<Integer, String> urlMetadata = createUrlMetadata(agentRequestsStatBatch);

        AgentRequestsStatBo agentRequestsStatBo = new AgentRequestsStatBo();
        agentRequestsStatBo.setAgentId(agentId);
        agentRequestsStatBo.setStartTimestamp(startTimestamp);

        long fastestEndTime = Long.MAX_VALUE;
        for (PRequestsStatData pRequestsStatData : requestsStatDataList) {
            int urlId = pRequestsStatData.getUrlId();
            String url = urlMetadata.get(urlId);
            agentRequestsStatBo.put(url, pRequestsStatData.getStatus(), pRequestsStatData.getStartTime(), pRequestsStatData.getElapsedTime());

            fastestEndTime = getFasterEndTime(fastestEndTime, pRequestsStatData);
        }
        agentRequestsStatBo.setTimestamp(fastestEndTime);

        return agentRequestsStatBo;
    }

    private Map<Integer, String> createUrlMetadata(PAgentRequestsStatBatch agentRequestsStatBatch) {
        Map<Integer, String> result = new HashMap<>();

        List<PRequestsStatUrlMetadata> urlMetadataList = agentRequestsStatBatch.getRequestsStatUrlMetadataList();
        for (PRequestsStatUrlMetadata urlMetadata : urlMetadataList) {
            result.put(urlMetadata.getId(), urlMetadata.getUrl());
        }

        return result;
    }

    private long getFasterEndTime(long currentFastestEndTime, PRequestsStatData pRequestsStatData) {
        long requestEndTime = pRequestsStatData.getStartTime() + pRequestsStatData.getElapsedTime();

        if (requestEndTime < currentFastestEndTime) {
            return requestEndTime;
        } else {
            return currentFastestEndTime;
        }
    }

}
