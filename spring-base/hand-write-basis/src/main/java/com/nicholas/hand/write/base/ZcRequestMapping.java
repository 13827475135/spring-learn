package com.nicholas.hand.write.base;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcRequestMapping {

    String value() default "";
}
