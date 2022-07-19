package com.nut.driver.app.service;

import com.nut.driver.app.domain.FltFleetLine;
import com.nut.driver.app.form.LineForm;
import com.nut.common.result.HttpCommandResultWithData;

import java.util.List;


public interface LineService  {

     HttpCommandResultWithData addLine(LineForm command) throws Exception;

     HttpCommandResultWithData list() throws Exception;

     HttpCommandResultWithData deleteLine(LineForm command) throws Exception;

     HttpCommandResultWithData updateLine(LineForm command) throws Exception;

     HttpCommandResultWithData detail(LineForm command) throws Exception;

     /**
      * 查询用户或车队有关的线路
      * @param form
      * @return
      */
     List<com.nut.app.driver.entity.FitFleetLineEntity> userList(LineForm form);
}
