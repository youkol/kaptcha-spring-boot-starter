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
package com.youkol.support.kaptcha.cache;

import java.util.Optional;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.springframework.util.StringUtils;

/**
 *
 * @author jackiea
 */
public abstract class AbstractkaptchaCacheResolver implements KaptchaCacheResolver {

    protected long timeout = 5 * 60 * 1000L;

    private String tokenName = KaptchaCacheConfig.DEFAULT_KAPTCHA_TOKEN_NAME;

    protected AbstractkaptchaCacheResolver() {
    }

    @Override
    public long getCacheTimeout() {
        return timeout;
    }

    @Override
    public void setCacheTimeout(long timeout) {
        this.timeout = timeout;
    }

    @Override
    public String getCacheTokenName() {
        return tokenName;
    }

    @Override
    public void setCacheTokenName(String tokenName) {
        this.tokenName = tokenName;
    }

    @Override
    public boolean validKaptcha(HttpServletRequest request, String text, boolean deleteCache) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        return this.getKaptcha(request, deleteCache)
                .filter(k -> text.equalsIgnoreCase(k.getText()))
                .filter(k -> {
                    long kaptchaTime = Optional.ofNullable(k.getTime()).orElse(0L);
                    long currentTime = System.currentTimeMillis();
                    long kaptchaTimeout = this.getCacheTimeout();
                    return (currentTime - kaptchaTime) <= kaptchaTimeout;
                }).isPresent();
    }

    /**
     * Generate uuid.
     *
     * @return uuid
     */
    protected String getUuid() {
        // generate uuid
        String uuid = UUID.randomUUID().toString().trim();
        return uuid.replace("-", "");
    }

    /**
     * Get cache key.
     *
     * @param uuid Unique identity
     * @return the cache key
     */
    protected String getCacheKey(String uuid) {
        return this.getCacheTokenName() + "." + uuid;
    }

    /**
     * Get id from request.
     *
     * Note: First get id from request header, when value is null,
     * then get id from request attribute.
     *
     * @param request HttpServletRequest
     * @return id
     */
    protected String getIdFromRequest(HttpServletRequest request) {
        String tokenKey = this.getCacheTokenName();
        return Optional.ofNullable(request.getHeader(tokenKey))
            .orElseGet(() -> (String) request.getAttribute(tokenKey));
    }

}
