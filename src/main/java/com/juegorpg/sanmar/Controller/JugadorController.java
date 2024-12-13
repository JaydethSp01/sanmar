package com.juegorpg.sanmar.Controller;

import com.juegorpg.sanmar.Services.JugadorService;
import com.juegorpg.sanmar.Model.Jugador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/jugadores")
public class JugadorController {

    @Autowired
    private JugadorService jugadorService;

    @PostMapping
    public Jugador registrarJugador(@RequestBody Map<String, String> request) {
        String nombre = request.get("nombre");
        validarNombre(nombre);
        return jugadorService.registrarJugador(nombre);
    }

    @PutMapping("/{id}")
    public Jugador modificarJugador(@PathVariable int id, @RequestBody Map<String, Object> request) {
        String nuevoNombre = (String) request.get("nombre");
        Integer nivel = (Integer) request.get("nivel");
        Integer puntuacion = (Integer) request.get("puntuacion");

        validarNombre(nuevoNombre);
        validarNivel(nivel);
        validarPuntuacion(puntuacion);

        return jugadorService.modificarJugador(id, nuevoNombre, nivel, puntuacion);
    }

    @DeleteMapping("/{id}")
    public void eliminarJugador(@PathVariable int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del jugador debe ser un número positivo.");
        }
        jugadorService.eliminarJugador(id);
    }

    @GetMapping
    public List<Jugador> consultarJugadores() {
        List<Jugador> jugadores = jugadorService.consultarJugadores();
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores registrados.");
        }
        return jugadores;
    }

    @PostMapping("/{id}/inventario")
    public Jugador agregarItemInventario(@PathVariable int id, @RequestBody Map<String, Object> request) {
        String nombreItem = (String) request.get("nombreItem");
        Integer cantidad = (Integer) request.get("cantidad");

        validarNombreItem(nombreItem);
        validarCantidad(cantidad);

        return jugadorService.agregarItemInventario(id, nombreItem, cantidad);
    }

    @GetMapping("/{id}/inventario")
    public Map<String, Integer> consultarInventario(@PathVariable int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del jugador debe ser un número positivo.");
        }
        return jugadorService.consultarInventario(id);
    }

    public void menuGestionJugadores(Scanner scanner) {
        boolean salir = false;

        while (!salir) {
            try {
                mostrarMenu();
                int opcion = leerOpcion(scanner);

                switch (opcion) {
                    case 1 -> registrarDesdeMenu(scanner);
                    case 2 -> modificarDesdeMenu(scanner);
                    case 3 -> eliminarDesdeMenu(scanner);
                    case 4 -> consultarJugadoresDesdeMenu();
                    case 5 -> agregarItemDesdeMenu(scanner);
                    case 6 -> consultarInventarioDesdeMenu(scanner);
                    case 7 -> salir = true;
                    default -> System.out.println("Opción no válida. Intente nuevamente.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Entrada no válida. Por favor, ingrese un número.");
                scanner.nextLine(); // Limpiar buffer
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Ocurrió un error inesperado: " + e.getMessage());
            }
        }
    }

    private void mostrarMenu() {
        System.out.println("\nGestión de Jugadores\n");
        System.out.println("1. Registrar Jugador");
        System.out.println("2. Modificar Jugador");
        System.out.println("3. Eliminar Jugador");
        System.out.println("4. Consultar Jugadores");
        System.out.println("5. Agregar Item al Inventario");
        System.out.println("6. Consultar Inventario");
        System.out.println("7. Volver al Menú Principal");
        System.out.print("\nSeleccione una opción: ");
    }

    private int leerOpcion(Scanner scanner) {
        return scanner.nextInt();
    }

    private void registrarDesdeMenu(Scanner scanner) {
        System.out.println("Ingrese el nombre del jugador:");
        String nombre = scanner.next();
        registrarJugador(Map.of("nombre", nombre));
    }

    private void modificarDesdeMenu(Scanner scanner) {
        System.out.println("Ingrese el ID del jugador a modificar:");
        int id = scanner.nextInt();

        System.out.println("Ingrese los nuevos datos del jugador:");
        System.out.print("Nombre: ");
        String nombre = scanner.next();
        System.out.print("Nivel: ");
        int nivel = scanner.nextInt();
        System.out.print("Puntuación: ");
        int puntuacion = scanner.nextInt();

        modificarJugador(id, Map.of("nombre", nombre, "nivel", nivel, "puntuacion", puntuacion));
    }

    private void eliminarDesdeMenu(Scanner scanner) {
        System.out.println("Ingrese el ID del jugador a eliminar:");
        int id = scanner.nextInt();
        eliminarJugador(id);
    }

    private void consultarJugadoresDesdeMenu() {
        List<Jugador> jugadores = consultarJugadores();
        if (jugadores.isEmpty()) {
            System.out.println("No hay jugadores registrados.");
        } else {
            jugadores.forEach(System.out::println);
        }
    }

    private void agregarItemDesdeMenu(Scanner scanner) {
        System.out.println("Ingrese el ID del jugador:");
        int id = scanner.nextInt();
        System.out.println("Ingrese el nombre del item:");
        String item = scanner.next();
        System.out.println("Ingrese la cantidad:");
        int cantidad = scanner.nextInt();

        agregarItemInventario(id, Map.of("nombreItem", item, "cantidad", cantidad));
    }

    private void consultarInventarioDesdeMenu(Scanner scanner) {
        System.out.println("Ingrese el ID del jugador:");
        int id = scanner.nextInt();
        Map<String, Integer> inventario = consultarInventario(id);
        System.out.println("Inventario del jugador: " + inventario);
    }

    private void validarNombre(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
    }

    private void validarNivel(Integer nivel) {
        if (nivel == null || nivel < 1 || nivel > 100) {
            throw new IllegalArgumentException("El nivel debe estar entre 1 y 100.");
        }
    }

    private void validarPuntuacion(Integer puntuacion) {
        if (puntuacion == null || puntuacion < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa.");
        }
    }

    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero.");
        }
    }

    private void validarNombreItem(String nombreItem) {
        if (nombreItem == null || nombreItem.isEmpty()) {
            throw new IllegalArgumentException("El nombre del item no puede estar vacío.");
        }
    }
}
