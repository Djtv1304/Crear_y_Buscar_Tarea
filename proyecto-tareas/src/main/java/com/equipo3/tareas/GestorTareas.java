package com.equipo3.tareas;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Gestiona las operaciones relacionadas con las tareas,
 * como crear, buscar, actualizar y listar tareas.
 */
public class GestorTareas {
    // Usamos un Map para almacenar las tareas, donde la clave es el ID de la tarea.
    // Esto permite una búsqueda eficiente por ID.
    private final Map<String, Tarea> repositorioTareas;

    /**
     * Constructor que inicializa el gestor de tareas.
     */
    public GestorTareas() {
        this.repositorioTareas = new HashMap<>();
    }

    /**
     * Crea una nueva tarea y la añade al sistema.
     * Los campos ID, título, descripción, prioridad y fecha de vencimiento son obligatorios.
     *
     * @param id El identificador único de la tarea.
     * @param titulo El título de la tarea.
     * @param descripcion La descripción detallada de la tarea.
     * @param prioridad La prioridad de la tarea (ALTA, MEDIA, BAJA).
     * @param fechaVencimiento La fecha de vencimiento de la tarea.
     * @return La tarea creada.
     * @throws IllegalArgumentException Si alguno de los parámetros obligatorios es nulo o inválido,
     * o si ya existe una tarea con el mismo ID.
     */
    public Tarea crearTarea(String id, String titulo, String descripcion, Prioridad prioridad, LocalDate fechaVencimiento) {
        // La validación de campos nulos/vacíos se hace en el constructor de Tarea.
        // Aquí validamos si el ID ya existe.
        if (id == null || id.trim().isEmpty()) {
             throw new IllegalArgumentException("El ID de la tarea no puede ser nulo ni vacío para crearla.");
        }
        if (repositorioTareas.containsKey(id)) {
            throw new IllegalArgumentException("Ya existe una tarea con el ID: " + id);
        }

        Tarea nuevaTarea = new Tarea(id, titulo, descripcion, prioridad, fechaVencimiento);
        repositorioTareas.put(nuevaTarea.getId(), nuevaTarea);
        System.out.println("Tarea creada exitosamente: " + nuevaTarea.getTitulo());
        return nuevaTarea;
    }

    /**
     * Busca una tarea por su ID.
     *
     * @param id El ID de la tarea a buscar.
     * @return Un Optional que contiene la tarea si se encuentra, o un Optional vacío si no.
     * @throws IllegalArgumentException Si el ID es nulo o vacío.
     */
    public Optional<Tarea> buscarTareaPorId(String id) {
        if (id == null || id.trim().isEmpty()) {
            // Devolvemos Optional vacío o lanzamos excepción según el contrato definido.
            // Para este caso, es mejor lanzar una excepción si el input es inválido.
            throw new IllegalArgumentException("El ID para buscar no puede ser nulo ni vacío.");
        }
        Tarea tareaEncontrada = repositorioTareas.get(id);
        return Optional.ofNullable(tareaEncontrada);
    }
    
    /**
     * Obtiene todas las tareas almacenadas.
     * Útil para pruebas o listados generales.
     * @return Una lista con todas las tareas.
     */
    public List<Tarea> obtenerTodasLasTareas() {
        return new ArrayList<>(repositorioTareas.values());
    }
}
