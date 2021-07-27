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
package com.youkol.support.kaptcha.text.impl;

import java.security.SecureRandom;
import java.util.Random;

import com.google.code.kaptcha.text.TextProducer;
import com.google.code.kaptcha.util.Configurable;

/**
 * {@link SimpleMathTextCreator} creates random simple math text.
 *
 * @author jackiea
 */
public class SimpleMathTextCreator extends Configurable implements TextProducer {

    public static final String SEPARATOR = "@";
    private static final String END = "=?" + SEPARATOR;

    private Random random = new SecureRandom();

    @Override
    public String getText() {
        int x = random.nextInt(9) + 1;
        int y = random.nextInt(9) + 1;

        int operator = random.nextInt(4);

        if (operator == 0) {
            StringBuilder result = new StringBuilder();
            result.append(x).append('+').append(y).append(END).append(x + y);
            return result.toString();
        }

        if (operator == 1) {
            int max = Math.max(x, y);
            int min = Math.min(x, y);

            StringBuilder result = new StringBuilder();
            result.append(max).append('-').append(min).append(END).append(max - min);
            return result.toString();
        }

        if (operator == 2) {
            StringBuilder result = new StringBuilder();
            result.append(x).append('*').append(y).append(END).append(x * y);
            return result.toString();
        }

        if (operator == 3) {
            if (x % y == 0) {
                StringBuilder result = new StringBuilder();
                result.append(x).append('/').append(y).append(END).append(x / y);
                return result.toString();
            }
            if (y % x == 0) {
                StringBuilder result = new StringBuilder();
                result.append(y).append('/').append(x).append(END).append(y / x);
                return result.toString();
            }
        }

        StringBuilder result = new StringBuilder();
        result.append(x).append('+').append(y).append(END).append(x + y);
        return result.toString();
    }

    public static String extractMathExpression(String text) {
        return text.substring(0, text.lastIndexOf(SEPARATOR));
    }

    public static String extractMathResult(String text) {
        return text.substring(text.lastIndexOf("@") + 1);
    }

    public static void main(String[] args) {
        SimpleMathTextCreator creator = new SimpleMathTextCreator();
        for (int i = 0; i < 100; i++) {
            String text = creator.getText();
            String express = SimpleMathTextCreator.extractMathExpression(text);
            String result = SimpleMathTextCreator.extractMathResult(text);
            System.out.println(text + "\t" + express + "\t" + result);
        }
    }

}
