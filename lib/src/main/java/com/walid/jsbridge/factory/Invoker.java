package com.walid.jsbridge.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:20
 * Describe :
 */
public interface Invoker {
    Object invoke(Object var1, Object... var2) throws InvocationTargetException, IllegalAccessException;

    Type[] getParameterTypes();

}