package com.equipo3.tareas;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa una tarea en el sistema de gestión de tareas.
 * Incluye ID, título, descripción, prioridad, fecha de vencimiento y estado.
 * La transición de estado es ordenada: Pendiente -> En_progreso -> Completada.
 */
public class Tarea {
    private String id;
    private String titulo;
    private String descripcion;
    private Prioridad prioridad;
    private LocalDate fechaVencimiento;
    private EstadoTarea estado;

    public Tarea(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaVencimiento) {
        // ... (constructor sin cambios, se mantiene igual que la versión anterior)
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser nulo ni vacío.");
        }
        try {
            Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("El ID de la tarea debe ser un valor numérico.");
        }
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
        if (fechaVencimiento.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha de vencimiento no puede ser anterior a la fecha actual.");
        }

        this.id = id;
        this.titulo = titulo;
        this.descripcion = descripcion;
        this.prioridad = prioridad;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = EstadoTarea.Pendiente;
    }

    // Getters (sin cambios)
    public String getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescripcion() { return descripcion; }
    public Prioridad getPrioridad() { return prioridad; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public EstadoTarea getEstado() { return estado; }

    /**
     * Actualiza el estado de la tarea.
     * La transición de estado debe seguir el orden: Pendiente -> En_progreso -> Completada.
     * No se permiten transiciones hacia atrás o saltos directos a Completada desde Pendiente.
     * Una tarea Completada no puede cambiar su estado.
     *
     * @param nuevoEstado El nuevo estado deseado para la tarea.
     * @throws IllegalArgumentException Si el nuevoEstado es nulo o la transición no es válida.
     */
    public void setEstado(EstadoTarea nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la tarea no puede ser nulo.");
        }

        switch (this.estado) {
            case Pendiente:
                if (nuevoEstado == EstadoTarea.En_progreso) {
                    this.estado = nuevoEstado;
                } else if (nuevoEstado == EstadoTarea.Pendiente) {
                    // No hacer nada, ya está en este estado
                } else {
                    throw new IllegalArgumentException("Transición de estado no válida: Desde 'Pendiente' solo se puede pasar a 'En_progreso'.");
                }
                break;
            case En_progreso:
                if (nuevoEstado == EstadoTarea.Completada) {
                    this.estado = nuevoEstado;
                } else if (nuevoEstado == EstadoTarea.En_progreso) {
                    // No hacer nada, ya está en este estado
                } else {
                    throw new IllegalArgumentException("Transición de estado no válida: Desde 'En_progreso' solo se puede pasar a 'Completada'.");
                }
                break;
            case Completada:
                if (nuevoEstado != EstadoTarea.Completada) {
                    throw new IllegalArgumentException("Una tarea 'Completada' no puede cambiar su estado.");
                }
                // Si es Completada a Completada, no hacer nada.
                break;
            default:
                throw new IllegalStateException("Estado actual desconocido: " + this.estado);
        }
    }

    @Override
    public String toString() {
        return "Tarea {" +
               "ID = '" + id + '\'' +
               ", Título = '" + titulo + '\'' +
               ", Descripción = '" + descripcion + '\'' +
               ", Prioridad = " + prioridad +
               ", Fecha de Vencimiento = " + fechaVencimiento +
               ", Estado = " + estado +
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
