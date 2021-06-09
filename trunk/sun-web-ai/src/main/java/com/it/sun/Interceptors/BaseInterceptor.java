package com.it.sun.Interceptors;

import com.alibaba.fastjson.JSONObject;
import com.it.common.util.BaseUtil;
import com.it.common.util.ErrorCode;
import com.it.common.util.redis.RedisService;
import com.it.common.util.utils.IpUtils;
import com.it.sun.entity.SysConfig;
//import com.it.sun.intercepts.AuthorityIntercept;
import com.it.sun.service.SysConfigService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 基础拦截器类
 * @author SunCoder
 */
@Configuration
public class BaseInterceptor implements HandlerInterceptor {
    //记录日志
    private final static Logger logger = LoggerFactory.getLogger(BaseInterceptor.class);

    @Autowired
    private SysConfigService sysConfigService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private MapFloorMapper mapFloorMapper;
//    @Autowired
//    private AuthorityIntercept authorityIntercept;
    @Autowired
    private MapPointTypeMapper mapPointTypeMapper;//区块类型
    @Autowired
    private MapBuildingsMapper mapBuildingsMapper; //建筑信息
    @Autowired
    private MapFloorMapper floorMapper; //楼层信息
    //在请求处理之前进行调用—Controller方法调用之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");

        //对品牌库的图片地址进行转发
        Map<String,String[]> mparm=request.getParameterMap();
        for (String key:mparm.keySet()) {
            if(key.equals("i")){
                response.sendRedirect(((String[])mparm.get("i"))[0].toString());
                return false;
            }
        }

        //开始进行授权验证
        String url = request.getRequestURL().toString();

//        //cmappax的授权限制去掉，直接返回固定的100，可以一直添加
//        if(url.indexOf("getPjAuth")!=-1){
//            JSONObject resule = new JSONObject();
//            resule.put("c",ErrorCode.SUCCESS.getStatus());
//            resule.put("msg",ErrorCode.SUCCESS.getMsg());
//            JSONObject joR=new JSONObject();
//            joR.put("bNum",100);
//            joR.put("fNum",100);
//            joR.put("PVNum",100);
//            resule.put("data",joR);
//            response.getWriter().write(resule.toJSONString());
//            return false;
//        }


        if(url.indexOf("/map/")!=-1){
            request.setAttribute("secretKey","sk8whhan6tc985rh211jk");
        }

        //获得项目编号
        String pid = "0";
        try {
            pid = request.getParameterValues("pid")[0];
        } catch (Exception e) {   }
        try {
            if(StringUtils.isEmpty(pid) || "0".equals(pid)) {
                pid = request.getParameterValues("projectId")[0];
            }
        } catch (Exception e) {   }
//        System.out.println("项目id：" + pid);
        if ("0".equals(pid)){
            //如果接口在调用的时候未传递项目pid，则通过数据关联信息去数据库查询出项目pid数据
            pid = getPidFromDataBase(request);
            request.setAttribute("pid",pid);
        }


        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   权限管理模块 开始  //////////////////////////////////////////////////////
        //权限判断门卫 ( doorkeeper方法返回false代表没有权限，直接结束掉请求 )
//        if(!authorityIntercept.doorkeeper(request,response)) {
//            //上面没有返回true代表没有权限，这里就返回false
//            try {
//                JSONObject result = new JSONObject();
//                result.put("c", ErrorCode.ERROR_PRIVILE_NO.getStatus());
//                result.put("msg", ErrorCode.ERROR_PRIVILE_NO.getMsg());
//                response.getWriter().write(result.toJSONString());
//            }catch (Exception e){}
//            return false;
//        }
        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////   权限管理模块 结束  //////////////////////////////////////////////////////



        if(!"0".equals(pid)) {
            //获得调用码 SecretKey，判断有没有调用权限
            String secretKey=null;
            try{secretKey=request.getParameterValues("secretKey")[0];}catch (Exception e){}
            if(StringUtils.isEmpty(secretKey)){
                try{secretKey=request.getAttribute("secretKey").toString();}catch (Exception e){}
            }
            if(StringUtils.isEmpty(secretKey)){
                JSONObject result=new JSONObject();
                result.put("c",ErrorCode.ERROR_PARAM.getStatus());
                result.put("msg","be short of:secretKey");
                response.getWriter().write(result.toJSONString());
                return false;
            }else {
                if(secretKey.equals("sk8whhan6tc985rh211jk")){//超级调用码
                    //内部使用的超级调用码
                }else {
                    String secretKeyRedis = "";
                    try {
                        secretKeyRedis = redisService.get("sk" + pid);
                    } catch (Exception e) {

                    }
                    if(secretKeyRedis!=null) {
                        if (!secretKeyRedis.equals(secretKey)) {//调用码不正确
                            JSONObject result = new JSONObject();
                            result.put("c", ErrorCode.LOAN_CONFUSE.getStatus());
                            result.put("msg", "secretKey invalid");
                            response.getWriter().write(result.toJSONString());
                            return false;
                        }
                    }else {
                        JSONObject result = new JSONObject();
                        result.put("c", ErrorCode.LOAN_CONFUSE.getStatus());
                        result.put("msg", "secretKey invalid");
                        response.getWriter().write(result.toJSONString());
                        return false;
                    }
                }
            }
            if (url.indexOf("addBuildings") != -1) {//添加建筑
                //开始查询当前项目的授权信息
                JSONObject jo = sysConfigService.getAuthCodeListInfo(pid);
                int bNum=jo.getInteger("bNum");//允许添加的楼栋个数
                if(bNum!=0) {
                    //查询当前项目下已有建筑的个数
                    List<Map<String,Object>> map = mapFloorMapper.getBAndFnum(pid);
                    if(map!=null && map.size()>0) {
                        int bc = Integer.parseInt(map.get(0).get("bc").toString());
                        if(bc>=bNum){
                            JSONObject result=new JSONObject();
                            result.put("c",ErrorCode.AUTHCODEBN_ERROR.getStatus());
                            result.put("msg",ErrorCode.AUTHCODEBN_ERROR.getMsg());
                            response.getWriter().write(result.toJSONString());
                            return false;
                        }
                    }
                    //没有数据代表是一个空项目，有授权码就可以直接放行
                }else {
                    JSONObject result=new JSONObject();
                    result.put("c",ErrorCode.AUTHCODEBN_ERROR.getStatus());
                    result.put("msg",ErrorCode.AUTHCODEBN_ERROR.getMsg());
                    response.getWriter().write(result.toJSONString());
                    return false;
                }
            } else if (url.indexOf("addMapFloor") != -1) {//添加楼层
                //开始查询当前项目的授权信息
                JSONObject jo = sysConfigService.getAuthCodeListInfo(pid);
                int fNum=jo.getInteger("fNum");//允许添加的楼层个数
                if(fNum!=0){
                    //查询当前项目下已有楼层的个数
                    List<Map<String,Object>> map = mapFloorMapper.getBAndFnum(pid);
                    if(map!=null && map.size()>0) {
                        int fc = Integer.parseInt(map.get(0).get("fc").toString());
                        if(fc>=fNum){
                            JSONObject result=new JSONObject();
                            result.put("c",ErrorCode.AUTHCODEFN_ERROR.getStatus());
                            result.put("msg",ErrorCode.AUTHCODEFN_ERROR.getMsg());
                            response.getWriter().write(result.toJSONString());
                            return false;
                        }
                    }
                    //没有数据代表是一个空项目，有授权码就可以直接放行
                }else {
                    JSONObject result=new JSONObject();
                    result.put("c",ErrorCode.AUTHCODEFN_ERROR.getStatus());
                    result.put("msg",ErrorCode.AUTHCODEFN_ERROR.getMsg());
                    response.getWriter().write(result.toJSONString());
                    return false;
                }

            }else if (url.indexOf("getAreaInfo") != -1) {//请求地图数据
                //开始查询当前项目的授权信息
                JSONObject jo = sysConfigService.getAuthCodeListInfo(pid);
                Long PVNum=jo.getLong("PVNum");//授权码的可用次数
                if(PVNum!=0) {
                    //查询数据库的访问量数据
                    List<SysConfig> authCodeList = sysConfigService.getParamValues("pvn" + pid);//从数据库读取当前项目的访问统计
                    if(authCodeList!=null && authCodeList.size()>0){//有数据就做判断，没有数据就暂时放行，在定时器中会将访问数据存储到数据库,每次访问都会在redis中添加一个访问数
                        Long allPvNum=Long.parseLong(authCodeList.get(0).getParamValue());
                        if(allPvNum<PVNum){
                            try{redisService.incr("pvn_"+pid,1L);}catch (Exception ep){}//暂时在redis中存储一个请求信息
                        }else {
                            JSONObject result=new JSONObject();
                            result.put("c",ErrorCode.AUTHCODEPV_ERROR.getStatus());
                            result.put("msg",ErrorCode.AUTHCODEPV_ERROR.getMsg());
                            response.getWriter().write(result.toJSONString());
                            return false;
                        }
                    }else {
                        try{redisService.incr("pvn_"+pid,1L);}catch (Exception ep){}//暂时在redis中存储一个请求信息
                    }
                }else{
                    JSONObject result=new JSONObject();
                    result.put("c",ErrorCode.AUTHCODEPV_ERROR.getStatus());
                    result.put("msg",ErrorCode.AUTHCODEPV_ERROR.getMsg());
                    response.getWriter().write(result.toJSONString());
                    return false;
                }
            }
        }
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //System.out.println("postHandle被调用");
        String pid=request.getParameter("pid");
        String ip = IpUtils.getIpAddr(request);
        String us=request.getHeader("user-agent");//客户端标识
        if(StringUtils.isNotEmpty(pid)){
            redisService.lpush("ips"+BaseUtil.GetDataStr("yyyyMMdd")+"_"+pid,ip);//为了统计IP数
            redisService.expire("ips"+BaseUtil.GetDataStr("yyyyMMdd")+"_"+pid,60L*60L*24L*2L);//2天后过期

            redisService.lpush("ips3"+BaseUtil.GetDataStr("yyyyMMdd")+"_"+pid,ip+us);//为了统计客户数
            redisService.expire("ips3"+BaseUtil.GetDataStr("yyyyMMdd")+"_"+pid,60L*60L*24L*2L);//2天后过期
        }

        redisService.lpush("ips"+BaseUtil.GetDataStr("yyyyMMdd"),ip);//为了统计IP数
        redisService.expire("ips"+BaseUtil.GetDataStr("yyyyMMdd"),60L*60L*24L*2L);//2天后过期
//        System.out.println(us);
        redisService.lpush("ips3"+BaseUtil.GetDataStr("yyyyMMdd"),ip+us);//为了统计客户数
        redisService.expire("ips3"+BaseUtil.GetDataStr("yyyyMMdd"),60L*60L*24L*2L);//2天后过期

        String url = request.getRequestURL().toString();
        if(url.indexOf("getArea")!=-1 ||
                url.indexOf("getAreaInfo")!=-1 ||
                url.indexOf("getBrandByPid")!=-1 ||
                url.indexOf("getBuildingsByPidAndBid")!=-1 ||
                url.indexOf("getFloorByPidAndBidAndFid")!=-1 ||
                url.indexOf("getMapBrandType")!=-1){
            //对ip进行记录
            redisService.incr("PV:PV"+BaseUtil.GetDataStr("yyyyMMdd"),1L);//为了统计请求量
            redisService.expire("PV:PV"+BaseUtil.GetDataStr("yyyyMMdd"),60L*60L*24L*90L);//90天过期
            if(StringUtils.isNotEmpty(pid)) {
                redisService.incr("PV:PV" + BaseUtil.GetDataStr("yyyyMMdd") + "_" + pid, 1L);//为了统计请求量
                redisService.expire("PV:PV"+BaseUtil.GetDataStr("yyyyMMdd") + "_" + pid,60L*60L*24L*90L);//90天过期
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion被调用");

    }

    /**
     * 当项目pid没有通过request传递过来，则通过数据关联关系从数据库中查询出对应的项目pid
     * @param request
     * @return
     */
    private String getPidFromDataBase(HttpServletRequest request){
        //获取当前请求方法名
        String funName = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
        String pid = "0";
        if (funName.equals("alterShowEditor")){
            //更新区块类型显示到编辑器状态数据
            String typeId = request.getParameter("id");
            MapPointType mapPointType = mapPointTypeMapper.selectByPrimaryKey(Long.parseLong(typeId));
            pid = mapPointType.getProjectId().toString();
        }else if (funName.equals("updateBuildings")){
            //更新建筑信息
            String buildingId = request.getParameter("id");
            MapBuildings mapBuildings = mapBuildingsMapper.selectByPrimaryKey(Long.parseLong(buildingId));
            pid = mapBuildings.getProjectId().toString();
        }else if (funName.equals("updateMapFloor")){
            //更新楼层数据
            String floorId = request.getParameter("id");
            MapFloor floor = floorMapper.selectByPrimaryKey(Long.parseLong(floorId));
            pid = floor.getProjectId().toString();
        }else if (funName.equals("editorArea")){
            //更新地图数据
            String data = request.getParameter("data");
            String floorID = JSONObject.parseObject(data).getString("FloorId");
            MapFloor mapFloor = floorMapper.selectByPrimaryKey(Long.parseLong(floorID));
            pid = mapFloor.getProjectId().toString();
        }else if (funName.equals("updateProject")){
            pid = request.getParameter("id");
        }

        return pid;
    }
}
