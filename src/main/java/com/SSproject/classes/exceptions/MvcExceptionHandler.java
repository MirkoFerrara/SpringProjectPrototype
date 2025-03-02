package com.SSproject.classes.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

@ControllerAdvice(annotations = Controller.class) // Applica solo ai controller MVC tradizionali
public class MvcExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public String handleDataIntegrityViolationException(DataIntegrityViolationException e,
                                                        RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Username già in uso. Scegli un altro username.");
        return "redirect:/register";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgumentException(IllegalArgumentException e,
                                                 RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Dati utente non validi: " + e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception e,
                                         RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("errorMessage", "Errore durante la registrazione: " + e.getMessage());
        return "redirect:/register";
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidationException(MethodArgumentNotValidException e,
                                            RedirectAttributes redirectAttributes) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
        redirectAttributes.addFlashAttribute("errorMessage", "Dati non validi: " + errorMessage);
        return "redirect:/register";
    }
}