# kaptcha-spring-boot-starter

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.youkol.support.kaptcha/kaptcha-spring-boot-starter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.youkol.support.kaptcha/kaptcha-spring-boot-starter)
[![Sonatype Nexus (Snapshots)](https://img.shields.io/nexus/s/com.youkol.support.kaptcha/kaptcha-spring-boot-starter?server=https%3A%2F%2Foss.sonatype.org)](https://oss.sonatype.org/content/repositories/snapshots/com/youkol/support/kaptcha/kaptcha-spring-boot-starter/)
[![License](https://img.shields.io/badge/license-apache-brightgreen)](http://www.apache.org/licenses/LICENSE-2.0.html)

Kaptcha for spring boot autoconfigure.

### 1. Maven
```xml
<dependency>
    <groupId>com.youkol.support.kaptcha</groupId>
    <artifactId>kaptcha-spring-boot-starter</artifactId>
    <version>2.3.4</version>
</dependency>
```

### 2. Config spring-boot application.yml 
```yaml
# Support zero-configuration mode, it will running by the default value.
# For Example:
youkol:
  web:
    kaptcha:
      enabled: true
      servlet:
        enabled: true
        url-mapping: /kaptcha
        size-param: size
        font-size-param: fontSize
      cache:
        # cache.type = session or redis.
        type: session
        token-name: YOUKOL_KAPTCHA_TOKEN
        timeout: 300000
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
          border: "no"
          #           2) Show border
          # border:
          #   color: "105,179,90"
          #   thickness: 1
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
            impl: com.youkol.support.kaptcha.text.impl.SimpleWordRenderer
          background:
            impl: com.google.code.kaptcha.impl.DefaultBackground
            clear:
              from: LIGHT_GRAY
              to: WHITE
          image:
            width: 200
            height: 50
```

### 3. Send Request Url
GET http(s)://{host}:{port}/{contextPath}/{urlMapping}   
For example:   
1. response image    

    Request: GET http://localhost:8080/kaptcha   
    Response: kaptcha image (image/jpeg)

2. response json    

    Request: GET http://localhost:8080/kaptcha?format=base64    
    Response: {"code": "200", "message": "OK", "data": { "uuid": "f7e7001f042a47eaa73dcdafe01a7b9d", "image": "data:image/jpeg;base64,/9j/4AA...RRRQB//9k="}}  

### 4. validate image code
sample code:
```java
// For example:
@Autowired
private KaptchaCacheResolver kaptchaCacheResolver;

public Object login(HttpServletRequest request) {
    // some things.
    // valid Kaptcha
    String code = request.getParameter("code");
    boolean bValid = kaptchaCacheResolver.validKaptcha(request, code);
    // other things.
}
```
**Note:** 
when use redis mode, you must put token-name and token-value in request header.   
 - token-name: `youkol.web.kaptcha.cache.token-name`
 - token-value: the uuid of response (the uuid also put in response header)

### About [Kaptcha](https://github.com/youkol/kaptcha)   

Please see more information about this project.  
http://code.google.com/p/kaptcha/  
