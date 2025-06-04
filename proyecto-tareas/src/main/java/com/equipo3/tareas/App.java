package com.equipo3.tareas;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Optional;
import java.util.Scanner;
// No se necesita UUID ya que el ID lo ingresa el usuario

/**
 * Clase principal para interactuar con el Sistema de Gestión de Tareas.
 * El usuario ahora ingresa el ID de la tarea y debe ser numérico.
 * La prioridad se ingresa en minúsculas.
 * La fecha de vencimiento no puede ser anterior a la actual.
 */
public class App {
    private static final GestorTareas gestor = new GestorTareas();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
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
        System.out.println("-----------------------------------------");
        System.out.println("Menú Principal:");
        System.out.println("1. Crear nueva tarea");
        System.out.println("2. Buscar tarea por ID");
        System.out.println("0. Salir");
        System.out.println("-----------------------------------------");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcionMenu() {
        while (!scanner.hasNextInt()) {
            System.out.print("Entrada inválida. Por favor, ingrese un número: ");
            scanner.next(); 
        }
        int opcion = scanner.nextInt();
        scanner.nextLine(); 
        return opcion;
    }

    private static void crearNuevaTarea() {
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
                Long.parseLong(idInput); // Validar si es numérico
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
            System.out.print("Ingrese la prioridad (alta, media, baja): "); // Ahora en minúsculas
            String prioridadStr = scanner.nextLine().toLowerCase(); // Convertir a minúsculas
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
            // El mensaje de éxito ya se imprime en gestor.crearTarea()
        } catch (IllegalArgumentException e) {
            System.err.println("Error al crear la tarea: " + e.getMessage());
        }
    }

    private static void buscarTarea() {
        System.out.println("\n--- Buscar Tarea por ID ---");
        System.out.print("Ingrese el ID numérico de la tarea a buscar: ");
        String idBusqueda = scanner.nextLine();

        try {
            // Opcional: Validar si es numérico aquí también antes de llamar al gestor,
            // aunque el gestor o Tarea ya lo hacen.
            // try { Long.parseLong(idBusqueda); } catch (NumberFormatException e) {
            //    System.err.println("Error: El ID para buscar debe ser numérico."); return; }

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
}
