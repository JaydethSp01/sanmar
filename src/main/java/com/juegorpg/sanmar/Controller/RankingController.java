package com.juegorpg.sanmar.Controller;

import com.juegorpg.sanmar.Services.RankingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Controller
public class RankingController {

    @Autowired
    private RankingService rankingService;

    public void menuRanking(Scanner scanner) {
        try {
            System.out.println("\nRanking Global - Top 10 Jugadores\n");
            List<Map<String, Object>> jugadoresTop10 = rankingService.obtenerTop10Jugadores();

            if (jugadoresTop10.isEmpty()) {
                System.out.println("No hay jugadores en el sistema.");
            } else {
                System.out.println("ID | Nombre       | Nivel | Puntuación");
                for (Map<String, Object> jugador : jugadoresTop10) {
                    System.out.printf("%-3s | %-11s | %-5s | %-10s%n",
                            jugador.get("id"),
                            jugador.get("nombre"),
                            jugador.get("nivel"),
                            jugador.get("puntuacion"));
                }
            }
        } catch (Exception e) {
            System.out.println("Error al obtener el ranking: " + e.getMessage());
        }
    }
}
