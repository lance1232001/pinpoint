/*
 * Copyright 2014 NAVER Corp.
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
package com.navercorp.pinpoint.profiler.objectfactory;

import com.navercorp.pinpoint.bootstrap.context.MethodDescriptor;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentClass;
import com.navercorp.pinpoint.bootstrap.instrument.InstrumentMethod;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.Name;
import com.navercorp.pinpoint.bootstrap.interceptor.annotation.NoCache;
import com.navercorp.pinpoint.bootstrap.interceptor.scope.InterceptorScope;
import com.navercorp.pinpoint.bootstrap.plugin.RequestRecorderFactory;
import com.navercorp.pinpoint.bootstrap.plugin.mapping.UrlMappingExtractorParameterValueProviderRegistry;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.DataSourceMonitorRegistry;
import com.navercorp.pinpoint.bootstrap.plugin.monitor.RequestStatMonitorFactory;
import com.navercorp.pinpoint.exception.PinpointException;
import com.navercorp.pinpoint.profiler.metadata.ApiMetaDataService;
import com.navercorp.pinpoint.profiler.util.TypeUtils;

import java.lang.annotation.Annotation;

/**
 * @author Jongho Moon
 *
 */
public class InterceptorArgumentProvider implements ArgumentProvider {
    private final DataSourceMonitorRegistry dataSourceMonitorRegistry;
    private final ApiMetaDataService apiMetaDataService;
    private final InterceptorScope interceptorScope;
    private final InstrumentClass targetClass;
    private final InstrumentMethod targetMethod;
    private final RequestRecorderFactory requestRecorderFactory;
    private final UrlMappingExtractorParameterValueProviderRegistry urlMappingExtractorParameterValueProviderRegistry;
    private final RequestStatMonitorFactory requestStatMonitorFactory;

    public InterceptorArgumentProvider(DataSourceMonitorRegistry dataSourceMonitorRegistry, ApiMetaDataService apiMetaDataService, RequestRecorderFactory requestRecorderFactory,
                                       UrlMappingExtractorParameterValueProviderRegistry urlMappingExtractorParameterValueProviderRegistry, RequestStatMonitorFactory requestStatMonitorFactory, InstrumentClass targetClass) {
        this(dataSourceMonitorRegistry, apiMetaDataService, requestRecorderFactory, urlMappingExtractorParameterValueProviderRegistry, requestStatMonitorFactory, null, targetClass, null);
    }
    
    public InterceptorArgumentProvider(DataSourceMonitorRegistry dataSourceMonitorRegistry, ApiMetaDataService apiMetaDataService, RequestRecorderFactory requestRecorderFactory,
                                       UrlMappingExtractorParameterValueProviderRegistry urlMappingExtractorParameterValueProviderRegistry, RequestStatMonitorFactory requestStatMonitorFactory, InterceptorScope interceptorScope, InstrumentClass targetClass, InstrumentMethod targetMethod) {
        if (dataSourceMonitorRegistry == null) {
            throw new NullPointerException("dataSourceMonitorRegistry");
        }
        if (apiMetaDataService == null) {
            throw new NullPointerException("apiMetaDataService");
        }
        this.dataSourceMonitorRegistry = dataSourceMonitorRegistry;
        this.apiMetaDataService = apiMetaDataService;
        this.requestRecorderFactory = requestRecorderFactory;
        this.urlMappingExtractorParameterValueProviderRegistry = urlMappingExtractorParameterValueProviderRegistry;
        this.requestStatMonitorFactory = requestStatMonitorFactory;
        this.interceptorScope = interceptorScope;
        this.targetClass = targetClass;
        this.targetMethod = targetMethod;
    }

    @Override
    public Option get(int index, Class<?> type, Annotation[] annotations) {
        if (type == InstrumentClass.class) {
            return Option.withValue(targetClass);
        } else if (type == MethodDescriptor.class) {
            MethodDescriptor descriptor = targetMethod.getDescriptor();
            cacheApiIfAnnotationNotPresent(annotations, descriptor);
            
            return Option.withValue(descriptor);
        } else if (type == InstrumentMethod.class) {
            return Option.withValue(targetMethod);
        } else if (type == InterceptorScope.class) {
            Name annotation = TypeUtils.findAnnotation(annotations, Name.class);
            
            if (annotation == null) {
                if (interceptorScope == null) {
                    throw new PinpointException("Scope parameter is not annotated with @Name and the target class is not associated with any Scope");
                } else {
                    return Option.withValue(interceptorScope);
                }
            } else {
                return Option.empty();
            }
        } else if (type == DataSourceMonitorRegistry.class) {
            return Option.withValue(dataSourceMonitorRegistry);
        } else if(type == RequestRecorderFactory.class) {
            return Option.withValue(requestRecorderFactory);
        } else if (type == UrlMappingExtractorParameterValueProviderRegistry.class) {
            return Option.withValue(urlMappingExtractorParameterValueProviderRegistry);
        } else if (type == RequestStatMonitorFactory.class) {
            return Option.withValue(requestStatMonitorFactory);
        }
        
        return Option.empty();
    }

    private void cacheApiIfAnnotationNotPresent(Annotation[] annotations, MethodDescriptor descriptor) {
        Annotation annotation = TypeUtils.findAnnotation(annotations, NoCache.class);
        if (annotation == null) {
            this.apiMetaDataService.cacheApi(descriptor);
        }
    }
}
