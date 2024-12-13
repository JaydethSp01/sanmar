package com.juegorpg.sanmar.Controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegorpg.sanmar.Services.MundosService;
import com.juegorpg.sanmar.Model.Mundo;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mundos")
public class MundosController {

    @Autowired
    private MundosService mundosService;

    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping
    public Mundo crearMundo(@RequestBody Map<String, Object> mundoRequest) {
        String nombre = (String) mundoRequest.get("nombre");
        List<String> nodos = (List<String>) mundoRequest.get("nodos");
        List<Map<String, Object>> aristas = (List<Map<String, Object>>) mundoRequest.get("aristas");
        return mundosService.crearMundo(nombre, nodos, aristas);
    }

    @PutMapping("/{id}")
    public Mundo modificarMundo(@PathVariable int id, @RequestParam String nuevoNombre) {
        return mundosService.modificarMundo(id, nuevoNombre);
    }

    @DeleteMapping("/{id}")
    public void eliminarMundo(@PathVariable int id) {
        mundosService.eliminarMundo(id);
    }

    @GetMapping
    public List<Mundo> consultarMundos() {
        return mundosService.consultarMundos();
    }

    @PostMapping("/ruta")
    public List<String> encontrarRutaMasCorta(@RequestBody Map<String, Object> request) {
        int mundoId = (int) request.get("mundoId");
        String origen = (String) request.get("origen");
        String destino = (String) request.get("destino");

        // Obtener el mundo y deserializar el grafo
        Mundo mundo = mundosService.consultarMundos().stream()
                .filter(m -> m.getId() == mundoId)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Mundo no encontrado"));

        Graph<String, DefaultWeightedEdge> grafo = deserializarGrafo(mundo.getGrafo_serializado());

        // Calcular la ruta más corta
        return mundosService.encontrarRutaMasCorta(grafo, origen, destino);
    }

    private Graph<String, DefaultWeightedEdge> deserializarGrafo(String grafoSerializado) {
        try {
            Map<String, Object> grafoData = objectMapper.readValue(grafoSerializado, new TypeReference<>() {});

            List<String> nodos = (List<String>) grafoData.get("nodos");
            List<Map<String, Object>> aristas = (List<Map<String, Object>>) grafoData.get("aristas");

            Graph<String, DefaultWeightedEdge> grafo = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);

            // Agregar nodos
            nodos.forEach(grafo::addVertex);

            // Agregar aristas
            for (Map<String, Object> arista : aristas) {
                String origen = (String) arista.get("origen");
                String destino = (String) arista.get("destino");
                double peso = ((Number) arista.get("peso")).doubleValue();

                DefaultWeightedEdge edge = grafo.addEdge(origen, destino);
                if (edge != null) {
                    grafo.setEdgeWeight(edge, peso);
                }
            }

            return grafo;
        } catch (Exception e) {
            throw new RuntimeException("Error al deserializar el grafo", e);
        }
    }

    public void menuGestionMundos(Scanner scanner) {
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = scanner.nextInt();
            scanner.nextLine();

            switch (opcion) {
                case 1 -> {
                    System.out.println("Ingrese los datos del mundo:");
                    Map<String, Object> mundoRequest = pedirDatosMundo(scanner);
                    crearMundo(mundoRequest);
                }
                case 2 -> {
                    System.out.println("Ingrese el ID del mundo a modificar:");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Ingrese los nuevos datos del mundo:");
                    System.out.println("Ingrese el nombre:");
                    String nombre = scanner.nextLine();
                    modificarMundo(id, nombre);
                }
                case 3 -> {
                    System.out.println("Ingrese el ID del mundo a eliminar:");
                    int id = scanner.nextInt();
                    scanner.nextLine();
                    eliminarMundo(id);
                }
                case 4 -> {
                    List<Mundo> mundos = consultarMundos();
                    mundos.forEach(System.out::println);
                }
                case 5 -> {
                    System.out.println("Ingrese el ID del mundo:");
                    int mundoId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.println("Ingrese el nodo de origen:");
                    String origen = scanner.nextLine();
                    System.out.println("Ingrese el nodo de destino:");
                    String destino = scanner.nextLine();

                    Map<String, Object> request = new HashMap<>();
                    request.put("mundoId", mundoId);
                    request.put("origen", origen);
                    request.put("destino", destino);

                    List<String> ruta = encontrarRutaMasCorta(request);
                    System.out.println("Ruta más corta: " + ruta);
                }
                case 6 -> salir = true;
                default -> System.out.println("Opción no válida. Intente nuevamente.");
            }
        }
    }

    private Map<String, Object> pedirDatosMundo(Scanner scanner) {
        Map<String, Object> mundoRequest = new HashMap<>();
        System.out.print("Nombre del Mundo: ");
        mundoRequest.put("nombre", scanner.nextLine());

        // Usar una lista de nodos
        System.out.println("Ingrese los nodos del grafo:");
        System.out.print("¿Cuántos nodos hay? ");
        int cantidadGrafo = scanner.nextInt();
        scanner.nextLine();

        List<String> nodos = new ArrayList<>();
        for (int i = 0; i < cantidadGrafo; i++) {
            System.out.print("Nodo " + (i + 1) + ": ");
            String nodo = scanner.nextLine();
            if (!nodos.contains(nodo)) {
                nodos.add(nodo);
            } else {
                System.out.println("El nodo ya existe: " + nodo);
            }
        }
        mundoRequest.put("nodos", nodos);

        // Usar una lista de aristas
        System.out.println("Ingrese las aristas (distancias) entre los nodos:");
        List<Map<String, Object>> aristas = new ArrayList<>();
        for (String nodo : nodos) {
            while (true) {
                System.out.print("Distancia desde " + nodo + " hacia otro nodo (dejar vacío si no hay): ");
                String destino = scanner.nextLine();
                if (destino.isEmpty()) break;
                if (!nodos.contains(destino)) {
                    System.out.println("El nodo destino no existe: " + destino);
                    continue;
                }
                System.out.print("Distancia: ");
                double distancia = scanner.nextDouble();
                scanner.nextLine(); // Consumir salto de línea

                Map<String, Object> arista = new HashMap<>();
                arista.put("origen", nodo);
                arista.put("destino", destino);
                arista.put("peso", distancia);
                aristas.add(arista);
            }
        }
        mundoRequest.put("aristas", aristas);

        return mundoRequest;
    }

    private void mostrarMenu() {
        System.out.println("\nGestión de Mundos Virtuales\n");
        System.out.println("1. Crear Mundo");
        System.out.println("2. Modificar Mundo");
        System.out.println("3. Eliminar Mundo");
        System.out.println("4. Consultar Mundos");
        System.out.println("5. Encontrar Ruta Más Corta");
        System.out.println("5. Volver al Menú Principal");
        System.out.print("\nSeleccione una opción: ");
    }

}
