package com.SSproject.classes.service;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.entity.UserPojo;
import com.SSproject.classes.model.MyUserDetails;
import com.SSproject.interfaces.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);
    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    /* --------------------- per le operazioni sul db --------------------- */

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Sto usando l'userDetailsService custom");
        UserPojo user = userRepo.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        return new MyUserDetails(user);
    }

}
