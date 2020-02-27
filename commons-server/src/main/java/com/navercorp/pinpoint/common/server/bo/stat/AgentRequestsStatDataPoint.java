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

package com.navercorp.pinpoint.common.server.bo.stat;

import com.navercorp.pinpoint.common.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Taejin Koo
 */
public class AgentRequestsStatDataPoint {

    private final String url;
    private final Map<Integer, Collection<StartAndElapsedTime>> statusMap = new HashMap<>();

    public AgentRequestsStatDataPoint(String url) {
        this.url = Objects.requireNonNull(url, "url");
    }

    public String getUrl() {
        return url;
    }

    public boolean addEachRequest(int status, long startTimestamp, long elapsedTime) {
        Collection<StartAndElapsedTime> startAndElapsedTimeList = statusMap.get(status);
        if (startAndElapsedTimeList == null) {
            startAndElapsedTimeList = new ArrayList<>();
            statusMap.put(status, startAndElapsedTimeList);
        }

        StartAndElapsedTime startAndElapsedTime = new StartAndElapsedTime(startTimestamp, elapsedTime);
        startAndElapsedTimeList.add(startAndElapsedTime);

        return true;
    }

    public Collection<Integer> getStatuses() {
        if (statusMap.size() == 0) {
            return Collections.emptyList();
        } else {
            return statusMap.keySet();
        }
    }

    public Collection<StartAndElapsedTime> getRequestList(int status) {
        if (!statusMap.containsKey(status)) {
            return Collections.emptyList();
        }

        Collection<StartAndElapsedTime> startAndElapsedTimeList = statusMap.get(status);
        if (startAndElapsedTimeList == null) {
            return Collections.emptyList();
        } else {
            return startAndElapsedTimeList;
        }
    }

    public Collection<RequestsStatSummaryBo> getAgentRequestsStatSummaryDataList(String agentId, long startTimestamp) {
        List<RequestsStatSummaryBo> result = new ArrayList<>();

        Set<Map.Entry<Integer, Collection<StartAndElapsedTime>>> entrySet = statusMap.entrySet();
        for (Map.Entry<Integer, Collection<StartAndElapsedTime>> entry : entrySet) {
            Integer status = entry.getKey();
            Collection<StartAndElapsedTime> timeCollection = entry.getValue();

            if (CollectionUtils.isEmpty(timeCollection)) {
                continue;
            }

            long totalTime = 0;
            long maxTime = 0;
            long fastestEndTime = System.currentTimeMillis();
            for (StartAndElapsedTime time : timeCollection) {
                long elapsedTime = time.getElapsedTime();
                long endTime = time.getStartTime() + elapsedTime;
                totalTime += elapsedTime;
                if (elapsedTime > maxTime) {
                    maxTime = elapsedTime;
                }
                if (fastestEndTime > endTime) {
                    fastestEndTime = endTime;
                }
            }

            RequestsStatSummaryBo agentRequestsStatSummaryData = new RequestsStatSummaryBo(url, status, timeCollection.size(), totalTime / timeCollection.size(), maxTime);
            agentRequestsStatSummaryData.setAgentId(agentId);
            agentRequestsStatSummaryData.setStartTimestamp(startTimestamp);
            agentRequestsStatSummaryData.setTimestamp(fastestEndTime);

            result.add(agentRequestsStatSummaryData);
        }

        return result;
    }


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AgentRequestsStatDataPoint{");
        sb.append("url='").append(url).append('\'');
        sb.append(", statusMap=").append(statusMap);
        sb.append('}');
        return sb.toString();
    }


}
