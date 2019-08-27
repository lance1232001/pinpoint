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

package com.navercorp.pinpoint.grpc.security;

import com.navercorp.pinpoint.common.util.Assert;

import java.util.Properties;

/**
 * @author Taejin Koo
 */
public class SslClientConfig {

    private static String GRPC_SSL_PREFIX = "profiler.transport.grpc.";

    private static String EMPTY_STRING = "";

    public static SslClientConfig DISABLED_CONFIG = new SslClientConfig(false, EMPTY_STRING, EMPTY_STRING);

    private final boolean enable;
    private final String sslProviderType;
    private final String trustCertFilePath;

    public SslClientConfig(boolean enable, String sslProviderType, String trustCertFilePath) {
        this.enable = enable;

        this.sslProviderType = Assert.requireNonNull(sslProviderType, "sslProviderType must not be null");

        this.trustCertFilePath = Assert.requireNonNull(trustCertFilePath, "trustCertFilePath must not be null");
    }

    public boolean isEnable() {
        return enable;
    }

    public String getSslProviderType() {
        return sslProviderType;
    }

    public String getTrustCertFilePath() {
        return trustCertFilePath;
    }

    public static SslClientConfig create(Properties properties) {
        return create(properties, GRPC_SSL_PREFIX);
    }

    public static SslClientConfig create(Properties properties, String propertyPrefix) {
        Assert.requireNonNull(properties, "properties must not be null");

        boolean enable = SslOption.ENABLE.readBoolean(properties, propertyPrefix);
        String providerType = SslOption.PROVIDER_TYPE.readString(properties, propertyPrefix);

        String trustCertFilePath = SslOption.TRUST_CERT_FILE_PATH.readString(properties, propertyPrefix);
        return new SslClientConfig(enable, providerType, trustCertFilePath);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SslClientConfig{");
        sb.append("enable=").append(enable);
        sb.append(", sslProviderType='").append(sslProviderType).append('\'');
        sb.append(", trustCertFilePath='").append(trustCertFilePath).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
