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

package com.navercorp.pinpoint.web.vo.stat;

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.common.server.bo.stat.AgentStatType;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.io.IOException;
import java.util.List;

/**
 * @author Taejin Koo
 */
public class RequestsStatSummaryResponseBo implements AgentStatDataPoint {

    private String agentId;
    private long startTimestamp;
    private long timestamp;

    private final List<RequestsStatics> urlStatisticsList;

    public RequestsStatSummaryResponseBo(List<RequestsStatics> urlStatisticsList) {
        this.urlStatisticsList = urlStatisticsList;
    }

    //    private List<UrlStatistics> urlStatistics = new ArrayList<>();

    public static class RequestsStaticsSerializer extends JsonSerializer<RequestsStatics> {

        @Override
        public void serialize(RequestsStatics value, JsonGenerator jgen, SerializerProvider serializers) throws IOException {
            jgen.writeStartObject();

            jgen.writeStringField("url", value.url);

            StatisticsData statisticsData = value.statisticsData;

            jgen.writeNumberField("count", statisticsData.count);
            jgen.writeNumberField("avgTime", statisticsData.avgTimes);
            jgen.writeNumberField("maxTime", statisticsData.maxTime);

            jgen.writeArrayFieldStart("status");
            for (StatusStatistics statusStatistics : value.statusStatisticsList) {
                jgen.writeStartObject();

                jgen.writeNumberField("status", statusStatistics.status);

                jgen.writeNumberField("count", statusStatistics.statisticsData.count);
                jgen.writeNumberField("avgTime", statusStatistics.statisticsData.avgTimes);
                jgen.writeNumberField("maxTime", statusStatistics.statisticsData.maxTime);

                jgen.writeEndObject();
            }
            jgen.writeEndArray();

            jgen.writeEndObject();
        }


    }


    @JsonSerialize(using = RequestsStaticsSerializer.class)
    static class RequestsStatics {
        private final String url;
        private final StatisticsData statisticsData;
        private final List<StatusStatistics> statusStatisticsList;

        public RequestsStatics(String url, StatisticsData statisticsData, List<StatusStatistics> statusStatisticsList) {
            this.url = url;
            this.statisticsData = statisticsData;
            this.statusStatisticsList = statusStatisticsList;
        }

    }

    static class StatusStatistics {

        private final int status;
        private final StatisticsData statisticsData;

        public StatusStatistics(int status, StatisticsData statisticsData) {
            this.status = status;
            this.statisticsData = statisticsData;
        }
    }


    static class StatisticsData {
        private final int count;
        private final long avgTimes;
        private final long maxTime;

        StatisticsData(int count, long avgTimes, long maxTime) {
            this.count = count;
            this.avgTimes = avgTimes;
            this.maxTime = maxTime;
        }
    }

    public List<RequestsStatics> getUrlStatisticsList() {
        return urlStatisticsList;
    }

    @Override
    public String getAgentId() {
        return agentId;
    }

    @Override
    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    @Override
    public long getStartTimestamp() {
        return startTimestamp;
    }

    @Override
    public void setStartTimestamp(long startTimestamp) {
        this.startTimestamp = startTimestamp;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public AgentStatType getAgentStatType() {
        return AgentStatType.REQUESTS_SUMMARY;
    }


}

