package com.nut.locationservice.common.util;

import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common.util
 * @Author: yzl
 * @CreateTime: 2021-06-16 19:38
 * @Version: 1.0
 */

public class ListUtil {

    public static boolean isEmpty(List list) {
        if (list == null || list.isEmpty()) {
            return true;
        }
        return false;
    }
}
