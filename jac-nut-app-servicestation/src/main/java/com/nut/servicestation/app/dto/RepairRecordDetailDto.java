package com.nut.servicestation.app.dto;

import lombok.Data;

import java.util.List;

@Data
public class RepairRecordDetailDto {
    /** 故障描述 */
    private String faultDescribe;
    /** 处理方式 */
    private Integer dealType;
    /** 服务类型 */
    private String serviceType;
    /** 费用类型 */
    private String chargeType;
    /** 付费方式 */
    private String payType;
    /**
     * 服务类型或费用类型
     * 对应前端展示的一级选项（服务类型和费用类型糅合到一个字段）
     */
    private String type1;
    /**
     * 处理方式或付费方式
     * 对应前端展示的二级选项（处理方式和付费方式糅合到一个字段）
     */
    private String type2;

    /** 是否调件 */
    private String transferParts;
    /** 图片种类列表 */
    private List<RepairRecordPhotoDetailDto> list;

    /** 配件码 */
    private List<PhotoType> partCodeList;

    @Data
    public static class PhotoType {
        private String photoTypeCode;
        private List<Part> list;


        public static class Part {

            public Part(String partCode) {
                this.partCode = partCode;
            }

            private String partCode;

            public String getPartCode() {
                return partCode;
            }

            public void setPartCode(String partCode) {
                this.partCode = partCode;
            }
        }
    }
}
