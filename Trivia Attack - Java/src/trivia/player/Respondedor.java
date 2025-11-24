package trivia.player;


import trivia.game.Turno;

public class Respondedor extends EstadoJugador {
    public Respondedor(Jugador j) { super(j); }
    @Override public void onTurnStart() { /* normalmente no hace nada */ }
    @Override public void onTurnEnd() { /* TODO */ }
    @Override public void recibirPregunta(Turno turno) { /* responder la pregunta del turno */ }
}