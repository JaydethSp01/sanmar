package com.juegorpg.sanmar.Services;

import com.juegorpg.sanmar.Model.Partida;
import com.juegorpg.sanmar.Repository.PartidaRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class PartidaService {

    @Autowired
    private PartidaRepository partidaRepository;

    private static class GameTreeNode {
        Partida partida;
        GameTreeNode left;
        GameTreeNode right;

        GameTreeNode(Partida partida) {
            this.partida = partida;
            this.left = null;
            this.right = null;
        }
    }

    private GameTreeNode root;

    @PostConstruct
    public void init() {
        cargarPartidasEnElArbol();
    }

    public void cargarPartidasEnElArbol() {
        List<Partida> partidas = partidaRepository.findAll();
        for (Partida partida : partidas) {
            insertIntoTree(partida);
        }
    }

    public Partida registrarPartida(String equipo1, String equipo2, int idJugador1, int idJugador2) {
        validarEntradas(equipo1, equipo2, idJugador1, idJugador2);

        Random random = new Random();
        int vidaJugador1 = 100;
        int vidaJugador2 = 100;

        StringBuilder registroSimulacion = new StringBuilder("Simulación de partida:\n");

        while (vidaJugador1 > 0 && vidaJugador2 > 0) {
            int ataqueJugador1 = random.nextInt(20) + 5;
            int ataqueJugador2 = random.nextInt(20) + 5;

            vidaJugador2 -= ataqueJugador1;
            vidaJugador1 -= ataqueJugador2;

            agregarMensajeConRetraso(registroSimulacion, String.format(
                "Jugador %s ataca con %d. Vida restante de %s: %d\n",
                equipo1, ataqueJugador1, equipo2, Math.max(vidaJugador2, 0)
            ));

            agregarMensajeConRetraso(registroSimulacion, String.format(
                "Jugador %s ataca con %d. Vida restante de %s: %d\n",
                equipo2, ataqueJugador2, equipo1, Math.max(vidaJugador1, 0)
            ));
        }

        boolean jugador1Gana = vidaJugador1 > 0;
        String ganador = jugador1Gana ? equipo1 : equipo2;
        int idGanador = jugador1Gana ? idJugador1 : idJugador2;
        int idPerdedor = jugador1Gana ? idJugador2 : idJugador1;

        registroSimulacion.append(String.format("\u00a1Ganador: %s!\n", ganador));

        Partida partida = new Partida();
        partida.setFecha(LocalDate.now());
        partida.setEquipo1_nombre(equipo1);
        partida.setEquipo2_nombre(equipo2);
        partida.setEquipo1_idjugador(idJugador1);
        partida.setEquipo2_idjugador(idJugador2);
        partida.setResultado(registroSimulacion.toString());

        Partida savedPartida = partidaRepository.save(partida);

        insertIntoTree(savedPartida);

        partidaRepository.executeStoredProcedure(idGanador, idPerdedor, 150, 75);

        System.out.printf("Jugador %s ha ganado la partida. Resultado registrado en el historial.%n", ganador);

        return savedPartida;
    }

    private void agregarMensajeConRetraso(StringBuilder registro, String mensaje) {
        registro.append(mensaje);
        System.out.print(mensaje);
        try {
            Thread.sleep(1000); // Retraso de 1 segundo para aumentar la intriga
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Error durante la simulación de la partida", e);
        }
    }

    private void validarEntradas(String equipo1, String equipo2, int idJugador1, int idJugador2) {
        if (equipo1 == null || equipo1.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo 1 no puede estar vacío.");
        }
        if (equipo2 == null || equipo2.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del equipo 2 no puede estar vacío.");
        }
        if (idJugador1 <= 0 || idJugador2 <= 0) {
            throw new IllegalArgumentException("Los IDs de los jugadores deben ser mayores a 0.");
        }
        if (idJugador1 == idJugador2) {
            throw new IllegalArgumentException("Los jugadores no pueden ser el mismo.");
        }
    }

    private void insertIntoTree(Partida partida) {
        root = insertRec(root, partida);
    }

    private GameTreeNode insertRec(GameTreeNode node, Partida partida) {
        if (node == null) {
            return new GameTreeNode(partida);
        }

        if (partida.getFecha().isBefore(node.partida.getFecha())) {
            node.left = insertRec(node.left, partida);
        } else if (partida.getFecha().isAfter(node.partida.getFecha())) {
            node.right = insertRec(node.right, partida);
        }

        return node;
    }

    public List<Partida> consultarPartidasEntreFechas(LocalDate inicio, LocalDate fin) {
        List<Partida> resultado = new ArrayList<>();
        buscarEntreFechas(root, inicio, fin, resultado);
        return resultado;
    }

    private void buscarEntreFechas(GameTreeNode node, LocalDate inicio, LocalDate fin, List<Partida> resultado) {
        if (node == null) {
            return;
        }

        if (!node.partida.getFecha().isBefore(inicio) && !node.partida.getFecha().isAfter(fin)) {
            resultado.add(node.partida);
        }

        if (node.partida.getFecha().isAfter(inicio)) {
            buscarEntreFechas(node.left, inicio, fin, resultado);
        }

        if (node.partida.getFecha().isBefore(fin)) {
            buscarEntreFechas(node.right, inicio, fin, resultado);
        }
    }
}
