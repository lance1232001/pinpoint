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

package com.navercorp.pinpoint.common.server.bo.codec.metric.strategy;

import com.navercorp.pinpoint.common.util.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author Taejin Koo
 */
public class DuplicateLongListAnalyzer {

    public enum Type {
        ALL_SAME((byte) 0),
        HAS_DIFFERENCE((byte) 1);

        private final byte code;

        Type(byte code) {
            this.code = code;
        }

        public byte getCode() {
            return code;
        }
    }

    public class AnalyzeResult {

        private final Type type;
        private final List<Long> values;

        public AnalyzeResult(Type type, List<Long> values) {
            this.values = Objects.requireNonNull(values, "values");
            this.type = Objects.requireNonNull(type, "type");
        }

        public List<Long> getValues() {
            return values;
        }

        public Type getType() {
            return type;
        }

    }

    private final List<List<Long>> longValuesList = new ArrayList<List<Long>>();

    public void addValue(List<Long> value) {
        Assert.requireNonNull(value, "value");

        longValuesList.add(value);
    }

    public AnalyzeResult analyze() {
        List<Long> representativeValues = null;
        for (List<Long> longValues : longValuesList) {
            if (representativeValues == null) {
                representativeValues = longValues;
            } else {
                if (!representativeValues.equals(longValues)) {
                    return new AnalyzeResult(Type.HAS_DIFFERENCE, Collections.emptyList());
                }
            }
        }

        return new AnalyzeResult(Type.ALL_SAME, representativeValues);
    }

}
