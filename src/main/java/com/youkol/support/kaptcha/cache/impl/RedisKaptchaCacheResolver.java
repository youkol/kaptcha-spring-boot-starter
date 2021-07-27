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
package com.youkol.support.kaptcha.cache.impl;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.youkol.support.kaptcha.cache.AbstractKaptchaCacheResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 *
 * @author jackiea
 */
public class RedisKaptchaCacheResolver extends AbstractKaptchaCacheResolver {

    private static final Logger log = LoggerFactory.getLogger(RedisKaptchaCacheResolver.class);

    private Optional<RedisTemplate<String, String>> redisTemplate;

    private ObjectMapper objectMapper = new ObjectMapper();

    public RedisKaptchaCacheResolver(RedisTemplate<String, String> redisTemplate) {
        super();
        Assert.notNull(redisTemplate, "redisTemplate must not be null.");
        this.redisTemplate = Optional.of(redisTemplate);
    }

    @Override
    public Optional<Kaptcha> getKaptcha(HttpServletRequest request, boolean deleteCache) {
        return redisTemplate.map(redis -> {
            String uuid = this.getIdFromRequest(request);
            String cacheKey = this.getCacheKey(uuid);
            String kaptcha = redis.opsForValue().get(cacheKey);
            if (deleteCache) {
                redis.delete(cacheKey);
            }
            if (StringUtils.isEmpty(kaptcha)) {
                return null;
            }

            try {
                return objectMapper.readValue(kaptcha, Kaptcha.class);
            } catch (JsonProcessingException ex) {
                log.error("parse json error", ex);
            }

            return null;
        });
    }

    @Override
    public boolean validKaptcha(HttpServletRequest request, String text) {
        return this.getKaptcha(request, true)
            .filter(k -> text.equalsIgnoreCase(k.getText()))
            .isPresent();
    }

    @Override
    public String putKaptcha(HttpServletRequest request, HttpServletResponse response, String text) {
        String uuid = this.getUuid();
        redisTemplate.ifPresent(redis -> {
            // add response header
            response.addHeader(this.getCacheTokenName(), uuid);

            // put redis
            String cacheKey = this.getCacheKey(uuid);
            long timeout = this.getCacheTimeout();
            Kaptcha kaptcha = new Kaptcha();
            kaptcha.setText(text);
            kaptcha.setTime(System.currentTimeMillis());
            String kaptchaJson = null;
            try {
                kaptchaJson = objectMapper.writeValueAsString(kaptcha);
            } catch (JsonProcessingException ex) {
                log.error("Process json error", ex);
            }

            redis.opsForValue().set(cacheKey, kaptchaJson, timeout, TimeUnit.MILLISECONDS);
        });

        return uuid;
    }

    public Optional<RedisTemplate<String, String>> getRedisTemplate() {
        return redisTemplate;
    }

    public void setRedisTemplate(Optional<RedisTemplate<String, String>> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

}
