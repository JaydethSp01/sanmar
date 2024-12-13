package com.juegorpg.sanmar.Controller;

import com.juegorpg.sanmar.Model.Partida;
import com.juegorpg.sanmar.Services.PartidaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

@RestController
@RequestMapping("/api/partidas")
public class PartidaController {

    @Autowired
    private PartidaService partidaService;

    /**
     * Endpoint para simular una partida entre dos equipos.
     */
    @PostMapping("/simular")
    public Partida simularPartida(@RequestParam String equipo1,
                                  @RequestParam String equipo2,
                                  @RequestParam int idJugador1,
                                  @RequestParam int idJugador2) {
        validarNombreEquipo(equipo1);
        validarNombreEquipo(equipo2);
        validarIdJugador(idJugador1);
        validarIdJugador(idJugador2);

        return partidaService.registrarPartida(equipo1, equipo2, idJugador1, idJugador2);
    }

    /**
     * Endpoint para consultar partidas entre dos fechas.
     */
    @GetMapping("/partidas")
    public ResponseEntity<List<Partida>> consultarPartidasEntreFechas(
            @RequestParam(value = "inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(value = "fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fin) {
        if (inicio.isAfter(fin)) {
            return ResponseEntity.badRequest().body(null);
        }
        List<Partida> partidas = partidaService.consultarPartidasEntreFechas(inicio, fin);
        return ResponseEntity.ok(partidas);
    }

    /**
     * Método interactivo para gestionar partidas desde la consola.
     */
    public void menuGestionPartidas(Scanner scanner) {
        boolean salir = false;
        while (!salir) {
            try {
                mostrarMenu();
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir nueva línea

                switch (opcion) {
                    case 1 -> simularPartidaDesdeMenu(scanner);
                    case 2 -> consultarPartidasDesdeMenu(scanner);
                    case 3 -> {
                        System.out.println("Saliendo del sistema de gestión de partidas.");
                        salir = true;
                    }
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
                scanner.nextLine(); // Limpiar buffer
            }
        }
    }

    /**
     * Mostrar el menú interactivo.
     */
    private void mostrarMenu() {
        System.out.println("\nGestión de Partidas\n");
        System.out.println("1. Simular Partida");
        System.out.println("2. Consultar Partidas por Fecha");
        System.out.println("3. Volver al Menú Principal");
        System.out.print("\nSeleccione una opción: ");
    }

    /**
     * Simular partida desde el menú.
     */
    private void simularPartidaDesdeMenu(Scanner scanner) {
        System.out.print("Ingrese el nombre del Equipo 1: ");
        String equipo1 = scanner.nextLine();

        System.out.print("Ingrese el nombre del Equipo 2: ");
        String equipo2 = scanner.nextLine();

        System.out.print("Ingrese el ID del Jugador 1: ");
        int idJugador1 = scanner.nextInt();

        System.out.print("Ingrese el ID del Jugador 2: ");
        int idJugador2 = scanner.nextInt();
        scanner.nextLine(); // Consumir nueva línea

        try {
            Partida partida = simularPartida(equipo1, equipo2, idJugador1, idJugador2);
            System.out.println("Partida simulada exitosamente: " + partida);
        } catch (IllegalArgumentException e) {
            System.out.println("Error al simular partida: " + e.getMessage());
        }
    }

    /**
     * Consultar partidas entre fechas desde el menú.
     */
    private void consultarPartidasDesdeMenu(Scanner scanner) {
        try {
            System.out.println("Ingrese fecha de inicio (YYYY-MM-DD):");
            LocalDate inicio = LocalDate.parse(scanner.nextLine());

            System.out.println("Ingrese fecha de fin (YYYY-MM-DD):");
            LocalDate fin = LocalDate.parse(scanner.nextLine());

            if (inicio.isAfter(fin)) {
                System.out.println("La fecha de inicio no puede ser posterior a la fecha de fin.");
                return;
            }

            List<Partida> partidas = partidaService.consultarPartidasEntreFechas(inicio, fin);
            if (partidas.isEmpty()) {
                System.out.println("No se encontraron partidas en el rango de fechas proporcionado.");
            } else {
                System.out.println("Partidas encontradas:");
                partidas.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.out.println("Error al consultar partidas: " + e.getMessage());
        }
    }

    /**
     * Validar nombre de equipo.
     */
    private void validarNombreEquipo(String nombreEquipo) {
        if (nombreEquipo == null || nombreEquipo.isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo no puede estar vacío.");
        }
    }

    /**
     * Validar ID del jugador.
     */
    private void validarIdJugador(int idJugador) {
        if (idJugador <= 0) {
            throw new IllegalArgumentException("El ID del jugador debe ser un número positivo.");
        }
    }
}
