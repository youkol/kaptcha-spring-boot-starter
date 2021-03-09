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

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youkol.support.kaptcha.cache.KaptchaCacheResolver;
import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.producer.KaptchaProducer;

import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;

/**
 * SimpleKaptchaServlet
 *
 * @author jackiea
 */
public class SimpleKaptchaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_SIZE_PARAM = "size";

    public static final String DEFAULT_FONT_SIZE_PARAM = "fontSize";

    public static final String DEFAULT_FORMAT_PARAM = "format";

    private String sizeParam = DEFAULT_SIZE_PARAM;

    private String fontSizeParam = DEFAULT_FONT_SIZE_PARAM;

    private String formatParam = DEFAULT_FORMAT_PARAM;

    private Properties props = new Properties();

    private KaptchaConfig kaptchaConfig;

    private KaptchaProducer kaptchaProducer;

    private KaptchaCacheResolver kaptchaCacheResolver;

    /*
     * (non-Javadoc)
     *
     * @see javax.servlet.Servlet#init(javax.servlet.ServletConfig)
     */
    @Override
    public void init(ServletConfig conf) throws ServletException {
        super.init(conf);

        // Switch off disk based caching.
        ImageIO.setUseCache(false);

        Enumeration<String> initParams = conf.getInitParameterNames();
        while (initParams.hasMoreElements()) {
            String key = initParams.nextElement();
            String value = conf.getInitParameter(key);
            this.props.put(key, value);
        }

        this.kaptchaConfig = new KaptchaConfig(this.props);

        if (this.kaptchaProducer == null) {
            this.kaptchaProducer = kaptchaConfig.getKaptchaProducerImpl();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int[] size = this.getSizeFromRequest(req);
        int fontSize = this.getFontSizeFromRequest(req);
        ResponseFormat format = this.getResponseFormat(req);

        // create the text for the image
        String capText = this.kaptchaProducer.createText();

        // cache the kaptcha text
        String uuid = null;
        if (kaptchaCacheResolver != null) {
            uuid = kaptchaCacheResolver.putKaptcha(req, resp, capText);
        }

        // create the image with the text
        BufferedImage bi = this.kaptchaProducer.createImage(capText, size[0], size[1], fontSize);

        if (ResponseFormat.IMAGE == format) {
            // Set to expire far in the past.
            resp.setDateHeader("Expires", 0);
            // Set standard HTTP/1.1 no-cache headers.
            resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
            resp.addHeader("Cache-Control", "post-check=0, pre-check=0");
            // Set standard HTTP/1.0 no-cache header.
            resp.setHeader("Pragma", "no-cache");

            // return a jpeg
            resp.setContentType("image/jpeg");

            ServletOutputStream out = resp.getOutputStream();

            // write the data out
            ImageIO.write(bi, "jpg", out);
            return;
        }
        if (ResponseFormat.BASE64 == format) {
            FastByteArrayOutputStream os = new FastByteArrayOutputStream();
            ImageIO.write(bi, "jpg", os);
            String imageBase64 = Base64.getEncoder().encodeToString(os.toByteArray());
            String result = String.format("{\"code\": \"%s\", \"message\": \"%s\", \"data\": { \"uuid\": \"%s\", \"image\": \"%s\"}}",
                "200", "OK", uuid, "data:image/jpeg;base64," + imageBase64);

            resp.setContentType("application/json;charset=UTF-8");
            resp.getOutputStream().write(result.getBytes());
        }
    }

    protected int getFontSizeFromRequest(HttpServletRequest request) {
        String fontSize = request.getParameter(this.getFontSizeParam());
        if (StringUtils.isEmpty(fontSize)) {
            return this.kaptchaConfig.getTextProducerFontSize();
        }

        String regex = "^[1-9]\\d*$";
        if (!fontSize.matches(regex)) {
            throw new IllegalArgumentException("The fontSize value is illegal.");
        }

        return Integer.parseInt(fontSize);
    }

    protected ResponseFormat getResponseFormat(HttpServletRequest request) {
        String format = request.getParameter(this.getFormatParam());
        return ResponseFormat.of(format, ResponseFormat.IMAGE);
    }

    private int[] getDefaultSize() {
        int width = this.kaptchaConfig.getWidth();
        int height = this.kaptchaConfig.getHeight();
        return new int[] {
            width,
            height
        };
    }

    protected int[] getSizeFromRequest(HttpServletRequest request) {
        String size = request.getParameter(this.getSizeParam());
        if (StringUtils.isEmpty(size)) {
            return this.getDefaultSize();
        }

        String regex = "^([1-9]\\d*)x([1-9]\\d*)$";
        if(!size.matches(regex)) {
            throw new IllegalArgumentException("The format of size parameter is illegal.");
        }

        String[] sizeArray = size.split("x");
        return new int[] {
            Integer.parseInt(sizeArray[0]),
            Integer.parseInt(sizeArray[1])
        };
    }

    public KaptchaProducer getKaptchaProducer() {
        return kaptchaProducer;
    }

    public void setKaptchaProducer(KaptchaProducer kaptchaProducer) {
        this.kaptchaProducer = kaptchaProducer;
    }

    public KaptchaCacheResolver getKaptchaCacheResolver() {
        return kaptchaCacheResolver;
    }

    public void setKaptchaCacheResolver(KaptchaCacheResolver kaptchaCacheResolver) {
        this.kaptchaCacheResolver = kaptchaCacheResolver;
    }

    public String getSizeParam() {
        return sizeParam;
    }

    public void setSizeParam(String sizeParam) {
        this.sizeParam = sizeParam;
    }

    public String getFontSizeParam() {
        return fontSizeParam;
    }

    public void setFontSizeParam(String fontSizeParam) {
        this.fontSizeParam = fontSizeParam;
    }

    public String getFormatParam() {
        return formatParam;
    }

    public void setFormatParam(String formatParam) {
        this.formatParam = formatParam;
    }

}
