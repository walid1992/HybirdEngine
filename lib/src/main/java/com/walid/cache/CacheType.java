package com.walid.cache;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by yale on 2018/7/13.
 */
@Retention(RetentionPolicy.SOURCE)
public @interface CacheType {
    int NORMAL = 0;
    int FORCE = 1;
}