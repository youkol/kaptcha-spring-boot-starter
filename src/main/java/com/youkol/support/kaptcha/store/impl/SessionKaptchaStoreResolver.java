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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.store.AbstractkaptchaStoreResolver;

/**
 * Kaptcha session storage.
 *
 * @author jackiea
 */
public class SessionKaptchaStoreResolver extends AbstractkaptchaStoreResolver {

    public SessionKaptchaStoreResolver() {
        super();
    }

    public SessionKaptchaStoreResolver(KaptchaConfig config) {
        super(config);
    }

    @Override
    public Optional<String> getKaptchaText(HttpServletRequest request) {
        String storeTextKey = this.config.getStoreTextKey();

        String textValue = (String)request.getSession().getAttribute(storeTextKey);

        return Optional.ofNullable(textValue);
    }

    @Override
    public Optional<Long> getKaptchaTime(HttpServletRequest request) {
        String storeTimeKey = this.config.getStoreTimeKey();
        Long timeValue = (Long)request.getSession().getAttribute(storeTimeKey);

        return Optional.ofNullable(timeValue);
    }

    @Override
    public void putKaptcha(HttpServletRequest request, HttpServletResponse response, String text) {
        String storeTextKey = this.config.getStoreTextKey();
        String storeTimeKey = this.config.getStoreTimeKey();

        // store the text in the session
        request.getSession().setAttribute(storeTextKey, text);

        // store the date milliseconds in the session so that it can be compared
        // against to make sure someone hasn't taken too long to enter
        // their kaptcha
        request.getSession().setAttribute(storeTimeKey, System.currentTimeMillis());
    }

}
