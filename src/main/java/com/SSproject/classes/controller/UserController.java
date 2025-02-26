package com.SSproject.classes.controller;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.response.ResponseDTO;
import com.SSproject.classes.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService){
        this.userService = userService;
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseDTO<List<UserTO>>> list(){
        List<UserTO> users = userService.list();
        ResponseDTO<List<UserTO>> response = new ResponseDTO<>("Utenti recuperati con successo", users);
        return ResponseEntity.ok(response);
    }
/*

    @GetMapping("/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest httpServletRequest){
        return (CsrfToken) httpServletRequest.getSession().getAttribute("_csrf");
    }

 */
    @PostMapping("/create")
    public ResponseEntity<ResponseDTO<Void>> create(@RequestBody UserTO user) {
        userService.save(user);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente creato con successo");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> update(@RequestBody UserTO user){
        userService.update(user);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente aggiornato con successo");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read")
    public ResponseEntity<ResponseDTO<UserTO>> read(@RequestParam String userName){
        UserTO user = userService.transformToDTO(userName);
        ResponseDTO<UserTO> response = new ResponseDTO<>("Utente recuperato con successo", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete") // Meglio usare DeleteMapping invece di PostMapping per le operazioni di cancellazione
    public ResponseEntity<ResponseDTO<Void>> delete(@RequestParam String userName){
        userService.delete(userName);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente eliminato con successo");
        return ResponseEntity.ok(response);
    }
}
