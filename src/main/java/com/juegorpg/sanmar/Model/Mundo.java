package com.juegorpg.sanmar.Model;

import jakarta.persistence.*;

@Entity
@Table(name = "mundos")
public class Mundo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String nombre;
    private String grafo_serializado;

    public Mundo() {
    }

    public Mundo(int id, String nombre, String grafo_serializado) {
        this.id = id;
        this.nombre = nombre;
        this.grafo_serializado = grafo_serializado;
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

    public String getGrafo_serializado() {
        return grafo_serializado;
    }

    public void setGrafo_serializado(String grafo_serializado) {
        this.grafo_serializado = grafo_serializado;
    }

    @Override
    public String toString() {
        return "Mundo{" +
               "id=" + id +
               ", nombre='" + nombre + '\'' +
               ", grafo_serializado='" + grafo_serializado + '\'' +
               '}';
    }
}
