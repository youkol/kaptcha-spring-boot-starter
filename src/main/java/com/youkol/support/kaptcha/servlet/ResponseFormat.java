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
package com.youkol.support.kaptcha.servlet;

/**
 *
 * @author jackiea
 */
public enum ResponseFormat {
    IMAGE, BASE64;

    public static ResponseFormat of(String name, ResponseFormat defaultFormat) {
        for (ResponseFormat format : ResponseFormat.values()) {
            if (format.name().equalsIgnoreCase(name)) {
                return format;
            }
        }

        return defaultFormat;
    }
}
