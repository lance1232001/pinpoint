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

package com.navercorp.pinpoint.web.mapper.metric.sampling.sampler;

import com.navercorp.pinpoint.common.server.bo.stat.AgentStatDataPoint;
import com.navercorp.pinpoint.web.mapper.stat.sampling.sampler.AgentStatSampler;
import com.navercorp.pinpoint.web.vo.metric.SampledAgentCustomMetricDataPoint;

/**
 * @author Taejin Koo
 */
public interface AgentCustomMetricSampler<T extends AgentStatDataPoint, S extends SampledAgentCustomMetricDataPoint>  extends AgentStatSampler<T, S> {

}
