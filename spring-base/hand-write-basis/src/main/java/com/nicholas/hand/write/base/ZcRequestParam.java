package com.nicholas.hand.write.base;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ZcRequestParam {
    
    String value() default "";
}
