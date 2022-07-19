package com.nut.common.base;

import cn.hutool.core.date.DateUtil;
import com.nut.common.annotation.*;
import com.nut.common.utils.RegexpUtils;
import com.nut.common.exception.ParamException;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

/**
 * @description: Controller业务基类
 * @author: hcb
 * @createTime: 2021/01/20 21:02
 * @version:1.0
 */
@Data
public class BaseController {

    private Logger log = LoggerFactory.getLogger(BaseController.class);

    /**
     * 参数校验
     *
     * @throws Exception
     */
    public void formValidate(Object form) throws Exception {
        Class clazz = form.getClass();
        NutFormValidator nutFormValidator = (NutFormValidator) clazz.getAnnotation(NutFormValidator.class);
        if (Objects.isNull(nutFormValidator)) {
            log.error("{}未使用注解[NutFormValidator]", clazz.getName());
            throw new Exception("参数校验失败，未使用注解[NutFormValidator]");
        }
        // 参数默认值处理
        this.defaultValue(form);
        // 参数的值不为空校验
        this.notBlankValidate(form);
        this.notNullValidate(form);
        // 字符串参数长度校验
        this.lengthValidate(form);
        // List参数长度校验
        this.sizeValidate(form);
        // 数字校验
        this.numberValidate(form);
        // 枚举校验
        this.enumValidate(form);
        // 时间格式校验
        this.dateFormatValidate(form);
        // 正则表达式校验
        this.patternValidate(form);
    }

    private void defaultValue(Object form) throws Exception {
        DefaultValue defaultValue;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            defaultValue = field.getAnnotation(DefaultValue.class);
            if (Objects.nonNull(defaultValue)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.isNull(value) || StringUtils.isEmpty(value.toString())) {
                    if (field.getType() == Integer.class) {
                        field.set(form, Integer.parseInt(defaultValue.value()));
                    } else if (field.getType() == Long.class) {
                        field.set(form, Long.parseLong(defaultValue.value()));
                    } else {
                        field.set(form, defaultValue.value());
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void notNullValidate(Object form) throws Exception {
        NotNull notNull;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            notNull = field.getAnnotation(NotNull.class);
            if (Objects.nonNull(notNull)) {
                field.setAccessible(true);
                Object value = field.get(form);
                String message;
                if ("{javax.validation.constraints.NotNull.message}".equals(notNull.message())) {
                    message = "参数[" + field.getName() + "]不能为空";
                } else {
                    message = notNull.message();
                }
                if (Objects.isNull(value)) {
                    throw new ParamException(message);
                }

                field.setAccessible(false);
            }
        }
    }

    private void notBlankValidate(Object form) throws Exception {
        NotBlank notBlank;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            notBlank = field.getAnnotation(NotBlank.class);
            if (Objects.nonNull(notBlank)) {
                field.setAccessible(true);
                Object value = field.get(form);
                String message;
                if ("{javax.validation.constraints.NotBlank.message}".equals(notBlank.message())) {
                    message = "参数[" + field.getName() + "]不能为空";
                } else {
                    message = notBlank.message();
                }
                if (Objects.isNull(value) || StringUtils.isEmpty(value.toString())) {
                    throw new ParamException(message);
                }

                field.setAccessible(false);
            }
        }
    }

    private void lengthValidate(Object form) throws Exception {
        Length length;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            length = field.getAnnotation(Length.class);
            if (Objects.nonNull(length)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.nonNull(value)) {
                    String v = value.toString();
                    String message = StringUtils.isEmpty(length.message()) ? "参数[" + field.getName() + "]长度不合法" : length.message();
                    if (v.length() < length.minLength()) {
                        throw new ParamException(message);
                    }
                    if (v.length() > length.maxLength()) {
                        throw new ParamException(message);
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void sizeValidate(Object form) throws Exception {
        SizeValidator sizeValidator;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            sizeValidator = field.getAnnotation(SizeValidator.class);
            if (Objects.nonNull(sizeValidator)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.nonNull(value) && value instanceof List) {
                    List<Object> objList = (List<Object>) value;
                    String message = StringUtils.isEmpty(sizeValidator.message()) ? "参数[" + field.getName() + "]size不合法" : sizeValidator.message();
                    if (objList.size() < sizeValidator.minSize()) {
                        throw new ParamException(message);
                    }
                    if (objList.size() > sizeValidator.maxSize()) {
                        throw new ParamException(message);
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void numberValidate(Object form) throws Exception {
        NumberValidator numberValidator;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            numberValidator = field.getAnnotation(NumberValidator.class);
            if (Objects.nonNull(numberValidator)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.nonNull(value)) {
                    String v = value.toString();
                    String message = StringUtils.isEmpty(numberValidator.message()) ? "参数[" + field.getName() + "]必须为数字" : numberValidator.message();
                    try {
                        new BigDecimal(v);
                    } catch (Exception e) {
                        throw new ParamException(message);
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void enumValidate(Object form) throws Exception {
        EnumValidator enumValidator;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            enumValidator = field.getAnnotation(EnumValidator.class);
            if (Objects.nonNull(enumValidator)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.nonNull(value)) {
                    boolean isEnum = false;
                    String[] values = enumValidator.values();
                    for (String v : values) {
                        if (String.valueOf(value).equals(v)) {
                            isEnum = true;
                            break;
                        }
                    }
                    if (!isEnum) {
                        throw new ParamException(enumValidator.message());
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void dateFormatValidate(Object form) throws Exception {
        DateFormat dateFormat;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            dateFormat = field.getAnnotation(DateFormat.class);
            if (Objects.nonNull(dateFormat)) {
                field.setAccessible(true);

                Object value = field.get(form);
                if (Objects.nonNull(value)) {
                    String v = value.toString();
                    String format = dateFormat.format();
                    Date date;
                    try {
                        date = DateUtil.parse(v, format);
                    } catch (Exception e) {
                        throw new ParamException(dateFormat.message());
                    }
                    if (Objects.isNull(date) || !v.equalsIgnoreCase(DateUtil.format(date, format))) {
                        throw new ParamException(dateFormat.message());
                    }
                }

                field.setAccessible(false);
            }
        }
    }

    private void patternValidate(Object form) throws Exception {
        Pattern pattern;
        Class clazz = form.getClass();
        List<Field> fields = this.getAllFields(clazz);
        for (Field field : fields) {
            pattern = field.getAnnotation(Pattern.class);
            if (null != pattern) {
                field.setAccessible(true);
                Object value = field.get(form);
                if (null != value) {
                    String v = value.toString();
                    if (null != pattern.regexp()){
                        String regexp = pattern.regexp();
                        if (!RegexpUtils.validateInfo(v, regexp)) {
                            throw new ParamException(pattern.message());
                        }
                    }
                }
                field.setAccessible(false);
            }
        }
    }

    /**
     * 获取本类及其父类的属性
     *
     * @param clazz 当前类对象
     * @return 字段数组
     */
    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        while (clazz != null) {
            fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
            clazz = clazz.getSuperclass();
        }
        return fieldList;
    }
}
