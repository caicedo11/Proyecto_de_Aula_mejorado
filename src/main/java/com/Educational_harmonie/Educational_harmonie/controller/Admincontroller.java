package com.Educational_harmonie.Educational_harmonie.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.Educational_harmonie.Educational_harmonie.model.Usuario;
import com.Educational_harmonie.Educational_harmonie.repository.Usuariorepository;

@Controller
@PreAuthorize("hasRole('ADMIN')")
public class Admincontroller {

    @Autowired
    private Usuariorepository usuarioRepository;

    @GetMapping("/admin")
    public String adminHome() {
        return "Admin";
    }

    @GetMapping("/admin/registro")
    public String registro() {
        return "fragments/registro";
    }

    @GetMapping("/admin/historial")
    public String historial() {
        return "fragments/historial";
    }

    @GetMapping("/admin/asignacion")
    public String asignacion() {
        return "fragments/asignacion";
    }

    @GetMapping("/admin/notificar")
    public String notificar() {
        return "fragments/notificar";
    }

    @GetMapping("/admin/registrarUsuario")
    public String registrarUsuario() {
        return "fragments/admin_registrar_usuario";
    }

    @GetMapping("/admin/registrarEstudiante")
    public String registrarEstudiante() {
        return "fragments/admin_registrar_estudiante";
    }

    // ===============================
    // BLOQUEAR / ACTIVAR USUARIO
    // ===============================

    @PostMapping("/admin/usuarios/cambiar-estado/{id}")
    @ResponseBody
    public Map<String, Object> cambiarEstado(@PathVariable Long id) {

        Map<String, Object> response = new HashMap<>();

        Optional<Usuario> optionalUsuario = usuarioRepository.findById(id);

        if (optionalUsuario.isEmpty()) {
            response.put("success", false);
            response.put("error", "Usuario no encontrado");
            return response;
        }

        Usuario usuario = optionalUsuario.get();

        // NO permitir bloquear administrador
        if (usuario.getIdCargo() == 1) {

            response.put("success", false);
            response.put("error", "No se puede bloquear el administrador");

            return response;
        }

        // CAMBIAR ESTADO
        if ("ACTIVO".equalsIgnoreCase(usuario.getEstado())) {

            usuario.setEstado("INACTIVO");

        } else {

            usuario.setEstado("ACTIVO");
        }

        Usuario usuarioGuardado = usuarioRepository.save(usuario);

        response.put("success", true);
        response.put("estado", usuarioGuardado.getEstado());

        return response;
    }
}