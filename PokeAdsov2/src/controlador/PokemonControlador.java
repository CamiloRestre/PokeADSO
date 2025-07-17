package controlador;

import modelo.Pokemon;
import modelo.JuegoModelo;
import modelo.Carta;
import servicio.PokemonApiServicio;
import java.util.List;
import java.util.Scanner;

/**
 * Controlador principal del juego de Pokemon
 */
public class PokemonControlador {
    private JuegoModelo juego;
    private PokemonApiServicio pokemonServicio;
    private Scanner scanner;

    public PokemonControlador() {
        this.juego = new JuegoModelo();
        this.pokemonServicio = new PokemonApiServicio();
        this.scanner = new Scanner(System.in);
    }

    /**
     * Inicia el juego y maneja el flujo principal
     */
    public void iniciarJuego() {
        mostrarBienvenida();

        if (!pokemonServicio.probarConexionApi()) {
            System.err.println("No se pudo conectar con la API de Pokemon. Verifica tu conexión a internet.");
            return;
        }

        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerOpcionMenu();

            switch (opcion) {
                case 1:
                    iniciarNuevoCombate();
                    break;
                case 2:
                    buscarPokemon();
                    break;
                case 3:
                    mostrarPokemonAleatorios();
                    break;
                case 4:
                    mostrarEstadisticasJuego();
                    break;
                case 5:
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida. Intenta de nuevo.");
            }
        }

        despedirse();
    }

    /**
     * Inicia un nuevo combate Pokemon
     */
    private void iniciarNuevoCombate() {
        System.out.println("\n=== NUEVO COMBATE POKEMON ===");
        juego.reiniciarJuego();

        // Configurar Pokemon para Jugador 1
        configurarPokemonJugador("Jugador 1");

        // Configurar Pokemon para Jugador 2
        configurarPokemonJugador("Jugador 2");

        // Verificar que ambos jugadores tengan Pokemon
        if (!juego.iniciarJuego()) {
            System.out.println("Error: No se pudo iniciar el combate. Asegúrate de que ambos jugadores tengan Pokemon.");
            return;
        }

        // Ejecutar el combate
        ejecutarCombate();
    }

    /**
     * Configura el Pokemon para un jugador
     */
    private void configurarPokemonJugador(String jugador) {
        System.out.println("\n--- Configuración para " + jugador + " ---");
        System.out.println("1. Elegir Pokemon por nombre");
        System.out.println("2. Elegir Pokemon por ID");
        System.out.println("3. Pokemon aleatorio");
        System.out.print("Selecciona una opción: ");

        int opcion = leerOpcionMenu();
        Pokemon pokemon = null;

        switch (opcion) {
            case 1:
                pokemon = elegirPokemonPorNombre();
                break;
            case 2:
                pokemon = elegirPokemonPorId();
                break;
            case 3:
                pokemon = pokemonServicio.obtenerPokemonAleatorio();
                break;
            default:
                System.out.println("Opción no válida. Asignando Pokemon aleatorio...");
                pokemon = pokemonServicio.obtenerPokemonAleatorio();
        }

        if (pokemon != null) {
            juego.agregarCarta(pokemon, jugador);
            System.out.println("Pokemon asignado a " + jugador + ": " + pokemon.getNombre());
            mostrarEstadisticasPokemon(pokemon);
        } else {
            System.out.println("Error al obtener Pokemon. Asignando Pokemon aleatorio...");
            pokemon = pokemonServicio.obtenerPokemonAleatorio();
            if (pokemon != null) {
                juego.agregarCarta(pokemon, jugador);
                System.out.println("Pokemon asignado a " + jugador + ": " + pokemon.getNombre());
            }
        }
    }

    /**
     * Ejecuta el combate principal
     */
    private void ejecutarCombate() {
        System.out.println("\n=== COMENZANDO COMBATE ===");
        mostrarEstadoJuego();

        while (juego.juegoEnCurso()) {
            System.out.println("\n--- Turno " + juego.getTurnoNumero() + " ---");
            System.out.println("Es el turno de: " + juego.getJugadorActual());

            System.out.println("\nPresiona Enter para atacar...");
            scanner.nextLine();

            Carta.ResultadoAtaque resultado = juego.ejecutarAtaque();

            if (resultado.isExitoso()) {
                System.out.println("¡Ataque exitoso!");
                System.out.println("Daño causado: " + resultado.getDaño());
                System.out.println(resultado.getMensaje());
            } else {
                System.out.println("Error en el ataque: " + resultado.getMensaje());
            }

            mostrarEstadoJuego();

            if (juego.juegoTerminado()) {
                System.out.println("\n🎉 ¡COMBATE TERMINADO! 🎉");
                System.out.println("¡Ganador: " + juego.getGanador() + "!");
                mostrarHistorialCombate();
                break;
            }

            // Pausa antes del siguiente turno
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Permite al usuario elegir un Pokemon por nombre
     */
    private Pokemon elegirPokemonPorNombre() {
        System.out.print("Ingresa el nombre del Pokemon: ");
        String nombre = scanner.nextLine().trim();

        if (nombre.isEmpty()) {
            System.out.println("Nombre no válido.");
            return null;
        }

        System.out.println("Buscando " + nombre + "...");
        Pokemon pokemon = pokemonServicio.obtenerPokemonPorNombre(nombre);

        if (pokemon == null) {
            System.out.println("Pokemon no encontrado. Verifica el nombre e intenta de nuevo.");
        }

        return pokemon;
    }

    /**
     * Permite al usuario elegir un Pokemon por ID
     */
    private Pokemon elegirPokemonPorId() {
        System.out.print("Ingresa el ID del Pokemon (1-150): ");

        try {
            int id = Integer.parseInt(scanner.nextLine().trim());

            if (id < 1 || id > 150) {
                System.out.println("ID fuera de rango. Debe estar entre 1 y 150.");
                return null;
            }

            System.out.println("Buscando Pokemon con ID " + id + "...");
            Pokemon pokemon = pokemonServicio.obtenerPokemonPorId(id);

            if (pokemon == null) {
                System.out.println("Pokemon no encontrado con ID " + id);
            }

            return pokemon;

        } catch (NumberFormatException e) {
            System.out.println("ID no válido. Debe ser un número.");
            return null;
        }
    }

    /**
     * Busca y muestra información de un Pokemon
     */
    private void buscarPokemon() {
        System.out.println("\n=== BUSCAR POKEMON ===");
        System.out.println("1. Buscar por nombre");
        System.out.println("2. Buscar por ID");
        System.out.print("Selecciona una opción: ");

        int opcion = leerOpcionMenu();
        Pokemon pokemon = null;

        switch (opcion) {
            case 1:
                pokemon = elegirPokemonPorNombre();
                break;
            case 2:
                pokemon = elegirPokemonPorId();
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }

        if (pokemon != null) {
            mostrarInformacionDetallada(pokemon);
        }
    }

    /**
     * Muestra Pokemon aleatorios
     */
    private void mostrarPokemonAleatorios() {
        System.out.println("\n=== POKEMON ALEATORIOS ===");
        System.out.print("¿Cuántos Pokemon quieres ver? (1-10): ");

        try {
            int cantidad = Integer.parseInt(scanner.nextLine().trim());

            if (cantidad < 1 || cantidad > 10) {
                System.out.println("Cantidad no válida. Debe estar entre 1 y 10.");
                return;
            }

            System.out.println("Obteniendo " + cantidad + " Pokemon aleatorios...");
            List<Pokemon> pokemonList = pokemonServicio.obtenerPokemonAleatorios(cantidad);

            if (pokemonList.isEmpty()) {
                System.out.println("No se pudieron obtener Pokemon aleatorios.");
                return;
            }

            System.out.println("\n--- POKEMON ENCONTRADOS ---");
            for (int i = 0; i < pokemonList.size(); i++) {
                System.out.println((i + 1) + ". " + pokemonList.get(i).toString());
            }

        } catch (NumberFormatException e) {
            System.out.println("Cantidad no válida. Debe ser un número.");
        }
    }

    /**
     * Muestra las estadísticas del juego actual
     */
    private void mostrarEstadisticasJuego() {
        if (juego.juegoPreparacion()) {
            System.out.println("\nNo hay ningún juego en curso.");
        } else {
            System.out.println("\n" + juego.getEstadoJuego());

            if (!juego.getHistorialCombate().isEmpty()) {
                System.out.println("\n--- HISTORIAL DE COMBATE ---");
                for (String evento : juego.getHistorialCombate()) {
                    System.out.println(evento);
                }
            }
        }
    }

    /**
     * Muestra el estado actual del juego
     */
    private void mostrarEstadoJuego() {
        System.out.println("\n" + juego.getEstadoJuego());
    }

    /**
     * Muestra el historial de combate
     */
    private void mostrarHistorialCombate() {
        System.out.println("\n" + juego.getHistorialComoString());
    }

    /**
     * Muestra estadísticas básicas de un Pokemon
     */
    private void mostrarEstadisticasPokemon(Pokemon pokemon) {
        System.out.println("--- Estadísticas ---");
        System.out.println(pokemon.toString());
        System.out.println("Habilidad: " + pokemon.getHabilidadPrincipal());
        System.out.println("Velocidad: " + pokemon.getVelocidad());
    }

    /**
     * Muestra información detallada de un Pokemon
     */
    private void mostrarInformacionDetallada(Pokemon pokemon) {
        System.out.println("\n" + pokemonServicio.obtenerEstadisticasFormateadas(pokemon.getId()));

        if (pokemon.getImagenUrl() != null) {
            System.out.println("Imagen: " + pokemon.getImagenUrl());
        }
    }

    /**
     * Muestra la bienvenida del juego
     */
    private void mostrarBienvenida() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("    🎮 BIENVENIDO AL JUEGO DE POKEMON 🎮");
        System.out.println("=".repeat(50));
        System.out.println("¡Prepárate para el combate definitivo!");
        System.out.println("Conectando con la API de Pokemon...");
    }

    /**
     * Muestra el menú principal
     */
    private void mostrarMenuPrincipal() {
        System.out.println("\n" + "=".repeat(30));
        System.out.println("         MENÚ PRINCIPAL");
        System.out.println("=".repeat(30));
        System.out.println("1. Iniciar nuevo combate");
        System.out.println("2. Buscar Pokemon");
        System.out.println("3. Ver Pokemon aleatorios");
        System.out.println("4. Ver estadísticas del juego");
        System.out.println("5. Salir");
        System.out.print("Selecciona una opción: ");
    }

    /**
     * Lee una opción del menú
     */
    private int leerOpcionMenu() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Mensaje de despedida
     */
    private void despedirse() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("  ¡Gracias por jugar Pokemon Combat!");
        System.out.println("     ¡Hasta la próxima entrenador!");
        System.out.println("=".repeat(40));
    }

    /**
     * Método para cerrar recursos
     */
    public void cerrarRecursos() {
        if (scanner != null) {
            scanner.close();
        }
    }

    // Getters para acceso a los modelos
    public JuegoModelo getJuego() {
        return juego;
    }

    public PokemonApiServicio getPokemonServicio() {
        return pokemonServicio;
    }
}