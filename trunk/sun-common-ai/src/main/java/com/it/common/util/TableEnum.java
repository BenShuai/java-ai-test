package com.it.common.util;

import org.apache.ibatis.ognl.Evaluation;

/**
 * 所有表的表名
 */
public enum TableEnum {

    //品牌库
    BRAND_LIBRARY("brand_library","品牌库"),

    //新增类型操作日志
    CREATE_LOG("create_log","新增类型操作日志"),

    //删除类型操作日志
    DELETE_LOG("delete_log","删除类型操作日志"),

    //"是否显示"的总开关配置
    MAP_BASE_CONFIG("map_base_config","总配置开关"),

    //建筑或园区信息
    MAP_BUILDINGS("map_buildings","建筑或园区信息"),

    //楼层信息
    MAP_FLOOR("map_floor","楼层信息"),

    //楼层分组
    MAP_FLOOR_GROUP("map_floor_group","楼层分组"),

    //路径表
    MAP_PATH("map_path","路径表"),

    //区块point
    MAP_POINT("map_point","区块"),

    //区块分组
    MAP_POINT_GROUP("map_point_group","区块分组"),

    //区块类型
    MAP_POINT_TYPE("map_point_type","区块类型"),

    //IDS活动类型
    IDS_ACT_TYPE("ids_act_type","IDS活动类型"),

    //IDS活动信息
    IDS_ACTIVITIES("ids_activities","IDS活动信息"),

    //IDS广告类型表
    IDS_ADV_TYPE("ids_adv_type","IDS广告类型表"),

    //IDS广告表
    IDS_ADV("ids_adv","IDS广告表"),

    //设备详情表
    IDS_DEVICE("ids_device","设备详情表"),

    //设备区域表
    IDS_DEVICE_REGION("ids_device_region","设备区域表"),

    //IDS人脸信息表
    IDS_FACE_INFO("ids_face_info","IDS人脸信息表"),

    //IDS人员使用操作详情表
    IDS_USE_INFO("ids_use_info","IDS人员使用操作详情表"),

    //IDS使用时长详情表
    IDS_USE_TIME("ids_use_time","IDS使用时长详情表"),

    //阿里云物联网对接数据
    MAP_ALIYUN("map_aliyun","阿里云物联网对接数据"),

    //写字楼信息数据表[威思客对接用]
    MAP_POINT_WSK("map_point_wsk","写字楼信息绑定"),

    //顶点数据表
    MAP_VERTEX("map_vertex","顶点数据表"),

    //地图调用授权码
    PROJECT_APPKEY("project_appkey","地图调用授权码"),

    //项目表
    PROJECTS("projects","项目表"),

    //用户账号信息表
    SYS_ACCOUNT("sys_account","用户账号信息表"),

    //系统操作日志
    SYS_LOG("sys_log","系统操作日志"),

    //菜单表
    SYS_MENU("sys_menu","菜单表"),

    //用户登录账户信息
    SYS_USER("sys_user","用户登录账户信息"),

    //更新类型操作日志
    UPDATE_LOG("update_log","更新类型操作日志"),

    //项目品牌数据
    MAP_BRAND("map_brand","项目品牌数据"),

    //品牌与区块绑定关系数据
    MAP_BRAND_SHOP("map_brand_shop","品牌与区块绑定关系数据"),

    //系统配置表
    SYS_CONFIG("sys_config","系统配置表"),

    //业态类型信息表【品牌类型】
    MAP_BRAND_TYPE("map_brand_type","业态类型信息表"),

    //业态类型和项目品牌信息关联表
    MAP_BRAND_TYPE_RELATION("map_brand_type_relation","业态类型和项目品牌信息关联表"),

    //活动表
    MAP_EVENTS("map_events","活动表"),

    //设备信息表
    MAP_DEVICE_INFO("map_device_info","设备信息表"),

    //广告信息表
    MAP_ADVERTISING("map_advertising","广告信息表"),

    //权限表
    MAP_AUTHO("map_autho","权限表"),

    //弘扬租户表
    HY_TENANTS("hy_tenants","弘阳租户表"),

    //评论表
    MAP_EVALUATION("map_evaluation","评论表"),

    //弘阳展厅表
    HY_HALL("hy_hall","弘阳展厅表"),

    //素材类型表
    LIBRARY_INFO("library_info","素材类型表"),

    //云招商-预约信息
    CI_APPOINTMENT("ci_appointment","云招商-预约信息"),

    //云招商-项目介绍图
    CI_PJ_IMGS("ci_pj_imgs","云招商-项目介绍图"),

    //云招商-首页轮播
    CI_HOMELOOP("ci_homeloop","云招商-首页轮播"),

    //云招商-集团信息
    CI_PJ_GROUP("ci_pj_group","云招商-集团信息"),

    //项目图层列表
    MAP_PJ_LAYER("map_pj_layer","项目图层列表"),

    //DP设备详情表
    DP_DEVICE("dp_device","DP设备详情表"),

    //DP设备区域表
    DP_DEVICE_REGION("dp_device_region","DP设备区域表"),

    //DP素材表
    DP_MATERIAL("dp_material","DP素材表"),

    //DP布局表
    DP_LAYOUT("dp_layout","DP布局表"),

    //DP布局详情子表
    DP_LAYOUT_INFO("dp_layout_info","DP布局详情子表"),

    //DP节目发布表
    DP_PROGRAM_PLAY("dp_program_play","DP节目发布表"),

    //DP节目表
    DP_PROGRAM("dp_program","DP节目表"),

    //DP节目表详情
    DP_PROGRAM_INFO("dp_program_info","DP节目表详情"),

    //权限用户拥有资源表
    AUTH_USER_RESOURCE("auth_user_resource","权限用户拥有资源表"),

    //权限角色拥有资源表
    AUTH_ROLE_RESOURCE("auth_role_resource","权限角色拥有资源表"),

    //权限角色和用户绑定表
    AUTH_ROLE_USER("auth_role_user","权限角色和用户绑定表"),

    //权限角色表
    AUTH_ROLE("auth_role","权限角色表"),

    //权限资源表
    AUTH_RESOURCE("auth_resource","权限资源表"),

    //快照
    SNAPSHOOT("Snapshoot","快照"),

    //弘阳铺位和区块点绑定关系表
    HY_PROPERTY_AREA("hy_property_area","弘阳铺位和区块点绑定关系表"),

    //添加动态表单字段表
    PLUS_TABLE_CLOUMN("plus_table_cloumn","添加动态表单字段表");

    private String tableName;

    private String relName;

    private TableEnum(String tableName,String relName){
        this.tableName = tableName;
        this.relName = relName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getRelName() {
        return relName;
    }

    public void setRelName(String relName) {
        this.relName = relName;
    }


    public static TableEnum findByAbbr(String abbr){
        for(TableEnum v : values()){
            if( v.getTableName().equals(abbr)){
                return v;
            }
        }
        return null;
    }
}
