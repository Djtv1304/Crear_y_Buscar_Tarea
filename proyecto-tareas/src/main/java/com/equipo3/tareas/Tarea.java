package com.equipo3.tareas;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una tarea en el sistema de gestión de tareas.
 * Cada tarea tiene un ID (numérico), título, descripción, prioridad y fecha de vencimiento.
 * La fecha de vencimiento no puede ser anterior a la fecha actual.
 */
public class Tarea {
    private String id; // Se mantiene como String para flexibilidad, pero se valida que sea numérico.
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private LocalDate fechaVencimiento;

    /**
     * Constructor para crear una nueva tarea.
     *
     * @param id El identificador único y numérico de la tarea. No debe ser nulo ni vacío.
     * @param titulo El título de la tarea. No debe ser nulo ni vacío.
     * @param descripcion La descripción detallada de la tarea. No debe ser nula ni vacía.
     * @param prioridad La prioridad de la tarea (alta, media, baja). No debe ser nula.
     * @param fechaVencimiento La fecha de vencimiento de la tarea. No debe ser nula ni anterior a la fecha actual.
     * @throws IllegalArgumentException Si alguno de los parámetros obligatorios es nulo, inválido,
     * o la fecha de vencimiento es anterior a la actual, o el ID no es numérico.
     */
    public Tarea(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaVencimiento) {
        // Validación de ID
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser nulo ni vacío.");
        }
        try {
            Long.parseLong(id); // Intenta convertir a Long para validar si es numérico
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID de la tarea debe ser un valor numérico.");
        }

        // Validación de otros campos obligatorios
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título de la tarea no puede ser nulo ni vacío.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la tarea no puede ser nula ni vacía.");
        }
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad de la tarea no puede ser nula.");
        }
        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento de la tarea no puede ser nula.");
        }
        // Nueva validación: Fecha de vencimiento no puede ser anterior a la fecha actual
        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha actual.");
        }

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaVencimiento = fechaVencimiento;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Prioridad getPrioridad() {
        return prioridad;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    @Override
    public String toString() {
        return "Tarea {" +
               "ID = '" + id + '\'' +
               ", Título = '" + titulo + '\'' +
               ", Descripción = '" + descripcion + '\'' +
               ", Prioridad = " + prioridad +
               ", Fecha de Vencimiento = " + fechaVencimiento +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tarea tarea = (Tarea) o;
        return Objects.equals(id, tarea.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
