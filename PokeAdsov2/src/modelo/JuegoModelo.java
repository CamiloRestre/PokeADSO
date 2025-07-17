package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase que maneja la lógica del juego de cartas Pokemon
 */
public class JuegoModelo {
    private List<Carta> cartasJugador1;
    private List<Carta> cartasJugador2;
    private Carta cartaActivaJugador1;
    private Carta cartaActivaJugador2;
    private String jugadorActual;
    private EstadoJuego estadoJuego;
    private int turnoNumero;
    private List<String> historialCombate;
    private String ganador;
    private String nombreJugador1;
    private String nombreJugador2;

    public enum EstadoJuego {
        PREPARACION,
        EN_CURSO,
        TERMINADO
    }

    public JuegoModelo() {
        this.cartasJugador1 = new ArrayList<>();
        this.cartasJugador2 = new ArrayList<>();
        this.jugadorActual = "Jugador 1";
        this.estadoJuego = EstadoJuego.PREPARACION;
        this.turnoNumero = 1;
        this.historialCombate = new ArrayList<>();
        this.ganador = null;
        this.nombreJugador1 = "Jugador 1";
        this.nombreJugador2 = "Jugador 2";
    }

    /**
     * Establece los nombres de los jugadores
     */
    public void establecerNombresJugadores(String jugador1, String jugador2) {
        this.nombreJugador1 = jugador1;
        this.nombreJugador2 = jugador2;
    }

    /**
     * Agrega una carta al jugador especificado
     */
    public void agregarCarta(Pokemon pokemon, String jugador) {
        Carta carta = new Carta(pokemon, jugador);

        // Determinar si es jugador 1 o 2 basándose en el nombre
        boolean esJugador1 = jugador.equals(this.nombreJugador1);
        boolean esJugador2 = jugador.equals(this.nombreJugador2);

        if (esJugador1) {
            this.cartasJugador1.add(carta);
            // Si es la primera carta, la activamos automáticamente
            if (this.cartasJugador1.size() == 1) {
                this.cartaActivaJugador1 = carta;
                carta.activar();
            }
        } else if (esJugador2) {
            this.cartasJugador2.add(carta);
            // Si es la primera carta, la activamos automáticamente
            if (this.cartasJugador2.size() == 1) {
                this.cartaActivaJugador2 = carta;
                carta.activar();
            }
        }
    }

    /**
     * Inicia el juego
     */
    public boolean iniciarJuego() {
        if (this.cartasJugador1.isEmpty() || this.cartasJugador2.isEmpty()) {
            System.out.println("DEBUG: No se puede iniciar el juego.");
            System.out.println("DEBUG: Cartas Jugador 1: " + this.cartasJugador1.size());
            System.out.println("DEBUG: Cartas Jugador 2: " + this.cartasJugador2.size());
            return false;
        }

        this.estadoJuego = EstadoJuego.EN_CURSO;
        this.historialCombate.add("=== INICIO DEL COMBATE ===");
        this.historialCombate.add(String.format("Turno %d - %s comienza", this.turnoNumero, this.jugadorActual));

        System.out.println("DEBUG: Juego iniciado correctamente.");
        System.out.println("DEBUG: Carta activa J1: " + (this.cartaActivaJugador1 != null ? this.cartaActivaJugador1.getPokemon().getNombre() : "null"));
        System.out.println("DEBUG: Carta activa J2: " + (this.cartaActivaJugador2 != null ? this.cartaActivaJugador2.getPokemon().getNombre() : "null"));

        return true;
    }

    /**
     * Ejecuta un ataque entre las cartas activas
     */
    public Carta.ResultadoAtaque ejecutarAtaque() {
        if (this.estadoJuego != EstadoJuego.EN_CURSO) {
            return new Carta.ResultadoAtaque(false, 0, "El juego no está en curso");
        }

        Carta cartaAtacante, cartaDefensora;

        if (this.jugadorActual.equals("Jugador 1")) {
            cartaAtacante = this.cartaActivaJugador1;
            cartaDefensora = this.cartaActivaJugador2;
        } else {
            cartaAtacante = this.cartaActivaJugador2;
            cartaDefensora = this.cartaActivaJugador1;
        }

        if (cartaAtacante == null || cartaDefensora == null) {
            return new Carta.ResultadoAtaque(false, 0, "No hay cartas activas para combatir");
        }

        // Ejecutar el ataque
        Carta.ResultadoAtaque resultado = cartaAtacante.atacar(cartaDefensora);

        // Agregar al historial
        this.historialCombate.add(String.format("Turno %d - %s", this.turnoNumero, resultado.getMensaje()));

        // Verificar si el Pokemon defensor fue derrotado
        if (cartaDefensora.getPokemon().estaDerrotado()) {
            this.historialCombate.add(String.format("%s (%s) ha sido derrotado!",
                    cartaDefensora.getPokemon().getNombre(),
                    cartaDefensora.getJugador()));

            // Verificar si el juego terminó
            if (this.verificarFinJuego()) {
                this.estadoJuego = EstadoJuego.TERMINADO;
                this.ganador = this.jugadorActual;
                this.historialCombate.add(String.format("=== %s GANA EL COMBATE ===", this.ganador));
            }
        }

        // Cambiar turno
        this.cambiarTurno();

        return resultado;
    }

    /**
     * Cambia el turno al siguiente jugador
     */
    private void cambiarTurno() {
        if (this.jugadorActual.equals("Jugador 1")) {
            this.jugadorActual = "Jugador 2";
        } else {
            this.jugadorActual = "Jugador 1";
            this.turnoNumero++;
        }

        // Incrementar turnos en las cartas activas
        if (this.cartaActivaJugador1 != null) {
            this.cartaActivaJugador1.incrementarTurno();
        }
        if (this.cartaActivaJugador2 != null) {
            this.cartaActivaJugador2.incrementarTurno();
        }
    }

    /**
     * Verifica si el juego ha terminado
     */
    private boolean verificarFinJuego() {
        // Por ahora, el juego termina cuando uno de los Pokemon activos es derrotado
        // En una versión más completa, podríamos tener múltiples cartas por jugador
        return (this.cartaActivaJugador1 != null && this.cartaActivaJugador1.getPokemon().estaDerrotado()) ||
                (this.cartaActivaJugador2 != null && this.cartaActivaJugador2.getPokemon().estaDerrotado());
    }

    /**
     * Reinicia el juego
     */
    public void reiniciarJuego() {
        this.cartasJugador1.clear();
        this.cartasJugador2.clear();
        this.cartaActivaJugador1 = null;
        this.cartaActivaJugador2 = null;
        this.jugadorActual = "Jugador 1";
        this.estadoJuego = EstadoJuego.PREPARACION;
        this.turnoNumero = 1;
        this.historialCombate.clear();
        this.ganador = null;
    }

    /**
     * Obtiene el estado actual del juego
     */
    public String getEstadoJuego() {
        StringBuilder estado = new StringBuilder();
        estado.append("=== ESTADO DEL JUEGO ===\n");
        estado.append("Estado: ").append(this.estadoJuego).append("\n");
        estado.append("Turno: ").append(this.turnoNumero).append("\n");
        estado.append("Jugador actual: ").append(this.jugadorActual).append("\n");

        if (this.cartaActivaJugador1 != null) {
            estado.append("\n--- JUGADOR 1 ---\n");
            estado.append(this.cartaActivaJugador1.toString()).append("\n");
        }

        if (this.cartaActivaJugador2 != null) {
            estado.append("\n--- JUGADOR 2 ---\n");
            estado.append(this.cartaActivaJugador2.toString()).append("\n");
        }

        if (this.ganador != null) {
            estado.append("\n*** GANADOR: ").append(this.ganador).append(" ***\n");
        }

        return estado.toString();
    }

    /**
     * Obtiene el historial de combate
     */
    public List<String> getHistorialCombate() {
        return new ArrayList<>(this.historialCombate);
    }

    /**
     * Obtiene el historial como string
     */
    public String getHistorialComoString() {
        StringBuilder historial = new StringBuilder();
        historial.append("=== HISTORIAL DE COMBATE ===\n");
        for (String evento : this.historialCombate) {
            historial.append(evento).append("\n");
        }
        return historial.toString();
    }

    // Getters
    public List<Carta> getCartasJugador1() { return new ArrayList<>(cartasJugador1); }
    public List<Carta> getCartasJugador2() { return new ArrayList<>(cartasJugador2); }
    public Carta getCartaActivaJugador1() { return cartaActivaJugador1; }
    public Carta getCartaActivaJugador2() { return cartaActivaJugador2; }
    public String getJugadorActual() { return jugadorActual; }
    public EstadoJuego getEstado() { return estadoJuego; }
    public int getTurnoNumero() { return turnoNumero; }
    public String getGanador() { return ganador; }
    public String getNombreJugador1() { return nombreJugador1; }
    public String getNombreJugador2() { return nombreJugador2; }

    public boolean juegoTerminado() {
        return this.estadoJuego == EstadoJuego.TERMINADO;
    }

    public boolean juegoEnCurso() {
        return this.estadoJuego == EstadoJuego.EN_CURSO;
    }

    public boolean juegoPreparacion() {
        return this.estadoJuego == EstadoJuego.PREPARACION;
    }
}