package modelo;

import org.json.JSONObject;
import org.json.JSONArray;

/**
 * Clase que representa un Pokemon con sus estadísticas y habilidades
 */
public class Pokemon {
    private int id;
    private String nombre;
    private String tipo;
    private int vida;
    private int vidaMaxima;
    private int ataque;
    private int defensa;
    private int velocidad;
    private String imagenUrl;
    private String habilidadPrincipal;

    // Constructor vacío
    public Pokemon() {
    }

    // Constructor con parámetros básicos
    public Pokemon(int id, String nombre, String tipo, int vida, int ataque, int defensa, int velocidad) {
        this.id = id;
        this.nombre = nombre;
        this.tipo = tipo;
        this.vida = vida;
        this.vidaMaxima = vida;
        this.ataque = ataque;
        this.defensa = defensa;
        this.velocidad = velocidad;
    }

    /**
     * Crea un Pokemon desde un JSONObject de la API
     */
    public static Pokemon desde(JSONObject jsonPokemon) {
        Pokemon pokemon = new Pokemon();

        try {
            pokemon.id = jsonPokemon.getInt("id");
            pokemon.nombre = jsonPokemon.getString("name");

            // Obtener tipo principal
            JSONArray tipos = jsonPokemon.getJSONArray("types");
            if (tipos.length() > 0) {
                pokemon.tipo = tipos.getJSONObject(0).getJSONObject("type").getString("name");
            }

            // Obtener estadísticas
            JSONArray stats = jsonPokemon.getJSONArray("stats");
            for (int i = 0; i < stats.length(); i++) {
                JSONObject stat = stats.getJSONObject(i);
                String nombreStat = stat.getJSONObject("stat").getString("name");
                int valorBase = stat.getInt("base_stat");

                switch (nombreStat) {
                    case "hp":
                        pokemon.vida = valorBase;
                        pokemon.vidaMaxima = valorBase;
                        break;
                    case "attack":
                        pokemon.ataque = valorBase;
                        break;
                    case "defense":
                        pokemon.defensa = valorBase;
                        break;
                    case "speed":
                        pokemon.velocidad = valorBase;
                        break;
                }
            }

            // Obtener imagen
            JSONObject sprites = jsonPokemon.getJSONObject("sprites");
            if (sprites.has("front_default") && !sprites.isNull("front_default")) {
                pokemon.imagenUrl = sprites.getString("front_default");
            }

            // Obtener habilidad principal
            JSONArray habilidades = jsonPokemon.getJSONArray("abilities");
            if (habilidades.length() > 0) {
                pokemon.habilidadPrincipal = habilidades.getJSONObject(0).getJSONObject("ability").getString("name");
            }

        } catch (Exception e) {
            System.err.println("Error al crear Pokemon desde JSON: " + e.getMessage());
        }

        return pokemon;
    }

    /**
     * Calcula el daño que este Pokemon causa a otro
     */
    public int calcularDaño(Pokemon enemigo) {
        // Fórmula simplificada de daño Pokemon
        double multiplicador = 1.0;

        // Bonificación por tipo (simplificada)
        if (this.esEfectivoContra(enemigo.getTipo())) {
            multiplicador = 1.5;
        } else if (this.esDebilContra(enemigo.getTipo())) {
            multiplicador = 0.5;
        }

        // Cálculo base del daño
        double dañoBase = (double) this.ataque / enemigo.defensa * 50;
        int dañoFinal = (int) (dañoBase * multiplicador);

        // Variación aleatoria (85-100% del daño)
        double variacion = 0.85 + (Math.random() * 0.15);
        dañoFinal = (int) (dañoFinal * variacion);

        return Math.max(1, dañoFinal); // Mínimo 1 de daño
    }

    /**
     * Recibe daño y reduce la vida
     */
    public void recibirDaño(int daño) {
        this.vida = Math.max(0, this.vida - daño);
    }

    /**
     * Verifica si el Pokemon está derrotado
     */
    public boolean estaDerrotado() {
        return this.vida <= 0;
    }

    /**
     * Restaura la vida al máximo
     */
    public void restaurarVida() {
        this.vida = this.vidaMaxima;
    }

    /**
     * Verifica efectividad de tipos (simplificado)
     */
    private boolean esEfectivoContra(String tipoEnemigo) {
        switch (this.tipo.toLowerCase()) {
            case "fire":
                return tipoEnemigo.equals("grass") || tipoEnemigo.equals("ice") || tipoEnemigo.equals("bug");
            case "water":
                return tipoEnemigo.equals("fire") || tipoEnemigo.equals("ground") || tipoEnemigo.equals("rock");
            case "grass":
                return tipoEnemigo.equals("water") || tipoEnemigo.equals("ground") || tipoEnemigo.equals("rock");
            case "electric":
                return tipoEnemigo.equals("water") || tipoEnemigo.equals("flying");
            case "psychic":
                return tipoEnemigo.equals("fighting") || tipoEnemigo.equals("poison");
            case "fighting":
                return tipoEnemigo.equals("normal") || tipoEnemigo.equals("rock") || tipoEnemigo.equals("steel");
            default:
                return false;
        }
    }

    private boolean esDebilContra(String tipoEnemigo) {
        switch (this.tipo.toLowerCase()) {
            case "fire":
                return tipoEnemigo.equals("water") || tipoEnemigo.equals("ground") || tipoEnemigo.equals("rock");
            case "water":
                return tipoEnemigo.equals("grass") || tipoEnemigo.equals("electric");
            case "grass":
                return tipoEnemigo.equals("fire") || tipoEnemigo.equals("ice") || tipoEnemigo.equals("poison");
            case "electric":
                return tipoEnemigo.equals("ground");
            case "psychic":
                return tipoEnemigo.equals("bug") || tipoEnemigo.equals("ghost") || tipoEnemigo.equals("dark");
            case "fighting":
                return tipoEnemigo.equals("flying") || tipoEnemigo.equals("psychic");
            default:
                return false;
        }
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getVida() { return vida; }
    public void setVida(int vida) { this.vida = vida; }

    public int getVidaMaxima() { return vidaMaxima; }
    public void setVidaMaxima(int vidaMaxima) { this.vidaMaxima = vidaMaxima; }

    public int getAtaque() { return ataque; }
    public void setAtaque(int ataque) { this.ataque = ataque; }

    public int getDefensa() { return defensa; }
    public void setDefensa(int defensa) { this.defensa = defensa; }

    public int getVelocidad() { return velocidad; }
    public void setVelocidad(int velocidad) { this.velocidad = velocidad; }

    public String getImagenUrl() { return imagenUrl; }
    public void setImagenUrl(String imagenUrl) { this.imagenUrl = imagenUrl; }

    public String getHabilidadPrincipal() { return habilidadPrincipal; }
    public void setHabilidadPrincipal(String habilidadPrincipal) { this.habilidadPrincipal = habilidadPrincipal; }

    @Override
    public String toString() {
        return String.format("%s (ID: %d, Tipo: %s, Vida: %d/%d, Ataque: %d, Defensa: %d)",
                nombre, id, tipo, vida, vidaMaxima, ataque, defensa);
    }
}