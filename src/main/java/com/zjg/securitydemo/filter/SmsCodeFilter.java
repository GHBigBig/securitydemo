package com.zjg.securitydemo.filter;

import com.zjg.securitydemo.beans.ImageCode;
import com.zjg.securitydemo.beans.SmsCode;
import com.zjg.securitydemo.controller.ValidateController;
import com.zjg.securitydemo.exception.ValidateCodeException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Spring Security实际上是由许多过滤器组成的过滤器链，
 * 处理手机登录逻辑的过滤器为 SmsAuthenticationFilter
 * 而短信验证码校验过程应该是在这个过滤器之前的，
 * 即只有短信验证码校验通过后才去读取用户信息
 * 由于Spring Security并没有直接提供短信验证码校验相关的过滤器接口，
 * 所以我们需要自己定义一个验证码校验的过滤器SmsCodeFilter
 *
 * @author zjg
 * @create 2020-03-13 12:39
 */
@Component
public class SmsCodeFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;
    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    /**
     * ValidateCodeFilter继承了org.springframework.web.filter.OncePerRequestFilter，该过滤器只会执行一次
     *
     * 在doFilterInternal方法中我们判断了请求URL是否为/login，
     * 该路径对应登录form表单的action路径，请求的方法是否为POST，
     * 是的话进行验证码校验逻辑，否则直接执行filterChain.doFilter让代码往下走。
     * 当在验证码校验的过程中捕获到异常时，
     * 调用Spring Security的校验失败处理器AuthenticationFailureHandler进行处理。
     *
     * @param httpServletRequest
     * @param httpServletResponse
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest,
                                    HttpServletResponse httpServletResponse, FilterChain filterChain)
            throws ServletException, IOException {

        if (StringUtils.equalsIgnoreCase("/login/mobile", httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase("post", httpServletRequest.getMethod())) {
            try {
                validateCode(new ServletWebRequest(httpServletRequest));
            } catch (ValidateCodeException e) {
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    /**
     *Session中获取了SmsCode对象和请求参数smsCode（对应登录页面的验证码<input>框name属性）,
     * 然后进行了各种判断并抛出相应的异常。当验证码过期或者验证码校验通过时，
     * 我们便可以删除Session中的SmsCode属性了。
     *
     * @param servletWebRequest
     * @throws ServletRequestBindingException
     */
    private void validateCode(ServletWebRequest servletWebRequest) throws ServletRequestBindingException {
        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "smsCode");
        String mobile = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "mobile");
        SmsCode codeInSession = (SmsCode) sessionStrategy.getAttribute(servletWebRequest, ValidateController.SESSION_KEY_SMS_CODE + mobile);

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码不为空！");
        }
        if (codeInSession==null) {
            throw new ValidateCodeException("验证码不存在，请重新发送！");
        }
        if (codeInSession.isExpire()) {
            sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_IMAGE_CODE);
            throw new ValidateCodeException("验证码已过期，请重新发送！");
        }
        if (!StringUtils.equalsIgnoreCase(codeInSession.getCode(), codeInRequest)) {
            throw new ValidateCodeException("验证码错误！");
        }

        sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY_SMS_CODE+mobile);
    }
}
