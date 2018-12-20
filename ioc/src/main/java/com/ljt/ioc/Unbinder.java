package com.ljt.ioc;

import android.annotation.UiThread;

/**
 * Created by lijiateng on 2018/12/20.
 */

public interface Unbinder {

    @UiThread
    void unbind();

    Unbinder EMPTY = new Unbinder(){

        @Override
        public void unbind() {

        }
    };

}
