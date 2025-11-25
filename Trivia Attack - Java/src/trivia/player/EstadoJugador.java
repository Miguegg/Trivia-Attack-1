package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.network.Server;

public abstract class EstadoJugador {
    protected final Jugador jugador;

    public EstadoJugador(Jugador jugador) {
        this.jugador = jugador;
    }

    /** Se ejecuta cuando empieza el turno del jugador */
    public abstract void onTurnStart(Partida partida, Server server);

    /** Se ejecuta cuando termina el turno del jugador */
    public abstract void onTurnEnd(Turno turno, Partida partida, Server server);

    /** Se ejecuta cuando el jugador recibe una pregunta */
    public abstract void recibirPregunta(Turno turno, Partida partida, Server server);

    /** Mostrar inventario, por ahora solo mensaje */
    public void mostrarInventario(Server server) {
        server.send(jugador, "Inventario de " + jugador.getNombre() + ": [No disponible todavía]");
    }

    /** Uso de cartas en espera, por ahora mensaje */
    protected void usarCarta(Server server) {
        server.send(jugador, "Uso de carta no disponible todavía.");
    }
}
