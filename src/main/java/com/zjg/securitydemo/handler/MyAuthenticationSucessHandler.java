package com.zjg.securitydemo.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security有一套默认的处理登录成功和失败的方法：
 * 当用户登录成功时，页面会跳转会引发登录的请求，
 * 比如在未登录的情况下访问http://localhost:8080/hello，
 * 页面会跳转到登录页，登录成功后再跳转回来；
 * 登录失败时则是跳转到Spring Security默认的错误提示页面。
 *
 * 改变默认的处理成功逻辑
 *
 * @author zjg
 * @create 2020-03-12 17:44
 */
@Component
public class MyAuthenticationSucessHandler implements AuthenticationSuccessHandler {
    //HttpSessionRequestCache为Spring Security提供的用于缓存请求的对象，
    // 通过调用它的getRequest方法可以获取到本次请求的HTTP信息。
    private RequestCache requestCache = new HttpSessionRequestCache();
    //DefaultRedirectStrategy的sendRedirect为Spring Security提供的用于处理重定向的方法。
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    @Autowired
    ObjectMapper mapper;

    /**
     *
     * @param request
     * @param response
     * @param authentication 参数既包含了认证请求的一些信息，比如IP，请求的SessionId等，
     *                       也包含了用户信息，即前面提到的User对象
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
//        response.setContentType("application/json;charset=utf-8");
//        response.getWriter().write(mapper.writeValueAsString(authentication));

        SavedRequest saveReqeust = requestCache.getRequest(request, response);
        /*
            配置，登录成功后页面将跳转回引发跳转的页面。如果想指定跳转的页面，
            比如跳转到/index，可以将savedRequest.getRedirectUrl()修改为/index：
            有可能是直接访问首页的那么就获取不到就跳转到首页
         */
        if (saveReqeust==null) {
            redirectStrategy.sendRedirect(request, response, "/index");
        }else {
            String redirectUrl = saveReqeust.getRedirectUrl()==null ? "/index" : saveReqeust.getRedirectUrl() ;
            redirectStrategy.sendRedirect(request, response, redirectUrl);
        }

    }
}
