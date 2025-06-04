package com.equipo3.tareas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.concurrent.TimeUnit; // Para medir tiempo

public class App {
    private static final GestorTareas gestor = new GestorTareas();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // ... (main sin cambios)
        System.out.println("Bienvenido al Sistema de Gestión de Tareas Empresarial");

        while (true) {
            mostrarMenu();
            int opcion = leerOpcionMenu();

            switch (opcion) {
                case 1:
                    crearNuevaTarea();
                    break;
                case 2:
                    buscarTarea();
                    break;
                case 3:
                    actualizarEstadoDeTarea();
                    break;
                case 4:
                    listarPorPrioridad();
                    break;
                case 5:
                    listarProximasAVencer();
                    break;
                case 0:
                    System.out.println("Saliendo del sistema...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Opción no válida. Por favor, intente de nuevo.");
            }
            System.out.println();
        }
    }

    private static void mostrarMenu() {
        // ... (mostrarMenu sin cambios)
        System.out.println("-----------------------------------------");
        System.out.println("Menú Principal:");
        System.out.println("1. Crear nueva tarea");
        System.out.println("2. Buscar tarea por ID");
        System.out.println("3. Actualizar estado de tarea");
        System.out.println("4. Listar tareas por prioridad");
        System.out.println("5. Listar tareas próximas a vencer");
        System.out.println("0. Salir");
        System.out.println("-----------------------------------------");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcionMenu() {
        // ... (leerOpcionMenu sin cambios)
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Por favor, ingrese un número: ");
            scanner.next();
        }
        int opcion = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        return opcion;
    }

    private static void crearNuevaTarea() {
        // ... (crearNuevaTarea sin cambios respecto a la última versión)
        System.out.println("\n--- Crear Nueva Tarea ---");
        String id = null;
        while (id == null) {
            System.out.print("Ingrese el ID numérico de la tarea: ");
            String idInput = scanner.nextLine();
            if (idInput.trim().isEmpty()) {
                System.out.println("El ID no puede estar vacío.");
                continue;
            }
            try {
                Long.parseLong(idInput);
                id = idInput;
            } catch (NumberFormatException e) {
                System.out.println("ID inválido. Debe ser un valor numérico.");
            }
        }
        System.out.print("Ingrese el título de la tarea: ");
        String titulo = scanner.nextLine();
        System.out.print("Ingrese la descripción de la tarea: ");
        String descripcion = scanner.nextLine();
        Prioridad prioridad = null;
        while (prioridad == null) {
            System.out.print("Ingrese la prioridad (alta, media, baja): ");
            String prioridadStr = scanner.nextLine().toLowerCase();
            try {
                prioridad = Prioridad.valueOf(prioridadStr);
            } catch (IllegalArgumentException e) {
                System.out.println("Prioridad no válida. Use alta, media o baja.");
            }
        }
        LocalDate fechaVencimiento = null;
        while (fechaVencimiento == null) {
            System.out.print("Ingrese la fecha de vencimiento (YYYY-MM-DD): ");
            String fechaStr = scanner.nextLine();
            try {
                LocalDate fechaIngresada = LocalDate.parse(fechaStr);
                if (fechaIngresada.isBefore(LocalDate.now())) {
                    System.out.println("La fecha de vencimiento no puede ser anterior a la fecha actual. Intente de nuevo.");
                } else {
                    fechaVencimiento = fechaIngresada;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Formato de fecha no válido. Use YYYY-MM-DD.");
            }
        }
        try {
            gestor.crearTarea(id, titulo, descripcion, prioridad, fechaVencimiento);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear la tarea: " + e.getMessage());
        }
    }

    private static void buscarTarea() {
        // ... (buscarTarea sin cambios)
        System.out.println("\n--- Buscar Tarea por ID ---");
        System.out.print("Ingrese el ID numérico de la tarea a buscar: ");
        String idBusqueda = scanner.nextLine();
        try {
            Optional<Tarea> tareaOpcional = gestor.buscarTareaPorId(idBusqueda);
            if (tareaOpcional.isPresent()) {
                System.out.println("Tarea encontrada:");
                System.out.println(tareaOpcional.get());
            } else {
                System.out.println("Tarea con ID '" + idBusqueda + "' no encontrada.");
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Error al buscar la tarea: " + e.getMessage());
        }
    }

    private static void actualizarEstadoDeTarea() {
        System.out.println("\n--- Actualizar Estado de Tarea ---");
        System.out.print("Ingrese el ID de la tarea a actualizar: ");
        String idTarea = scanner.nextLine();

        if (idTarea.trim().isEmpty()) {
            System.out.println("El ID de la tarea no puede estar vacío.");
            return;
        }
        try {
            Long.parseLong(idTarea);
        } catch (NumberFormatException e) {
            System.out.println("ID inválido. Debe ser un valor numérico.");
            return;
        }

        Optional<Tarea> tareaOpcional = gestor.buscarTareaPorId(idTarea);
        if (!tareaOpcional.isPresent()) {
            System.out.println("Tarea con ID '" + idTarea + "' no encontrada.");
            return;
        }

        System.out.println("Tarea actual: " + tareaOpcional.get());
        System.out.print("Ingrese el nuevo estado (Pendiente, En_progreso, Completada): ");
        String estadoStr = scanner.nextLine();
        try {
            EstadoTarea nuevoEstado = EstadoTarea.valueOf(estadoStr);
            // La llamada a gestor.actualizarEstadoTarea puede lanzar una excepción si la transición es inválida
            if (gestor.actualizarEstadoTarea(idTarea, nuevoEstado)) {
                // El mensaje de éxito ya se imprime en el gestor si la actualización es exitosa
                // y la transición es permitida por Tarea.setEstado()
            }
            // Si la tarea no se encuentra, gestor.actualizarEstadoTarea imprime un mensaje y devuelve false
            // Si la transición es inválida, tarea.setEstado() (llamado desde el gestor) lanzará IllegalArgumentException
        } catch (IllegalArgumentException e) { // Captura errores de valueOf o de transición inválida
            System.err.println("Error al actualizar estado: " + e.getMessage());
        }
    }

    private static void listarPorPrioridad() {
        System.out.println("\n--- Listar Tareas por Prioridad ---");
        System.out.print("Ingrese la prioridad para listar (alta, media, baja): ");
        String prioridadStr = scanner.nextLine().toLowerCase();
        try {
            Prioridad prioridad = Prioridad.valueOf(prioridadStr);
            
            long startTime = System.nanoTime(); // Iniciar temporizador
            List<Tarea> tareas = gestor.listarTareasPorPrioridad(prioridad); //
            long endTime = System.nanoTime(); // Detener temporizador
            long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime); // Duración en milisegundos

            if (tareas.isEmpty()) {
                System.out.println("No se encontraron tareas con prioridad: " + prioridad);
            } else {
                System.out.println("Tareas con prioridad '" + prioridad + "':");
                tareas.forEach(System.out::println);
            }
            System.out.println("(Consulta completada en " + duration + " ms)");

        } catch (IllegalArgumentException e) {
            System.out.println("Prioridad no válida. Use alta, media o baja.");
        }
    }

    private static void listarProximasAVencer() {
        System.out.println("\n--- Listar Tareas Próximas a Vencer ---");
        System.out.print("Listar tareas que vencen en los próximos cuántos días (ej. 7 para la próxima semana): ");
        int dias;
        while (true) {
            if (scanner.hasNextInt()) {
                dias = scanner.nextInt();
                scanner.nextLine(); 
                if (dias >= 0) {
                    break;
                } else {
                    System.out.println("El número de días no puede ser negativo. Intente de nuevo:");
                }
            } else {
                System.out.println("Entrada inválida. Por favor ingrese un número. Intente de nuevo:");
                scanner.nextLine(); 
            }
        }

        long startTime = System.nanoTime(); // Iniciar temporizador
        List<Tarea> tareas = gestor.listarTareasProximasAVencer(dias); //
        long endTime = System.nanoTime(); // Detener temporizador
        long duration = TimeUnit.NANOSECONDS.toMillis(endTime - startTime); // Duración en milisegundos


        if (tareas.isEmpty()) {
            System.out.println("No hay tareas próximas a vencer en los siguientes " + dias + " días.");
        } else {
            System.out.println("Tareas que vencen en los próximos " + dias + " días (o hoy):");
            tareas.forEach(System.out::println);
        }
        System.out.println("(Consulta completada en " + duration + " ms)");
    }
}
