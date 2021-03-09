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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youkol.support.kaptcha.cache.AbstractkaptchaCacheResolver;

/**
 * Kaptcha session storage.
 *
 * @author jackiea
 */
public class SessionKaptchaCacheResolver extends AbstractkaptchaCacheResolver {

    public SessionKaptchaCacheResolver() {
        super();
    }

    @Override
    public Optional<Kaptcha> getKaptcha(HttpServletRequest request, boolean deleteCache) {
        String cacheTokenName = this.getCacheTokenName();
        Kaptcha kaptcha = (Kaptcha) request.getSession().getAttribute(cacheTokenName);
        if (deleteCache) {
            request.getSession().removeAttribute(cacheTokenName);
        }

        return Optional.ofNullable(kaptcha);
    }

    @Override
    public String putKaptcha(HttpServletRequest request, HttpServletResponse response, String text) {
        String uuid = this.getUuid();
        String cacheTokenName = this.getCacheTokenName();
        // add response header
        response.addHeader(cacheTokenName, uuid);

        Kaptcha kaptcha = new Kaptcha();
        kaptcha.setText(text);
        kaptcha.setTime(System.currentTimeMillis());
        // cache the text in the session
        // cache the date milliseconds in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
        request.getSession().setAttribute(cacheTokenName, kaptcha);

        return uuid;
    }

}
