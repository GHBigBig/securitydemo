package com.zjg.securitydemo.beans;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author zjg
 * @create 2020-03-12 15:49
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MyUser  implements Serializable {
    private static final long serialVersionUID = -1420395659122703115L;

    private String userName;

    private String password;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked= true;

    private boolean credentialsNonExpired= true;

    private boolean enabled= true;
}
