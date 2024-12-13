package com.juegorpg.sanmar.Model;

import java.util.HashMap;
import java.util.Map;
import jakarta.persistence.*;

@Entity
@Table(name = "jugadores")
public class Jugador {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private int nivel;
    private int puntuacion;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "inventario", joinColumns = @JoinColumn(name = "jugador_id"))
    @MapKeyColumn(name = "nombre_articulo")
    @Column(name = "cantidad")
    private Map<String, Integer> inventario = new HashMap<>();

    public Jugador() {
    }

    public Jugador(int id, String nombre, int nivel, int puntuacion, Map<String, Integer> inventario) {
        this.id = id;
        this.nombre = nombre;
        this.nivel = nivel;
        this.puntuacion = puntuacion;
        this.inventario = inventario;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public Map<String, Integer> getInventario() {
        return inventario;
    }

    public void setInventario(Map<String, Integer> inventario) {
        this.inventario = inventario;
    }

    @Override
    public String toString() {
        return "Jugador{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", nivel=" + nivel +
               ", puntuacion=" + puntuacion +
               ", inventario=" + inventario +
               '}';
    }
}
