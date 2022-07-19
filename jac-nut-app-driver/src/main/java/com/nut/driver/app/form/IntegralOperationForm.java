package com.nut.driver.app.form;


import com.nut.common.base.BaseForm;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 添加积分模型
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.driver.app.driver.form
 * @Author: yzl
 * @CreateTime: 2021-06-15 19:12
 * @Version: 1.0
 */
@Data
@ApiModel("APP端添加积分Entity")
public class IntegralOperationForm extends BaseForm {


    /**
     * 操作动作
     * 0：添加积分
     * 1：减掉积分
     */
    @ApiModelProperty(name = "operationId",notes = "加减积分",dataType = "Integer")
    private Integer operationId;

    /**
     * 积分（加减）数量
     */
    @ApiModelProperty(name = "integralCounts",notes = "积分（加减）数量",dataType = "Integer")
    private Integer integralCounts;

    /**
     * 积分操作动作
     * 1：注册
     * 2：邦车
     * 3：回复
     * 4：发帖
     * 5：分享
     * 6：服务预约
     * 7：服务评价
     * 8：兑吧积分消费成功（消费成功，积分余额减少）
     * 9：兑吧积分消费失败（消费失败，回补消费时减去的积分）
     * <p>
     * 21：活动发帖审核通过，送200积分
     * 22：活动故事点赞破200，送300积分
     */
    @ApiModelProperty(name = "actionId",notes = "积分操作动作",dataType = "String")
    private String actionId;
    @ApiModelProperty(name = "consumeOrder",notes = "消费",dataType = "String")
    private String consumeOrder;
    @ApiModelProperty(name = "orderNum",notes = "订单号",dataType = "String")
    private String orderNum;

}
