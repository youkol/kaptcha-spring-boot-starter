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
package com.youkol.support.kaptcha.producer;

import java.awt.image.BufferedImage;

import com.google.code.kaptcha.Producer;

/**
 * Producer extended support.
 *
 * @author jackiea
 */
public interface KaptchaProducer extends Producer {

    /**
     * Create an image with the specified width and height which will have written a distorted text.
     *
     * @param text the distorted characters
     * @param width image width
     * @param height image height
     * @return image with the text
     */
    BufferedImage createImage(String text, int width, int height);

    /**
     * Create an image with the specified width and height and font size which will have written a distorted text.
     *
     * @param text the distorted characters
     * @param width image width
     * @param height image height
     * @param fontSize the character's font size
     * @return image with the text
     */
    BufferedImage createImage(String text, int width, int height, int fontSize);
}
