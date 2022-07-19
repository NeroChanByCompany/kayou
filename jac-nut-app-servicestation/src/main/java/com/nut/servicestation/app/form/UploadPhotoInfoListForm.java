package com.nut.servicestation.app.form;


import com.nut.common.base.BaseForm;
import lombok.Data;

import java.util.List;

@Data
public class UploadPhotoInfoListForm extends BaseForm {

    private List<UploadPhotoInfoForm> listCommand;

}
