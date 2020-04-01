package com.nicholas.hand.write.base;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcAutowired {

    String value() default "";
}
