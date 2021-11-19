package com.bus.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author jv.lee
 * @date 2019-07-10
 * @description
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface InjectBus{}