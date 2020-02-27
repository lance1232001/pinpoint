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

package com.navercorp.pinpoint.profiler.context.monitor.storage.request;

import com.navercorp.pinpoint.profiler.monitor.storage.request.RequestsStatInnerStorage;
import com.navercorp.pinpoint.profiler.monitor.vo.RequestsStatInfo;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

/**
 * @author Taejin Koo
 */
public class RequestsStatInnerStorageTest {

    private static Random RANDOM = new Random(System.currentTimeMillis());

    @Test
    public void name() {
        RequestsStatInnerStorage storage = new RequestsStatInnerStorage(100);

        for (int i = 0; i < 10; i++) {
            int elapsedTime = RANDOM.nextInt(500);
            RequestsStatInfo requestsStatInfo = new RequestsStatInfo("url-" + i, 200, System.currentTimeMillis(), elapsedTime);

            storage.store(requestsStatInfo);
            if (storage.needsFlush()) {
                storage.createAndClear();
                storage.needsFlush();
                Assert.assertFalse(storage.needsFlush());
                return;
            }
        }

        Assert.fail();
    }

}
