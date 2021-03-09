/**
 * Copyright (C) 2016 the original author or authors.
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
package com.youkol.support.kaptcha.spring.boot.autoconfigure;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.youkol.support.kaptcha.cache.KaptchaCacheConfig;
import com.youkol.support.kaptcha.config.KaptchaConfig;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties(prefix = KaptchaProperties.KAPTCHA_PREFIX, ignoreUnknownFields = true)
public class KaptchaProperties {

    public static final String KAPTCHA_PREFIX = "youkol.web.kaptcha";

    private boolean enabled = true;

    @NestedConfigurationProperty
    private Servlet servlet = new Servlet();

    @NestedConfigurationProperty
    private Cache cache = new Cache();

    /**
     * kaptcha inner config {@link com.google.code.kaptcha.Constants}
     */
    @NestedConfigurationProperty
    private Map<String, String> config = new HashMap<>();

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Servlet getServlet() {
        return servlet;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }

    public Map<String, String> getConfig() {
        return config;
    }

    public void setConfig(Map<String, String> config) {
        this.config = config;
    }

    public KaptchaConfig createKaptchaConfig() {
        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : this.config.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        return new KaptchaConfig(properties);
    }

    public static class Servlet {
        private boolean enabled = true;

        private String urlMapping = "/kaptcha";

        private String sizeParam = "size";

        private String fontSizeParam = "fontSize";

        public boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public String getUrlMapping() {
            return urlMapping;
        }

        public void setUrlMapping(String urlMapping) {
            this.urlMapping = urlMapping;
        }

        public String getSizeParam() {
            return sizeParam;
        }

        public void setSizeParam(String sizeParam) {
            this.sizeParam = sizeParam;
        }

        public String getFontSizeParam() {
            return fontSizeParam;
        }

        public void setFontSizeParam(String fontSizeParam) {
            this.fontSizeParam = fontSizeParam;
        }
    }

    public static class Cache {
        private String type = "session";

        private String tokenName = KaptchaCacheConfig.DEFAULT_KAPTCHA_TOKEN_NAME;

        private int timeout = 5 * 60 * 1000;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getTokenName() {
            return tokenName;
        }

        public void setTokenName(String tokenName) {
            this.tokenName = tokenName;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }

    }

}
