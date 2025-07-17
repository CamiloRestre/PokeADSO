package modelo;

/**
 * Clase que representa una carta de Pokemon en el juego
 */
public class Carta {
    private Pokemon pokemon;
    private String jugador;
    private boolean activa;
    private int turnosEnJuego;

    public Carta(Pokemon pokemon, String jugador) {
        this.pokemon = pokemon;
        this.jugador = jugador;
        this.activa = false;
        this.turnosEnJuego = 0;
    }

    /**
     * Ataca a otra carta
     */
    public ResultadoAtaque atacar(Carta cartaEnemiga) {
        if (this.pokemon.estaDerrotado()) {
            return new ResultadoAtaque(false, 0, "El Pokemon está derrotado y no puede atacar");
        }

        int daño = this.pokemon.calcularDaño(cartaEnemiga.getPokemon());
        cartaEnemiga.getPokemon().recibirDaño(daño);

        String mensaje = String.format("%s ataca a %s causando %d puntos de daño!",
                this.pokemon.getNombre(),
                cartaEnemiga.getPokemon().getNombre(),
                daño);

        // Verificar si el enemigo fue derrotado
        if (cartaEnemiga.getPokemon().estaDerrotado()) {
            mensaje += String.format(" %s ha sido derrotado!", cartaEnemiga.getPokemon().getNombre());
        }

        return new ResultadoAtaque(true, daño, mensaje);
    }

    /**
     * Verifica si la carta puede atacar
     */
    public boolean puedeAtacar() {
        return !this.pokemon.estaDerrotado() && this.activa;
    }

    /**
     * Activa la carta para combate
     */
    public void activar() {
        this.activa = true;
        this.pokemon.restaurarVida();
    }

    /**
     * Desactiva la carta
     */
    public void desactivar() {
        this.activa = false;
    }

    /**
     * Incrementa los turnos en juego
     */
    public void incrementarTurno() {
        if (this.activa) {
            this.turnosEnJuego++;
        }
    }

    /**
     * Obtiene el porcentaje de vida restante
     */
    public double getPorcentajeVida() {
        if (this.pokemon.getVidaMaxima() == 0) return 0;
        return (double) this.pokemon.getVida() / this.pokemon.getVidaMaxima() * 100;
    }

    /**
     * Obtiene información detallada de la carta
     */
    public String getInformacionCompleta() {
        return String.format(
                "=== CARTA DE %s ===\n" +
                        "Pokemon: %s (ID: %d)\n" +
                        "Tipo: %s\n" +
                        "Vida: %d/%d (%.1f%%)\n" +
                        "Ataque: %d\n" +
                        "Defensa: %d\n" +
                        "Velocidad: %d\n" +
                        "Habilidad: %s\n" +
                        "Jugador: %s\n" +
                        "Estado: %s\n" +
                        "Turnos en juego: %d",
                this.pokemon.getNombre().toUpperCase(),
                this.pokemon.getNombre(),
                this.pokemon.getId(),
                this.pokemon.getTipo(),
                this.pokemon.getVida(),
                this.pokemon.getVidaMaxima(),
                this.getPorcentajeVida(),
                this.pokemon.getAtaque(),
                this.pokemon.getDefensa(),
                this.pokemon.getVelocidad(),
                this.pokemon.getHabilidadPrincipal(),
                this.jugador,
                this.activa ? "Activa" : "Inactiva",
                this.turnosEnJuego
        );
    }

    // Getters y Setters
    public Pokemon getPokemon() { return pokemon; }
    public void setPokemon(Pokemon pokemon) { this.pokemon = pokemon; }

    public String getJugador() { return jugador; }
    public void setJugador(String jugador) { this.jugador = jugador; }

    public boolean isActiva() { return activa; }
    public void setActiva(boolean activa) { this.activa = activa; }

    public int getTurnosEnJuego() { return turnosEnJuego; }
    public void setTurnosEnJuego(int turnosEnJuego) { this.turnosEnJuego = turnosEnJuego; }

    @Override
    public String toString() {
        return String.format("Carta[%s - %s, Vida: %d/%d, Jugador: %s]",
                pokemon.getNombre(), pokemon.getTipo(),
                pokemon.getVida(), pokemon.getVidaMaxima(), jugador);
    }

    /**
     * Clase interna para representar el resultado de un ataque
     */
    public static class ResultadoAtaque {
        private boolean exitoso;
        private int daño;
        private String mensaje;

        public ResultadoAtaque(boolean exitoso, int daño, String mensaje) {
            this.exitoso = exitoso;
            this.daño = daño;
            this.mensaje = mensaje;
        }

        public boolean isExitoso() { return exitoso; }
        public int getDaño() { return daño; }
        public String getMensaje() { return mensaje; }
    }
}