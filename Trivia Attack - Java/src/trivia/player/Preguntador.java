package trivia.player;
import trivia.game.Turno;
public class Preguntador extends EstadoJugador {

    public Preguntador(Jugador j) { super(j); }

    @Override public void onTurnStart() {/* TODO: acciones al iniciar turno */}
    @Override public void onTurnEnd() {/* TODO */}

    @Override public void recibirPregunta(Turno turno) { throw new
            IllegalStateException("No puedes recibir pregunta si eres preguntador"); }
}