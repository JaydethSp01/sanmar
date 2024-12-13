package com.juegorpg.sanmar.Model;

import java.time.LocalDate;
import jakarta.persistence.*;

@Entity
@Table(name = "partidas")
public class Partida {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private String equipo1_nombre;

    @Column(nullable = false)
    private String equipo2_nombre;

    @Column(nullable = false)
    private int equipo1_idjugador;

    @Column(nullable = false)
    private int equipo2_idjugador;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String resultado;

    public Partida() {
    }

    public Partida(int id, LocalDate fecha, String equipo1_nombre, String equipo2_nombre, 
                   int equipo1_idjugador, int equipo2_idjugador, String resultado) {
        this.id = id;
        this.fecha = fecha;
        this.equipo1_nombre = equipo1_nombre;
        this.equipo2_nombre = equipo2_nombre;
        this.equipo1_idjugador = equipo1_idjugador;
        this.equipo2_idjugador = equipo2_idjugador;
        this.resultado = resultado;
    }

    // Getters y Setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getEquipo1_nombre() {
        return equipo1_nombre;
    }

    public void setEquipo1_nombre(String equipo1_nombre) {
        this.equipo1_nombre = equipo1_nombre;
    }

    public String getEquipo2_nombre() {
        return equipo2_nombre;
    }

    public void setEquipo2_nombre(String equipo2_nombre) {
        this.equipo2_nombre = equipo2_nombre;
    }

    public int getEquipo1_idjugador() {
        return equipo1_idjugador;
    }

    public void setEquipo1_idjugador(int equipo1_idjugador) {
        this.equipo1_idjugador = equipo1_idjugador;
    }

    public int getEquipo2_idjugador() {
        return equipo2_idjugador;
    }

    public void setEquipo2_idjugador(int equipo2_idjugador) {
        this.equipo2_idjugador = equipo2_idjugador;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    @Override
    public String toString() {
        return "Partida{" +
               "id=" + id +
               ", fecha=" + fecha +
               ", equipo1_nombre='" + equipo1_nombre + '\'' +
               ", equipo2_nombre='" + equipo2_nombre + '\'' +
               ", equipo1_idjugador=" + equipo1_idjugador +
               ", equipo2_idjugador=" + equipo2_idjugador +
               ", resultado='" + resultado + '\'' +
               '}';
    }
}
