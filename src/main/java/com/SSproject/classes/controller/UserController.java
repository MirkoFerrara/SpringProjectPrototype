package com.SSproject.classes.controller;

import com.SSproject.classes.dto.UserTO;
import com.SSproject.classes.response.ResponseDTO;
import com.SSproject.classes.service.JWTService;
import com.SSproject.classes.service.MyUserDetailsService;
import com.SSproject.classes.service.MyUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(MyUserService.class);

    @Autowired
    private MyUserService myUserService;
    @Autowired
    private JWTService jwtService;


    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("userTO", new UserTO());
        return "login";
    }

    @PostMapping("/authenticateLogin")
    public String processLogin(@ModelAttribute("userTO") UserTO userTO,
                               HttpServletRequest request,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        try {
            // Salva il token nella sessione
            session.setAttribute("jwt_token", myUserService.generateToken(userTO));
            // Opzionale: salva anche lo username per uso nella UI
            session.setAttribute("username", userTO.getUsername());
            return "redirect:/home";
        } catch (Exception e) {
            // Aggiunge un messaggio di errore che sarà disponibile nella view
            redirectAttributes.addFlashAttribute("errorMessage", "Credenziali non valide. Riprova.");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("userTO", new UserTO());
        return "register";
    }

    @PostMapping("/authenticateRegister")
    public String authenticateRegister(@Valid @ModelAttribute("userTO") UserTO userTO,
                                       BindingResult bindingResult,
                                       HttpServletRequest request,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        // Gestisce errori di validazione delle annotazioni direttamente
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldErrors().stream()
                    .map(error -> error.getField() + ": " + error.getDefaultMessage())
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("errorMessage", "Dati non validi: " + errorMessage);
            return "redirect:/register";
        }
        try {
            // Salva l'utente
            myUserService.save(userTO);
            // Effettua l'autenticazione automatica e salva il token nella sessione
            session.setAttribute("jwt_token", myUserService.generateToken(userTO));
            // Salva lo username per uso nella UI
            session.setAttribute("username", userTO.getUsername());
            // Redirect alla home page
            return "redirect:/home";
        } catch (DataIntegrityViolationException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Username già in uso. Scegli un altro username.");
            return "redirect:/register";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Errore durante la registrazione: " + e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/home")
    public String homePage(HttpSession session, Model model) {
        // Verifica se esiste un token nella sessione
        String token = (String) session.getAttribute("jwt_token");
        if (token == null) {
            return "redirect:/login";
        }
        // Puoi passare dati alla vista home
        String username = (String) session.getAttribute("username");
        model.addAttribute("username", username);

        return "home";
    }

    @GetMapping("/list")
    public String listUsers(HttpSession session, Model model) {
        // Verifica se esiste un token nella sessione
        String token = (String) session.getAttribute("jwt_token");
        if (token == null) {
            return "redirect:/login";
        }

        // Utilizza direttamente il servizio per ottenere la lista utenti
        List<UserTO> users = myUserService.list();
        model.addAttribute("users", users);

        return "user-list"; // Template con la lista degli utenti
    }

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> update(@RequestBody UserTO user){
        myUserService.update(user);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente aggiornato con successo");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read")
    public ResponseEntity<ResponseDTO<UserTO>> read(@RequestParam String userName){
        UserTO user = myUserService.transformToDTO(userName);
        ResponseDTO<UserTO> response = new ResponseDTO<>("Utente recuperato con successo", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete") // Meglio usare DeleteMapping invece di PostMapping per le operazioni di cancellazione
    public ResponseEntity<ResponseDTO<Void>> delete(@RequestParam String userName){
        myUserService.delete(userName);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente eliminato con successo");
        return ResponseEntity.ok(response);
    }

    /*

        @GetMapping("/csrf-token")
        public CsrfToken getCsrfToken(HttpServletRequest httpServletRequest){
            return (CsrfToken) httpServletRequest.getSession().getAttribute("_csrf");
        }

     */

}

/*
@RestController
public class RegisterRestController {

    private final MyUserService myUserService;

    @Autowired
    public RegisterRestController(MyUserService myUserService) {
        this.myUserService = myUserService;
    }

    @GetMapping("/api/register")
    public String registerPage() {
        // In un controller REST, questo metodo potrebbe non essere necessario
        // o potrebbe restituire informazioni sul form di registrazione
        return "Utilizza POST /api/register per creare un nuovo utente";
    }

    @PostMapping("/api/register")
    public ResponseEntity<ResponseDTO<?>> register(@RequestBody UserTO userTO) {
        try {
            myUserService.save(userTO);
            ResponseDTO<Void> response = new ResponseDTO<>("SUCCESS", "Utente creato con successo", null);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (UserAlreadyExistsException e) {
            ResponseDTO<Void> response = new ResponseDTO<>("ERROR", "Username già in uso", null);
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        } catch (InvalidUserDataException e) {
            ResponseDTO<Void> response = new ResponseDTO<>("ERROR", "Dati utente non validi: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            ResponseDTO<Void> response = new ResponseDTO<>("ERROR", "Errore durante la registrazione: " + e.getMessage(), null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}

    @PutMapping("/update")
    public ResponseEntity<ResponseDTO<Void>> update(@RequestBody UserTO user){
        myUserService.update(user);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente aggiornato con successo");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/read")
    public ResponseEntity<ResponseDTO<UserTO>> read(@RequestParam String userName){
        UserTO user = myUserService.transformToDTO(userName);
        ResponseDTO<UserTO> response = new ResponseDTO<>("Utente recuperato con successo", user);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete") // Meglio usare DeleteMapping invece di PostMapping per le operazioni di cancellazione
    public ResponseEntity<ResponseDTO<Void>> delete(@RequestParam String userName){
        myUserService.delete(userName);
        ResponseDTO<Void> response = new ResponseDTO<>("Utente eliminato con successo");
        return ResponseEntity.ok(response);
    }
 */