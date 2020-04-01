package com.nicholas.hand.write.base;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcController {
    String value() default "";
}
