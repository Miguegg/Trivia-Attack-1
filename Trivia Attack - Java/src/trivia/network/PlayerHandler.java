package trivia.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import trivia.player.Jugador;

public class PlayerHandler implements Runnable {
    private final Socket socket;
    private final Jugador jugador;
    private final BufferedReader in;
    private final PrintWriter out;
    private final BlockingQueue<String> incoming = new LinkedBlockingQueue<>();
    private volatile boolean running = true;
    private static final int MIN_JUGADORES = 3;

    PlayerHandler(Socket socket, int id) throws IOException {
        this.socket = socket;
        // Crear Jugador local: si tu clase Jugador es distinta, ajusta constructor
        this.jugador = new Jugador(id, "Jugador" + id);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        enviarMensaje("SERVIDOR: Bienvenido " + jugador.getNombre() + " (ID=" + id + ")");
        enviarMensaje("SERVIDOR: Espera a que haya al menos " + MIN_JUGADORES + " jugadores.");
    }

    public Jugador getJugador() {
        return jugador;
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                // Añadimos la entrada a la cola para consumo por el servidor
                incoming.offer(line);
                // Log en servidor
                System.out.println("[" + jugador.getNombre() + "] -> " + line);
            }
        } catch (IOException e) {
            System.out.println("Conexión cerrada para " + jugador.getNombre());
        } finally {
            running = false;
            try { socket.close(); } catch (IOException ignored) {}
        }
    }

    public void enviarMensaje(String msg) {
        out.println(msg);
    }

    /**
     * Espera bloqueante hasta la siguiente línea enviada por el cliente.
     */
    String takeNextMessage() throws InterruptedException {
        return incoming.take();
    }

    /**
     * Espera hasta timeout; devuelve null si no llega nada en ese tiempo.
     */
    public String pollNextMessage(long timeout, TimeUnit unit) throws InterruptedException {
        return incoming.poll(timeout, unit);
    }

    void close() throws IOException {
        running = false;
        try { socket.close(); } catch (IOException ignored) {}
    }
}