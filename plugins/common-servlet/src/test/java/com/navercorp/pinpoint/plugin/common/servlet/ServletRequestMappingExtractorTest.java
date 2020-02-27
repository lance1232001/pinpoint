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

import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProvider;
import com.navercorp.pinpoint.common.trace.UrlMappingExtractorType;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Collections;

/**
 * @author Taejin Koo
 */
public class ServletRequestMappingExtractorTest {

    @Test
    public void mappingExtractorTest() {
        String mappingKeyName = "mockMapping";
        String expectedMappingUrl = "/hello.*";

        String defaultOftenUsedUrl = ServletRequestMappingExtractor.DEFAULT_OFTEN_USED_URL[1];

        UrlMappingExtractorParameterValueProvider parameterValueProvider
            = new UrlMappingExtractorParameterValueProvider(UrlMappingExtractorType.SERVLET_REQUEST_ATTRIBUTE, new String[]{mappingKeyName});

        ServletRequestMappingExtractor servletRequestMappingExtractor = ServletRequestAttributeMappingExtractorFactory.create(Arrays.asList(parameterValueProvider));

        HttpServletRequest mockRequest1 = Mockito.mock(HttpServletRequest.class);
        Mockito.when(mockRequest1.getAttribute(mappingKeyName)).thenReturn(expectedMappingUrl);


        String extractedUrl = servletRequestMappingExtractor.getUrl(mockRequest1, defaultOftenUsedUrl);
        Assert.assertEquals(expectedMappingUrl, extractedUrl);


        HttpServletRequest mockRequest2 = Mockito.mock(HttpServletRequest.class);

        extractedUrl = servletRequestMappingExtractor.getUrl(mockRequest2, "/test");
        Assert.assertEquals(ServletRequestMappingExtractor.NOT_FOUNDED_MAPPING, extractedUrl);

        extractedUrl = servletRequestMappingExtractor.getUrl(mockRequest2, defaultOftenUsedUrl);
        Assert.assertEquals(defaultOftenUsedUrl, extractedUrl);
    }

    @Test
    public void createIfInsertEmptyTest() {
        ServletRequestMappingExtractor servletRequestMappingExtractor = ServletRequestAttributeMappingExtractorFactory.create(Collections.<UrlMappingExtractorParameterValueProvider>emptyList());
        Assert.assertNull(servletRequestMappingExtractor);
    }

}
