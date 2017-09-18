package com.walid.jsbridge.factory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Author   : walid
 * Date     : 2017-08-03  16:23
 * Describe :
 */
public class MethodInvoker implements Invoker {

    final Method method;
    Type[] params;

    public MethodInvoker(Method method) {
        this.method = method;
        this.params = this.method.getGenericParameterTypes();
    }

    public Object invoke(Object receiver, Object... params) throws InvocationTargetException, IllegalAccessException {
        return this.method.invoke(receiver, params);
    }

    public Type[] getParameterTypes() {
        if (this.params == null) {
            this.params = this.method.getGenericParameterTypes();
        }
        return this.params;
    }

    public String toString() {
        return this.method.getName();
    }
}