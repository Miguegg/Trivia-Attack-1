package trivia.game;
import trivia.model.Pregunta;
import trivia.model.Respuesta;
import trivia.player.Jugador;
import trivia.cards.CartaPoder;

public class Turno {
    public final Jugador jugadorPreguntador;
    public final Jugador jugadorRespondedor;
    public final Pregunta pregunta;

    public Respuesta respuestaRespondedor;
    public int puntosAsignados;


    public Turno(Jugador preguntador, Jugador respondedor, Pregunta pregunta) {
        this.jugadorPreguntador = preguntador;
        this.jugadorRespondedor = respondedor;
        this.pregunta = pregunta;
    }

    public void setPuntosAsignados(Integer puntos) {this.puntosAsignados = puntos;}
    public void setRespuestaRespondedor(Respuesta respuesta) {this.respuestaRespondedor = respuesta;}

    public Jugador getJugadorPreguntador() {return jugadorPreguntador;}
    public Jugador getJugadorRespondedor() {return jugadorRespondedor;}
    public Pregunta getPregunta() {return pregunta;}
    public Respuesta getRespuestaRespondedor() {return respuestaRespondedor;}

    public void asignarPuntos(Partida partida) {
        if(respuestaRespondedor.esCorrecta()) {
            partida.actualizarPuntuacion(jugadorRespondedor, +puntosAsignados);
            partida.actualizarPreguntasGanadas(jugadorRespondedor);
        } else partida.actualizarPuntuacion(jugadorPreguntador, +puntosAsignados);
    }

}