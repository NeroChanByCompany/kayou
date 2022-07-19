package com.nut.servicestation.app.form;

import com.nut.servicestation.app.domain.WorkOrder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class WorkOrderForm extends WorkOrder implements Serializable {

    private static final long serialVersionUID = -1509334462817156210L;
}