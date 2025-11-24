package trivia.game;
import trivia.model.Pregunta;
import trivia.player.Jugador;
import trivia.cards.CartaPoder;

public class Turno {
    public final Jugador jugadorPreguntador;
    public final Jugador jugadorRespondedor;
    public final Pregunta pregunta;

    public String respuestaRespondedor; // letra o texto
    public int puntosAsignados;
    public int temporizador; // segundos


    public Turno(Jugador preguntador, Jugador respondedor, Pregunta pregunta,
                 int temporizador) {
        this.jugadorPreguntador = preguntador;
        this.jugadorRespondedor = respondedor;
        this.pregunta = pregunta;
        this.temporizador = temporizador;
    }
}