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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.code.kaptcha.text.TextProducer;
import com.youkol.support.kaptcha.cache.KaptchaCacheResolver;
import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.text.impl.SimpleMathTextCreator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.StringUtils;

/**
 * SimpleKaptchaServlet
 *
 * @author jackiea
 */
public class SimpleKaptchaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final Logger logger = LoggerFactory.getLogger(SimpleKaptchaServlet.class);

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

    private ObjectMapper objectMapper = new ObjectMapper();

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
        String kaptchaText;
        String kaptchaResult;

        // create the text for the image
        String text = this.kaptchaProducer.createText();
        kaptchaText = text;
        kaptchaResult = text;

        // when create text by simple math text creator
        TextProducer textProducer = this.kaptchaConfig.getTextProducerImpl();
        if (textProducer instanceof SimpleMathTextCreator) {
            kaptchaText = SimpleMathTextCreator.extractMathExpression(text);
            kaptchaResult = SimpleMathTextCreator.extractMathResult(text);
        }

        // cache the kaptcha text
        String uuid = null;
        if (kaptchaCacheResolver != null) {
            uuid = kaptchaCacheResolver.putKaptcha(req, resp, kaptchaResult);
        }

        // create the image with the text
        BufferedImage bi = this.kaptchaProducer.createImage(kaptchaText, size[0], size[1], fontSize);

        try {
            if (ResponseFormat.IMAGE == format) {
                this.imageFormatHandler(bi, resp, uuid);
                return;
            }
            if (ResponseFormat.BASE64 == format) {
                this.base64FormatHandler(bi, resp, uuid);
            }
        } catch (IOException ex) {
            logger.error("doGet error", ex);
        }
    }

    protected void imageFormatHandler(BufferedImage bi, HttpServletResponse response, String uuid) throws IOException {
        // Set to expire far in the past.
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");

        // return a jpeg
        response.setContentType("image/jpeg");

        ServletOutputStream out = response.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    protected void base64FormatHandler(BufferedImage bi, HttpServletResponse response, String uuid) throws IOException {
        FastByteArrayOutputStream os = new FastByteArrayOutputStream();
        ImageIO.write(bi, "jpg", os);
        String imageBase64 = Base64.getEncoder().encodeToString(os.toByteArray());

        Base64ImageData base64Data = new Base64ImageData(uuid, "data:image/jpeg;base64," + imageBase64);
        KaptchaResult result = KaptchaResult.success(base64Data);

        objectMapper.writeValue(response.getOutputStream(), result);

        response.setContentType("application/json;charset=UTF-8");
        response.getOutputStream().write(objectMapper.writeValueAsBytes(result));
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
