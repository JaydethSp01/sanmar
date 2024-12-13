package com.juegorpg.sanmar.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "ranking")
public class Ranking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int id_jugador;
    private int puntuacion;
    private int posicion;
   

    public Ranking() {
    }

    public Ranking(int id, int id_jugador, int puntuacion, int posicion) {
        this.id = id;
        this.id_jugador = id_jugador;
        this.puntuacion = puntuacion;
        this.posicion = posicion;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_jugador() {
        return id_jugador;
    }

    public void setId_jugador(int id_jugador) {
        this.id_jugador = id_jugador;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }

    public int getPosicion() {
        return posicion;
    }

    public void setPosicion(int posicion) {
        this.posicion = posicion;
    }


    @Override
    public String toString() {
        return "Ranking{" +
               "id=" + id +
               ", id_jugador=" + id_jugador +
               ", puntuacion=" + puntuacion +
               ", posicion=" + posicion +
               '}';
    }
}
