package com.nut.common.validator;

import cn.hutool.extra.emoji.EmojiUtil;
import com.nut.common.annotation.EmojiForbid;
import com.nut.common.utils.StringUtil;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.common
 * @Author: yzl
 * @CreateTime: 2021-06-21 19:22
 * @Version: 1.0
 */

public class EmojiForbidValidator implements ConstraintValidator<EmojiForbid, String> {

    @Override
    public void initialize(EmojiForbid emojiForbid) {

    }

    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext) {

        // 如果该属性不为空
        if (StringUtil.isNotEmpty(str)) {
            // 如果包含Emoji表情，校验失败
            if (EmojiUtil.containsEmoji(str)) {
                return false;
            }
        }
        return true;
    }

}
