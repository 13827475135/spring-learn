package com.nicholas.v2.spring.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcRequestMapping {

    String value() default "";
}
