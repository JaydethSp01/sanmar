package com.juegorpg.sanmar.Services;

import com.juegorpg.sanmar.Model.Jugador;
import com.juegorpg.sanmar.Repository.JugadorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class JugadorService {

    @Autowired
    private JugadorRepository jugadorRepository;

    public Jugador registrarJugador(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }
        

        Jugador jugador = new Jugador();
        jugador.setNombre(nombre.trim());
        jugador.setNivel(1);
        jugador.setPuntuacion(0);
        jugador.setInventario(new HashMap<>());
        return jugadorRepository.save(jugador);
    }

    public Jugador modificarJugador(int id, String nuevoNombre, int nivel, int puntuacion) {
        Optional<Jugador> jugadorOptional = jugadorRepository.findById(id);
        if (jugadorOptional.isEmpty()) {
            throw new RuntimeException("Jugador no encontrado con ID: " + id);
        }

        if (nuevoNombre == null || nuevoNombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del jugador no puede estar vacío.");
        }

        if (nivel <= 0) {
            throw new IllegalArgumentException("El nivel debe ser mayor a 0.");
        }

        if (puntuacion < 0) {
            throw new IllegalArgumentException("La puntuación no puede ser negativa.");
        }

        Jugador jugador = jugadorOptional.get();
        jugador.setNombre(nuevoNombre.trim());
        jugador.setNivel(nivel);
        jugador.setPuntuacion(puntuacion);
        return jugadorRepository.save(jugador);
    }

    public void eliminarJugador(int id) {
        if (!jugadorRepository.existsById(id)) {
            throw new RuntimeException("No se puede eliminar: Jugador no encontrado con ID: " + id);
        }
        jugadorRepository.deleteById(id);
    }

    public List<Jugador> consultarJugadores() {
        List<Jugador> jugadores = jugadorRepository.findAll();
        if (jugadores.isEmpty()) {
            throw new RuntimeException("No hay jugadores registrados en el sistema.");
        }
        return jugadores;
    }

    public Jugador agregarItemInventario(int jugadorId, String nombreItem, int cantidad) {
        if (nombreItem == null || nombreItem.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del ítem no puede estar vacío.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad del ítem debe ser mayor a 0.");
        }

        Optional<Jugador> jugadorOptional = jugadorRepository.findById(jugadorId);
        if (jugadorOptional.isEmpty()) {
            throw new RuntimeException("Jugador no encontrado con ID: " + jugadorId);
        }

        Jugador jugador = jugadorOptional.get();
        Map<String, Integer> inventario = jugador.getInventario();
        inventario.put(nombreItem.trim(), inventario.getOrDefault(nombreItem.trim(), 0) + cantidad);
        jugador.setInventario(inventario);
        return jugadorRepository.save(jugador);
    }

    public Map<String, Integer> consultarInventario(int jugadorId) {
        Optional<Jugador> jugadorOptional = jugadorRepository.findById(jugadorId);
        if (jugadorOptional.isEmpty()) {
            throw new RuntimeException("Jugador no encontrado con ID: " + jugadorId);
        }

        Map<String, Integer> inventario = jugadorOptional.get().getInventario();
        if (inventario.isEmpty()) {
            throw new RuntimeException("El inventario del jugador con ID " + jugadorId + " está vacío.");
        }

        return inventario;
    }
}
