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

package com.navercorp.pinpoint.common.server.bo.codec.metric;

import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatCodec;
import com.navercorp.pinpoint.common.server.bo.codec.stat.AgentStatCodecTestBase;
import com.navercorp.pinpoint.common.server.bo.codec.stat.TestAgentStatFactory;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricBo;
import com.navercorp.pinpoint.common.server.bo.metric.IntCountMetricListBo;

import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author Taejin Koo
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-test.xml")
public class IntCountMetricCodecTest extends AgentStatCodecTestBase<IntCountMetricListBo> {

    @Autowired
    private IntCountMetricCodec intCountMetricCodec;

    @Override
    protected List<IntCountMetricListBo> createAgentStats(String agentId, long startTimestamp, long initialTimestamp) {
        return TestAgentStatFactory.createIntCountMetricListBos(agentId, startTimestamp, initialTimestamp);
    }

    @Override
    protected AgentStatCodec<IntCountMetricListBo> getCodec() {
        return intCountMetricCodec;
    }

    @Override
    protected void verify(IntCountMetricListBo expected, IntCountMetricListBo actual) {
        Assert.assertEquals("agentId", expected.getAgentId(), actual.getAgentId());
        Assert.assertEquals("startTimestamp", expected.getStartTimestamp(), actual.getStartTimestamp());
        Assert.assertEquals("timestamp", expected.getTimestamp(), actual.getTimestamp());
        Assert.assertEquals(expected.getName(), actual.getName());
        Assert.assertEquals(expected.size(), actual.size());

        List<IntCountMetricBo> expectedList = expected.getList();
        List<IntCountMetricBo> actualList = actual.getList();

        System.out.println("ExpectedList : " + expectedList);
        System.out.println("actualList   : " + actualList);

        for (int i = 0; i < expected.size(); i++) {
            verify(expectedList.get(i), actualList.get(i));
        }

    }

    private void verify(IntCountMetricBo expected, IntCountMetricBo actual) {
        Assert.assertEquals("agentId", expected.getAgentId(), actual.getAgentId());
        Assert.assertEquals("startTimestamp", expected.getStartTimestamp(), actual.getStartTimestamp());
        Assert.assertEquals("timestamp", expected.getTimestamp(), actual.getTimestamp());

        Assert.assertEquals("value", expected.getValue(), actual.getValue());
    }

}

