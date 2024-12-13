package com.juegorpg.sanmar.Repository;

import com.juegorpg.sanmar.Model.Jugador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JugadorRepository extends JpaRepository<Jugador, Integer> {
    // Métodos adicionales si es necesario
}
