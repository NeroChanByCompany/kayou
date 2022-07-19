package com.nut.servicestation.app.form;

import com.nut.servicestation.app.domain.TrackPoint;
import lombok.Data;

import java.util.List;

/**
 * @Description: 实际距离请求参数
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-08-26 13:51
 * @Version: 1.0
 */
@Data
public class DistanceTrueForm {

    private List<TrackPoint> list;
}
