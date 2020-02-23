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
package com.youkol.support.kaptcha.store;

import javax.servlet.http.HttpServletRequest;

import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.Configurable;
import com.youkol.support.kaptcha.config.KaptchaConfig;

import org.springframework.util.StringUtils;

/**
 * Compatible with older configurable.
 *
 * @author jackiea
 */
public abstract class AbstractkaptchaStoreResolver extends Configurable implements KaptchaStoreResolver {

    protected KaptchaConfig config;

    public AbstractkaptchaStoreResolver() {
    }

    public AbstractkaptchaStoreResolver(KaptchaConfig config) {
        if (config != null) {
            this.setConfig(config);
        }
    }

    @Override
    public void setConfig(Config config) {
        this.config = (KaptchaConfig) config;
    }

    @Override
    public boolean validKaptcha(HttpServletRequest request, String text) {
        if (StringUtils.isEmpty(text)) {
            return false;
        }

        String kaptcha = this.getKaptchaText(request).orElse(null);
        if (StringUtils.isEmpty(kaptcha)) {
            return false;
        }

        long kaptchaTime = this.getKaptchaTime(request).orElse(0L);
        long currentTime = System.currentTimeMillis();
        long kaptchaTimeout = this.config.getStoreTimeout();
        if ( (currentTime - kaptchaTime) > kaptchaTimeout) {
            return false;
        }

        return text.equalsIgnoreCase(kaptcha);
    }

}
