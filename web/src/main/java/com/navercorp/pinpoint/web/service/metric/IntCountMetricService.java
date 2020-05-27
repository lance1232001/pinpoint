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

package com.navercorp.pinpoint.web.service.metric;

import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.web.dao.metric.IntCountMetricDao;
import com.navercorp.pinpoint.web.vo.Range;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
@Service
public class IntCountMetricService implements AgentCustomMetricService<IntCountMetricListBo> {

    private final IntCountMetricDao intCountMetricDao;

    public IntCountMetricService(@Qualifier("intCountMetricDao") IntCountMetricDao intCountMetricDao) {
        this.intCountMetricDao = intCountMetricDao;
    }

    @Override
    public List<IntCountMetricListBo> selectAgentStatList(String agentId, Range range) {
        Objects.requireNonNull(agentId, "agentId");
        Objects.requireNonNull(range, "range");

        return intCountMetricDao.getAgentStatList(agentId, range);
    }

}
