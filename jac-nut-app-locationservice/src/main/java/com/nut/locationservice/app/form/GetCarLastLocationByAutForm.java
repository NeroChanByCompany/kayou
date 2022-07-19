package com.nut.locationservice.app.form;

import com.nut.common.annotation.NutFormValidator;
import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.apache.commons.collections.CollectionUtils;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @Description: 针对行程整合redis拆分，末次位置信息取位置云
 *               不再取行程整合保存的末次位置
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.locationstation.app.locationService.form
 * @Author: yzl
 * @CreateTime: 2021-06-17 10:55
 * @Version: 1.0
 */
@Data
@Accessors(chain = true)
@ToString
@NutFormValidator
public class GetCarLastLocationByAutForm extends BaseForm implements Serializable {

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
