package com.nut.servicestation.app.domain;

import com.nut.common.base.BaseForm;
import com.nut.common.result.HttpCommandResultWithData;
import lombok.Data;

/**
 * Created by wangjc on 2017/6/19.
 */
@Data
public class IntegralEntity {
    private String commandName;
    private String distId;
    private BaseForm command;
    private HttpCommandResultWithData result;

}
