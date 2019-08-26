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
public final class SslServerConfig {

    private static String GRPC_SSL_PREFIX = "collector.receiver.grpc.";

    private static String EMPTY_STRING = "";

    public static SslServerConfig DISABLED_CONFIG = new SslServerConfig(false, EMPTY_STRING, EMPTY_STRING, EMPTY_STRING);

    private final boolean enable;
    private final String sslProviderType;
    private final String keyFilePath;
    private final String keyCertChainFilePath;

    public SslServerConfig(boolean enable, String sslProviderType, String keyFilePath, String keyCertChainFilePath) {
        this.enable = enable;

        this.sslProviderType = Assert.requireNonNull(sslProviderType, "sslProviderType must not be null");

        this.keyFilePath = Assert.requireNonNull(keyFilePath, "keyFilePath must not be null");
        this.keyCertChainFilePath = Assert.requireNonNull(keyCertChainFilePath, "keyCertChainFilePath must not be null");
    }

    public boolean isEnable() {
        return enable;
    }

    public String getSslProviderType() {
        return sslProviderType;
    }

    public String getKeyFilePath() {
        return keyFilePath;
    }

    public String getKeyCertChainFilePath() {
        return keyCertChainFilePath;
    }

    public static SslServerConfig create(Properties properties) {
        return create(properties, GRPC_SSL_PREFIX);
    }

    public static SslServerConfig create(Properties properties, String propertyPrefix) {
        Assert.requireNonNull(properties, "properties must not be null");

        boolean enable = readBoolean(properties, propertyPrefix, SslOption.ENABLE);
        String providerType = readString(properties, propertyPrefix, SslOption.PROVIDER_TYPE);

        String keyFilePath = readString(properties, propertyPrefix, SslOption.KEY_FILE_PATH);
        String keyCertChainFilePath = readString(properties, propertyPrefix, SslOption.KEY_CERT_CHAIN_FILE_PATH);
        return new SslServerConfig(enable, providerType, keyFilePath, keyCertChainFilePath);
    }

    private static boolean readBoolean(Properties properties, String prefix, SslOption option) {
        String value = properties.getProperty(prefix + option.getKey(), option.getDefaultValue());
        return Boolean.valueOf(value);
    }

    private static String readString(Properties properties, String prefix, SslOption option) {
        return properties.getProperty(prefix + option.getKey(), option.getDefaultValue());
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Server{");
        sb.append("enable=").append(enable);
        sb.append(", sslProviderType='").append(sslProviderType).append('\'');
        sb.append(", keyFilePath='").append(keyFilePath).append('\'');
        sb.append(", keyCertChainFilePath='").append(keyCertChainFilePath).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
