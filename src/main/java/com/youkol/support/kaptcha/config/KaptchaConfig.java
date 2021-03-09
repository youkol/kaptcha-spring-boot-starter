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
package com.youkol.support.kaptcha.config;

import java.util.Properties;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.util.Config;
import com.google.code.kaptcha.util.ConfigHelper;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.producer.SimpleKaptchaProducer;
import com.youkol.support.kaptcha.text.KaptchaWordRenderer;
import com.youkol.support.kaptcha.text.impl.SimpleWordRenderer;

/**
 * Kaptcha Config extended support
 *
 * @author jackiea
 */
public class KaptchaConfig extends Config {

    private ConfigHelper helper;

    public KaptchaConfig(Properties properties) {
        super(properties);
        this.helper = new ConfigHelper();
    }

    public KaptchaProducer getKaptchaProducerImpl() {
        String paramName = Constants.KAPTCHA_PRODUCER_IMPL;
        String paramValue = this.getProperties().getProperty(paramName);
        return (KaptchaProducer) this.helper.getClassInstance(paramName, paramValue, new SimpleKaptchaProducer(), this);
    }

    @Override
    public KaptchaWordRenderer getWordRendererImpl() {
        String paramName = Constants.KAPTCHA_WORDRENDERER_IMPL;
		String paramValue = this.getProperties().getProperty(paramName);
		return (KaptchaWordRenderer) this.helper.getClassInstance(paramName, paramValue,
				new SimpleWordRenderer(), this);
    }

}
