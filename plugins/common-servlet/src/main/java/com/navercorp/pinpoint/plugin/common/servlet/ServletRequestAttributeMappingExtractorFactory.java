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

import com.navercorp.pinpoint.bootstrap.logging.PLogger;
import com.navercorp.pinpoint.bootstrap.logging.PLoggerFactory;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractor;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProvider;
import com.navercorp.pinpoint.common.trace.UrlMappingExtractorType;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Taejin Koo
 */
public class ServletRequestAttributeMappingExtractorFactory {

    private static final PLogger LOGGER = PLoggerFactory.getLogger(ServletRequestAttributeMappingExtractorFactory.class);

    public static List<UrlMappingExtractor<HttpServletRequest>> create(List<UrlMappingExtractorParameterValueProvider> parameterValueProviderList) {
        List<UrlMappingExtractor<HttpServletRequest>> result = new ArrayList<UrlMappingExtractor<HttpServletRequest>>();

        for (UrlMappingExtractorParameterValueProvider parameterValueProvider : parameterValueProviderList) {
            UrlMappingExtractor<HttpServletRequest> servletRequestUrlMappingExtractor = create(parameterValueProvider);
            if (servletRequestUrlMappingExtractor != null) {
                result.add(servletRequestUrlMappingExtractor);
            }
        }

        return result;
    }

    public static UrlMappingExtractor<HttpServletRequest> create(UrlMappingExtractorParameterValueProvider parameterValueProvider) {
        UrlMappingExtractorType urlMappingExtractorType = parameterValueProvider.getUrlMappingExtractorType();
        if (urlMappingExtractorType != ServletRequestAttributesMappingExtractor.URL_MAPPING_EXTRACTOR_TYPE) {
            return null;
        }

        Object parameterValue = parameterValueProvider.getParameterValue();
        if (!urlMappingExtractorType.assertParameter(parameterValue)) {
            LOGGER.info("parameterValue has invalid type. (expected type:{})", urlMappingExtractorType.getParameterClazz().getName());
        }

        String[] stringArrayParameter = (String[]) parameterValue;
        if (stringArrayParameter.length == 0) {
            LOGGER.info("parameterValue must not be empty");
            return null;
        }

        return new ServletRequestAttributesMappingExtractor(stringArrayParameter);
    }

}
