package trivia.game;

import trivia.model.Categoria;
import trivia.model.Pregunta;
import trivia.network.Server;
import trivia.player.EnEspera;
import trivia.player.Jugador;
import trivia.cards.CartaPoder;
import trivia.piles.MontonPreguntas;
import trivia.player.Preguntador;
import trivia.player.Respondedor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Partida {

    private int vueltaActual = 1;
    private Jugador jugadorActual;

    private List<Turno> historialTurnos = new ArrayList<>();
    private final List<MontonPreguntas> montonesPreguntas = new ArrayList<>(6);

    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Integer> puntuacionJugadores = new ArrayList<>();
    private final List<Integer> preguntasGanadasJugadores = new ArrayList<>();
    private final List<Integer> tamanosInventario = new ArrayList<>();
    private final List<List<CartaPoder>> inventarios = new ArrayList<>();
    private final List<CartaPoder> cartasActivas = new ArrayList<>();
    private final List<Pregunta> yaPreguntadas = new ArrayList<>();
    // Reemplazamos List<Boolean> listos por un contador de bonos por jugador
    private List<Integer> bonusPorJugador = new ArrayList<>();
    private List<Boolean> preguntadosEnVuelta = new ArrayList<>();

    public Partida(List<Jugador> jugadores, Server server) {
        if (jugadores.size() < 3 || jugadores.size() > 6) {
            throw new IllegalArgumentException("Número de jugadores inválido");
        }

        this.jugadores.addAll(jugadores);

        // Todos los jugadores empiezan con 3 puntos y 2 huecos en el inventario
        for (int i = 0; i < jugadores.size(); i++) {
            puntuacionJugadores.add(3);
            preguntasGanadasJugadores.add(0);
            tamanosInventario.add(2);
            inventarios.add(new ArrayList<>());
            // inicializamos el contador de bonus a 0
            bonusPorJugador.add(0);
            preguntadosEnVuelta.add(false);
        }

        // Siempre empieza el primer jugador
        jugadorActual = jugadores.get(0);
    }

    public int getVueltaActual() {
        return vueltaActual;
    }
    public List<MontonPreguntas> getMontonesPreguntas() {return montonesPreguntas;}
    public Jugador getJugadorActual() {return jugadorActual;}
    public List<Jugador> getJugadores() {return jugadores;}
    public List<Integer> getPuntuaciones() {return puntuacionJugadores;}

    public void actualizarPuntuacion(Jugador jugador, int puntosAsignados) {
        int id = jugador.getId();
        int puntosAnteriores = puntuacionJugadores.get(id);
        puntuacionJugadores.set(id, puntosAnteriores + puntosAsignados);
    }

    public synchronized void registrarPreguntaUsada(Pregunta p) {
        yaPreguntadas.add(p);
    }

    public void actualizarPreguntasGanadas(Jugador jugador) {
        int id = jugador.getId();
        int preguntasAnteriores = preguntasGanadasJugadores.get(id);
        preguntasGanadasJugadores.set(id, preguntasAnteriores + 1);
    }

    private void inicializarMontones() {
        String baseURL = "https://raw.githubusercontent.com/osvecino/Trivia-Attack/main/Preguntas%20y%20cartas/";
        for (Categoria cat : Categoria.values()) {
            MontonPreguntas monton = new MontonPreguntas(cat);
            try {
                String urlCSV = baseURL + cat.name().toLowerCase() + ".csv";
                monton.cargarDesdeCSV(urlCSV);
            } catch (IOException e) {
                System.out.println("Error cargando preguntas de " + cat.name() + ": " + e.getMessage());
            }
            montonesPreguntas.add(monton);
        }
    }

    public void agregarTurnoHistorial(Turno turno) {historialTurnos.add(turno);}
    public void actualizarPreguntados(Jugador jugador, Boolean preguntado) {
        preguntadosEnVuelta.set(jugador.getId(), preguntado);
    }

    /**
     * Incrementa el bonus (contador) de los jugadores que NO fueron preguntados en la vuelta completa.
     * Se llama cuando termina una vuelta completa (cuando el índice de turno vuelve a 0).
     */
    private void incrementarBonusesNoPreguntados() {
        for (Jugador j : jugadores) {
            if (!preguntadosEnVuelta.get(j.getId())) {
                int id = j.getId();
                int viejo = bonusPorJugador.get(id);
                bonusPorJugador.set(id, viejo + 1);
            }
        }
    }

    public void setBonus(Jugador jugador, int valor) { bonusPorJugador.set(jugador.getId(), valor); }
    public int getBonus(Jugador jugador) { return bonusPorJugador.get(jugador.getId()); }
    public void resetBonus(Jugador jugador) { bonusPorJugador.set(jugador.getId(), 0); }

    private void actualizarListos() {
        // este método ya no marca booleans; delega a incrementarBonusesNoPreguntados
        incrementarBonusesNoPreguntados();
    }

    public void gameLoop(Server server) {
        int numJugadores = jugadores.size();
        int indiceTurno = 0;
        int maxVueltas = 6;

        //inicializamos las barajas de preguntas
        inicializarMontones();

        while (vueltaActual <= maxVueltas) {
            jugadorActual = jugadores.get(indiceTurno);
            // Actualizar los estados de cada jugador (el jugador actual es preguntador, el resto pasa a espera)
            actualizarEstados();
            // Mostrar info de vuelta y jugadores
            mostrarVuelta(server);
            //Mostrar información de la partida para cada jugador
            mostrarInfoPartida(server);

            // Ejecutar turno del preguntador
            empezarTurno(jugadorActual, server);
            // Ejecutar turno de jugadores en espera
            for (Jugador j : jugadores) {
                if (j != jugadorActual) empezarTurno(j, server);
            }

            // Finalizar turno
            terminarTurno(server);

            // Pasar a siguiente jugador / vuelta
            indiceTurno = (indiceTurno + 1) % numJugadores;
            if (indiceTurno == 0) {
                // Marcamos como listos a los no preguntados (ahora incrementa su bonus)
                actualizarListos();

                // Reseteamos los preguntados
                for(Jugador j : jugadores) actualizarPreguntados(j, false);

                vueltaActual++;
            }
        }

        server.broadcast("¡Partida finalizada!");
    }

    private void terminarTurno(Server server) {
        Turno turnoActual = ((Preguntador) jugadorActual.getEstado()).getTurnoActual();
        if (turnoActual != null) {
            jugadorActual.getEstado().onTurnEnd(turnoActual, this, server);
            turnoActual.getJugadorRespondedor().getEstado().onTurnEnd(turnoActual, this, server);
        }
    }

    private void empezarTurno(Jugador jugadorActual, Server server) {
        jugadorActual.getEstado().onTurnStart(this, server);
    }

    private void mostrarInfoPartida(Server server) {
        for (Jugador j : jugadores) {
            server.broadcast("");
            server.broadcast("================================================================================================");
            server.broadcast(j.mostrarInformacion());
            server.broadcast("Puntuación: " + puntuacionJugadores.get(j.getId()));
            server.broadcast("Preguntas ganadas: " + preguntasGanadasJugadores.get(j.getId()));

            if(vueltaActual!=1) {
                int bonus = bonusPorJugador.get(j.getId());
                if(bonus > 0) server.broadcast("Este jugador tiene bonus de " + bonus + " (no fue preguntado durante " + bonus + " vuelta(s)). Se jugarán " + bonus + " puntos adicionales en la siguiente pregunta que reciba.");
                else server.broadcast("Este jugador no tiene bonus actualmente.");
            }

            server.broadcast("================================================================================================");
        }
    }

    private void actualizarEstados() {
        for (Jugador j : jugadores) {
            if (j == jugadorActual) j.setEstado(new Preguntador(j));
            else j.setEstado(new EnEspera(j));
        }
    }

    private void mostrarVuelta(Server server) {
        server.broadcast("");
        server.broadcast("=======");
        server.broadcast("VUELTA " + vueltaActual);
        server.broadcast("=======");
        server.broadcast("Información de jugadores:");
    }


}
