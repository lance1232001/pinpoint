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

package com.navercorp.pinpoint.profiler.context.monitor.metric;

import com.navercorp.pinpoint.bootstrap.plugin.monitor.metric.CustomMetric;
import com.navercorp.pinpoint.common.util.Assert;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author Taejin Koo
 */
public class CustomMetricWrapperListHolder<T extends CustomMetricWrapper> {

    private final Class<T> clazz;
    private final CopyOnWriteArrayList<T> customMetricWrapperList = new CopyOnWriteArrayList<T>();

    public CustomMetricWrapperListHolder(Class<T> clazz) {
        this.clazz = Assert.requireNonNull(clazz, "clazz");
    }

    public boolean add(T customMetricWrapper) {
        return customMetricWrapperList.add(customMetricWrapper);
    }

    public boolean remove(CustomMetric customMetric) {
        for (T customMetricWrapper : customMetricWrapperList) {
            if (customMetricWrapper.equalsWithUnwrap(customMetric)) {
                return customMetricWrapperList.remove(customMetricWrapper);
            }
        }

        return false;
    }

    public List<T> getList() {
        return new ArrayList<T>(customMetricWrapperList);
    }

    public Class<T> getHoldingClazz() {
        return clazz;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CustomMetricWrapperListHolder{");
        sb.append("clazz=").append(clazz);
        sb.append(", customMetricWrapperList=").append(customMetricWrapperList);
        sb.append('}');
        return sb.toString();
    }

}
