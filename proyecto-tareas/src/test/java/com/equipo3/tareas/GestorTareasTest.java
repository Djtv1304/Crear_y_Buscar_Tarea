package com.equipo3.tareas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas unitarias para la clase GestorTareas.
 * Actualizado para reflejar ID numérico y validación de fecha.
 */
class GestorTareasTest {

    private GestorTareas gestor;
    private LocalDate fechaValida;

    @BeforeEach
    void setUp() {
        gestor = new GestorTareas();
        fechaValida = LocalDate.now().plusDays(1); // Una fecha futura válida
    }

    @Test
    @DisplayName("Crear una tarea exitosamente con ID numérico y fecha válida")
    void crearTarea_conDatosValidos_debeCrearTarea() {
        String id = "12345";
        String titulo = "Implementar API";
        String descripcion = "Desarrollar los endpoints REST.";
        Prioridad prioridad = Prioridad.alta; // Usar minúsculas

        Tarea tareaCreada = gestor.crearTarea(id, titulo, descripcion, prioridad, fechaValida);

        assertNotNull(tareaCreada);
        assertEquals(id, tareaCreada.getId());
        assertEquals(titulo, tareaCreada.getTitulo());
        assertEquals(prioridad, tareaCreada.getPrioridad());
        assertEquals(fechaValida, tareaCreada.getFechaVencimiento());

        Optional<Tarea> tareaBuscada = gestor.buscarTareaPorId(id);
        assertTrue(tareaBuscada.isPresent());
        assertEquals(tareaCreada, tareaBuscada.get());
    }

    @Test
    @DisplayName("Intentar crear tarea con ID no numérico debe lanzar IllegalArgumentException")
    void crearTarea_conIdNoNumerico_debeLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.crearTarea("abc", "Título", "Descripción", Prioridad.media, fechaValida);
        });
        assertEquals("El ID de la tarea debe ser un valor numérico.", exception.getMessage());
    }
    
    @Test
    @DisplayName("Intentar crear tarea con fecha de vencimiento anterior a hoy debe lanzar IllegalArgumentException")
    void crearTarea_conFechaVencimientoAnterior_debeLanzarExcepcion() {
        LocalDate fechaAnterior = LocalDate.now().minusDays(1);
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.crearTarea("67890", "Tarea Urgente", "Con fecha pasada", Prioridad.alta, fechaAnterior);
        });
        assertEquals("La fecha de vencimiento no puede ser anterior a la fecha actual.", exception.getMessage());
    }


    @Test
    @DisplayName("Intentar crear tarea con ID duplicado debe lanzar IllegalArgumentException")
    void crearTarea_conIdDuplicado_debeLanzarExcepcion() {
        String id = "111";
        gestor.crearTarea(id, "Tarea Inicial", "Descripción inicial", Prioridad.media, fechaValida);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.crearTarea(id, "Otra Tarea", "Otra descripción", Prioridad.baja, fechaValida.plusDays(1));
        });
        
        assertEquals("Ya existe una tarea con el ID: " + id, exception.getMessage());
    }

    @Test
    @DisplayName("Intentar crear tarea con campos obligatorios nulos o vacíos debe fallar")
    void crearTarea_conCamposObligatoriosNulosOVacios_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> new Tarea(null, "Título", "Desc", Prioridad.alta, fechaValida));
        assertThrows(IllegalArgumentException.class, () -> gestor.crearTarea(null, "Título", "Desc", Prioridad.alta, fechaValida));
        assertThrows(IllegalArgumentException.class, () -> new Tarea("123", "  ", "Desc", Prioridad.alta, fechaValida));
        assertThrows(IllegalArgumentException.class, () -> new Tarea("123", "Título", null, Prioridad.alta, fechaValida));
        assertThrows(IllegalArgumentException.class, () -> new Tarea("123", "Título", "Desc", null, fechaValida));
        assertThrows(IllegalArgumentException.class, () -> new Tarea("123", "Título", "Desc", Prioridad.media, null));
    }


    @Test
    @DisplayName("Buscar tarea existente por ID (numérico) debe devolver la tarea correcta")
    void buscarTareaPorId_existente_debeDevolverTarea() {
        String id = "777";
        Tarea tareaOriginal = gestor.crearTarea(id, "Tarea de Prueba", "Buscar esta tarea.", Prioridad.media, fechaValida);
        Optional<Tarea> tareaEncontradaOpcional = gestor.buscarTareaPorId(id);
        assertTrue(tareaEncontradaOpcional.isPresent());
        assertEquals(tareaOriginal.getId(), tareaEncontradaOpcional.get().getId());
    }

    @Test
    @DisplayName("Buscar tarea con ID no existente debe devolver Optional vacío")
    void buscarTareaPorId_noExistente_debeDevolverOptionalVacio() {
        Optional<Tarea> tareaEncontradaOpcional = gestor.buscarTareaPorId("9999999"); // ID numérico no existente
        assertFalse(tareaEncontradaOpcional.isPresent());
    }

    // La validación de ID no numérico en buscarTareaPorId es opcional en GestorTareas si Tarea ya lo hace,
    // pero si se quiere ser estricto en la entrada del método buscarTareaPorId del gestor:
    /*
    @Test
    @DisplayName("Buscar tarea con ID no numérico debe lanzar IllegalArgumentException")
    void buscarTareaPorId_conIdNoNumerico_debeLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.buscarTareaPorId("idNoNumerico");
        });
        // El mensaje exacto dependerá de dónde se coloque la validación estricta en buscarTareaPorId.
        // Por ahora, el código actual de buscarTareaPorId no valida esto explícitamente, 
        // ya que asume que los IDs almacenados son válidos.
        // Si se añade la validación, se debe ajustar el mensaje esperado.
        // assertEquals("El ID para buscar debe ser numérico.", exception.getMessage());
        // Por ahora, esta prueba fallaría o pasaría dependiendo de si el ID "idNoNumerico" existe o no.
        // Para el propósito actual, la validación principal está en la creación.
        // Si se busca un ID no numérico que no existe, simplemente devolverá Optional.empty().
         Optional<Tarea> tareaEncontradaOpcional = gestor.buscarTareaPorId("idNoNumerico");
         assertFalse(tareaEncontradaOpcional.isPresent(), "No se esperaba encontrar tarea con ID no numérico.");
    }
    */
    
    @Test
    @DisplayName("Buscar tarea con ID nulo debe lanzar IllegalArgumentException")
    void buscarTareaPorId_conIdNulo_debeLanzarExcepcion() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gestor.buscarTareaPorId(null));
        assertEquals("El ID para buscar no puede ser nulo ni vacío.", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar tarea con ID vacío debe lanzar IllegalArgumentException")
    void buscarTareaPorId_conIdVacio_debeLanzarExcepcion() {
         IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> gestor.buscarTareaPorId("   "));
        assertEquals("El ID para buscar no puede ser nulo ni vacío.", exception.getMessage());
    }
}
