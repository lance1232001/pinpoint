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

package com.navercorp.pinpoint.plugin.common.servlet;

import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractor;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorType;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.common.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

/**
 * @author Taejin Koo
 */
public class ServletRequestAttributesMappingExtractor implements UrlMappingExtractor<HttpServletRequest> {

    static final UrlMappingExtractorType URL_MAPPING_EXTRACTOR_TYPE = UrlMappingExtractorType.SERVLET_REQUEST_ATTRIBUTE;
    private final String[] attributeNames;

    public ServletRequestAttributesMappingExtractor(String[] attributeNames) {
        if (attributeNames == null || attributeNames.length == 0) {
            throw new IllegalArgumentException("attributeNames must not be empty");
        }

        this.attributeNames = Assert.requireNonNull(attributeNames, "attributeName");
    }

    @Override
    public UrlMappingExtractorType getUrlMappingExtractorType() {
        return URL_MAPPING_EXTRACTOR_TYPE;
    }

    @Override
    public String getUrl(HttpServletRequest request, String defaultUrl) {
        try {
            for (String attributeName : attributeNames) {
                Object urlMapping = request.getAttribute(attributeName);
                if (urlMapping instanceof String && StringUtils.hasLength((String) urlMapping)) {
                    return (String) urlMapping;
                }
            }

            for (String oftenUsedUrl : DEFAULT_OFTEN_USED_URL) {
                if (oftenUsedUrl.equals(defaultUrl)) {
                    return oftenUsedUrl;
                }
            }
        } catch (Exception e) {

        }
        return NOT_FOUNDED_MAPPING;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ServletRequestAttributesMappingExtractor{");
        sb.append("attributeNames=").append(Arrays.toString(attributeNames));
        sb.append('}');
        return sb.toString();
    }

}
