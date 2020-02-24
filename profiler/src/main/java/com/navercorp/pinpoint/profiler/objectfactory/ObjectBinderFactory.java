/*
 * Copyright 2017 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.profiler.objectfactory;

import com.google.inject.Provider;
import com.navercorp.pinpoint.bootstrap.config.ProfilerConfig;
import com.navercorp.pinpoint.bootstrap.context.TraceContext;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentContext;
import com.navercorp.pinpoint.bootstrap.plugin.RequestRecorderFactory;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProviderRegistry;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitorRegistry;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.RequestStatMonitorFactory;
import com.navercorp.pinpoint.common.util.Assert;
import com.navercorp.pinpoint.profiler.context.monitor.DataSourceMonitorRegistryAdaptor;
import com.navercorp.pinpoint.profiler.context.monitor.DataSourceMonitorRegistryService;
import com.navercorp.pinpoint.profiler.interceptor.factory.AnnotatedInterceptorFactory;
import com.navercorp.pinpoint.profiler.interceptor.factory.ExceptionHandlerFactory;
import com.navercorp.pinpoint.profiler.metadata.ApiMetaDataService;

/**
 * @author Woonduk Kang(emeroad)
 */
public class ObjectBinderFactory {
    private final ProfilerConfig profilerConfig;
    private final Provider<TraceContext> traceContextProvider;
    private final DataSourceMonitorRegistry dataSourceMonitorRegistry;
    private final Provider<ApiMetaDataService> apiMetaDataServiceProvider;
    private final ExceptionHandlerFactory exceptionHandlerFactory;
    private final RequestRecorderFactory requestRecorderFactory;
    private final Provider<UrlMappingExtractorParameterValueProviderRegistry> urlMappingExtractorParameterValueProviderProvider;
    private final Provider<RequestStatMonitorFactory> requestStatMonitorFactoryProvider;

    public ObjectBinderFactory(ProfilerConfig profilerConfig,
                               Provider<TraceContext> traceContextProvider,
                               DataSourceMonitorRegistryService dataSourceMonitorRegistryService,
                               Provider<ApiMetaDataService> apiMetaDataServiceProvider,
                               ExceptionHandlerFactory exceptionHandlerFactory,
                               RequestRecorderFactory requestRecorderFactory,
                               Provider<UrlMappingExtractorParameterValueProviderRegistry> urlMappingExtractorParameterValueProviderProvider,
                               Provider<RequestStatMonitorFactory> requestStatMonitorFactoryProvider) {
        System.out.println("**********************************************");
        this.profilerConfig = Assert.requireNonNull(profilerConfig, "profilerConfig");
        this.traceContextProvider = Assert.requireNonNull(traceContextProvider, "traceContextProvider");

        Assert.requireNonNull(dataSourceMonitorRegistryService, "dataSourceMonitorRegistryService");
        this.dataSourceMonitorRegistry = new DataSourceMonitorRegistryAdaptor(dataSourceMonitorRegistryService);

        this.apiMetaDataServiceProvider = Assert.requireNonNull(apiMetaDataServiceProvider, "apiMetaDataServiceProvider");
        this.exceptionHandlerFactory = Assert.requireNonNull(exceptionHandlerFactory, "exceptionHandlerFactory");
        this.requestRecorderFactory = Assert.requireNonNull(requestRecorderFactory, "requestRecorderFactory");
        this.urlMappingExtractorParameterValueProviderProvider = Assert.requireNonNull(urlMappingExtractorParameterValueProviderProvider, "urlMappingExtractorParameterValueProviderProvider");
        this.requestStatMonitorFactoryProvider = Assert.requireNonNull(requestStatMonitorFactoryProvider, "requestStatMonitorFactoryProvider");
    }

    public AutoBindingObjectFactory newAutoBindingObjectFactory(InstrumentContext pluginContext, ClassLoader classLoader, ArgumentProvider... argumentProviders) {
        final TraceContext traceContext = this.traceContextProvider.get();
        return new AutoBindingObjectFactory(profilerConfig, traceContext, pluginContext, classLoader, argumentProviders);
    }


    public InterceptorArgumentProvider newInterceptorArgumentProvider(InstrumentClass instrumentClass) {
        ApiMetaDataService apiMetaDataService = this.apiMetaDataServiceProvider.get();
        UrlMappingExtractorParameterValueProviderRegistry urlMappingExtractorParameterValueProviderRegistry = urlMappingExtractorParameterValueProviderProvider.get();
        RequestStatMonitorFactory requestStatMonitorFactory = requestStatMonitorFactoryProvider.get();
        return new InterceptorArgumentProvider(dataSourceMonitorRegistry, apiMetaDataService, requestRecorderFactory, urlMappingExtractorParameterValueProviderRegistry, requestStatMonitorFactory,  instrumentClass);
    }

    public AnnotatedInterceptorFactory newAnnotatedInterceptorFactory(InstrumentContext pluginContext) {
        final TraceContext traceContext = this.traceContextProvider.get();
        ApiMetaDataService apiMetaDataService = this.apiMetaDataServiceProvider.get();
        UrlMappingExtractorParameterValueProviderRegistry urlMappingExtractorParameterValueProviderRegistry = urlMappingExtractorParameterValueProviderProvider.get();
        RequestStatMonitorFactory requestStatMonitorFactory = requestStatMonitorFactoryProvider.get();
        return new AnnotatedInterceptorFactory(profilerConfig, traceContext, dataSourceMonitorRegistry, apiMetaDataService, pluginContext, exceptionHandlerFactory, requestRecorderFactory, urlMappingExtractorParameterValueProviderRegistry, requestStatMonitorFactory);
    }
}
