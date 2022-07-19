package com.nut.driver.app.service;

import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.UserContactAddForm;
import com.nut.driver.app.form.UserContactDeleteForm;
import com.nut.driver.app.form.UserContactsForm;

public interface ContactService {

    /**
     * 增加
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData add(UserContactAddForm command);
    /**
     * 删除
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData delete(UserContactDeleteForm command);
    /**
     * 列表(司机)
     *
     * @param command 接口参数
     * @return HttpCommandResultWithData
     */
    HttpCommandResultWithData query(UserContactsForm command);

    HttpCommandResultWithData addContact(UserContactAddForm form);
}
