// SanmarApplication.java
package com.juegorpg.sanmar;

import com.juegorpg.sanmar.Controller.JugadorController;
import com.juegorpg.sanmar.Controller.MundosController;
import com.juegorpg.sanmar.Controller.PartidaController;
import com.juegorpg.sanmar.Controller.RankingController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.InputMismatchException;
import java.util.Scanner;

@SpringBootApplication
public class SanmarApplication implements CommandLineRunner {

    @Autowired
    private JugadorController jugadorController;

    @Autowired
    private MundosController mundoController;

    @Autowired
    private PartidaController partidaController;

    @Autowired
    private RankingController rankingController;

    public static void main(String[] args) {
        SpringApplication.run(SanmarApplication.class, args);
    }

    @Override
    public void run(String... args) {
        Scanner scanner = new Scanner(System.in);
        boolean salir = false;

        while (!salir) {
            mostrarMenu();

            try {
                System.out.print("\nIngrese una opción: ");
                int opcion = scanner.nextInt();
                scanner.nextLine(); // Consumir la nueva línea

                switch (opcion) {
                    case 1 -> jugadorController.menuGestionJugadores(scanner);
                    case 2 -> mundoController.menuGestionMundos(scanner);
                    case 3 -> partidaController.menuGestionPartidas(scanner);
                    case 4-> rankingController.menuRanking(scanner);
                    case 5 -> {
                        salir = true;
                        mostrarMensaje("Gracias por usar el sistema. ¡Hasta luego!", "EXIT");
                    }
                    default -> mostrarMensaje("Opción no válida. Intente nuevamente.", "ERROR");
                }
            } catch (InputMismatchException e) {
                mostrarMensaje("Entrada inválida. Por favor, ingrese un número.", "ERROR");
                scanner.nextLine(); 
            }
        }
    }

    private void mostrarMenu() {
        mostrarMensaje("\nBienvenido al Sistema de Gestión de Videojuego ", "HEADER");
        System.out.println("1. Gestión de Jugadores");
        System.out.println("2. Gestión de Mundos Virtuales");
        System.out.println("3. Gestión de Partidas");
        System.out.println("4. Ranking Global top 10");
        System.out.println("5. Salir del Sistema");
    }

    private void mostrarMensaje(String mensaje, String tipo) {
        String prefix;
        switch (tipo) {
            case "HEADER" -> prefix = "\u001B[1;36m"; 
            case "ERROR" -> prefix = "\u001B[1;31m";
            case "EXIT" -> prefix = "\u001B[1;32m";
            default -> prefix = "\u001B[0m"; 
        }
        System.out.println(prefix + mensaje + "\u001B[0m"); 
    }
}