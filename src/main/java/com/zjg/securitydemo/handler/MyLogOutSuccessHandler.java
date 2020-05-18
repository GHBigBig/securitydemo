package com.zjg.securitydemo.handler;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security默认的退出登录URL为/logout，退出登录后，Spring Security会做如下处理：
 *
 * 是当前的Sesion失效；
 *
 * 清除与当前用户关联的RememberMe记录；
 *
 * 清空当前的SecurityContext；
 *
 * 重定向到登录页。
 *
 *
 * 我们通过logoutSuccessHandler指定退出成功处理器来处理退出成功后的逻辑
 *
 * @author zjg
 * @create 2020-03-14 16:09
 */
@Component
public class MyLogOutSuccessHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write("退出成功，请重新登录！");
    }
}
