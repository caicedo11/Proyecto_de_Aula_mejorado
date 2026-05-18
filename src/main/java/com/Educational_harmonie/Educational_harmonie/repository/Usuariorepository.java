package com.Educational_harmonie.Educational_harmonie.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.Educational_harmonie.Educational_harmonie.model.Usuario;

@Repository
public interface Usuariorepository extends JpaRepository<Usuario, Long> {
    
    // El "First" es vital para que no explote en la llave de cierre del Service
    Optional<Usuario> findFirstByUsuario(String usuario);
    
    Optional<Usuario> findFirstByCorreo(String correo);
}