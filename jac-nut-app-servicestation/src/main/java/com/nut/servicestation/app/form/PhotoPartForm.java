package com.nut.servicestation.app.form;

import com.nut.common.base.BaseForm;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class PhotoPartForm extends BaseForm {

    @Length(max = 8, message = "配件码类型过长！")
    private String photoTypeCode;
    @Length(max = 100, message = "配件码过长！")
    private String partCode;

    public String getPhotoTypeCode() {
        return photoTypeCode;
    }

    public void setPhotoTypeCode(String photoTypeCode) {
        this.photoTypeCode = photoTypeCode;
    }

    public String getPartCode() {
        return partCode;
    }

    public void setPartCode(String partCode) {
        this.partCode = partCode;
    }


}
