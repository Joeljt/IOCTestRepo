package com.ljt.ioc;

import android.app.Activity;

import java.lang.reflect.Constructor;

/**
 * Created by lijiateng on 2018/12/19.
 */

public class IOCUtil {

    public static Unbinder bind(Activity activity) {

        String viewBindClassName = activity.getClass().getName() + "_ViewBinding";
        try {
            Class<? extends Unbinder> unBindderClass = (Class<? extends Unbinder>) Class.forName(viewBindClassName);
            Constructor<? extends Unbinder> constructor = unBindderClass.getDeclaredConstructor(activity.getClass());
            return constructor.newInstance(activity);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return Unbinder.EMPTY;

    }

}
