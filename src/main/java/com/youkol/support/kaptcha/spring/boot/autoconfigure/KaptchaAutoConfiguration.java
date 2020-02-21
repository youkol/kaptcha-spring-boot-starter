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
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.google.code.kaptcha.util.Config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
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
    public Config kaptchaConfig() {
        return this.kaptchaProperties.createKaptchaConfig();
    }

    @Bean(name = "kaptchaProducer")
    @ConditionalOnMissingBean
    public Producer kaptchaProducer(Config config) {
        DefaultKaptcha producer = new DefaultKaptcha();
        producer.setConfig(config);

        return producer;
    }

    @Bean(name = "kaptchaServlet")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = KaptchaProperties.KAPTCHA_PREFIX, value = "enabled", matchIfMissing = true)
    public KaptchaServlet kaptchaServlet() {
        KaptchaServlet kaptchaServlet = new KaptchaServlet();

        return kaptchaServlet;
    }

    @Bean(name = "kaptchaServletRegistrationBean")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = KaptchaProperties.KAPTCHA_PREFIX, value = "enabled", matchIfMissing = true)
    public ServletRegistrationBean<KaptchaServlet> kaptchaServletRegistrationBean(KaptchaServlet kaptchaServlet){
        ServletRegistrationBean<KaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setName("kaptchaServlet");
        registration.setServlet(kaptchaServlet);
        registration.addUrlMappings(kaptchaProperties.getUrlMapping());
        registration.setInitParameters(kaptchaProperties.getConfig());

        return registration;
    }
}
