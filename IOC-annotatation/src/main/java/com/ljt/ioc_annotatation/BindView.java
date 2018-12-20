package com.ljt.ioc_annotatation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lijiateng on 2018/12/19.
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.CLASS )
public @interface BindView {

    int[] value();

}
