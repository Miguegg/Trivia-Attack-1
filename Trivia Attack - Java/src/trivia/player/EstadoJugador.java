package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;

public abstract class EstadoJugador {
    protected final Jugador jugador;

    public EstadoJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    /** Se ejecuta cuando empieza el turno del jugador */
    public abstract void onTurnStart(Partida partida);

    /** Se ejecuta cuando termina el turno del jugador */
    public abstract void onTurnEnd(Turno turno, Partida partida);

    /** Se ejecuta cuando el jugador recibe una pregunta */
    public abstract void recibirPregunta(Turno turno, Partida partida);

    /** Mostrar inventario, por ahora solo mensaje */
    protected void mostrarInventario() {
        System.out.println("Inventario de " + jugador.getNombre() + ": [No disponible todavía]");
    }

    /** Uso de cartas en espera, por ahora mensaje */
    protected void usarCarta() {
        System.out.println("Uso de carta no disponible todavía.");
    }
}
