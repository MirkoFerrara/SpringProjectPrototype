package com.SSproject.classes.service;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.entity.UserPojo;
import com.SSproject.classes.model.MyUserDetails;
import com.SSproject.interfaces.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Sto usando l'userDetailsService custom");
        UserPojo user = userRepo.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        return new MyUserDetails(user);
    }

    /* --------------------- per le operazioni sul db --------------------- */
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);
    private final UserRepo userRepo;

    public MyUserDetailsService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    public UserPojo read(String userName) {
        return userRepo.findByUsername(userName);
    }

    public void save(UserTO userTO) {
        userTO.setPassword(bCryptPasswordEncoder.encode(userTO.getPassword()));
        userRepo.save( transformToPojo(userTO) );
    }

    public void delete(String userName) {
        userRepo.delete(userRepo.findByUsername(userName));
    }

    public void update(UserTO userTO) {
        // da fare
        return;
    }

    public List<UserTO> list() {
        // da fare
        return null;
    }

    public UserTO transformToDTO(String userName){
        UserPojo userPojo = read(userName);
        UserTO userTO = new UserTO();
        userTO.setId(userPojo.getId());
        userTO.setUsername(userPojo.getUsername());
        /* se c'è altro */
       return userTO;
    }

    public UserPojo transformToPojo(UserTO userTO){
        UserPojo userPojo = new UserPojo();
        userPojo.setUsername(userTO.getUsername());
        userPojo.setPassword(userTO.getPassword());
        /* se c'è altro */
        return userPojo;
    }
}
