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

package com.navercorp.pinpoint.collector.service;

import com.navercorp.pinpoint.collector.dao.AgentCustomMetricDao;
import com.navercorp.pinpoint.common.server.bo.metric.AgentCustomMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;
import com.navercorp.pinpoint.common.util.Assert;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Taejin Koo
 */
@Service("hBaseAgentCustomMetricService")
public class HBaseAgentCustomMetricService implements AgentCustomMetricService {

    private final Logger logger = LoggerFactory.getLogger(HBaseAgentStatService.class.getName());

    private final AgentCustomMetricDao<IntCountMetricListBo> intCountMetricDao;

    public HBaseAgentCustomMetricService(AgentCustomMetricDao<IntCountMetricListBo> intCountMetricDao) {
        this.intCountMetricDao = Assert.requireNonNull(intCountMetricDao, "intCountMetricDao");
    }

    @Override
    public void save(AgentCustomMetricBo agentCustomMetricBo) {
        String agentId = agentCustomMetricBo.getAgentId();

        List<IntCountMetricListBo> intCountMetricBoList = agentCustomMetricBo.getIntCountMetricBoList();
        insert(agentId, intCountMetricBoList);
    }

    private void insert(String agentId, List<IntCountMetricListBo> intCountMetricListBos) {
        try {
            intCountMetricDao.insert(agentId, intCountMetricListBos);
        } catch (Exception e) {
            logger.warn("Failed to insert intCountMetric. message:{}", e.getMessage(), e);
        }
    }


}
