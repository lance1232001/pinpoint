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

import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.grpc.trace.PAgentRequestsStatBatch;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatData;
import com.navercorp.pinpoint.grpc.trace.PRequestsStatUrlMetadata;
import com.navercorp.pinpoint.profiler.monitor.vo.RequestsStatInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Taejin Koo
 */
public class RequestsStatInnerStorage {

    private final Map<String, Integer> urlMetadata = new HashMap<String, Integer>();
    private final List<PRequestsStatData> requestsStatDataList = new ArrayList<PRequestsStatData>();

    private final int maxWaterMarkSize;

    private int currentDataSize = 0;

    public RequestsStatInnerStorage(int maxWaterMarkSize) {
        Assert.isTrue(maxWaterMarkSize > 0, "'maxWaterMarkSize' must be > 0");
        this.maxWaterMarkSize = maxWaterMarkSize;
    }

    public void store(RequestsStatInfo requestsStatInfo) {
        String url = requestsStatInfo.getUrl();
        Integer urlId = urlMetadata.get(url);

        if (urlId == null) {
            urlId = urlMetadata.size() + 1;
            urlMetadata.put(url, urlId);
            currentDataSize += url.length() + 4;
        }

        PRequestsStatData.Builder builder = PRequestsStatData.newBuilder();
        builder.setUrlId(urlId);
        builder.setStatus(requestsStatInfo.getStatus());
        builder.setStartTime(requestsStatInfo.getStartTime());
        builder.setElapsedTime(requestsStatInfo.getElapsedTime());

        PRequestsStatData requestsStatData = builder.build();
        requestsStatDataList.add(requestsStatData);
        currentDataSize += RequestsStatInfo.DEFAULT_DATA_SIZE_WITHOUT_URL;
    }

    public boolean needsFlush() {
        if (currentDataSize > maxWaterMarkSize) {
            return true;
        } else {
            return false;
        }
    }

    public PAgentRequestsStatBatch createAndClear() {
        List<PRequestsStatUrlMetadata> requestsStatUrlMetadataList = createUrlMetadataList(urlMetadata);

        PAgentRequestsStatBatch.Builder pRequestsStatBatchBuilder = PAgentRequestsStatBatch.newBuilder();
        pRequestsStatBatchBuilder.addAllRequestsStatUrlMetadata(requestsStatUrlMetadataList);
        pRequestsStatBatchBuilder.addAllRequestsStatData(requestsStatDataList);

        clear();

        return pRequestsStatBatchBuilder.build();
    }

    private List<PRequestsStatUrlMetadata> createUrlMetadataList(Map<String, Integer> urlMetadata) {
        List<PRequestsStatUrlMetadata> requestsStatUrlMetadataList = new ArrayList<PRequestsStatUrlMetadata>(urlMetadata.size());
        for (Map.Entry<String, Integer> urlMetaDataEntry : urlMetadata.entrySet()) {
            PRequestsStatUrlMetadata.Builder urlMetadataBuilder = PRequestsStatUrlMetadata.newBuilder();
            urlMetadataBuilder.setUrl(urlMetaDataEntry.getKey());
            urlMetadataBuilder.setId(urlMetaDataEntry.getValue());
            requestsStatUrlMetadataList.add(urlMetadataBuilder.build());
        }
        return requestsStatUrlMetadataList;
    }

    private void clear() {
        urlMetadata.clear();
        requestsStatDataList.clear();
        currentDataSize = 0;
    }

    public boolean hasData() {
        return currentDataSize != 0;
    }

}
