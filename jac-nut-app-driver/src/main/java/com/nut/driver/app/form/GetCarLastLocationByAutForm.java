package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author liuBing
 * @Classname GetCarLastLocationByAutForm
 * @Description TODO
 * @Date 2021/6/24 15:43
 */
@Data
public class GetCarLastLocationByAutForm extends BaseForm {

    /**
     * 通信号集合
     */
    @NotNull(message = "通信号不能为空")
    @ApiModelProperty(name = "terminalIdList", notes = "终端ID列表", dataType = "List<Long>")
    private List<Long> terminalIdList;

    @Override
    public String toString() {
        StringBuilder sb = null;
        if (CollectionUtils.isNotEmpty(terminalIdList)) {
            sb = new StringBuilder();
            sb.append("[");
            for (Long star : terminalIdList) {
                sb.append(star);
                sb.append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }
        String terminalId = null;
        if (sb != null && sb.length() > 0) {
            terminalId = sb.toString();
        }


        return "GetCarLastLocationByAutCommand{" +
                "terminalId='" + terminalId + '\'' +
                '}' + super.toString();
    }
}