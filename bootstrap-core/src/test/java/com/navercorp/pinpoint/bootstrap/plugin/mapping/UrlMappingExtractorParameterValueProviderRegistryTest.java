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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

/**
 * @author Taejin Koo
 */
public class UrlMappingExtractorParameterValueProviderRegistryTest {

    @Test
    public void providerRegistryTest() {
        String[] parameterArray1 = {"hello"};
        UrlMappingExtractorParameterValueProvider provider1
            = new UrlMappingExtractorParameterValueProvider(UrlMappingExtractorType.SERVLET_REQUEST_ATTRIBUTE, parameterArray1);

        String[] parameterArray2 = {"hi"};
        UrlMappingExtractorParameterValueProvider provider2
            = new UrlMappingExtractorParameterValueProvider(UrlMappingExtractorType.SERVLET_REQUEST_ATTRIBUTE, parameterArray2);


        UrlMappingExtractorParameterValueProviderRegistry registry
            = new UrlMappingExtractorParameterValueProviderRegistry(Arrays.asList(provider1, provider2));

        List<UrlMappingExtractorParameterValueProvider> result = registry.get(UrlMappingExtractorType.SERVLET_REQUEST_ATTRIBUTE);
        Assert.assertEquals(2, result.size());

        result = registry.get(UrlMappingExtractorType.NONE);
        Assert.assertEquals(0, result.size());
    }

}
