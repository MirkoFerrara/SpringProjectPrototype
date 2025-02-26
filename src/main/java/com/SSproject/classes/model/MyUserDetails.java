package com.SSproject.classes.model;

import com.SSproject.classes.entity.UserPojo;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

 import java.util.Collection;
import java.util.Collections;

public class MyUserDetails implements UserDetails {

     private final UserPojo userPojo;

     public MyUserDetails(UserPojo userPojo){
         this.userPojo=userPojo;
     }
    @Override
    public String getPassword() {
        return userPojo.getPassword();
    }
    @Override
    public String getUsername() {
        return userPojo.getUsername();
    }
    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }
    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }
    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }
}
