package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import lombok.Data;

import java.util.List;

/**
 * 上传外出救援轨迹点（多个）接口
 */
@Data
public class UploadRescueRoutePointListForm extends BaseForm {
    /**
     * 轨迹点列表
     */
    private List<UploadRescueRoutePointForm> pointList;

}
