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

    public List<MontonPreguntas> getMontonesPreguntas() {
        return montonesPreguntas;
    }

    public Jugador getJugadorActual() {
        return jugadorActual;
    }

    public List<Jugador> getJugadores() {
        return jugadores;
    }

    public List<Integer> getPuntuaciones() {return puntuacionJugadores;}

    public void actualizarPuntuacion(Jugador jugador, int puntosAsignados) {
        int id = jugador.getId();
        int puntosAnteriores = puntuacionJugadores.get(id);
        puntuacionJugadores.set(id, puntosAnteriores + puntosAsignados);
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


    public void agregarTurno(Turno turno) {
        historialTurnos.add(turno);
    }


    public void iniciarLoop(Server server) {
        int numJugadores = jugadores.size();
        int indiceTurno = 0;
        int maxVueltas = 6;

        inicializarMontones();

        while (vueltaActual <= maxVueltas) {

            jugadorActual = jugadores.get(indiceTurno);
            for (Jugador j : jugadores) {
                if (j == jugadorActual) j.setEstado(new Preguntador(j));
                else j.setEstado(new EnEspera(j));
            }

            // Mostrar info de vuelta y jugadores
            server.broadcast("");
            server.broadcast("=======");
            server.broadcast("VUELTA " + vueltaActual);
            server.broadcast("=======");
            server.broadcast("Información de jugadores:");
            for (Jugador j : jugadores) {
                server.broadcast(j.mostrarInformacion());
                server.broadcast("Puntuación: " + puntuacionJugadores.get(j.getId()));
            }

            // Ejecutar turno del preguntador
            jugadorActual.getEstado().onTurnStart(this, server);

            // Ejecutar onTurnStart de jugadores en espera
            for (Jugador j : jugadores) {
                if (j != jugadorActual) j.getEstado().onTurnStart(this, server);
            }

            // Finalizar turno
            Turno turnoActual = ((Preguntador) jugadorActual.getEstado()).getTurnoActual();
            if (turnoActual != null) {
                jugadorActual.getEstado().onTurnEnd(turnoActual, this, server);
                turnoActual.getJugadorRespondedor().getEstado().onTurnEnd(turnoActual, this, server);
            }

            // Pasar a siguiente jugador / vuelta
            indiceTurno = (indiceTurno + 1) % numJugadores;
            if (indiceTurno == 0) vueltaActual++;
        }

        server.broadcast("¡Partida finalizada!");
    }


}
