package com.nut.driver.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;

/**
 * @Description 邦车增加优惠券Form
 */
@Data
public class ScoreBindcarReceiveForm extends BaseForm {

    private String vin;


    @Override
    public String toString() {
        return "ScoreBuycarReceiveCommand{" +
                "vin=" + vin +
                '}';
    }

}
