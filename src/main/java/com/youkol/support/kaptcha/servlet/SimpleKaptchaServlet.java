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
import java.util.Enumeration;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.youkol.support.kaptcha.config.KaptchaConfig;
import com.youkol.support.kaptcha.producer.KaptchaProducer;
import com.youkol.support.kaptcha.store.KaptchaStoreResolver;

import org.springframework.util.StringUtils;

/**
 * SimpleKaptchaServlet
 *
 * @author jackiea
 */
public class SimpleKaptchaServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static final String DEFAULT_SIZE_PARAM = "size";

    private Properties props = new Properties();

    private KaptchaProducer kaptchaProducer = null;

    private KaptchaStoreResolver kaptchaStoreResolver = null;

    private String sizeParam = DEFAULT_SIZE_PARAM;

    private int width = 200;

    private int height = 50;

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

        KaptchaConfig kaptchaConfig = new KaptchaConfig(this.props);

        if (this.kaptchaProducer == null) {
            this.kaptchaProducer = kaptchaConfig.getKaptchaProducerImpl();
        }

        if (this.kaptchaStoreResolver == null) {
            this.kaptchaStoreResolver = kaptchaConfig.getStoreResolverImpl();
        }

        this.width = kaptchaConfig.getWidth();
        this.height = kaptchaConfig.getHeight();
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int[] size = this.parseSizeParam(req);

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

        // create the text for the image
        String capText = this.kaptchaProducer.createText();

        // store the text
        kaptchaStoreResolver.putKaptcha(req, resp, capText);

        // create the image with the text
        BufferedImage bi = this.kaptchaProducer.createImage(capText, size[0], size[1]);

        ServletOutputStream out = resp.getOutputStream();

        // write the data out
        ImageIO.write(bi, "jpg", out);
    }

    protected int[] parseSizeParam(HttpServletRequest request) throws ServletException {
        String size = (String)request.getParameter(this.getSizeParam());
        if (StringUtils.isEmpty(size)) {
            return new int[] {
                width, height
            };
        }

        String regex = "^([1-9]\\d*)x([1-9]\\d*)$";
        if(!size.matches(regex)) {
            throw new ServletException("The format of size parameter is illegal.");
        }

        String[] sizeArray = size.split("x");
        return new int[] {
            Integer.valueOf(sizeArray[0]).intValue(),
            Integer.valueOf(sizeArray[1]).intValue()
        };
    }

    public KaptchaProducer getKaptchaProducer() {
        return kaptchaProducer;
    }

    public void setKaptchaProducer(KaptchaProducer kaptchaProducer) {
        this.kaptchaProducer = kaptchaProducer;
    }

    public KaptchaStoreResolver getKaptchaStoreResolver() {
        return kaptchaStoreResolver;
    }

    public void setKaptchaStoreResolver(KaptchaStoreResolver kaptchaStoreResolver) {
        this.kaptchaStoreResolver = kaptchaStoreResolver;
    }

    public String getSizeParam() {
        return sizeParam;
    }

    public void setSizeParam(String sizeParam) {
        this.sizeParam = sizeParam;
    }

}
