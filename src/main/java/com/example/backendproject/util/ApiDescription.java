package com.example.backendproject.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface ApiDescription {
    String value() default "";

    String equal() default "";

    boolean show() default true;

    String code() default "";

    boolean checkRole() default true;
}
