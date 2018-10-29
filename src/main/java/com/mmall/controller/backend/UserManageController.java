package com.mmall.controller.backend;

import com.mmall.common.Const;
import com.mmall.common.ServerResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.util.CookieUtil;
import com.mmall.util.JsonUtil;
import com.mmall.util.RedisShardedPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class UserManageController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.GET)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse response){
        ServerResponse<User> serverResponse = iUserService.login(username,password);
        if(!serverResponse.isSuccess()){
            return serverResponse;
        }
        User user = serverResponse.getData();
        if(user.getRole() == Const.Role.ROLE_ADMIN){
            //说明登录的是管理员
//            session.setAttribute(Const.CURRENT_USER,user);
            //新增redis共享cookie, session的方式
            CookieUtil.writeLoginToken(response,session.getId());
            RedisShardedPoolUtil.setEx(session.getId(), JsonUtil.obj2Str(serverResponse.getData()), Const.RedisCacheExTime.REDIS_SESSION_EXTIME);
            return serverResponse;
        } else {
            return ServerResponse.createByErrorMessage("不是管理员,无法登录");
        }
    }

}
