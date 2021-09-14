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

import com.youkol.support.kaptcha.cache.KaptchaCacheResolver;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.servlet.SimpleKaptchaServlet;
import com.youkol.support.kaptcha.spring.boot.autoconfigure.KaptchaProperties.Servlet;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@ConditionalOnWebApplication(type = Type.SERVLET)
@AutoConfigureAfter({ WebMvcAutoConfiguration.class, DispatcherServletAutoConfiguration.class })
public class KaptchaServletConfiguration {

    @Bean(name = "kaptchaServlet")
    @ConditionalOnMissingBean
    @ConditionalOnProperty(prefix = "youkol.web.kaptcha.servlet", value = "enabled", matchIfMissing = true)
    public SimpleKaptchaServlet kaptchaServlet(KaptchaProperties kaptchaProperties,
            KaptchaProducer kaptchaProducer, KaptchaCacheResolver kaptchaCacheResolver) {
        SimpleKaptchaServlet kaptchaServlet = new SimpleKaptchaServlet();
        kaptchaServlet.setKaptchaCacheResolver(kaptchaCacheResolver);
        kaptchaServlet.setKaptchaProducer(kaptchaProducer);

        Servlet servletInfo = kaptchaProperties.getServlet();
        kaptchaServlet.setSizeParam(servletInfo.getSizeParam());
        kaptchaServlet.setFontSizeParam(servletInfo.getFontSizeParam());

        return kaptchaServlet;
    }

    @Bean(name = "kaptchaServletRegistrationBean")
    @ConditionalOnBean(value = SimpleKaptchaServlet.class, name = "kaptchaServlet")
    @ConditionalOnProperty(prefix = "youkol.web.kaptcha.servlet", value = "enabled", matchIfMissing = true)
    public ServletRegistrationBean<SimpleKaptchaServlet> kaptchaServletRegistrationBean(
            KaptchaProperties kaptchaProperties, SimpleKaptchaServlet kaptchaServlet){
        ServletRegistrationBean<SimpleKaptchaServlet> registration = new ServletRegistrationBean<>();
        registration.setName("kaptchaServlet");
        registration.setServlet(kaptchaServlet);
        Servlet servletInfo = kaptchaProperties.getServlet();
        registration.addUrlMappings(servletInfo.getUrlMapping());
        registration.setInitParameters(kaptchaProperties.getConfig());

        return registration;
    }
}
