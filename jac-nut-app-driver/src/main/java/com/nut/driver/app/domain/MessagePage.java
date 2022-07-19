package com.nut.driver.app.domain;

import com.nut.driver.app.dto.QueryMessageDetailListDto;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author liuBing
 * @Classname MessagePage
 * @Description TODO 分页结果集
 * @Date 2021/8/5 17:15
 */
@Data
@Accessors(chain = true)
public class MessagePage<E> implements Serializable{

    private static final long serialVersionUID = -280930777626281735L;
    /**
     * 总数
     */
    private Long total;

    /**
     * 总页数
     */
    private Integer pages;

    /**
     * 结果集
     */
    private List<E> result;
}
