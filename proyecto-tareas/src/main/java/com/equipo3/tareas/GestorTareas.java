package com.equipo3.tareas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Gestiona las operaciones relacionadas con las tareas.
 * Incluye creación, búsqueda, actualización de estado y listados.
 */
public class GestorTareas {
    private final Map<String, Tarea> repositorioTareas;

    public GestorTareas() {
        this.repositorioTareas = new HashMap<>();
    }

    public Tarea crearTarea(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaVencimiento) {
        if (id == null || id.trim().isEmpty()) {
             throw new IllegalArgumentException("El ID de la tarea no puede ser nulo ni vacío para crearla en el gestor.");
        }
        if (repositorioTareas.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe una tarea con el ID: " + id);
        }
        // La validación de ID numérico, campos nulos/vacíos y fecha de vencimiento
        // se realiza en el constructor de Tarea.
        Tarea nuevaTarea = new Tarea(id, titulo, descripcion, prioridad, fechaVencimiento);
        repositorioTareas.put(nuevaTarea.getId(), nuevaTarea);
        System.out.println("Tarea creada exitosamente: " + nuevaTarea.getTitulo() + " (ID: " + nuevaTarea.getId() + ")");
        return nuevaTarea;
    }

    public Optional<Tarea> buscarTareaPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID para buscar no puede ser nulo ni vacío.");
        }
        // Si se quisiera reforzar que el ID buscado sea numérico aquí también, se podría añadir:
        // try { Long.parseLong(id); } catch (NumberFormatException e) { throw new IllegalArgumentException("El ID para buscar debe ser numérico."); }
        return Optional.ofNullable(repositorioTareas.get(id));
    }

    /**
     * Actualiza el estado de una tarea existente.
     *
     * @param idTarea El ID de la tarea a actualizar.
     * @param nuevoEstado El nuevo estado para la tarea.
     * @return true si la tarea se actualizó correctamente, false si la tarea no se encontró.
     * @throws IllegalArgumentException si el idTarea o nuevoEstado son nulos, o si la transición de estado es inválida.
     */
    public boolean actualizarEstadoTarea(String idTarea, EstadoTarea nuevoEstado) {
        if (idTarea == null || idTarea.trim().isEmpty()) {
            throw new IllegalArgumentException("El ID de la tarea no puede ser nulo ni vacío para actualizar el estado.");
        }
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado no puede ser nulo.");
        }
        Optional<Tarea> tareaOpcional = buscarTareaPorId(idTarea);
        if (tareaOpcional.isPresent()) {
            Tarea tarea = tareaOpcional.get();
            // La validación de la transición ocurrirá aquí, dentro de tarea.setEstado()
            // Si la transición es inválida, setEstado lanzará una excepción.
            tarea.setEstado(nuevoEstado); 
            System.out.println("Estado de la tarea ID '" + idTarea + "' actualizado a: " + nuevoEstado);
            return true;
        } else {
            System.out.println("No se pudo actualizar: Tarea con ID '" + idTarea + "' no encontrada.");
            return false;
        }
    }

    /**
     * Lista todas las tareas que coinciden con una prioridad específica.
     *
     * @param prioridad La prioridad por la cual filtrar las tareas.
     * @return Una lista de tareas que tienen la prioridad especificada.
     * @throws IllegalArgumentException si la prioridad es nula.
     */
    public List<Tarea> listarTareasPorPrioridad(Prioridad prioridad) {
        if (prioridad == null) {
            throw new IllegalArgumentException("La prioridad para listar no puede ser nula.");
        }
        return repositorioTareas.values().stream()
                .filter(tarea -> tarea.getPrioridad() == prioridad)
                .collect(Collectors.toList());
    }

    /**
     * Lista todas las tareas cuya fecha de vencimiento está próxima.
     * Se considera "próxima" si la fecha de vencimiento es hoy o dentro de los 'diasLimite' especificados.
     * No incluye tareas ya vencidas (cuya fecha de vencimiento es anterior a hoy).
     *
     * @param diasLimite El número de días en el futuro (incluyendo hoy) para considerar una tarea como próxima a vencer.
     * @return Una lista de tareas próximas a vencer.
     * @throws IllegalArgumentException si diasLimite es negativo.
     */
    public List<Tarea> listarTareasProximasAVencer(int diasLimite) {
        if (diasLimite < 0) {
            throw new IllegalArgumentException("El límite de días no puede ser negativo.");
        }
        LocalDate hoy = LocalDate.now();
        // Imprimir la fecha actual para depuración
        System.out.println("DEBUG: La fecha actual (hoy) en GestorTareas es: " + hoy);

        LocalDate fechaTope = hoy.plusDays(diasLimite);

        return repositorioTareas.values().stream()
                .filter(tarea -> !tarea.getFechaVencimiento().isBefore(hoy) && // No vencidas antes de hoy
                                 !tarea.getFechaVencimiento().isAfter(fechaTope)) // Dentro del rango
                .collect(Collectors.toList());
    }
    
    public List<Tarea> obtenerTodasLasTareas() { // Método auxiliar para pruebas o listados generales
        return new ArrayList<>(repositorioTareas.values());
    }
}