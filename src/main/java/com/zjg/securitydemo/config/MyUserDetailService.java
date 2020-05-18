package com.zjg.securitydemo.config;

import com.zjg.securitydemo.beans.MyUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zjg
 * @create 2020-03-12 15:54
 */
@Service
public class MyUserDetailService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 模拟一个用户，替代数据库获取逻辑
        MyUser user = new MyUser();
        user.setUserName(username);
        user.setPassword(this.passwordEncoder.encode("123456"));
        // 输出加密后的密码
        System.out.println(user.getPassword());

        List<GrantedAuthority> authorities = new ArrayList<>();
        if (StringUtils.equalsIgnoreCase("zjg",username)) {
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        }else {
            authorities = AuthorityUtils.commaSeparatedStringToAuthorityList("test");
        }

        return new User(username, user.getPassword(), user.isEnabled(),
                user.isAccountNonExpired(),user.isCredentialsNonExpired(),
                user.isAccountNonLocked(),
                //模拟一个admin的权限，该方法可以将逗号分隔的字符串转换为权限集合
                authorities);
    }
}
