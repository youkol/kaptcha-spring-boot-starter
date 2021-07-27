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

import java.io.Serializable;

public class KaptchaResult implements Serializable {

    private String code;

    private String message;

    private Base64ImageData data;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Base64ImageData getData() {
        return data;
    }

    public void setData(Base64ImageData data) {
        this.data = data;
    }

    public static KaptchaResult success(Base64ImageData data) {
        KaptchaResult result = new KaptchaResult();
        result.setCode("200");
        result.setMessage("OK");
        result.setData(data);
        return result;
    }

    public static KaptchaResult error() {
        KaptchaResult result = new KaptchaResult();
        result.setCode("500");
        result.setMessage("Error");
        return result;
    }

}
