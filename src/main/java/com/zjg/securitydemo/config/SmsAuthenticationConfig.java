package com.zjg.securitydemo.config;

import com.zjg.securitydemo.filter.SmsAuthenticationFilter;
import com.zjg.securitydemo.provider.SmsAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

/**
 * 将短信登录的组件组合起来形成一个和上面流程图对应的流程
 *
 * 在流程中第一步需要配置SmsAuthenticationFilter，分别设置了
 * AuthenticationManager、AuthenticationSuccessHandler和AuthenticationFailureHandler属性。
 * 这些属性都是来自SmsAuthenticationFilter继承的AbstractAuthenticationProcessingFilter类中。
 *
 * 第二步配置SmsAuthenticationProvider，这一步只需要将我们自个的UserDetailService注入进来即可。
 *
 * 最后调用HttpSecurity的authenticationProvider方法指定了AuthenticationProvider为
 * SmsAuthenticationProvider，并将SmsAuthenticationFilter过滤器添加到了
 * UsernamePasswordAuthenticationFilter后面。
 *
 * @author zjg
 * @create 2020-03-13 20:51
 */
//@Configuration    应为 @Component
@Component
public class SmsAuthenticationConfig extends
        SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;
    @Autowired
    private AuthenticationFailureHandler authenticationFailureHandler;

    @Qualifier("myUserDetailService")
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     *
     * @param http
     * @throws Exception
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        SmsAuthenticationFilter smsAuthenticationFilter = new SmsAuthenticationFilter();
        smsAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        smsAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);
        smsAuthenticationFilter.setAuthenticationFailureHandler(authenticationFailureHandler);

        SmsAuthenticationProvider smsAuthenticationProvider = new SmsAuthenticationProvider();
        smsAuthenticationProvider.setUserDetailsService(userDetailsService);

        http.authenticationProvider(smsAuthenticationProvider)
                .addFilterAfter(smsAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    }
}
