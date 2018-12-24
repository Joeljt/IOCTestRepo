package com.ljt.ioc;

import android.app.Activity;
import android.view.View;

/**
 * Created by lijiateng on 2018/12/24.
 */

public class Utils {

    public static <T extends View> T findViewById(Activity activity, int resId) {
        return activity.findViewById(resId);
    }

}
