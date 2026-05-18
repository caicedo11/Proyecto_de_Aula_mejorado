package com.Educational_harmonie.Educational_harmonie.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.Educational_harmonie.Educational_harmonie.model.Invitacion;
import com.Educational_harmonie.Educational_harmonie.model.Usuario;
import com.Educational_harmonie.Educational_harmonie.service.InvitacionService;
import com.Educational_harmonie.Educational_harmonie.service.UsuarioPendienteService;

@Controller
@RequestMapping("/registro")
public class RegistroPublicoController {

    @Autowired
    private UsuarioPendienteService usuarioPendienteService;

    @Autowired
    private InvitacionService invitacionService;

    // Carga inicial del formulario desde el enlace de la invitación
    @GetMapping
    public String mostrarRegistro(@RequestParam(required = false) String codigo, Model model) {
        Usuario nuevoUsuario = new Usuario();
        String correoPredefinido = "";
        Integer cargoPredefinido = null;

        if (codigo != null && !codigo.isEmpty()) {
            Invitacion invitacion = invitacionService.validarInvitacion(codigo);
            if (invitacion != null) {
                correoPredefinido = invitacion.getCorreoInvitado();
                nuevoUsuario.setCorreo(correoPredefinido);

                String rolTexto = invitacion.getRol().toUpperCase();
                if (rolTexto.equals("DOCENTE")) {
                    cargoPredefinido = 3; 
                } else if (rolTexto.equals("ACUDIENTE")) {
                    cargoPredefinido = 2; 
                }
                nuevoUsuario.setIdCargo(cargoPredefinido);
            }
        }

        model.addAttribute("nuevoUsuario", nuevoUsuario);
        model.addAttribute("codigo", codigo);
        model.addAttribute("cargoInvitado", cargoPredefinido);
        model.addAttribute("emailInvitado", correoPredefinido);
        
        return "Registro";
    }

    @GetMapping("/completar")
    public String mostrarRegistroCompletarGet(@RequestParam(required = false) String codigo, Model model) {
        return mostrarRegistro(codigo, model);
    }

    // POST /registro/completar - PROCESA Y REPARA ERRORES DE SUBMIT
    @PostMapping("/completar")
    public String completarRegistro(
            @RequestParam(required = false) String codigo,
            @ModelAttribute("nuevoUsuario") Usuario usuario,
            Model model) {

        // Inicializamos variables de recuperación por si el registro falla
        String correoPredefinido = "";
        Integer cargoPredefinido = null;

        try {
            usuarioPendienteService.registrarUsuario(usuario);
            model.addAttribute("mensaje", "✅ ¡Registro exitoso! Ya puedes iniciar sesión.");
            
            // Si todo sale bien, limpiamos el formulario enviando un objeto vacío
            Usuario usuarioVacio = new Usuario();
            model.addAttribute("nuevoUsuario", usuarioVacio);
            
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "❌ " + e.getMessage());
            
            // Mantenemos los datos que el usuario ya digitó mal para que no los vuelva a escribir
            model.addAttribute("nuevoUsuario", usuario);

            // CORRECCIÓN CLAVE: Si hay un error, recalculamos los datos de la invitación original
            if (codigo != null && !codigo.isEmpty()) {
                Invitacion invitacion = invitacionService.validarInvitacion(codigo);
                if (invitacion != null) {
                    correoPredefinido = invitacion.getCorreoInvitado();
                    String rolTexto = invitacion.getRol().toUpperCase();
                    if (rolTexto.equals("DOCENTE")) {
                        cargoPredefinido = 3;
                    } else if (rolTexto.equals("ACUDIENTE")) {
                        cargoPredefinido = 2;
                    }
                }
            }
        }

        // Enviamos siempre las variables requeridas por el HTML, incluso ante fallos
        model.addAttribute("codigo", codigo);
        model.addAttribute("cargoInvitado", cargoPredefinido);
        model.addAttribute("emailInvitado", correoPredefinido);
        
        return "Registro";
    }
}