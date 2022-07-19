package com.nut.servicestation.app.domain;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangjc on 2017/6/22.
 */
@Component
public class IntegralCommandList {

    public static String getIntegralFlg = "true";

    public static List getCommandList() {
        // 需要处理的Command（注册、加车、预约、评价、邀请注册、邀请注册首次登录、购车优惠券积分）
        List commandList = new ArrayList();
        commandList.add("RegisterCommand");
        commandList.add("UserCarAddCommand");
        commandList.add("ReviewOrderCommand");
        commandList.add("ScanReceiveCommand");
        commandList.add("RegisterInviteCommand");
        commandList.add("LoginRorFirstInviteCommand");
        commandList.add("CouponBuyCarCommand");
        commandList.add("CrmAddScoreToAccountCommand");
        commandList.add("QuesStatisEvalPurchaseCommand");
        commandList.add("QuesStatisEvalPersonalCommand");
        return commandList;
    }
}
