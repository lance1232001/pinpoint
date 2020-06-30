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

package com.navercorp.pinpoint.common.server.bo.stat.join;

import com.navercorp.pinpoint.common.util.CollectionUtils;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author Taejin Koo
 */
public abstract class AbstractJoinFieldBo<V extends Number> implements JoinFieldBo<V> {

    private final V avgValue;

    private final V minValue;

    private final String minAgentId;

    private final V maxValue;

    private final String maxAgentid;

    public AbstractJoinFieldBo(V avgValue, V minValue, String minAgentId, V maxValue, String maxAgentid) {
        this.avgValue = avgValue;
        this.minValue = minValue;
        this.minAgentId = Objects.requireNonNull(minAgentId, "minAgentId");
        this.maxValue = maxValue;
        this.maxAgentid = Objects.requireNonNull(maxAgentid, "maxAgentid");
    }

    @Override
    public V getAvg() {
        return avgValue;
    }

    @Override
    public V getMin() {
        return minValue;
    }

    @Override
    public String getMinAgentId() {
        return minAgentId;
    }

    @Override
    public V getMax() {
        return maxValue;
    }

    @Override
    public String getMaxAgentId() {
        return maxAgentid;
    }

    abstract protected JoinFieldBo<V> getUncollectedValue();

//    abstract protected JoinFieldBo<V> merge(List<JoinFieldBo<V>> joinFieldBoList);

//    public JoinFieldBo<V> merge(List<JoinFieldBo<V>> joinFieldBoList) {
//        if (CollectionUtils.isEmpty(joinFieldBoList)) {
//            return getUncollectedValue();
//        }
//
////        JoinFieldBo<V> joinFieldBo = joinFieldBoList.get(0);
////        int sumTotalValue= 0;
////        String maxAgentId = joinFieldBo.getMaxAgentId();
////        int maxValue = joinFieldBo.getMax();
////        String minTotalCountAgentId = initJoinActiveTraceBo.getMinTotalCountAgentId();
//        int minTotalCount = initJoinActiveTraceBo.getMinTotalCount();
////
////        for (JoinActiveTraceBo joinActiveTraceBo : joinActiveTraceBoList) {
////            sumTotalcount += joinActiveTraceBo.getTotalCount();
////
////            if (joinActiveTraceBo.getMaxTotalCount() > maxTotalCount) {
////                maxTotalCount = joinActiveTraceBo.getMaxTotalCount();
////                maxTotalCountAgentId = joinActiveTraceBo.getMaxTotalCountAgentId();
////            }
////            if (joinActiveTraceBo.getMinTotalCount() < minTotalCount) {
////                minTotalCount = joinActiveTraceBo.getMinTotalCount();
////                minTotalCountAgentId = joinActiveTraceBo.getMinTotalCountAgentId();
////            }
////        }
////
////        final JoinActiveTraceBo newJoinActiveTraceBo = new JoinActiveTraceBo();
////        newJoinActiveTraceBo.setId(initJoinActiveTraceBo.getId());
////        newJoinActiveTraceBo.setTimestamp(timestamp);
////        newJoinActiveTraceBo.setHistogramSchemaType(initJoinActiveTraceBo.getHistogramSchemaType());
////        newJoinActiveTraceBo.setVersion(initJoinActiveTraceBo.getVersion());
////        newJoinActiveTraceBo.setTotalCount(sumTotalcount / boCount);
////        newJoinActiveTraceBo.setMaxTotalCount(maxTotalCount);
////        newJoinActiveTraceBo.setMaxTotalCountAgentId(maxTotalCountAgentId);
////        newJoinActiveTraceBo.setMinTotalCount(minTotalCount);
////        newJoinActiveTraceBo.setMinTotalCountAgentId(minTotalCountAgentId);
////
////        return newJoinActiveTraceBo;
////
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractJoinFieldBo<?> that = (AbstractJoinFieldBo<?>) o;
        return Objects.equals(avgValue, that.avgValue) &&
                Objects.equals(minValue, that.minValue) &&
                Objects.equals(minAgentId, that.minAgentId) &&
                Objects.equals(maxValue, that.maxValue) &&
                Objects.equals(maxAgentid, that.maxAgentid);
    }

    @Override
    public int hashCode() {
        return Objects.hash(avgValue, minValue, minAgentId, maxValue, maxAgentid);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "[", "]")
                .add("avgValue=" + avgValue)
                .add("minValue=" + minValue)
                .add("minAgentId='" + minAgentId + "'")
                .add("maxValue=" + maxValue)
                .add("maxAgentid='" + maxAgentid + "'")
                .toString();
    }

}
