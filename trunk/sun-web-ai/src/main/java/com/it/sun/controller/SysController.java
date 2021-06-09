package com.it.sun.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.it.common.util.BaseUtil;
import com.it.common.util.ErrorCode;
import com.it.common.util.TableEnum;
import com.it.common.util.files.FileUtils;
import com.it.common.util.redis.RedisService;
import com.it.common.util.utils.AuthCodeUtil;
import com.it.common.util.utils.IpUtils;
import com.it.sun.entity.*;
import com.it.sun.service.*;
import com.sun.management.OperatingSystemMXBean;
import io.swagger.annotations.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.management.ManagementFactory;
import java.util.*;


@Api(value="/sys",description="系统相关数据接口",position = 1)
@RestController
@RequestMapping(value="/sys")
public class SysController {
    //http://localhost:8085/swagger-ui.html

    private final static Logger logger = LoggerFactory.getLogger(SysController.class);

    //【如果看不下去报错，IDEA中设置“Settings→Inspections→搜索spring core→在Code的下面有个Autowiring Bean Class→Severity→将换成Warring”】
    @Autowired
    private RedisService redisService;
    @Autowired
    private SysConfigService sysConfigService;


    @ApiOperation(value = "查询配置信息", notes = "查询配置信息")
    @RequestMapping(value = "/getConfig", method = RequestMethod.GET)
    @ApiIgnore
    public String getConfig(HttpServletRequest request, HttpServletResponse response,
                                 @ApiParam(name = "keyStr", value = "keyStr", required = true) @RequestParam(required = true) String keyStr) {
        Long statrTime = System.currentTimeMillis();

        JSONObject resultJo = new JSONObject();
        resultJo.put("msg", "OK");
        resultJo.put("c", ErrorCode.SUCCESS.getStatus());

        String valueStr=sysConfigService.getParamValue(keyStr);
        resultJo.put("v",valueStr);

        Long endTime = System.currentTimeMillis();
        logger.info("| response time |  " + "接口:getConfig  " + "响应时间:" + (endTime - statrTime));
        return resultJo.toJSONString();
    }
    @ApiOperation(value = "修改和新增配置信息", notes = "修改和新增配置信息")
    @RequestMapping(value = "/setConfig", method = RequestMethod.GET)
    @ApiIgnore
    public String setConfig(HttpServletRequest request, HttpServletResponse response,
                            @ApiParam(name = "keyStr", value = "keyStr", required = true) @RequestParam(required = true) String keyStr,
                            @ApiParam(name = "valStr", value = "valStr", required = true) @RequestParam(required = true) String valStr) {
        Long statrTime = System.currentTimeMillis();

        JSONObject resultJo = new JSONObject();
        resultJo.put("msg", "OK");
        resultJo.put("c", ErrorCode.SUCCESS.getStatus());

        List<SysConfig> scs = sysConfigService.getParamValues(keyStr);
        if(scs!=null && scs.size()>0){
            scs.get(0).setParamValue(valStr);
            sysConfigService.updSysConfig(scs.get(0));
        }else {
            SysConfig sc=new SysConfig();
            sc.setParamKey(keyStr);
            sc.setParamValue(valStr);
            sysConfigService.addSysConfig(sc);
        }

        Long endTime = System.currentTimeMillis();
        logger.info("| response time |  " + "接口:setConfig  " + "响应时间:" + (endTime - statrTime));
        return resultJo.toJSONString();
    }
}
