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

import javax.servlet.Servlet;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.google.code.kaptcha.util.Config;
import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.producer.SimpleKaptchaProducer;
import com.youkol.support.kaptcha.servlet.SimpleKaptchaServlet;
import com.youkol.support.kaptcha.store.KaptchaStoreResolver;
import com.youkol.support.kaptcha.store.impl.CacheKaptchaStoreResolver;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication
@ConditionalOnClass({ Config.class, Producer.class, Servlet.class, KaptchaServlet.class })
@EnableConfigurationProperties(KaptchaProperties.class)
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
public class KaptchaAutoConfiguration {

    private final KaptchaProperties kaptchaProperties;

    public KaptchaAutoConfiguration(KaptchaProperties kaptchaProperties) {
        this.kaptchaProperties = kaptchaProperties;
    }

    @Bean(name = "kaptchaConfig")
    @ConditionalOnMissingBean
    public KaptchaConfig kaptchaConfig() {
        return this.kaptchaProperties.createKaptchaConfig();
    }

    @Bean(name = "kaptchaProducer")
    @ConditionalOnMissingBean
    public KaptchaProducer kaptchaProducer(KaptchaConfig config) {
        SimpleKaptchaProducer simpleKaptchaProducer = new SimpleKaptchaProducer();
        simpleKaptchaProducer.setConfig(config);

        return simpleKaptchaProducer;
    }

    @Bean(name = "kaptchaStoreResolver", initMethod = "init")
    @ConditionalOnMissingBean
    public KaptchaStoreResolver kaptchaStoreResolver(KaptchaConfig config, @Autowired(required = false) CacheManager cacheManager)
            throws Exception {
        KaptchaStoreResolver kaptchaStoreResolver = config.getStoreResolverImpl();
        if (kaptchaStoreResolver instanceof CacheKaptchaStoreResolver) { // when CacheKaptchaStoreResolver
            if (cacheManager == null) {
                throw new Exception("The CacheManager bean is not exist!");
            }
            CacheKaptchaStoreResolver cacheKaptchaStoreResolver = (CacheKaptchaStoreResolver)kaptchaStoreResolver;
            cacheKaptchaStoreResolver.setCacheManager(cacheManager);

            return cacheKaptchaStoreResolver;
        }

        return kaptchaStoreResolver;
    }

    @Bean(name = "kaptchaServlet")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = KaptchaProperties.KAPTCHA_PREFIX, value = "enabled", matchIfMissing = true)
    public SimpleKaptchaServlet kaptchaServlet(KaptchaProducer kaptchaProducer, KaptchaStoreResolver kaptchaStoreResolver) {
        SimpleKaptchaServlet kaptchaServlet = new SimpleKaptchaServlet();
        kaptchaServlet.setKaptchaProducer(kaptchaProducer);
        kaptchaServlet.setKaptchaStoreResolver(kaptchaStoreResolver);
        kaptchaServlet.setSizeParam(kaptchaProperties.getSizeParam());

        return kaptchaServlet;
    }

    @Bean(name = "kaptchaServletRegistrationBean")
    @ConditionalOnBean(value = SimpleKaptchaServlet.class, name = "kaptchaServlet")
    @ConditionalOnProperty(prefix = KaptchaProperties.KAPTCHA_PREFIX, value = "enabled", matchIfMissing = true)
    public ServletRegistrationBean<SimpleKaptchaServlet> kaptchaServletRegistrationBean(SimpleKaptchaServlet kaptchaServlet){
        ServletRegistrationBean<SimpleKaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setName("kaptchaServlet");
        registration.setServlet(kaptchaServlet);
        registration.addUrlMappings(kaptchaProperties.getUrlMapping());
        registration.setInitParameters(kaptchaProperties.getConfig());

        return registration;
    }
}
