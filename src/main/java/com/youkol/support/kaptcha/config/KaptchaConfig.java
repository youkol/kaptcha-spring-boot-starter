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
package com.youkol.support.kaptcha.config;

import java.util.Properties;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.ConfigHelper;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.producer.SimpleKaptchaProducer;
import com.youkol.support.kaptcha.store.KaptchaStoreResolver;
import com.youkol.support.kaptcha.store.impl.CacheKaptchaStoreResolver;
import com.youkol.support.kaptcha.store.impl.SessionKaptchaStoreResolver;

/**
 * Kaptcha Config extended support
 *
 * @author jackiea
 */
public class KaptchaConfig extends Config {

    public static final String KAPTCHA_STORE_RESOLVER_CLASS_NAME = "kaptcha.store.resolver-class-name";
    public static final String KAPTCHA_STORE_TEXT_KEY = "kaptcha.store.text-key";
    public static final String KAPTCHA_STORE_TIME_KEY = "kaptcha.store.time-key";
    public static final String KAPTCHA_STORE_TIMEOUT = "kaptcha.store.timeout";
    public static final String KAPTCHA_STORE_CACHE_CACHE_NAME = "kaptcha.store.cache.cache-name";
    public static final String KAPTCHA_STORE_CACHE_HEADER_NAME = "kaptcha.store.cache.header-name";

    public static final String KAPTCHA_STORE_DEFAULT_TEXT_KEY = "YOUKOL_KAPTCHA_STORE_TEXT_KEY";
    public static final String KAPTCHA_STORE_DEFAULT_TIME_KEY = "YOUKOL_KAPTCHA_STORE_TIME_KEY";
    public static final String KAPTCHA_STORE_CACHE_DEFAULT_CACHE_NAME = "YOUKOL_KAPTCHA_STORE_CACHE_NAME";


    private ConfigHelper helper;

    public KaptchaConfig(Properties properties) {
        super(properties);
        this.helper = new ConfigHelper();
    }

    public KaptchaProducer getKaptchaProducerImpl() {
        String paramName = Constants.KAPTCHA_PRODUCER_IMPL;
        String paramValue = this.getProperties().getProperty(paramName);
        KaptchaProducer producer = (KaptchaProducer) this.helper.getClassInstance(paramName, paramValue, new SimpleKaptchaProducer(), this);
        return producer;
    }

    public KaptchaStoreResolver getStoreResolverImpl() {
        String paramName = KAPTCHA_STORE_RESOLVER_CLASS_NAME;
        String paramValue = this.getProperties().getProperty(paramName);
        KaptchaStoreResolver resolver = (KaptchaStoreResolver) this.helper.getClassInstance(paramName, paramValue, new SessionKaptchaStoreResolver(), this);
        return resolver;
    }

    public String getStoreTextKey() {
        return this.getProperties().getProperty(KAPTCHA_STORE_TEXT_KEY, KAPTCHA_STORE_DEFAULT_TEXT_KEY);
    }

    public String getStoreTimeKey() {
        return this.getProperties().getProperty(KAPTCHA_STORE_TIME_KEY, KAPTCHA_STORE_DEFAULT_TIME_KEY);
    }

    public int getStoreTimeout() {
        String paramName = KAPTCHA_STORE_TIMEOUT;
        String paramValue = this.getProperties().getProperty(paramName);
        return this.helper.getPositiveInt(paramName, paramValue, 15 * 60 * 1000); // default 15min
    }

    public String getStoreCacheName() {
        String paramName = KAPTCHA_STORE_CACHE_CACHE_NAME;
        String paramValue = this.getProperties().getProperty(paramName, KAPTCHA_STORE_CACHE_DEFAULT_CACHE_NAME);

        return paramValue;
    }

    public String getStoreHeaderName() {
        String paramName = KAPTCHA_STORE_CACHE_HEADER_NAME;
        String paramValue = this.getProperties().getProperty(paramName, CacheKaptchaStoreResolver.DEFAULT_KAPTCHA_HEADER_NAME);

        return paramValue;
    }

}
