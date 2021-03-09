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

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication
@ConditionalOnClass({ Config.class, Producer.class, Servlet.class, KaptchaServlet.class })
@EnableConfigurationProperties(KaptchaProperties.class)
@ConditionalOnProperty(prefix = KaptchaProperties.KAPTCHA_PREFIX, value = "enabled", matchIfMissing = true)
@Import({ KaptchaCacheResolverConfiguration.class, KaptchaServletConfiguration.class })
public class KaptchaAutoConfiguration {

    @Bean(name = "kaptchaConfig")
    @ConditionalOnMissingBean
    public KaptchaConfig kaptchaConfig(KaptchaProperties kaptchaProperties) {
        return kaptchaProperties.createKaptchaConfig();
    }

    @Bean(name = "kaptchaProducer")
    @ConditionalOnMissingBean
    public KaptchaProducer kaptchaProducer(KaptchaConfig config) {
        SimpleKaptchaProducer simpleKaptchaProducer = new SimpleKaptchaProducer();
        simpleKaptchaProducer.setConfig(config);

        return simpleKaptchaProducer;
    }
}
