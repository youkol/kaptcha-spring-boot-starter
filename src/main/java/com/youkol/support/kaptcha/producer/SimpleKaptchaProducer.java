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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;

import com.google.code.kaptcha.BackgroundProducer;
import com.google.code.kaptcha.GimpyEngine;
import com.google.code.kaptcha.text.WordRenderer;
import com.google.code.kaptcha.util.Configurable;
import com.youkol.support.kaptcha.text.impl.SimpleWordRenderer;

/**
 * SimpleKaptchaProducer
 *
 * @author jackiea
 */
public class SimpleKaptchaProducer extends Configurable implements KaptchaProducer {

    @Override
    public BufferedImage createImage(String text, int width, int height) {
        int fontSize = this.getConfig().getTextProducerFontSize();
        return this.createImage(text, width, height, fontSize);
    }

    /**
     * Create an image which will have written a distorted text.
     *
     * @param text the distorted characters
     * @return image with the text
     */
    @Override
    public BufferedImage createImage(String text) {
        return createImage(text, this.getConfig().getWidth(), this.getConfig().getHeight());
    }

    @Override
    public BufferedImage createImage(String text, int width, int height, int fontSize) {
        WordRenderer wordRenderer = getConfig().getWordRendererImpl();
        GimpyEngine gimpyEngine = getConfig().getObscurificatorImpl();
        BackgroundProducer backgroundProducer = getConfig().getBackgroundImpl();
        boolean isBorderDrawn = getConfig().isBorderDrawn();

        BufferedImage bi;
        if (wordRenderer instanceof SimpleWordRenderer) {
            bi = ((SimpleWordRenderer) wordRenderer).renderWord(text, width, height, fontSize);
        } else {
            bi = wordRenderer.renderWord(text, width, height);
        }

        bi = gimpyEngine.getDistortedImage(bi);
        bi = backgroundProducer.addBackground(bi);
        Graphics2D graphics = bi.createGraphics();
        if (isBorderDrawn) {
            drawBox(graphics, width, height);
        }

        return bi;
    }

    protected void drawBox(Graphics2D graphics, int width, int height) {
        Color borderColor = getConfig().getBorderColor();
        int borderThickness = getConfig().getBorderThickness();

        graphics.setColor(borderColor);

        if (borderThickness != 1) {
            BasicStroke stroke = new BasicStroke((float) borderThickness);
            graphics.setStroke(stroke);
        }

        Line2D line1 = new Line2D.Double(0, 0, 0, width);
        graphics.draw(line1);
        Line2D line2 = new Line2D.Double(0, 0, width, 0);
        graphics.draw(line2);
        line2 = new Line2D.Double(0, (double) (height - 1), width, (double) (height - 1));
        graphics.draw(line2);
        line2 = new Line2D.Double((double) (width - 1), (double) (height - 1), (double) (width - 1), 0);
        graphics.draw(line2);
    }

    /**
     * @return the text to be drawn
     */
    public String createText() {
        return getConfig().getTextProducerImpl().getText();
    }

}
