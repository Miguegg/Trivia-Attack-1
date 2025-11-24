package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;

public class EnEspera extends EstadoJugador {

    public EnEspera(Jugador j) {
        super(j);
    }

    @Override
    public void onTurnStart(Partida partida) {
        System.out.println(jugador.getNombre() + " estás en espera. Puedes usar cartas (no disponible todavía).");
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida) {
        // Nada que hacer al terminar turno si está en espera
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida) {
        throw new IllegalStateException("Jugador en espera no puede recibir preguntas.");
    }
}
