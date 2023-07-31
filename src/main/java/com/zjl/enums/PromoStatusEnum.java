package com.zjl.enums;

/**
 * @author zhangjiling
 * @date 2023/7/28 21:58
 */
public enum PromoStatusEnum {
    READY(1, "即将开始"),
    RUNING(2, "正在进行"),
    NOPROMO(0, "无活动")
    ;

    private Integer code;
    private String description;



    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    PromoStatusEnum(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public static PromoStatusEnum findByCode(int code) {
        for(PromoStatusEnum promoStatusEnum : PromoStatusEnum.values()) {
            if(promoStatusEnum.getCode() == code) {
                return promoStatusEnum;
            }
        }
        return null;
    }
}
