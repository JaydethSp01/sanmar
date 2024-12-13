package com.juegorpg.sanmar.Repository;

import com.juegorpg.sanmar.Model.Partida;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface PartidaRepository extends JpaRepository<Partida, Integer> {

    @Query(value = "CALL registrarResultadoYActualizarRanking(:idGanador, :idPerdedor, :puntosGanador, :puntosPerdedor)", nativeQuery = true)
    void executeStoredProcedure(@Param("idGanador") int idGanador,
                                @Param("idPerdedor") int idPerdedor,
                                @Param("puntosGanador") int puntosGanador,
                                @Param("puntosPerdedor") int puntosPerdedor);
}

