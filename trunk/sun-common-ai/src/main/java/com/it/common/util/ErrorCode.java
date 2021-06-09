package com.it.common.util;

public enum ErrorCode {
	
	
	SUCCESS("成功", 200),
    ERROR("逻辑错误", 500),
    ERROR_PARAM("参数不正确", 401),
    ERROR_REQUEST("请求失败", 400),
    ERROR_NOT_LOGIN("未登录", 510),
    ERROR_PRIVILE_NO("权限不足", 511),
    BLACK_LIST("禁止调用", 1010),//当和第三方结束合作时，会禁止接口调用
    LOAN_CONFUSE("系统拒绝中,部分服务不可用或正在维护升级", 1014),
    SMS_AUTH_ERROR("短信验证码验证失败", 1015),
    PHONE_AUTH_ERROR("手机后四位验证失败", 1016),
    LICENSE_ERROR("许可码错误", 1018),
    AUTHCODEPV_ERROR("剩余授权可用访问量为0,请购买授权码",1019),
    AUTHCODEBN_ERROR("剩余授权可用新增建筑为0,请购买授权码",1020),
    AUTHCODEFN_ERROR("剩余授权可用新增楼层为0,请购买授权码",1021),
    SECRET_KEY_ERROR("secret_key无效,请重新获取",1055),
    CRE_SNAPSHOOT_ERROR("创建地图快照失败",1066),
    IMPORT_ERROR("导入文件无效",2031);
    // 成员变量  
    private String msg;  
    private int status;  
    // 构造方法  
    private ErrorCode(String msg, int status) {  
        this.msg = msg;  
        this.status = status;  
    }  
    // 普通方法  
    public static String getName(int status) {  
        for (ErrorCode e : ErrorCode.values()) {  
            if (e.getStatus() == status) {  
                return e.msg;  
            }  
        }  
        return null;  
    }  
    // get set 方法  
    public String getMsg() {  
        return msg;  
    }  
    public void setMsg(String msg) {  
        this.msg = msg;  
    }  
    public int getStatus() {  
        return status;  
    }  
    public void setStatus(int status) {  
        this.status = status;  
    } 

	
	
}
