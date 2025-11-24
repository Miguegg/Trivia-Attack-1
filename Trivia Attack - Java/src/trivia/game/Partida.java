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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partida {

    private int vueltaActual = 1;
    private Jugador jugadorActual;

    private List<Turno> historialTurnos = new ArrayList<>();
    private final List<trivia.piles.MontonPreguntas> montonesPreguntas = new ArrayList<>(6);

    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Integer> puntuacionJugadores = new ArrayList<>();
    private final List<Integer> preguntasGanadasJugadores = new ArrayList<>();
    private final List<Integer> tamanosInventario = new ArrayList<>();
    private final List<List<CartaPoder>> inventarios = new ArrayList<>();
    private final List<CartaPoder> cartasActivas = new ArrayList<>();
    private final List<Pregunta> yaPreguntadas = new ArrayList<>();
    private final List<Boolean> listos = new ArrayList<>();

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
            listos.add(false);
        }

        // Siempre empieza el primer jugador
        jugadorActual = jugadores.get(0);
    }


    public synchronized void registrarPreguntaUsada(Pregunta p) {
        yaPreguntadas.add(p);
    }

    public int getVueltaActual() {
        return vueltaActual;
    }

    public void siguienteTurno() {
    }

    public Jugador getJugadorActual() {return jugadorActual;}
    public List<Jugador> getJugadores() {return jugadores;}

    public void actualizarPuntuacion(Jugador jugador, int puntosAsignados){
        int id = jugador.getId();
        int puntosAnteriores = puntuacionJugadores.get(id);
        puntuacionJugadores.set(id, puntosAnteriores + puntosAsignados);
    }

    public void actualizarPreguntasGanadas(Jugador jugador) {
        int id = jugador.getId();
        int preguntasAnteriores = preguntasGanadasJugadores.get(id);
        preguntasGanadasJugadores.set(id, preguntasAnteriores+1);
    }

    /** Loop principal de la partida */
    public synchronized void iniciarLoop(Server server) {
      
    }
}
