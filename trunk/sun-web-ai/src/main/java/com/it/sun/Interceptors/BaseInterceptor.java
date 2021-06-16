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

//    @Autowired
//    private AuthorityIntercept authorityIntercept;

    //在请求处理之前进行调用—Controller方法调用之前
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        response.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=utf-8");
        return true;
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //System.out.println("postHandle被调用");

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
//        System.out.println("afterCompletion被调用");

    }


}
