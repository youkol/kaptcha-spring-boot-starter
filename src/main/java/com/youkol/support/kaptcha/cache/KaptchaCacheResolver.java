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

import java.io.Serializable;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jackiea
 */
public interface KaptchaCacheResolver extends KaptchaCacheConfig {

    /**
     * Get kaptcha text from cache or session, then delete it from cache.
     *
     * @param request HttpServletRequest
     * @return Kaptcha
     */
    default Optional<Kaptcha> getKaptcha(HttpServletRequest request) {
        return getKaptcha(request, true);
    }

    /**
     * Get kaptcha text from cache or session.
     *
     * @param request HttpServletRequest
     * @param deleteCache when true, delete kaptcha from cache.
     * @return Kaptcha Info.
     */
    Optional<Kaptcha> getKaptcha(HttpServletRequest request, boolean deleteCache);

    /**
     * Put text to cache.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @param text the kaptcha text
     * @return The cache key id without key prefix.
     *     For example:
     *     redis key is YOUKOL_KAPTCHA_TOKEN.0707908f030e4de284f538297a40f202,
     *     it will be return 0707908f030e4de284f538297a40f202
     */
    String putKaptcha(HttpServletRequest request, HttpServletResponse response, String text);

    /**
     * Validate the kaptcha and delete it from cache.
     *
     * @param request HttpServletRequest
     * @param text The string to valid
     * @return when validate success, it return true, otherwise false.
     */
    default boolean validKaptcha(HttpServletRequest request, String text) {
        return validKaptcha(request, text, true);
    }

    /**
     * Validate the kaptcha.
     *
     * @param request HttpServletRequest
     * @param text The string to valid
     * @param deleteCache Whether to delete the cache.
     * @return when validate success, it return true, otherwise false.
     */
    boolean validKaptcha(HttpServletRequest request, String text, boolean deleteCache);

    public static class Kaptcha implements Serializable {

        private static final long serialVersionUID = 1L;

        /**
         * katpcha text.
         */
        private String text;

        /**
         * generate kaptcha time.
         */
        private Long time;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public Long getTime() {
            return time;
        }

        public void setTime(Long time) {
            this.time = time;
        }
    }
}
