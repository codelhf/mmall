package com.mmall.controller.common.interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Const;
import com.mmall.common.ResponseCode;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

/**
 * @author liuhf
 * @Title: AuthorityInterceptor
 * @Description:
 * @Copyright: Copyright (c) 2018
 * @Company: SI-TECH
 * @createtime 2018/10/29 14:04
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        log.info("preHandle");
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) o;

        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();
        String requestParam = getRequestParam(httpServletRequest);
        if (StringUtils.equals(className, "UserManageController") && StringUtils.equals(methodName, "login")) {
            //如果是拦截到登录请求，不打印参数，因为参数里面有密码，全部会打印到日志中，防止日志泄露
            log.info("权限拦截器拦截到请求,className:{},methodName:{}", className, methodName);
            return true;
        }
        log.info("权限拦截器拦截到请求,className:{},methodName:{},param:{}", className, methodName, requestParam);

        User user = null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if (StringUtils.isNotEmpty(loginToken)) {
            user = JsonUtil.str2Obj(RedisShardedPoolUtil.get(loginToken), User.class);
        }
        //返回false,即不会调用controller里的方法
        if (user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN) {
            //上传由于富文本的控件要求, 要特殊处理返回, 这里面区分是否登录以及是否权限
            if (user == null) {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "请登录管理员");
                    permissionMessage(httpServletResponse, resultMap);
                } else {
                    noLogin(httpServletResponse);
                }
            } else {
                if (StringUtils.equals(className, "ProductManageController") && StringUtils.equals(methodName, "richtextImgUpload")) {
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success", false);
                    resultMap.put("msg", "无权限操作");
                    permissionMessage(httpServletResponse, resultMap);
                } else {
                    noPermission(httpServletResponse, "没有权限操作");
                }
            }
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandle");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }

    private String getRequestParam(HttpServletRequest request){
        //解析参数, 具体的参数的key以及value是什么
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = request.getParameterMap();
        Iterator iterator = paramMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String mapKey = (String) entry.getKey();
            String mapValue = (String) entry.getValue();

            //request这个参数的map,里面的value返回的是一个String[]
            Object object = entry.getValue();
            if (object instanceof String[]) {
                String[] strings = (String[]) object;
                mapValue = Arrays.toString(strings);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        return requestParamBuffer.toString();
    }

    private void noLogin(HttpServletResponse response) throws IOException {
        ServerResponse message = ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(), ResponseCode.NEED_LOGIN.getDesc());
        permissionMessage(response, message);
    }

    private void noPermission(HttpServletResponse response, String message) throws IOException {
        ServerResponse responseMessage = ServerResponse.createByErrorMessage(message);
        permissionMessage(response, responseMessage);
    }

    private void permissionMessage(HttpServletResponse response, Object message) throws IOException {
        response.reset();//这里要添加reset,否则报异常 getWriter() has already been called for this response
        response.setCharacterEncoding("UTF-8");//设置编码，否则乱码
        response.setContentType("application/json;charset=UTF-8");//设置返回值类型,json接口
        PrintWriter out = response.getWriter();
        message = JsonUtil.obj2Str(message);
        out.print(message);
        out.flush();
        out.close();
    }
}
