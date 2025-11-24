package trivia.player;

import trivia.game.Turno;

public class EnEspera extends EstadoJugador {
    public EnEspera(Jugador j) { super(j); }
    @Override public void onTurnStart() { }
    @Override public void onTurnEnd() { }
    @Override public void recibirPregunta(Turno turno) { /* aceptar o recibir
preguntas */ }
}