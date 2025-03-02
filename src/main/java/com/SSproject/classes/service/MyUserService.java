package com.SSproject.classes.service;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.entity.UserPojo;
import com.SSproject.interfaces.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

    public String generateToken(UserTO userTO){
        return verify(transformToPojo(userTO));
    }

    public String verify(UserPojo userPojo) {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            userPojo.getUsername(),
                            userPojo.getPassword()));

            if (authentication.isAuthenticated()) {
                return jwtService.generateToken(userPojo.getUsername()) ;
            } else
                throw new UsernameNotFoundException("Autenticazione fallita");
    }


    public UserPojo read(String userName) {
        return userRepo.findByUsername(userName);
    }

    public void save(UserTO userTO) {

        UserPojo user = transformToPojo(userTO);
        if (user.getUsername() == null || user.getUsername().trim().isEmpty())
            throw new IllegalArgumentException("Username non può essere vuoto");

        if (user.getPassword() == null || user.getPassword().trim().isEmpty())
            throw new IllegalArgumentException("Password non può essere vuota");

        UserPojo existingUser = userRepo.findByUsername(user.getUsername());
        if (existingUser != null)
            throw new DataIntegrityViolationException("Username già esistente: " + user.getUsername());

        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));

        try {
            userRepo.save(user);
        } catch (Exception e) {
            throw new RuntimeException("Errore durante il salvataggio dell'utente", e);
        }
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
