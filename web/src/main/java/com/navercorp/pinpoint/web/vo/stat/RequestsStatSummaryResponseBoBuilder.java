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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Taejin Koo
 */
public class RequestsStatSummaryResponseBoBuilder {

    private Map<String, RequestsStaticsBuilder> urlStatisticsDataBuilderMap = new HashMap<>();

    public void add(String url, int status, int count, long avgTimes, long maxTime) {
        RequestsStaticsBuilder requestsStaticsBuilder = urlStatisticsDataBuilderMap.get(url);
        if (requestsStaticsBuilder == null) {
            requestsStaticsBuilder = new RequestsStaticsBuilder(url);
            urlStatisticsDataBuilderMap.put(url, requestsStaticsBuilder);
        }

        requestsStaticsBuilder.add(status, count, avgTimes, maxTime);
    }

    public RequestsStatSummaryResponseBo build() {
        List<RequestsStatSummaryResponseBo.RequestsStatics> requestsStaticsList = new ArrayList<>();
        Collection<RequestsStaticsBuilder> values = urlStatisticsDataBuilderMap.values();
        for (RequestsStaticsBuilder value : values) {
            RequestsStatSummaryResponseBo.RequestsStatics requestsStatics = value.build();
            requestsStaticsList.add(requestsStatics);
        }

        return new RequestsStatSummaryResponseBo(requestsStaticsList);
    }

    class RequestsStaticsBuilder {

        private final String url;
        private StatisticsDataBuilder statisticsDataBuilder = new StatisticsDataBuilder();
        private Map<Integer, StatusStatisticsBuilder> statusStatisticsBuilderMap = new HashMap<>();

        public RequestsStaticsBuilder(String url) {
            this.url = url;
        }

        private void add(int status, int count, long avgTimes, long maxTime) {
            StatusStatisticsBuilder statusStatisticsBuilder = statusStatisticsBuilderMap.get(status);
            if (statusStatisticsBuilder == null) {
                statusStatisticsBuilder = new StatusStatisticsBuilder(status);
                statusStatisticsBuilderMap.put(status, statusStatisticsBuilder);
            }

            statisticsDataBuilder.add(count, avgTimes, maxTime);
            statusStatisticsBuilder.add(count, avgTimes, maxTime);
        }

        private RequestsStatSummaryResponseBo.RequestsStatics build() {

            RequestsStatSummaryResponseBo.StatisticsData statisticsData = statisticsDataBuilder.build();

            List<RequestsStatSummaryResponseBo.StatusStatistics> result = new ArrayList<>();
            Collection<StatusStatisticsBuilder> statusStatisticsBuilderCollection = statusStatisticsBuilderMap.values();
            for (StatusStatisticsBuilder builder : statusStatisticsBuilderCollection) {
                RequestsStatSummaryResponseBo.StatusStatistics statusStatistics = builder.build();
                result.add(statusStatistics);
            }

            return new RequestsStatSummaryResponseBo.RequestsStatics(url, statisticsData, result);
        }

    }

    class StatusStatisticsBuilder {

        private final int status;
        private StatisticsDataBuilder statisticsDataBuilder = new StatisticsDataBuilder();

        public StatusStatisticsBuilder(int status) {
            this.status = status;
        }

        private void add(int count, long avgTimes, long maxTime) {
            statisticsDataBuilder.add(count, avgTimes, maxTime);
        }

        private RequestsStatSummaryResponseBo.StatusStatistics build() {
            RequestsStatSummaryResponseBo.StatisticsData statisticsData = statisticsDataBuilder.build();
            return new RequestsStatSummaryResponseBo.StatusStatistics(status, statisticsData);
        }

    }

    class StatisticsDataBuilder {

        private int totalCount;
        private long totalTimes = 0;
        private long maxTime;

        private void add(int count, long avgTimes, long maxTime) {
            this.totalCount += count;

            long totalTimes = count * avgTimes;
            this.totalTimes += totalTimes;

            if (maxTime > this.maxTime) {
                this.maxTime = maxTime;
            }
        }

        private RequestsStatSummaryResponseBo.StatisticsData build() {
            long avgTimes = totalTimes / totalCount;
            return new RequestsStatSummaryResponseBo.StatisticsData(totalCount, avgTimes, maxTime);
        }
    }


}
