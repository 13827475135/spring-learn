package com.nicholas.v2.spring.annotation;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcRequestParam {
    
    String value() default "";
}
