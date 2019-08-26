/*
 * Copyright 2019 NAVER Corp.
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

package com.navercorp.pinpoint.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author Taejin Koo
 */
public final class ResourceUtils {

    public static InputStream getFileInputStream(String filePath) throws FileNotFoundException {
        Assert.isTrue(StringUtils.hasLength(filePath), "filePath must not be empty");

        // find order
        // 1. application classLoader -> 2. ResourceUtils.classLoader -> 3 Absolute path
        InputStream result = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
        if (result != null) {
            return result;
        }

        result = ResourceUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (result != null) {
            return result;
        }

        final File file = new File(filePath);
        if (file.exists()) {
            return new FileInputStream(file);
        }

        throw new FileNotFoundException("File Not Found: " + filePath);
    }

}
