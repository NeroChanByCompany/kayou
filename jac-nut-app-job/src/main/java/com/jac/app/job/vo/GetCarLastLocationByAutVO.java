package com.jac.app.job.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author liuBing
 * @Classname GetCarLastLocationByAutVO
 * @Description TODO
 * @Date 2021/8/12 16:47
 */
@Data
@Accessors(chain = true)
public class GetCarLastLocationByAutVO implements Serializable {
    private static final long serialVersionUID = 5505982011653233836L;
    /**
     * 通信号集合
     */
    @NotNull(message = "通信号不能为空")
    private List<Long> terminalIdList;
}
