package com.walid.jsbridge.factory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:23
 * Describe :
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Target({ElementType.TYPE})
public @interface JSMoudle {
    String name();
}