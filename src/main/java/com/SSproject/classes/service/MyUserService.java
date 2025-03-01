package com.SSproject.classes.service;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.entity.UserPojo;
import com.SSproject.interfaces.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MyUserService {

    private static final Logger logger = LoggerFactory.getLogger(MyUserService.class);

    private final UserRepo userRepo;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService ;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(12);

    public MyUserService(UserRepo userRepo , @Lazy AuthenticationManager authenticationManager, JWTService jwtService){
        this.authenticationManager = authenticationManager;
        this.userRepo=userRepo;
        this.jwtService = jwtService;
    }

    public String verify(UserTO userTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userTO.getUsername(),
                            userTO.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(userTO.getUsername()) ;
            } else {
                return "Autenticazione fallita";
            }
        } catch (Exception e) {
            // Log the exception
            logger.error("Authentication error for user {}: {}", userTO.getUsername(), e.getMessage());
            return "Autenticazione fallita: " + e.getMessage();
        }
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
