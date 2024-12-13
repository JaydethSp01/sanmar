package com.juegorpg.sanmar.Services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juegorpg.sanmar.Model.Mundo;
import com.juegorpg.sanmar.Repository.MundoRepository;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MundosService {

    @Autowired
    private MundoRepository mundoRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Mundo crearMundo(String nombre, List<String> nodos, List<Map<String, Object>> aristas) {
        if (nombre == null || nombre.isEmpty()) {
            throw new IllegalArgumentException("El nombre del mundo no puede estar vacío.");
        }
        if (nodos == null || nodos.isEmpty()) {
            throw new IllegalArgumentException("Debe haber al menos un nodo en el mundo.");
        }
        if (new HashSet<>(nodos).size() != nodos.size()) {
            throw new IllegalArgumentException("La lista de nodos contiene duplicados.");
        }
        System.out.println("Creando Mundo con los siguientes datos:");
        System.out.println("Nombre: " + nombre);
        System.out.println("Nodos: " + nodos);

        // Crear el grafo
        Graph<String, DefaultWeightedEdge> grafo = new DefaultDirectedWeightedGraph<>(DefaultWeightedEdge.class);
        nodos.forEach(grafo::addVertex);

        System.out.println("Validando y añadiendo aristas...");
        for (Map<String, Object> arista : aristas) {
            String origen = (String) arista.get("origen");
            String destino = (String) arista.get("destino");
            Double peso = (Double) arista.get("peso");

            if (origen == null || destino == null || peso == null) {
                throw new IllegalArgumentException("Cada arista debe tener un origen, un destino y un peso.");
            }
            if (!nodos.contains(origen) || !nodos.contains(destino)) {
                throw new IllegalArgumentException("Los nodos de las aristas deben existir en el mundo. Nodo inválido: " + origen + " o " + destino);
            }
            if (peso <= 0) {
                throw new IllegalArgumentException("El peso de las aristas debe ser mayor que cero.");
            }

            DefaultWeightedEdge edge = grafo.addEdge(origen, destino);
            if (edge == null) {
                throw new IllegalArgumentException("La arista entre " + origen + " y " + destino + " ya existe.");
            }
            grafo.setEdgeWeight(edge, peso);
        }

        // Serializar el grafo a una estructura JSON simple
        String grafoSerializado = serializarGrafo(grafo);

        // Crear y guardar el mundo
        Mundo mundo = new Mundo();
        mundo.setNombre(nombre);
        mundo.setGrafo_serializado(grafoSerializado);

        System.out.println("Mundo creado y guardado exitosamente.");
        return mundoRepository.save(mundo);
    }

    private String serializarGrafo(Graph<String, DefaultWeightedEdge> grafo) {
        Map<String, Object> grafoData = new HashMap<>();
        grafoData.put("nodos", new ArrayList<>(grafo.vertexSet()));

        List<Map<String, Object>> aristas = new ArrayList<>();
        for (DefaultWeightedEdge arista : grafo.edgeSet()) {
            Map<String, Object> aristaData = new HashMap<>();
            aristaData.put("origen", grafo.getEdgeSource(arista));
            aristaData.put("destino", grafo.getEdgeTarget(arista));
            aristaData.put("peso", grafo.getEdgeWeight(arista));
            aristas.add(aristaData);
        }
        grafoData.put("aristas", aristas);

        try {
            return objectMapper.writeValueAsString(grafoData);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando el grafo", e);
        }
    }

    public Mundo modificarMundo(int id, String nuevoNombre) {
        if (nuevoNombre == null || nuevoNombre.isEmpty()) {
            throw new IllegalArgumentException("El nuevo nombre no puede estar vacío.");
        }

        Optional<Mundo> mundoOptional = mundoRepository.findById(id);
        if (mundoOptional.isPresent()) {
            Mundo mundo = mundoOptional.get();
            mundo.setNombre(nuevoNombre);
            System.out.println("Mundo con ID " + id + " modificado exitosamente.");
            return mundoRepository.save(mundo);
        }
        throw new RuntimeException("Mundo no encontrado.");
    }

    public List<String> encontrarRutaMasCorta(Graph<String, DefaultWeightedEdge> grafo, String origen, String destino) {
        if (!grafo.containsVertex(origen) || !grafo.containsVertex(destino)) {
            throw new IllegalArgumentException("Los nodos de origen y destino deben existir en el grafo.");
        }

        try {
            DijkstraShortestPath<String, DefaultWeightedEdge> dijkstra = new DijkstraShortestPath<>(grafo);
            return dijkstra.getPath(origen, destino).getVertexList();
        } catch (Exception e) {
            throw new RuntimeException("Error al calcular la ruta más corta: " + e.getMessage(), e);
        }
    }

    public void eliminarMundo(int id) {
        if (!mundoRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: el mundo con ID " + id + " no existe.");
        }
        mundoRepository.deleteById(id);
        System.out.println("Mundo con ID " + id + " eliminado exitosamente.");
    }

    public List<Mundo> consultarMundos() {
        System.out.println("Consultando todos los mundos disponibles...");
        List<Mundo> mundos = mundoRepository.findAll();
        System.out.println("Se encontraron " + mundos.size() + " mundos.");
        return mundos;
    }
}
