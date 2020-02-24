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

package com.navercorp.pinpoint.profiler.context.provider;

import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProvider;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProviderRegistry;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.plugin.PluginContextLoadResult;

import com.google.inject.Inject;
import com.google.inject.Provider;

import java.util.List;

/**
 * @author Taejin Koo
 */
public class UrlMappingExtractorParameterValueProviderRegistryProvider implements Provider<UrlMappingExtractorParameterValueProviderRegistry> {

    private final Provider<PluginContextLoadResult> pluginContextLoadResultProvider;

    @Inject
    public UrlMappingExtractorParameterValueProviderRegistryProvider(Provider<PluginContextLoadResult> pluginContextLoadResultProvider) {
        this.pluginContextLoadResultProvider = Assert.requireNonNull(pluginContextLoadResultProvider, "pluginContextLoadResultProvider");
    }

    @Override
    public UrlMappingExtractorParameterValueProviderRegistry get() {
        PluginContextLoadResult pluginContextLoadResult = this.pluginContextLoadResultProvider.get();
        List<UrlMappingExtractorParameterValueProvider> urlMappingExtractorParameterValueProviderList = pluginContextLoadResult.getUrlMappingExtractorParameterValueProviderList();

        System.out.println("======================= " + urlMappingExtractorParameterValueProviderList);

        return new UrlMappingExtractorParameterValueProviderRegistry(urlMappingExtractorParameterValueProviderList);
    }

}

