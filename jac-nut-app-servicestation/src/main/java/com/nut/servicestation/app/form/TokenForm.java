package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

import java.util.List;

/**
 * @Author zhangling
 * @Date 2022/5/26 11:21
 * @Description
 */
@Data
public class TokenForm{

    private List<Long> accoutIdList;
}
