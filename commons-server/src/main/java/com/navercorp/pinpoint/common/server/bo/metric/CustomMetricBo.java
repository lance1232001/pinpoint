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

package com.navercorp.pinpoint.common.server.bo.metric;

import com.navercorp.pinpoint.common.util.Assert;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
public abstract class CustomMetricBo<T extends Number> {

    private final String name;
    private final List<T> valueList = new ArrayList<>();
    private final List<Long> timestampList = new ArrayList<>();

    public CustomMetricBo(String name) {
        this.name = Assert.requireNonNull(name, "name");
    }

    public void add(T value, long timestamp) {
        valueList.add(value);
        timestampList.add(timestamp);
    }

    public T getValue(int index) {
        return valueList.get(index);
    }

    public long getTimestamp(int index) {
        return timestampList.get(index);
    }

    public int getSize() {
        return valueList.size();
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(getClass().getSimpleName());
        sb.append("{");
        sb.append("name='").append(name).append('\'');
        sb.append(", valueList=").append(valueList);
        sb.append(", timestampList=").append(timestampList);
        sb.append('}');
        return sb.toString();
    }

}
