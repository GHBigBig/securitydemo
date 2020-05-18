package com.zjg.securitydemo.config;

import com.zjg.securitydemo.filter.SmsCodeFilter;
import com.zjg.securitydemo.filter.ValidateCodeFilter;
import com.zjg.securitydemo.handler.MyAuthenticationAccessDeniedHandler;
import com.zjg.securitydemo.handler.MyAuthenticationFailureHandler;
import com.zjg.securitydemo.handler.MyAuthenticationSucessHandler;
import com.zjg.securitydemo.handler.MyLogOutSuccessHandler;
import com.zjg.securitydemo.strategy.MySessionExpiredStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

/**
 *
 *
 *
 * @author zjg
 * @create 2020-03-12 14:54
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)  //开启安全注解
public class BrowserSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private MyAuthenticationSucessHandler authenticationSucessHandler;
    @Autowired
    private MyAuthenticationFailureHandler authenticationFailureHandler;
    @Autowired
    private ValidateCodeFilter validateCodeFilter;
    @Qualifier("myUserDetailService")
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private SmsCodeFilter smsCodeFilter;
    @Autowired
    private SmsAuthenticationConfig smsAuthenticationConfig;
    @Autowired
    private MySessionExpiredStrategy sessionExpiredStrategy;
    @Autowired
    private MyLogOutSuccessHandler logOutSuccessHandler;
    @Autowired
    private MyAuthenticationAccessDeniedHandler authenticationAccessDeniedHandler;


    /**
     * 注入了PasswordEncoder对象，该对象用于密码加密，
     * <p>
     * PasswordEncoder是一个密码加密接口，
     * 而BCryptPasswordEncoder是Spring Security提供的一个实现方法，
     * 我们也可以自己实现PasswordEncoder。
     * 不过Spring Security实现的BCryptPasswordEncoder已经足够强大，
     * 它对相同的密码进行加密后可以生成不同的结果。
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置个token持久化对象
     * PersistentTokenRepository为一个接口类，这里我们用的是数据库持久化，
     * 所以实例用的是PersistentTokenRepository的实现类JdbcTokenRepositoryImpl。
     *
     * @return
     */
    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        /*
           JdbcTokenRepositoryImpl需要指定数据源，
     * 所以我们将配置好的数据源对象DataSource
     * 注入进来并配置到JdbcTokenRepositoryImpl的dataSource属性中。
         */
        jdbcTokenRepository.setDataSource(dataSource);
        //createTableOnStartup属性用于是否启动项目时创建保存token信息的数据表，这里设置为false，我们自己手动创建。
        jdbcTokenRepository.setCreateTableOnStartup(false);
        return jdbcTokenRepository;
    }


    /**
     * 默认 开启了一个HTTP basic类型的认证，所有服务的访问都必须先过这个认证，
     * 默认的用户名为user，密码由Sping Security自动生成，
     * 将HTTP Basic认证修改为基于表单的认证方式。
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //将ValidateCodeFilter添加到UsernamePasswordAuthenticationFilter前面
        http.addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)//添加验证码校验过滤器
                .addFilterBefore(smsCodeFilter, UsernamePasswordAuthenticationFilter.class)// 添加短信验证码校验过滤器
                .formLogin()    // 表单方式登录
//        http.httpBasic()    //HTTP Basic的认证方式
//                .loginPage("/login.html")   //指定了跳转到登录页面的请求URL
                .loginPage("/authentication/require")   //登录跳转 url
                .loginProcessingUrl("/login")  //对应登录页面form表单的action="/login"，我觉得应该是登录的 url
                .successHandler(authenticationSucessHandler)    //处理登录成功
                .failureHandler(authenticationFailureHandler)   //处理失败成功
                .and()
                .exceptionHandling()
                .accessDeniedHandler(authenticationAccessDeniedHandler) //设置权限不足处理器
                .and()
                .logout()
                .logoutUrl("/signout")  //配置了退出登录的URL
//                .logoutSuccessUrl("/signout/success")   //退出成功后跳转的URL
                .logoutSuccessHandler(logOutSuccessHandler)
                .deleteCookies("JSESSIONID")    //退出成功后删除名称为JSESSIONID的cookie
                .and()
                .rememberMe()   //启动自动登录
                .tokenRepository(persistentTokenRepository())   // 配置 token 持久化仓库
                .tokenValiditySeconds(3600) // remember 过期时间，单为秒
                .userDetailsService(userDetailsService) // 处理自动登录逻辑
                .and()
                .authorizeRequests()    // 授权配置
                .antMatchers("/authentication/require",
                        "/login.html",
                        "/code/image",
                        "/code/sms",
                        "/session/invalid",
                        "/signout/success").permitAll() //表示跳转到登录页面的请求不被拦截
                .anyRequest()   // 所有请求
                .authenticated()   // 都需要认证
                .and()
                .sessionManagement()    // 添加 Session管理器
                .invalidSessionUrl("/session/invalid")  // Session失效后跳转到这个链接
                .maximumSessions(1) //配置了最大Session并发数量为1个
                .maxSessionsPreventsLogin(true) //当Session达到最大有效数的时候，不再允许相同的账户登录。
                .expiredSessionStrategy(sessionExpiredStrategy) //配置了Session在并发下失效后的处理策略
                .and()
                .and()
                .csrf()
                .disable()     //把CSRF攻击防御关了
                .apply(smsAuthenticationConfig);    // 将短信验证码认证配置加到 Spring Security 中
    }
}
