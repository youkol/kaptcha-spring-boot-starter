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

import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jackiea
 */
public interface KaptchaStoreResolver {

    Optional<String> getKaptchaText(HttpServletRequest request);

    Optional<Long> getKaptchaTime(HttpServletRequest request);

    void putKaptcha(HttpServletRequest request, HttpServletResponse response, String text);

    boolean validKaptcha(HttpServletRequest request, String text);
}
