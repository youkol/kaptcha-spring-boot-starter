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
import com.youkol.support.kaptcha.cache.impl.RedisKaptchaCacheResolver;
import com.youkol.support.kaptcha.cache.impl.SessionKaptchaCacheResolver;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration(proxyBeanMethods = false)
@ConditionalOnMissingBean(KaptchaCacheResolver.class)
public class KaptchaCacheResolverConfiguration {

    @Bean
    @ConditionalOnProperty(prefix = "youkol.web.kaptcha.cache", name = "type", havingValue = "session", matchIfMissing = true)
    public SessionKaptchaCacheResolver kaptchaStoreResolver(KaptchaProperties properties) {
        SessionKaptchaCacheResolver kaptchaStoreResolver = new SessionKaptchaCacheResolver();
        kaptchaStoreResolver.setCacheTimeout(properties.getCache().getTimeout());
        kaptchaStoreResolver.setCacheTokenName(properties.getCache().getTokenName());

        return kaptchaStoreResolver;
    }

    @Configuration(proxyBeanMethods = false)
    @AutoConfigureAfter(RedisAutoConfiguration.class)
    static class RedisKaptchaCacheResolverConfiguration {

        @Bean
        @ConditionalOnProperty(prefix = "youkol.web.kaptcha.cache", name = "type", havingValue = "redis")
        public RedisKaptchaCacheResolver kaptchaStoreResolver(KaptchaProperties properties,
                RedisTemplate<String, String> redisTemplate) {
            RedisKaptchaCacheResolver kaptchaStoreResolver = new RedisKaptchaCacheResolver(redisTemplate);
            kaptchaStoreResolver.setCacheTimeout(properties.getCache().getTimeout());
            kaptchaStoreResolver.setCacheTokenName(properties.getCache().getTokenName());

            return kaptchaStoreResolver;
        }
    }

}
