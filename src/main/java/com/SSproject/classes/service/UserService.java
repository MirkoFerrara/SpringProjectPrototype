package com.SSproject.classes.service;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.entity.UserPojo;
import com.SSproject.classes.model.UserModel;
import com.SSproject.interfaces.repo.UserRepo;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPojo user = userRepo.findByUsername(username);
        if (user == null)
            throw new UsernameNotFoundException("User not found");
        return new UserModel(user);
    }

    /* --------------------- per le operazioni sul db --------------------- */

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo){
        this.userRepo=userRepo;
    }

    public UserPojo read(String userName) {
        return userRepo.findByUsername(userName);
    }

    public void save(UserTO userTO) {
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
