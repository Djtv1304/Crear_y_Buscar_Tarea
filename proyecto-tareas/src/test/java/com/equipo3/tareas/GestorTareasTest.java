package com.equipo3.tareas;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class GestorTareasTest {

    private GestorTareas gestor;
    private LocalDate fechaHoy;
    private LocalDate fechaManana;
    private LocalDate fechaPasadaManana;
    private LocalDate fechaSemanaQueViene;


    @BeforeEach
    void setUp() {
        gestor = new GestorTareas();
        fechaHoy = LocalDate.now();
        fechaManana = LocalDate.now().plusDays(1);
        fechaPasadaManana = LocalDate.now().plusDays(2);
        fechaSemanaQueViene = LocalDate.now().plusDays(7);

        // Precarga de tareas existente
        gestor.crearTarea("100", "Tarea Hoy Alta", "Desc Hoy", Prioridad.alta, fechaHoy); //
        gestor.crearTarea("101", "Tarea Mañana Media", "Desc Mañana", Prioridad.media, fechaManana); //
        gestor.crearTarea("102", "Tarea Pasado Mañana Baja", "Desc Pasado", Prioridad.baja, fechaPasadaManana); //
        gestor.crearTarea("103", "Tarea Semana Que Viene Alta", "Desc Semana", Prioridad.alta, fechaSemanaQueViene); //
        gestor.crearTarea("104", "Tarea Hoy Media", "Desc Hoy 2", Prioridad.media, fechaHoy); //
    }

    // --- Pruebas Sprint 1 (Revisadas) ---
    @Test
    @DisplayName("Crear una tarea exitosamente")
    void crearTarea_conDatosValidos_debeCrearTarea() {
        String id = "12345";
        Tarea tareaCreada = gestor.crearTarea(id, "Implementar API", "Endpoints", Prioridad.alta, fechaManana); //
        assertNotNull(tareaCreada);
        assertEquals(id, tareaCreada.getId()); //
        assertEquals(EstadoTarea.Pendiente, tareaCreada.getEstado()); //
    }
    
    @Test
    @DisplayName("Intentar crear tarea con ID no numérico debe lanzar IllegalArgumentException")
    void crearTarea_conIdNoNumerico_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gestor.crearTarea("abc", "Título", "Desc", Prioridad.media, fechaManana)); //
    }
    
    @Test
    @DisplayName("Intentar crear tarea con fecha de vencimiento anterior a hoy debe lanzar IllegalArgumentException")
    void crearTarea_conFechaVencimientoAnterior_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gestor.crearTarea("67890", "Tarea Urgente", "Desc", Prioridad.alta, LocalDate.now().minusDays(1))); //
    }

    @Test
    @DisplayName("Intentar crear tarea con ID duplicado debe lanzar IllegalArgumentException")
    void crearTarea_conIdDuplicado_debeLanzarExcepcion() {
        String id = "111";
        gestor.crearTarea(id, "Tarea Inicial", "Desc", Prioridad.media, fechaManana); //
        assertThrows(IllegalArgumentException.class, () -> gestor.crearTarea(id, "Otra Tarea", "Desc", Prioridad.baja, fechaPasadaManana)); //
    }

    @Test
    @DisplayName("Buscar tarea existente por ID (numérico) debe devolver la tarea correcta")
    void buscarTareaPorId_existente_debeDevolverTarea() {
        String id = "100";
        Optional<Tarea> tareaOpcional = gestor.buscarTareaPorId(id); //
        assertTrue(tareaOpcional.isPresent());
        assertEquals(id, tareaOpcional.get().getId()); //
    }


    // --- Pruebas Sprint 2 (Actualizadas y Nuevas) ---

    @Test
    @DisplayName("Actualizar estado de tarea existente (Pendiente a En_progreso)")
    void actualizarEstadoTarea_existente_PendienteAEnProgreso() {
        String idTarea = "101"; // Estado inicial: Pendiente
        assertTrue(gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso)); //
        Optional<Tarea> tareaActualizada = gestor.buscarTareaPorId(idTarea); //
        assertTrue(tareaActualizada.isPresent());
        assertEquals(EstadoTarea.En_progreso, tareaActualizada.get().getEstado()); //
    }

    @Test
    @DisplayName("Actualizar estado de tarea existente (En_progreso a Completada)")
    void actualizarEstadoTarea_existente_EnProgresoACompletada() {
        String idTarea = "101";
        gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso); // Primero a En_progreso //
        assertTrue(gestor.actualizarEstadoTarea(idTarea, EstadoTarea.Completada)); //
        Optional<Tarea> tareaActualizada = gestor.buscarTareaPorId(idTarea); //
        assertTrue(tareaActualizada.isPresent());
        assertEquals(EstadoTarea.Completada, tareaActualizada.get().getEstado()); //
    }
    
    @Test
    @DisplayName("Actualizar estado de tarea a sí mismo no debe fallar")
    void actualizarEstadoTarea_ASiMismo_noDebeFallar() {
        String idTarea = "101"; // Pendiente
        assertTrue(gestor.actualizarEstadoTarea(idTarea, EstadoTarea.Pendiente)); //
        assertEquals(EstadoTarea.Pendiente, gestor.buscarTareaPorId(idTarea).get().getEstado()); //

        gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso); //
        assertTrue(gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso)); //
        assertEquals(EstadoTarea.En_progreso, gestor.buscarTareaPorId(idTarea).get().getEstado()); //
    }


    @Test
    @DisplayName("Transición inválida: Pendiente a Completada debe lanzar excepción")
    void actualizarEstadoTarea_invalida_PendienteACompletada_debeLanzarExcepcion() {
        String idTarea = "102"; // Estado inicial: Pendiente
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.actualizarEstadoTarea(idTarea, EstadoTarea.Completada); //
        });
        assertEquals("Transición de estado no válida: Desde 'Pendiente' solo se puede pasar a 'En_progreso'.", exception.getMessage());
    }

    @Test
    @DisplayName("Transición inválida: En_progreso a Pendiente debe lanzar excepción")
    void actualizarEstadoTarea_invalida_EnProgresoAPendiente_debeLanzarExcepcion() {
        String idTarea = "102";
        gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso); // Pasa a En_progreso //
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.actualizarEstadoTarea(idTarea, EstadoTarea.Pendiente); //
        });
        assertEquals("Transición de estado no válida: Desde 'En_progreso' solo se puede pasar a 'Completada'.", exception.getMessage());
    }
    
    @Test
    @DisplayName("Transición inválida: Tarea Completada no puede cambiar de estado")
    void actualizarEstadoTarea_invalida_CompletadaAEnProgreso_debeLanzarExcepcion() {
        String idTarea = "102";
        gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso); //
        gestor.actualizarEstadoTarea(idTarea, EstadoTarea.Completada); // Pasa a Completada //
        
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            gestor.actualizarEstadoTarea(idTarea, EstadoTarea.En_progreso); //
        });
        assertEquals("Una tarea 'Completada' no puede cambiar su estado.", exception.getMessage());
    }


    @Test
    @DisplayName("Actualizar estado de tarea no existente debe devolver false")
    void actualizarEstadoTarea_noExistente_debeDevolverFalse() {
        assertFalse(gestor.actualizarEstadoTarea("id_inexistente", EstadoTarea.Completada)); //
    }
    
    @Test
    @DisplayName("Listar tareas por prioridad alta")
    void listarTareasPorPrioridad_alta_debeDevolverTareasCorrectas() {
        List<Tarea> tareasAlta = gestor.listarTareasPorPrioridad(Prioridad.alta); //
        assertEquals(2, tareasAlta.size()); 
        assertTrue(tareasAlta.stream().allMatch(t -> t.getPrioridad() == Prioridad.alta)); //
    }

    @Test
    @DisplayName("Listar tareas por prioridad media")
    void listarTareasPorPrioridad_media_debeDevolverTareasCorrectas() {
        List<Tarea> tareasMedia = gestor.listarTareasPorPrioridad(Prioridad.media); //
        assertEquals(2, tareasMedia.size()); 
        assertTrue(tareasMedia.stream().allMatch(t -> t.getPrioridad() == Prioridad.media)); //
    }
    
    @Test
    @DisplayName("Listar tareas por prioridad baja")
    void listarTareasPorPrioridad_baja_debeDevolverTareasCorrectas() {
        List<Tarea> tareasBaja = gestor.listarTareasPorPrioridad(Prioridad.baja); //
        assertEquals(1, tareasBaja.size()); 
        assertTrue(tareasBaja.stream().allMatch(t -> t.getPrioridad() == Prioridad.baja)); //
    }

    @Test
    @DisplayName("Listar tareas por prioridad con ninguna tarea coincidente")
    void listarTareasPorPrioridad_sinCoincidencias_debeDevolverListaVacia() {
        GestorTareas gestorVacio = new GestorTareas();
        // CORRECCIÓN AQUÍ: Usar un ID numérico
        gestorVacio.crearTarea("999", "Temp Tarea", "Desc", Prioridad.alta, fechaManana); //
        List<Tarea> tareasMedia = gestorVacio.listarTareasPorPrioridad(Prioridad.media); //
        assertTrue(tareasMedia.isEmpty());
    }
    
    @Test
    @DisplayName("Listar tareas por prioridad nula debe lanzar IllegalArgumentException")
    void listarTareasPorPrioridad_nula_debeLanzarExcepcion() {
         assertThrows(IllegalArgumentException.class, () -> gestor.listarTareasPorPrioridad(null)); //
    }


    @Test
    @DisplayName("Listar tareas próximas a vencer (hoy y mañana, límite 1 día desde hoy)")
    void listarTareasProximasAVencer_limite1Dia_debeDevolverTareasHoyYManana() {
        List<Tarea> proximas = gestor.listarTareasProximasAVencer(1);  //
        assertEquals(3, proximas.size());
        assertTrue(proximas.stream().anyMatch(t -> t.getId().equals("100"))); //
        assertTrue(proximas.stream().anyMatch(t -> t.getId().equals("101"))); //
        assertTrue(proximas.stream().anyMatch(t -> t.getId().equals("104"))); //
    }

    @Test
    @DisplayName("Listar tareas próximas a vencer (límite 0 días, solo hoy)")
    void listarTareasProximasAVencer_limite0Dias_debeDevolverTareasHoy() {
        List<Tarea> proximas = gestor.listarTareasProximasAVencer(0);  //
        assertEquals(2, proximas.size());
        assertTrue(proximas.stream().anyMatch(t -> t.getId().equals("100"))); //
        assertTrue(proximas.stream().anyMatch(t -> t.getId().equals("104"))); //
        assertFalse(proximas.stream().anyMatch(t -> t.getId().equals("101"))); //
    }
    
    @Test
    @DisplayName("Listar tareas próximas a vencer (límite 7 días)")
    void listarTareasProximasAVencer_limite7Dias_debeDevolverTodasLasPrecargadasNoVencidas() {
        List<Tarea> proximas = gestor.listarTareasProximasAVencer(7); //
        assertEquals(5, proximas.size()); 
    }

    @Test
    @DisplayName("Listar tareas próximas a vencer con ninguna tarea coincidente")
    void listarTareasProximasAVencer_sinCoincidencias_debeDevolverListaVacia() {
        GestorTareas gestorNuevo = new GestorTareas();
        gestorNuevo.crearTarea("300", "Tarea Lejana", "Desc", Prioridad.media, LocalDate.now().plusDays(10)); //
        List<Tarea> proximas = gestorNuevo.listarTareasProximasAVencer(5);  //
        assertTrue(proximas.isEmpty());
    }
    
    @Test
    @DisplayName("Listar tareas próximas a vencer con límite negativo debe lanzar IllegalArgumentException")
    void listarTareasProximasAVencer_limiteNegativo_debeLanzarExcepcion() {
        assertThrows(IllegalArgumentException.class, () -> gestor.listarTareasProximasAVencer(-1)); //
    }
}
