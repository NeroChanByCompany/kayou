package com.nut.locationservice.app.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 车辆基础信息
 *
 * @author yzl
 * @email yzl379131121@163.com
 * @date 2021-06-16 19:22:48
 */
@Data
@TableName("hy_car")
public class HyCarEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 表主键
     */
    @TableId
    private Long carId;
    /**
     * 服务分区编码(内蒙：国标码或者自定义编码)
     */
    private Integer districtId;
    /**
     * 车牌号
     */
    private String carCph;
    /**
     * 车牌颜色（数据字典）
     */
    private Integer carColor;
    /**
     * 终端ID
     */
    private Long carTerminal;
    /**
     * 车组ID
     */
    private Long carTeamId;
    /**
     * 服务状态（数据字典）
     */
    private Integer carState;
    /**
     * 车辆查询密码(要设置默认值：888888；密文MD5加密字符串)
     */
    private String carPw;
    /**
     * 注册车辆账户
     */
    private String carAccountName;
    /**
     * 车辆自编号
     */
    private String carAutoNumber;
    /**
     * 入网时间
     */
    private Long carDate;
    /**
     * 车籍地
     */
    private String carPlace;
    /**
     * 车辆所属公司
     */
    private String carCompany;
    /**
     * 逻辑删除标记(1:删除，0：正常，default：0)
     */
    private Integer delFlag;
    /**
     * 车辆类型（数据字典）
     */
    private Integer carType;
    /**
     * 运输行业类型(数据字典)
     */
    private Integer carTrade;
    /**
     * 是否服务到期停止
     */
    private Integer carServiceStop;
    /**
     * 服务期开始时间
     */
    private Long serviceBegin;
    /**
     * 服务期结束时间
     */
    private Long serviceEnd;
    /**
     * 入网时间
     */
    private Long nettingTime;
    /**
     * 入网位置-经度
     */
    private Long nettingLog;
    /**
     * 入网位置-纬度
     */
    private Long nettingLat;
    /**
     * 底盘号
     */
    private String chassisNum;
    /**
     * 结构号
     */
    private String structureNum;
    /**
     * 油箱容量
     */
    private String oilCapacity;
    /**
     * 锁车状态：0（未激活未锁车00）1（未激活锁车01）2（激活未锁车10）3（激活锁车11）
     */
    private Integer lockStauts;
    /**
     * 北斗终端ID
     */
    private Long carTerminalId;
    /**
     * 1,平台录入，其他：DMS
     */
    private Integer autoFlag;
    /**
     * 防拆方案
     */
    private Integer tamperStatue;
    /**
     * 操作备注
     */
    private String operateCommon;
    /**
     * 操作人
     */
    private String operateUser;
    /**
     * 操作时间
     */
    private Long operateDate;
    /**
     * 位置云防拆通知状
     * <p>
     * 态
     */
    private Integer tamperNoticeStatus;
    /**
     * 下线时间
     */
    private Long offlineTime;
    /**
     * 出库时间
     */
    private Long removalTime;
    /**
     * 末次注册时间
     */
    private Long registerTime;
    /**
     * 操作人IP
     */
    private String operateIp;
    /**
     * 防控时效
     */
    private Long carFkdate;
    /**
     * 电池类型
     */
    private Integer batteryType;
    /**
     * 电池批次
     */
    private Integer batteryBatches;
    /**
     * 车型码
     */
    private String carModelCode;
    /**
     * 上线时间
     */
    private Long onlineTime;
    /**
     * 车辆型号
     */
    private String carModel;
    /**
     * 入库位置-经度
     */
    private Long warehouseLog;
    /**
     * 入库位置-纬度
     */
    private Long warehouseLat;
    /**
     * 入库时间
     */
    private Long warehouseTime;
    /**
     * 订单号
     */
    private String orderNumber;
    /**
     * 同步时间
     */
    private Date syncTime;
    /**
     * 锁车方案
     */
    private String lockMethod;
    /**
     * 整车二维码
     */
    private String qrCode;
    /**
     * 所属金融公司
     */
    private Long financingCompany;
    /**
     * 付款方式
     */
    private Long payType;
    /**
     * 创建者ID
     */
    private Long createAccountId;
    /**
     * 安装单位(服务站)ID
     */
    private Long createStationId;
    /**
     * 安装类型（1、前装;2、后装）
     */
    private Long instalType;
    /**
     * 工厂代码
     */
    private String vfactory;
    /**
     *
     */
    private Long teamIdOld;
    /**
     * 扫码车辆在库状态 0：未下线空入 1：下线空入
     */
    private Integer state;
    /**
     * 老VIN
     */
    private String vinOld;
    /**
     * 老底盘号
     */
    private String chassisNumOld;
    /**
     * 1:F9车辆; 0:非F9车辆
     */
    private Integer isf9;
    /**
     * 车辆驱动ID
     */
    private Long carDriveId;
    /**
     *
     */
    private Long stationTime;
    /**
     * 备注一
     */
    private String remarkOne;
    /**
     * 备注二
     */
    private String remarkTwo;
    /**
     * 是否消贷车 1为消贷车，0不为消贷车
     */
    private String loanFlag;
    /**
     * 国六发动机标准车辆向康明斯注册状态：-1:未注册 0：注册失败  1：注册成功
     */
    private Integer cumminsStatus;
    /**
     * 基础车型
     */
    private String prodCode;
    /**
     * 车型名称
     */
    private String mateName;
    /**
     * 在途时间
     */
    private Long transitTime;
    /**
     * 经销商收车时间
     */
    private Long agencyCloseTime;
    /**
     * 车辆入库时间(车厂)
     */
    private Long factoryWarehouseTime;
    /**
     * 车辆数量 1:军车,2:民车 mes同步
     */
    private String carAttributes;
    /**
     * 车辆颜色 mes同步
     */
    private String vehicleColor;
    /**
     * 1:燃油车 2:燃气车
     */
    private Integer fuel;
    /**
     * 金融公司添加车辆时间
     */
    private Date addFinanceCompanyTime;
    /**
     * 车型结构号
     */
    private String structureNo;
    /**
     *
     */
    private String remark;
    /**
     * 车联网flag  1：带车联网配置；2：不带车联网配置
     */
    private Integer clwflag;

}
