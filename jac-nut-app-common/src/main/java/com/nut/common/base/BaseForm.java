package com.nut.common.base;

import com.nut.common.utils.RegexpUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.validation.constraints.Pattern;

/**
 * @description:
 * @author: hcb
 * @createTime: 2021/01/20 17:05
 * @version:1.0
 */
@Data
public abstract class BaseForm {

    /**
     * 当前登录用户的token
     */
    private String token;

    /**
     * 当前登录用户的ID
     */
    private String userId;

    /**
     * 当前登录用户的所属机构ID
     */
    private String orgId;
    /**
     * 当前登录用户
     */
    private String userName;

    /**
     * 排序字段
     */
    private String sort;

    /**
     * 排序方式
     */
    private Integer sortType;

    /**
     * 每页行数
     */
    @Pattern(regexp = RegexpUtils.POSITIVE_NUMBER_BLANK_REGEXP, message = "page_size应为正整数")
    private String page_size = "20";

    /**
     * 当前页
     */
    @Pattern(regexp = RegexpUtils.POSITIVE_NUMBER_BLANK_REGEXP, message = "page_number应为正整数")
    private String page_number = "1";

    /**
     * 总页数
     */
    @Pattern(regexp = RegexpUtils.POSITIVE_NUMBER_BLANK_REGEXP, message = "page_total应为正整数")
    private String page_total;

    /**
     * 自增id，用户app存储用户id
     */
    private Long autoIncreaseId;

    /**
     * B端AccountId
     */
    private String accId;

    /**
     * B端用ucId,C端暂时用userid字段
     */
    private String ucId;

    /**
     * B端用accPhone，C端暂时未使用
     */
    private String accPhone;

    /**
     * B端用户姓名
     */
    private String accountNickname;

    /**
     * B端用户姓名
     */
    private String userAttribute;

    /**
     * 操作系统
     */
    private String osName;

    /**
     * 设备型号
     */

    private String deviceModel;

    /**
     * 版本号
     */
    private String version;

    /**
     * 版本号类型（1安卓/2苹果）
     */
    private String versionType;

    /**
     * APP类型
     */
    private String appType;


    /**
     * 获取排序类型
     *
     * @return sortType
     */
    public Integer getSortType() {
        if (sortType == null) {
            this.sortType = 1;
        }
        return sortType;
    }


    public String getPage_size() {
        if (StringUtils.isEmpty(page_size)) {
            page_size = "20";
        }
        return page_size;
    }

    public String getPage_number() {
        if (StringUtils.isEmpty(page_number)) {
            return "1";
        }
        return page_number;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
