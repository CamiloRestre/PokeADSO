package servicio;

import modelo.Pokemon;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Servicio para interactuar con la API de Pokemon
 */
public class PokemonApiServicio {
    private static final String API_BASE_URL = "https://pokeapi.co/api/v2/pokemon/";
    private static final int TIMEOUT = 10000; // 10 segundos
    private Random random;

    public PokemonApiServicio() {
        this.random = new Random();
    }

    /**
     * Obtiene un Pokemon por su ID
     */
    public Pokemon obtenerPokemonPorId(int id) {
        try {
            String url = API_BASE_URL + id;
            String respuestaJson = realizarPeticionHttp(url);

            if (respuestaJson != null) {
                JSONObject jsonPokemon = new JSONObject(respuestaJson);
                return Pokemon.desde(jsonPokemon);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener Pokemon con ID " + id + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un Pokemon por su nombre
     */
    public Pokemon obtenerPokemonPorNombre(String nombre) {
        try {
            String url = API_BASE_URL + nombre.toLowerCase();
            String respuestaJson = realizarPeticionHttp(url);

            if (respuestaJson != null) {
                JSONObject jsonPokemon = new JSONObject(respuestaJson);
                return Pokemon.desde(jsonPokemon);
            }
        } catch (Exception e) {
            System.err.println("Error al obtener Pokemon con nombre " + nombre + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * Obtiene un Pokemon aleatorio
     */
    public Pokemon obtenerPokemonAleatorio() {
        // La API de Pokemon tiene Pokemon del 1 al 1010 (aproximadamente)
        int idAleatorio = random.nextInt(150) + 1; // Limitamos a los primeros 150 por velocidad
        return obtenerPokemonPorId(idAleatorio);
    }

    /**
     * Obtiene una lista de Pokemon aleatorios
     */
    public List<Pokemon> obtenerPokemonAleatorios(int cantidad) {
        List<Pokemon> pokemonList = new ArrayList<>();
        List<Integer> idsUsados = new ArrayList<>();

        for (int i = 0; i < cantidad; i++) {
            int intentos = 0;
            Pokemon pokemon = null;

            while (pokemon == null && intentos < 5) {
                int id = random.nextInt(150) + 1;

                // Evitar duplicados
                if (!idsUsados.contains(id)) {
                    pokemon = obtenerPokemonPorId(id);
                    if (pokemon != null) {
                        pokemonList.add(pokemon);
                        idsUsados.add(id);
                    }
                }
                intentos++;
            }
        }

        return pokemonList;
    }

    /**
     * Busca Pokemon por tipo
     */
    public List<Pokemon> buscarPokemonPorTipo(String tipo, int cantidad) {
        List<Pokemon> pokemonDelTipo = new ArrayList<>();
        int intentos = 0;
        int maxIntentos = cantidad * 10; // Límite de intentos para evitar bucles infinitos

        while (pokemonDelTipo.size() < cantidad && intentos < maxIntentos) {
            Pokemon pokemon = obtenerPokemonAleatorio();

            if (pokemon != null && pokemon.getTipo().equalsIgnoreCase(tipo)) {
                // Verificar que no esté duplicado
                boolean yaExiste = pokemonDelTipo.stream()
                        .anyMatch(p -> p.getId() == pokemon.getId());

                if (!yaExiste) {
                    pokemonDelTipo.add(pokemon);
                }
            }
            intentos++;
        }

        return pokemonDelTipo;
    }

    /**
     * Verifica si un Pokemon existe por ID
     */
    public boolean existePokemon(int id) {
        return obtenerPokemonPorId(id) != null;
    }

    /**
     * Verifica si un Pokemon existe por nombre
     */
    public boolean existePokemon(String nombre) {
        return obtenerPokemonPorNombre(nombre) != null;
    }

    /**
     * Obtiene información básica de un Pokemon (solo nombre e ID)
     */
    public String obtenerInfoBasicaPokemon(int id) {
        Pokemon pokemon = obtenerPokemonPorId(id);
        if (pokemon != null) {
            return String.format("ID: %d, Nombre: %s, Tipo: %s",
                    pokemon.getId(), pokemon.getNombre(), pokemon.getTipo());
        }
        return "Pokemon no encontrado";
    }

    /**
     * Obtiene Pokemon de combate balanceados
     */
    public List<Pokemon> obtenerPokemonParaCombate(int cantidad) {
        List<Pokemon> pokemonCombate = new ArrayList<>();

        // Intentar obtener Pokemon con estadísticas balanceadas
        for (int i = 0; i < cantidad; i++) {
            Pokemon pokemon = null;
            int intentos = 0;

            while (pokemon == null && intentos < 10) {
                Pokemon candidato = obtenerPokemonAleatorio();

                // Verificar que tenga estadísticas mínimas para combate
                if (candidato != null &&
                        candidato.getVida() >= 30 &&
                        candidato.getAtaque() >= 20 &&
                        candidato.getDefensa() >= 15) {

                    // Verificar que no esté duplicado
                    boolean yaExiste = pokemonCombate.stream()
                            .anyMatch(p -> p.getId() == candidato.getId());

                    if (!yaExiste) {
                        pokemon = candidato;
                    }
                }
                intentos++;
            }

            if (pokemon != null) {
                pokemonCombate.add(pokemon);
            }
        }

        return pokemonCombate;
    }

    /**
     * Realiza una petición HTTP a la API
     */
    private String realizarPeticionHttp(String urlString) throws IOException {
        HttpURLConnection conexion = null;
        BufferedReader lector = null;

        try {
            URL url = new URL(urlString);
            conexion = (HttpURLConnection) url.openConnection();

            // Configurar la conexión
            conexion.setRequestMethod("GET");
            conexion.setConnectTimeout(TIMEOUT);
            conexion.setReadTimeout(TIMEOUT);
            conexion.setRequestProperty("User-Agent", "Pokemon-Game-Client/1.0");

            // Verificar código de respuesta
            int codigoRespuesta = conexion.getResponseCode();
            if (codigoRespuesta != HttpURLConnection.HTTP_OK) {
                System.err.println("Error en la petición HTTP: " + codigoRespuesta);
                return null;
            }

            // Leer la respuesta
            lector = new BufferedReader(new InputStreamReader(conexion.getInputStream()));
            StringBuilder respuesta = new StringBuilder();
            String linea;

            while ((linea = lector.readLine()) != null) {
                respuesta.append(linea);
            }

            return respuesta.toString();

        } finally {
            // Cerrar recursos
            if (lector != null) {
                try {
                    lector.close();
                } catch (IOException e) {
                    System.err.println("Error al cerrar el lector: " + e.getMessage());
                }
            }
            if (conexion != null) {
                conexion.disconnect();
            }
        }
    }

    /**
     * Obtiene estadísticas de un Pokemon como String formateado
     */
    public String obtenerEstadisticasFormateadas(int id) {
        Pokemon pokemon = obtenerPokemonPorId(id);
        if (pokemon != null) {
            return String.format(
                    "=== %s ===\n" +
                            "ID: %d\n" +
                            "Tipo: %s\n" +
                            "Vida: %d\n" +
                            "Ataque: %d\n" +
                            "Defensa: %d\n" +
                            "Velocidad: %d\n" +
                            "Habilidad: %s\n",
                    pokemon.getNombre().toUpperCase(),
                    pokemon.getId(),
                    pokemon.getTipo(),
                    pokemon.getVida(),
                    pokemon.getAtaque(),
                    pokemon.getDefensa(),
                    pokemon.getVelocidad(),
                    pokemon.getHabilidadPrincipal()
            );
        }
        return "Pokemon no encontrado";
    }

    /**
     * Método de prueba para verificar la conexión con la API
     */
    public boolean probarConexionApi() {
        try {
            Pokemon pikachu = obtenerPokemonPorId(25); // Pikachu
            return pikachu != null;
        } catch (Exception e) {
            System.err.println("Error en la prueba de conexión: " + e.getMessage());
            return false;
        }
    }
}