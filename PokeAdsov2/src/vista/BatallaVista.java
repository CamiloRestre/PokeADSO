package vista;

import modelo.Pokemon;
import modelo.JuegoModelo;
import modelo.Carta;
import servicio.PokemonApiServicio;
import controlador.PokemonControlador;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class BatallaVista extends JFrame {
    private JPanel main;
    private JTextField nombre1;
    private JTextField vida1;
    private JTextField ataque1;
    private JLabel foto1;
    private JTextField nombre2;
    private JTextField vida2;
    private JTextField ataque2;
    private JLabel foto2;
    private JButton jugarButton;
    private JButton seleccionarButton;
    private JButton selecionarButton;
    private JTextField name1;
    private JTextField name2;
    private JLabel ganadas1;
    private JLabel ganadas2;
    private JButton SALIRButton;
    private JButton pokemonAleatorio1;
    private JButton pokemonAleatorio2;
    private JButton buscarNombre1;
    private JButton buscarNombre2;

    // Modelos y servicios
    private JuegoModelo juegoModelo;
    private PokemonApiServicio pokemonServicio;
    private PokemonControlador controlador;

    // Pokemon actuales
    private Pokemon pokemonJugador1;
    private Pokemon pokemonJugador2;

    // Contadores de victorias
    private int count1 = 0;
    private int count2 = 0;

    // Nombres de jugadores - Usar nombres fijos para compatibilidad con JuegoModelo
    private String jugador1Nombre = "Jugador 1";
    private String jugador2Nombre = "Jugador 2";

    // Nombres personalizados para mostrar en la interfaz
    private String jugador1NombrePersonalizado = "Jugador 1";
    private String jugador2NombrePersonalizado = "Jugador 2";

    public BatallaVista() {
        // Inicializar servicios y modelos
        this.juegoModelo = new JuegoModelo();
        this.pokemonServicio = new PokemonApiServicio();
        this.controlador = new PokemonControlador();

        // Verificar conexión con la API
        if (!pokemonServicio.probarConexionApi()) {
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar con la API de Pokemon.\nVerifica tu conexión a internet.",
                    "Error de conexión",
                    JOptionPane.ERROR_MESSAGE);
        }

        initializeComponents();
        configureFrame();
        obtenerPokemonIniciales();
        solicitarNombresJugadores();
        configurarEventos();
    }

    private void initializeComponents() {
        main = new JPanel();
        main.setLayout(new BorderLayout());

        // Panel principal de batalla
        JPanel battlePanel = new JPanel(new GridLayout(1, 2, 10, 10));
        battlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel Jugador 1
        JPanel player1Panel = createPlayerPanel(1);
        // Panel Jugador 2
        JPanel player2Panel = createPlayerPanel(2);

        battlePanel.add(player1Panel);
        battlePanel.add(player2Panel);

        // Panel de controles
        JPanel controlPanel = createControlPanel();

        main.add(battlePanel, BorderLayout.CENTER);
        main.add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel createPlayerPanel(int playerNumber) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Jugador " + playerNumber));

        // Información del jugador
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));

        if (playerNumber == 1) {
            name1 = new JTextField();
            name1.setEditable(false);
            name1.setHorizontalAlignment(JTextField.CENTER);
            name1.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(new JLabel("Nombre:"));
            infoPanel.add(name1);

            ganadas1 = new JLabel("0");
            ganadas1.setHorizontalAlignment(JLabel.CENTER);
            ganadas1.setFont(new Font("Arial", Font.BOLD, 16));
            JPanel winsPanel = new JPanel(new BorderLayout());
            winsPanel.add(new JLabel("Victorias:"), BorderLayout.WEST);
            winsPanel.add(ganadas1, BorderLayout.CENTER);
            infoPanel.add(winsPanel);
        } else {
            name2 = new JTextField();
            name2.setEditable(false);
            name2.setHorizontalAlignment(JTextField.CENTER);
            name2.setFont(new Font("Arial", Font.BOLD, 14));
            infoPanel.add(new JLabel("Nombre:"));
            infoPanel.add(name2);

            ganadas2 = new JLabel("0");
            ganadas2.setHorizontalAlignment(JLabel.CENTER);
            ganadas2.setFont(new Font("Arial", Font.BOLD, 16));
            JPanel winsPanel = new JPanel(new BorderLayout());
            winsPanel.add(new JLabel("Victorias:"), BorderLayout.WEST);
            winsPanel.add(ganadas2, BorderLayout.CENTER);
            infoPanel.add(winsPanel);
        }

        panel.add(infoPanel, BorderLayout.NORTH);

        // Panel Pokemon
        JPanel pokemonPanel = new JPanel(new BorderLayout());

        // Imagen
        JLabel foto = new JLabel();
        foto.setPreferredSize(new Dimension(250, 250));
        foto.setHorizontalAlignment(JLabel.CENTER);
        foto.setBorder(BorderFactory.createEtchedBorder());

        if (playerNumber == 1) {
            foto1 = foto;
        } else {
            foto2 = foto;
        }

        pokemonPanel.add(foto, BorderLayout.CENTER);

        // Estadísticas
        JPanel statsPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Estadísticas"));

        JTextField nombreField = new JTextField();
        nombreField.setEditable(false);
        JTextField vidaField = new JTextField();
        vidaField.setEditable(false);
        JTextField ataqueField = new JTextField();
        ataqueField.setEditable(false);

        if (playerNumber == 1) {
            nombre1 = nombreField;
            vida1 = vidaField;
            ataque1 = ataqueField;
        } else {
            nombre2 = nombreField;
            vida2 = vidaField;
            ataque2 = ataqueField;
        }

        statsPanel.add(new JLabel("Nombre:"));
        statsPanel.add(nombreField);
        statsPanel.add(new JLabel("Vida:"));
        statsPanel.add(vidaField);
        statsPanel.add(new JLabel("Ataque:"));
        statsPanel.add(ataqueField);

        pokemonPanel.add(statsPanel, BorderLayout.SOUTH);

        // Botones de selección
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));

        JButton aleatorioBtn = new JButton("Pokemon Aleatorio");
        JButton buscarBtn = new JButton("Buscar por Nombre");
        JButton seleccionarBtn = new JButton("Nuevo Pokemon");

        if (playerNumber == 1) {
            pokemonAleatorio1 = aleatorioBtn;
            buscarNombre1 = buscarBtn;
            seleccionarButton = seleccionarBtn;
        } else {
            pokemonAleatorio2 = aleatorioBtn;
            buscarNombre2 = buscarBtn;
            selecionarButton = seleccionarBtn;
        }

        buttonPanel.add(aleatorioBtn);
        buttonPanel.add(buscarBtn);
        buttonPanel.add(seleccionarBtn);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(pokemonPanel, BorderLayout.CENTER);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        panel.add(bottomPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createControlPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        jugarButton = new JButton("⚔️ BATALLA");
        jugarButton.setFont(new Font("Arial", Font.BOLD, 16));
        jugarButton.setBackground(Color.RED);
        jugarButton.setForeground(Color.WHITE);
        jugarButton.setPreferredSize(new Dimension(150, 40));

        SALIRButton = new JButton("🚪 SALIR");
        SALIRButton.setFont(new Font("Arial", Font.BOLD, 16));
        SALIRButton.setBackground(Color.GRAY);
        SALIRButton.setForeground(Color.WHITE);
        SALIRButton.setPreferredSize(new Dimension(150, 40));

        panel.add(jugarButton);
        panel.add(SALIRButton);

        return panel;
    }

    private void configureFrame() {
        setTitle("Pokemon ADSO - Batalla");
        setContentPane(main);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void obtenerPokemonIniciales() {
        // Obtener Pokemon inicial para jugador 1
        pokemonJugador1 = pokemonServicio.obtenerPokemonAleatorio();
        if (pokemonJugador1 != null) {
            actualizarInterfazPokemon(pokemonJugador1, 1);
        }

        // Obtener Pokemon inicial para jugador 2
        pokemonJugador2 = pokemonServicio.obtenerPokemonAleatorio();
        if (pokemonJugador2 != null) {
            actualizarInterfazPokemon(pokemonJugador2, 2);
        }
    }

    private void solicitarNombresJugadores() {
        jugador1NombrePersonalizado = JOptionPane.showInputDialog(this, "Ingrese nombre del Jugador 1:");
        if (jugador1NombrePersonalizado == null || jugador1NombrePersonalizado.trim().isEmpty()) {
            jugador1NombrePersonalizado = "Jugador 1";
        }

        jugador2NombrePersonalizado = JOptionPane.showInputDialog(this, "Ingrese nombre del Jugador 2:");
        if (jugador2NombrePersonalizado == null || jugador2NombrePersonalizado.trim().isEmpty()) {
            jugador2NombrePersonalizado = "Jugador 2";
        }

        name1.setText(jugador1NombrePersonalizado);
        name2.setText(jugador2NombrePersonalizado);
    }

    private void configurarEventos() {
        // Evento batalla
        jugarButton.addActionListener(e -> iniciarBatalla());

        // Eventos selección Pokemon Jugador 1
        seleccionarButton.addActionListener(e -> obtenerNuevoPokemon(1));
        pokemonAleatorio1.addActionListener(e -> obtenerPokemonAleatorio(1));
        buscarNombre1.addActionListener(e -> buscarPokemonPorNombre(1));

        // Eventos selección Pokemon Jugador 2
        selecionarButton.addActionListener(e -> obtenerNuevoPokemon(2));
        pokemonAleatorio2.addActionListener(e -> obtenerPokemonAleatorio(2));
        buscarNombre2.addActionListener(e -> buscarPokemonPorNombre(2));

        // Evento salir
        SALIRButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Estás seguro de que quieres salir?",
                    "Confirmar salida",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        });
    }

    private void obtenerNuevoPokemon(int jugador) {
        Pokemon nuevoPokemon = pokemonServicio.obtenerPokemonAleatorio();
        if (nuevoPokemon != null) {
            if (jugador == 1) {
                pokemonJugador1 = nuevoPokemon;
            } else {
                pokemonJugador2 = nuevoPokemon;
            }
            actualizarInterfazPokemon(nuevoPokemon, jugador);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Error al obtener Pokemon. Verifica tu conexión.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void obtenerPokemonAleatorio(int jugador) {
        SwingUtilities.invokeLater(() -> {
            JDialog loadingDialog = new JDialog(this, "Obteniendo Pokemon...", true);
            loadingDialog.setSize(200, 100);
            loadingDialog.setLocationRelativeTo(this);
            loadingDialog.add(new JLabel("Buscando Pokemon aleatorio...", JLabel.CENTER));

            SwingWorker<Pokemon, Void> worker = new SwingWorker<Pokemon, Void>() {
                @Override
                protected Pokemon doInBackground() throws Exception {
                    return pokemonServicio.obtenerPokemonAleatorio();
                }

                @Override
                protected void done() {
                    loadingDialog.dispose();
                    try {
                        Pokemon pokemon = get();
                        if (pokemon != null) {
                            if (jugador == 1) {
                                pokemonJugador1 = pokemon;
                            } else {
                                pokemonJugador2 = pokemon;
                            }
                            actualizarInterfazPokemon(pokemon, jugador);
                        } else {
                            JOptionPane.showMessageDialog(BatallaVista.this,
                                    "No se pudo obtener Pokemon aleatorio.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(BatallaVista.this,
                                "Error: " + e.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };

            worker.execute();
            loadingDialog.setVisible(true);
        });
    }

    private void buscarPokemonPorNombre(int jugador) {
        String nombre = JOptionPane.showInputDialog(this,
                "Ingresa el nombre del Pokemon:",
                "Buscar Pokemon",
                JOptionPane.QUESTION_MESSAGE);

        if (nombre != null && !nombre.trim().isEmpty()) {
            SwingUtilities.invokeLater(() -> {
                JDialog loadingDialog = new JDialog(this, "Buscando Pokemon...", true);
                loadingDialog.setSize(200, 100);
                loadingDialog.setLocationRelativeTo(this);
                loadingDialog.add(new JLabel("Buscando " + nombre + "...", JLabel.CENTER));

                SwingWorker<Pokemon, Void> worker = new SwingWorker<Pokemon, Void>() {
                    @Override
                    protected Pokemon doInBackground() throws Exception {
                        return pokemonServicio.obtenerPokemonPorNombre(nombre);
                    }

                    @Override
                    protected void done() {
                        loadingDialog.dispose();
                        try {
                            Pokemon pokemon = get();
                            if (pokemon != null) {
                                if (jugador == 1) {
                                    pokemonJugador1 = pokemon;
                                } else {
                                    pokemonJugador2 = pokemon;
                                }
                                actualizarInterfazPokemon(pokemon, jugador);
                            } else {
                                JOptionPane.showMessageDialog(BatallaVista.this,
                                        "Pokemon '" + nombre + "' no encontrado.\nVerifica el nombre e intenta de nuevo.",
                                        "Pokemon no encontrado",
                                        JOptionPane.WARNING_MESSAGE);
                            }
                        } catch (Exception e) {
                            JOptionPane.showMessageDialog(BatallaVista.this,
                                    "Error al buscar Pokemon: " + e.getMessage(),
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                };

                worker.execute();
                loadingDialog.setVisible(true);
            });
        }
    }

    private void actualizarInterfazPokemon(Pokemon pokemon, int jugador) {
        SwingUtilities.invokeLater(() -> {
            if (jugador == 1) {
                nombre1.setText(pokemon.getNombre());
                vida1.setText(String.valueOf(pokemon.getVida()));
                ataque1.setText(String.valueOf(pokemon.getAtaque()));

                if (pokemon.getImagenUrl() != null && !pokemon.getImagenUrl().isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getImagenUrl()));
                        Image imagenEscalada = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                        foto1.setIcon(new ImageIcon(imagenEscalada));
                        foto1.setText("");
                    } catch (Exception e) {
                        foto1.setText("Sin imagen");
                        foto1.setIcon(null);
                    }
                } else {
                    foto1.setText("Sin imagen");
                    foto1.setIcon(null);
                }
            } else {
                nombre2.setText(pokemon.getNombre());
                vida2.setText(String.valueOf(pokemon.getVida()));
                ataque2.setText(String.valueOf(pokemon.getAtaque()));

                if (pokemon.getImagenUrl() != null && !pokemon.getImagenUrl().isEmpty()) {
                    try {
                        ImageIcon icon = new ImageIcon(new java.net.URL(pokemon.getImagenUrl()));
                        Image imagenEscalada = icon.getImage().getScaledInstance(250, 250, Image.SCALE_SMOOTH);
                        foto2.setIcon(new ImageIcon(imagenEscalada));
                        foto2.setText("");
                    } catch (Exception e) {
                        foto2.setText("Sin imagen");
                        foto2.setIcon(null);
                    }
                } else {
                    foto2.setText("Sin imagen");
                    foto2.setIcon(null);
                }
            }
        });
    }

    private void iniciarBatalla() {
        // Verificar que ambos Pokemon estén disponibles
        if (pokemonJugador1 == null || pokemonJugador2 == null) {
            JOptionPane.showMessageDialog(this,
                    "Error: No se encontraron Pokemon para ambos jugadores.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Reiniciar el juego
        juegoModelo.reiniciarJuego();

        // Restaurar vida completa de los Pokemon
        pokemonJugador1.restaurarVida();
        pokemonJugador2.restaurarVida();

        // Agregar Pokemon al modelo de juego usando nombres fijos
        juegoModelo.agregarCarta(pokemonJugador1, jugador1Nombre);
        juegoModelo.agregarCarta(pokemonJugador2, jugador2Nombre);

        System.out.println("Jugador 1: " + jugador1NombrePersonalizado + ", Pokémon: " + pokemonJugador1);
        System.out.println("Jugador 2: " + jugador2NombrePersonalizado + ", Pokémon: " + pokemonJugador2);

        // Intentar iniciar el juego
        boolean iniciado = juegoModelo.iniciarJuego();
        System.out.println("¿Juego iniciado?: " + iniciado);

        // Verificar si se inició correctamente
        if (!iniciado) {
            JOptionPane.showMessageDialog(this,
                    "Error al iniciar la batalla. Verifica que ambos jugadores tengan Pokemon.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Ejecutar batalla
        ejecutarBatalla();
    }

    private void ejecutarBatalla() {
        // Determinar quién ataca primero (basado en velocidad)
        boolean jugador1Primero = pokemonJugador1.getVelocidad() >= pokemonJugador2.getVelocidad();

        Random random = new Random();

        // Si hay empate en velocidad, decidir aleatoriamente
        if (pokemonJugador1.getVelocidad() == pokemonJugador2.getVelocidad()) {
            jugador1Primero = random.nextBoolean();
        }

        String atacante, defensor;
        Pokemon pokemonAtacante, pokemonDefensor;

        if (jugador1Primero) {
            atacante = jugador1NombrePersonalizado;
            defensor = jugador2NombrePersonalizado;
            pokemonAtacante = pokemonJugador1;
            pokemonDefensor = pokemonJugador2;
        } else {
            atacante = jugador2NombrePersonalizado;
            defensor = jugador1NombrePersonalizado;
            pokemonAtacante = pokemonJugador2;
            pokemonDefensor = pokemonJugador1;
        }

        // Calcular daño
        int daño = pokemonAtacante.calcularDaño(pokemonDefensor);
        int vidaAntes = pokemonDefensor.getVida();

        // Aplicar daño
        pokemonDefensor.recibirDaño(daño);

        // Mostrar resultado del ataque
        JOptionPane.showMessageDialog(this,
                String.format("🗡️ Turno de %s\n" +
                                "🎯 %s ataca con %d de daño\n" +
                                "❤️ Vida de %s: %d → %d",
                        atacante,
                        pokemonAtacante.getNombre(),
                        daño,
                        defensor,
                        vidaAntes,
                        pokemonDefensor.getVida()),
                "Resultado del Ataque",
                JOptionPane.INFORMATION_MESSAGE);

        // Actualizar interfaz
        actualizarVidasEnInterfaz();

        // Verificar si hay ganador
        if (pokemonDefensor.estaDerrotado()) {
            // El atacante gana
            if (jugador1Primero) {
                count1++;
                ganadas1.setText(String.valueOf(count1));
                JOptionPane.showMessageDialog(this,
                        "🎉 ¡" + jugador1NombrePersonalizado + " gana con " + pokemonJugador1.getNombre() + "!",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                count2++;
                ganadas2.setText(String.valueOf(count2));
                JOptionPane.showMessageDialog(this,
                        "🎉 ¡" + jugador2NombrePersonalizado + " gana con " + pokemonJugador2.getNombre() + "!",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Preguntar si quiere otra ronda
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Quieres jugar otra ronda?",
                    "Nueva Ronda",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                obtenerPokemonIniciales();
            }
        } else {
            // Continuar con el siguiente turno (cambiar atacante)
            boolean finalJugador1Primero = jugador1Primero;
            SwingUtilities.invokeLater(() -> {
                Timer timer = new Timer(2000, e -> continuarBatalla(!finalJugador1Primero));
                timer.setRepeats(false);
                timer.start();
            });
        }
    }

    private void continuarBatalla(boolean jugador1Turno) {
        String atacante, defensor;
        Pokemon pokemonAtacante, pokemonDefensor;

        if (jugador1Turno) {
            atacante = jugador1NombrePersonalizado;
            defensor = jugador2NombrePersonalizado;
            pokemonAtacante = pokemonJugador1;
            pokemonDefensor = pokemonJugador2;
        } else {
            atacante = jugador2NombrePersonalizado;
            defensor = jugador1NombrePersonalizado;
            pokemonAtacante = pokemonJugador2;
            pokemonDefensor = pokemonJugador1;
        }

        // Calcular daño
        int daño = pokemonAtacante.calcularDaño(pokemonDefensor);
        int vidaAntes = pokemonDefensor.getVida();

        // Aplicar daño
        pokemonDefensor.recibirDaño(daño);

        // Mostrar resultado del ataque
        JOptionPane.showMessageDialog(this,
                String.format("🗡️ Turno de %s\n" +
                                "🎯 %s ataca con %d de daño\n" +
                                "❤️ Vida de %s: %d → %d",
                        atacante,
                        pokemonAtacante.getNombre(),
                        daño,
                        defensor,
                        vidaAntes,
                        pokemonDefensor.getVida()),
                "Resultado del Ataque",
                JOptionPane.INFORMATION_MESSAGE);

        // Actualizar interfaz
        actualizarVidasEnInterfaz();

        // Verificar si hay ganador
        if (pokemonDefensor.estaDerrotado()) {
            // El atacante gana
            if (jugador1Turno) {
                count1++;
                ganadas1.setText(String.valueOf(count1));
                JOptionPane.showMessageDialog(this,
                        "🎉 ¡" + jugador1NombrePersonalizado + " gana con " + pokemonJugador1.getNombre() + "!",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                count2++;
                ganadas2.setText(String.valueOf(count2));
                JOptionPane.showMessageDialog(this,
                        "🎉 ¡" + jugador2NombrePersonalizado + " gana con " + pokemonJugador2.getNombre() + "!",
                        "¡Victoria!",
                        JOptionPane.INFORMATION_MESSAGE);
            }

            // Preguntar si quiere otra ronda
            int confirm = JOptionPane.showConfirmDialog(this,
                    "¿Quieres jugar otra ronda?",
                    "Nueva Ronda",
                    JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                obtenerPokemonIniciales();
            }
        } else {
            // Continuar con el siguiente turno
            SwingUtilities.invokeLater(() -> {
                Timer timer = new Timer(2000, e -> continuarBatalla(!jugador1Turno));
                timer.setRepeats(false);
                timer.start();
            });
        }
    }

    private void actualizarVidasEnInterfaz() {
        SwingUtilities.invokeLater(() -> {
            if (pokemonJugador1 != null) {
                vida1.setText(String.valueOf(pokemonJugador1.getVida()));
            }
            if (pokemonJugador2 != null) {
                vida2.setText(String.valueOf(pokemonJugador2.getVida()));
            }
        });
    }

    // Método para restablecer el juego
    private void restablecerJuego() {
        count1 = 0;
        count2 = 0;
        ganadas1.setText("0");
        ganadas2.setText("0");
        obtenerPokemonIniciales();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            new BatallaVista().setVisible(true);
        });
    }
}