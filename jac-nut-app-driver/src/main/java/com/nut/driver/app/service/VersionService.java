package com.nut.driver.app.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.nut.driver.app.entity.VersionEntity;
import com.nut.driver.app.form.OperationManualListForm;

import java.util.List;
import java.util.Map;

/**
 * 版本更新说明表
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 14:17:55
 */
public interface VersionService extends IService<VersionEntity> {

    List getUserGuide(OperationManualListForm form);
}

