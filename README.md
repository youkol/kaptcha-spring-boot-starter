# kaptcha-spring-boot-starter
kaptcha for spring boot autoconfigure
=====================================
[![License](http://img.shields.io/:license-apache-brightgreen.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

### 1. Features
 - [x] Support configuration metadata.
 - [x] Flexible enabled or disabled.
 - [x] Custom your urlMapping for kaptcha servlet.
 - [x] Auto register bean, more information please see KaptchaAutoConfiguration.

### 2. Maven
```
<dependency>
    <groupId>com.youkol.support.kaptcha</groupId>
    <artifactId>kaptcha-spring-boot-starter</artifactId>
    <version>2.3.2</version>
</dependency>
```

### 3. application.yml
```
# support zero-configuration mode, it will running by the default value.
# youkol:
#   web:
#     kaptcha:
#       enabled: true # default value is true
#       url-mapping: /captcha # default value is "/captcha"
#       config:
#         kaptcha:
#           border: "no"
#           border.color: 105,179,90
#           border.thickness: 
#           producer.impl: 
#           textproducer.impl: 
#           textproducer.char.string: abcde2345678gfynmnpwx
#           textproducer.char.length: 5
#           textproducer.font.names: \u5fae\u8f6f\u96c5\u9ed1,Arial,\u5b8b\u4f53,\u6977\u4f53
#           textproducer.font.size: 40
#           textproducer.font.color: blue
#           textproducer.char.space: 
#           noise.impl: com.google.code.kaptcha.impl.NoNoise
#           noise.color: 
#           obscurificator.impl: com.google.code.kaptcha.impl.WaterRipple
#           word.impl: 
#           background.impl: 
#           background.clear.from: lightGray
#           background.clear.to: lightGray
#           image.width: 200
#           image.height: 50
#           session.key: YOUKOL_CAPTCHA_SESSION_KEY
#           session.date: YOUKOL_CAPTCHA_SESSION_DATE
```

### About Kaptcha
Please see more information about this project.  
http://code.google.com/p/kaptcha/  
