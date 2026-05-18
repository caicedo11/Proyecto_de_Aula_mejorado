package com.Educational_harmonie.Educational_harmonie.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class Logincontroller {

    @GetMapping("/login")
    public String mostrarLogin(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        
        // Si Spring Security redirige aquí con ?error, enviamos el mensaje al HTML
        if (error != null) {
            model.addAttribute("error", "Usuario o contraseña incorrectos. Por favor, intente de nuevo.");
        }
        
        // Si redirige aquí con ?logout, confirmamos el cierre de sesión exitoso
        if (logout != null) {
            model.addAttribute("logout", "Ha cerrado sesión correctamente en la plataforma.");
        }
        
        return "login"; 
    }
}