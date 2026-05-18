package com.Educational_harmonie.Educational_harmonie.service;


import org.springframework.stereotype.Service;

import com.Educational_harmonie.Educational_harmonie.model.Estudiante;
import com.Educational_harmonie.Educational_harmonie.repository.Estudianterepository;

@Service
public class Estudianteservice {
    private final Estudianterepository repository;

    public Estudianteservice(Estudianterepository repository) {
        this.repository = repository;
    }

    public Estudiante guardar(Estudiante estudiante) {
        return repository.save(estudiante);
    }
}