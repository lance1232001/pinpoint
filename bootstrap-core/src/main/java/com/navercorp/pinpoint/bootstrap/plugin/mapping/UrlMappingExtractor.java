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

package com.navercorp.pinpoint.bootstrap.plugin.mapping;

import com.navercorp.pinpoint.common.trace.UrlMappingExtractorType;

/**
 * @author Taejin Koo
 */
public interface UrlMappingExtractor<T> {

//    "javax.servlet.ServletRequest", "javax.servlet.ServletResponse"
    String NOT_FOUNDED_MAPPING = "/NOT_FOUND_MAPPING_URL";

    String[] DEFAULT_OFTEN_USED_URL = {"/", "/index.html"};

    UrlMappingExtractorType getUrlMappingExtractorType();

    String getUrl(T target, String rawUrl);

}
