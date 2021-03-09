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

/**
 *
 * @author jackiea
 */
public interface KaptchaCacheConfig {

    public static final String DEFAULT_KAPTCHA_TOKEN_NAME = "YOUKOL_KAPTCHA_TOKEN";

    default String getCacheTokenName() {
        return DEFAULT_KAPTCHA_TOKEN_NAME;
    }

    void setCacheTokenName(String tokenName);

    long getCacheTimeout();

    void setCacheTimeout(long timeout);

}
