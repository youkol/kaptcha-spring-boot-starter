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
package com.youkol.support.kaptcha.store.impl;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.store.AbstractkaptchaStoreResolver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.util.StringUtils;

/**
 * CacheKaptchaStoreResolver Use spring cache implementation.
 *
 * @author jackiea
 */
public class CacheKaptchaStoreResolver extends AbstractkaptchaStoreResolver {

    private static final Logger log = LoggerFactory.getLogger(CacheKaptchaStoreResolver.class);

    public static final String DEFAULT_KAPTCHA_HEADER_NAME = "YOUKOL_KAPTCHA_HEADER_TOKEN";

    private static final AtomicInteger INSTANCE_COUNT = new AtomicInteger();

    private static final String DEFAULT_KAPTCHA_CACHE_SUFFIX = ".kaptchaCache";

    private String kaptchaHeaderName = DEFAULT_KAPTCHA_HEADER_NAME;

    private String kaptchaCacheName;

    private CacheManager cacheManager;

    private Cache kaptchaCache;

    public CacheKaptchaStoreResolver() {
        this(null, null);
    }

    public CacheKaptchaStoreResolver(KaptchaConfig config, CacheManager cacheManager) {
        super(config);

        if (StringUtils.isEmpty(this.kaptchaCacheName)) {
            this.kaptchaCacheName = getClass().getName() + DEFAULT_KAPTCHA_CACHE_SUFFIX;
            int instanceNumber = INSTANCE_COUNT.getAndIncrement();
            if (instanceNumber > 0) {
                this.kaptchaCacheName = this.kaptchaCacheName + "." + instanceNumber;
            }
        }

        if (cacheManager != null) {
            this.setCacheManager(cacheManager);
        }
    }

    @PostConstruct
    public void init() {
        String storeCacheName = this.config.getStoreCacheName();
        if (!StringUtils.isEmpty(storeCacheName)) {
            this.setKaptchaCacheName(storeCacheName);
        }

        String storeHeaderName = this.config.getStoreHeaderName();
        if (StringUtils.isEmpty(storeHeaderName)) {
            this.setKaptchaHeaderName(storeHeaderName);
        }

        this.getAvailableKaptchaCache();
    }

    public String getKaptchaHeaderName() {
        return kaptchaHeaderName;
    }

    public void setKaptchaHeaderName(String kaptchaHeaderName) {
        this.kaptchaHeaderName = kaptchaHeaderName;
    }

    public String getKaptchaCacheName() {
        return kaptchaCacheName;
    }

    public void setKaptchaCacheName(String kaptchaCacheName) {
        this.kaptchaCacheName = kaptchaCacheName;
    }

    public CacheManager getCacheManager() {
        return cacheManager;
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private Cache getAvailableKaptchaCache() {
        if (this.kaptchaCache == null) {
            CacheManager cacheManager = getCacheManager();
            if (cacheManager != null) {
                String cacheName = getKaptchaCacheName();
                log.debug("CacheManager [{}] configured.  Building kaptcha cache '{}'", cacheManager, cacheName);
                this.kaptchaCache = cacheManager.getCache(cacheName);
            }else {
                log.debug("You must ensure the CacheManager has been configured.");
            }
        }

        return this.kaptchaCache;
    }

    @Override
    public Optional<String> getKaptchaText(HttpServletRequest request) {
        // get cache
        Cache cache = this.getAvailableKaptchaCache();
        if (cache == null) {
            return Optional.empty();
        }

        // get uuid form header
        String uuid = request.getHeader(this.getKaptchaHeaderName());

        // generate cache key
        String textKey = uuid + "_" + this.config.getStoreTextKey();

        // get value from cache
        log.trace("Attempting to retrieve the text value of  generated kaptcha  from cache.");
        String textValue = cache.get(textKey, String.class);
        log.trace("Found cached the text value of generated kaptcha for key [{}]", textValue);

        // evict cache by key
        cache.evict(textKey);

        return Optional.ofNullable(textValue);
    }

    @Override
    public Optional<Long> getKaptchaTime(HttpServletRequest request) {
        // get cache
        Cache cache = this.getAvailableKaptchaCache();
        if (cache == null) {
            return Optional.empty();
        }
        // get uuid form header
        String uuid = request.getHeader(this.getKaptchaHeaderName());

        // generate cache key
        String timeKey = uuid + "_" + this.config.getStoreTimeKey();

        // get value from cache
        log.trace("Attempting to retrieve the milliseconds value of generated kaptcha from cache.");
        Long timeValue = cache.get(timeKey, Long.class);
        log.trace("Found cached the milliseconds value of generated kaptcha for key [{}]", timeKey);

        // evict cache by key
        cache.evict(timeKey);

        return Optional.ofNullable(timeValue);
    }

    @Override
    public void putKaptcha(HttpServletRequest request, HttpServletResponse response, String text) {
        // get cache
        Cache cache = this.getAvailableKaptchaCache();
        if (cache == null) {
            return;
        }
        // generate uuid
        String uuid = UUID.randomUUID().toString().trim().replaceAll("-", "");

        // add response header
        response.addHeader(this.getKaptchaHeaderName(), uuid);

        // generate cache key
        String textKey = uuid + "_" + this.config.getStoreTextKey();
        String timeKey = uuid + "_" + this.config.getStoreTimeKey();
        long currentTime = System.currentTimeMillis();

        // put cache
        cache.put(textKey, text);
        cache.put(timeKey, currentTime);

        log.trace("Cached kaptcha text.  key=[{}], value=[{}].", textKey, text);
        log.trace("Cached kaptcha time.  key=[{}], value=[{}].", timeKey, currentTime);
    }

}
