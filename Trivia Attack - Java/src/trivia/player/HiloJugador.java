package trivia.player;
import trivia.game.Partida;

/** Hilo asociado a cada jugador para permitir acciones fuera de su turno */
public class HiloJugador implements Runnable {
    private final Jugador jugador;
    private final Partida partida;
    private volatile boolean running = true;
    public HiloJugador(Jugador jugador, Partida partida) {
        this.jugador = jugador;
        this.partida = partida;
    }
    public void stop() { running = false; }
    @Override
    public void run() {
        while (running) {
// TODO: esperar notificaciones del servidor/partida y permitir usar cartas fuera de turno
            try { Thread.sleep(200); } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); }
        }
    }
}