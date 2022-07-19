package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @BelongsProject: jac-nut-app
 * @BelongsPackage: com.nut.servicestation.app.form
 * @Author: yzl
 * @CreateTime: 2021-07-27 09:51
 * @Version: 1.0
 */
@Data
public class BranchListForm extends BaseForm {

    private String branchName;
    private String province;
    private String city;
    private String branchType;

    /**
     * 票据
     */
    private String ticket;

    /**
     * 发放或兑换，1发放 2兑换
     */
    private String giveOrExchange;

    /**
     * 优惠券ID
     */
    private String infoId;
    private String agencyFlgId;
    private List<String> agencyFlgIds;

    public List<String> getAgencyFlgIdsList(){

        List<String> list = new ArrayList<>();
        if (null != agencyFlgId && !agencyFlgId.equals("")){
            String[] agencyFlgIdArr = agencyFlgId.split(",");
            for (int i = 0; i < agencyFlgIdArr.length ; i++){
                list.add(agencyFlgIdArr[i]);
            }
        }

        return list;
    }

}
