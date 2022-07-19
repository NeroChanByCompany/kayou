package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

import java.util.List;

/**
 * @author liuBing
 * @Classname ForumForGetUserListForm
 * @Description TODO
 * @Date 2021/7/23 15:23
 */
@Data
public class ForumForGetUserListForm extends BaseForm {
    private List<String> idList;
}
