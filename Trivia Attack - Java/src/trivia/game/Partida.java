package trivia.game;

import trivia.model.Pregunta;
import trivia.player.Jugador;
import trivia.cards.CartaPoder;
import trivia.piles.MontonPreguntas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Partida {

    private int vueltaActual = 1;

    private final List<Turno> historialTurnos = new ArrayList<>();
    private final List<trivia.piles.MontonPreguntas> montonesPreguntas = new ArrayList<>(6);

    private final List<Jugador> jugadores = new ArrayList<>();
    private final List<Integer> puntuacionJugadores = new ArrayList<>();
    private final List<Integer> preguntasGanadasJugadores = new ArrayList<>();
    private final List<Integer> tamanosInventario = new ArrayList<>();
    private final List<List<CartaPoder>> inventarios = new ArrayList<>();
    private final List<CartaPoder> cartasActivas = new ArrayList<>();
    private final List<Pregunta> yaPreguntadas = new ArrayList<>();
    private final List<Boolean> listos = new ArrayList<>();

    public Partida(List<Jugador> jugadores) {
        if (jugadores.size() < 3 || jugadores.size() > 6) {
            throw new IllegalArgumentException("Número de jugadores inválido");
        }

        this.jugadores.addAll(jugadores);

        for (int i = 0; i < jugadores.size(); i++) {
            puntuacionJugadores.add(3);
            preguntasGanadasJugadores.add(0);
            tamanosInventario.add(2);
            inventarios.add(new ArrayList<>());
            listos.add(false);
        }
    }

    public synchronized void incrementarPuntos(Jugador j, int delta) {
        int idx = jugadores.indexOf(j);
        if (idx < 0) return;
        puntuacionJugadores.set(idx, puntuacionJugadores.get(idx) + delta);
    }

    public synchronized void registrarPreguntaUsada(Pregunta p) {
        yaPreguntadas.add(p);
    }

    public int getVueltaActual() {
        return vueltaActual;
    }

    public void siguienteTurno() {
    }
}
