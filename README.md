# kaptcha-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.youkol.support.kaptcha/kaptcha-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.youkol.support.kaptcha/kaptcha-spring-boot-starter)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.youkol.support.kaptcha/kaptcha-spring-boot-starter?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/youkol/support/kaptcha/kaptcha-spring-boot-starter/)
[![License](https://img.shields.io/badge/license-apache-brightgreen)](http://www.apache.org/licenses/LICENSE-2.0.html)

Kaptcha for spring boot autoconfigure.

### 1. Features
 - [x] Support configuration metadata.
 - [x] Flexible enabled or disabled kaptcha.
 - [x] Custom your urlMapping for kaptcha servlet.
 - [x] Auto register bean, more information please see KaptchaAutoConfiguration.

### 2. Maven
```xml
<dependency>
    <groupId>com.youkol.support.kaptcha</groupId>
    <artifactId>kaptcha-spring-boot-starter</artifactId>
    <version>2.3.2</version>
</dependency>
```

### 3. Config spring-boot application.yml 
```yaml
# Support zero-configuration mode, it will running by the default value.
# For Example:
 youkol:
   web:
     kaptcha:
       enabled: true
       url-mapping: /kaptcha
       config:
         kaptcha:
           #
           # Note: To avoid syntax errors in yaml files,
           #       when border property is "no", don't set value of border color and border thickness.
           #       when border property is "yes", You must omit the border property setting and
           #       set value of border color and border thickness directly.
           #
           #       Take a look at the following example:
           #           1) Hide border
           # border: "no"
           #           2) Show border
           border:
             color: "105,179,90"
             thickness: 1
           producer:
             impl: com.google.code.kaptcha.impl.DefaultKaptcha
           textproducer:
             impl: com.google.code.kaptcha.text.impl.DefaultTextCreator
             char:
               string: abcde2345678gfynmnpwx
               length: 5
               space: 2
             font:
               names: \u5fae\u8f6f\u96c5\u9ed1,Arial,\u5b8b\u4f53,\u6977\u4f53
               size: 40
               color: BLUE
           noise:
             impl: com.google.code.kaptcha.impl.NoNoise
             color: BLACK
           obscurificator:
             impl: com.google.code.kaptcha.impl.WaterRipple
           word:
             impl: com.google.code.kaptcha.text.impl.DefaultWordRenderer
           background:
             impl: com.google.code.kaptcha.impl.DefaultBackground
             clear:
               from: LIGHT_GRAY
               to: WHITE
           image:
             width: 200
             height: 50
           session:
             key: YOUKOL_KAPTCHA_SESSION_KEY
             date: YOUKOL_KAPTCHA_SESSION_DATE
```

### About Kaptcha
Please see more information about this project.  
http://code.google.com/p/kaptcha/  
