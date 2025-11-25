package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.network.PlayerHandler;
import trivia.network.Server;

public class EnEspera extends EstadoJugador {

    public EnEspera(Jugador j) {
        super(j);
    }

    @Override
    public void onTurnStart(Partida partida, Server server) {
        // Obtener handler para enviar mensaje al jugador
        PlayerHandler handler = server.getHandler(jugador);
        if (handler != null) {
            handler.enviarMensaje("Estás en espera, " + jugador.getNombre() + ".");
            handler.enviarMensaje("Puedes usar cartas (no disponible todavía).");
            mostrarInventario(handler);
        }
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida, Server server) {
        // Nada que hacer al terminar turno si está en espera
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida, Server server) {
        throw new IllegalStateException("Jugador en espera no puede recibir preguntas.");
    }

    /** Mostrar inventario, por ahora solo mensaje */
    private void mostrarInventario(PlayerHandler handler) {
        handler.enviarMensaje("Inventario: [No disponible todavía]");
    }
}
