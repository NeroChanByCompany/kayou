package com.nut.driver.app.controller;

import com.nut.common.annotation.LoginRequired;
import com.nut.common.annotation.RequestJson;
import com.nut.common.result.ECode;
import com.nut.common.result.HttpCommandResultWithData;
import com.nut.driver.app.form.QuerySpareStockForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.nut.driver.app.service.SpareStockService;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-28 16:17:56
 */
@RestController
@Slf4j
@Api(tags = "配件")
public class SpareStockController {

    @Autowired
    private SpareStockService spareStockService;

    /**
    * @Description：${配件查询}
    * @author YZL
    * @data 2021/6/28 16:46
    */
    @PostMapping(value = "/querySpareStock")
    @ApiOperation("配件查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "searchType" , value = "搜索类型" , dataType = "Integer"),
            @ApiImplicitParam(name = "searchInfo" , value = "搜索条件" , dataType = "String"),
            @ApiImplicitParam(name = "lon" , value = "经度" , dataType = "String"),
            @ApiImplicitParam(name = "lat" , value = "纬度" , dataType = "String")
    })
    @CrossOrigin
    @LoginRequired
    public HttpCommandResultWithData querySpareStock(@ApiIgnore @RequestJson QuerySpareStockForm form){
        log.info("querySpareStock start param:{}",form);
        HttpCommandResultWithData result = new HttpCommandResultWithData();
        try {
            result = spareStockService.getSpareStockList(form);
        } catch (Exception e) {
            result.setResultCode(ECode.SERVER_ERROR.code());
            result.setMessage("获取配件列表失败!");
            log.error(e.getMessage(), e);
        }
        log.info("spareStockService end return:{}",result.getData());
        return result;
    }

}
