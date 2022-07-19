package com.nut.servicestation.app.dao;

import com.nut.servicestation.app.domain.AppointmentItems;

import java.util.List;

/*
 *  @author wuhaotian 2021/7/5
 */
public interface AppointmentItemsDao {

    List<AppointmentItems> selectAllItems();
}
