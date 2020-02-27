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

package com.navercorp.pinpoint.common.server.bo.stat;

import com.navercorp.pinpoint.common.util.Assert;

/**
 * @author Taejin Koo
 */
public class StartAndElapsedTime {

    private long startTime;
    private long elapsedTime;

    public StartAndElapsedTime(long startTime, long elapsedTime) {
        Assert.isTrue(startTime > 0, "'startTime' must be > 0");
        Assert.isTrue(elapsedTime >= 0, "'elapsedTime' must be >= 0");

        this.startTime = startTime;
        this.elapsedTime = elapsedTime;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("StartAndElapsedTime{");
        sb.append("startTime=").append(startTime);
        sb.append(", elapsedTime=").append(elapsedTime);
        sb.append('}');
        return sb.toString();
    }

}
