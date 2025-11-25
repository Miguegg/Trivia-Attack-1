package trivia.network;

import trivia.game.Partida;
import trivia.player.Jugador;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class Server {

    private static final int PORT = 12345;
    private static final int MIN_JUGADORES = 3;
    private static final int MAX_JUGADORES = 6;
    private static final long HOST_DECISION_TIMEOUT_SEC = 30L;

    private final List<PlayerHandler> handlers = new ArrayList<>();
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private volatile boolean partidaIniciada = false;

    public static void main(String[] args) {
        new Server().start();
    }

    public void start() {
        printBanner();
        System.out.println("Servidor iniciado en puerto " + PORT);

        // Hilo que acepta jugadores constantemente
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (!partidaIniciada) {
                    Socket socket = serverSocket.accept();
                    synchronized (handlers) {
                        if (handlers.size() >= MAX_JUGADORES) {
                            socket.close();
                            continue;
                        }

                        PlayerHandler ph = new PlayerHandler(socket, handlers.size());
                        handlers.add(ph);
                        pool.submit(ph);

                        broadcast("Jugadores conectados: " + handlers.size() + "/" + MAX_JUGADORES);
                        System.out.println("Conectado: " + ph.getJugador().getNombre());

                        // Si tenemos mínimo jugadores, preguntar al host (primer jugador)
                        if (handlers.size() >= MIN_JUGADORES && !partidaIniciada) {
                            preguntarHostParaIniciar();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void preguntarHostParaIniciar() {
        // Evita lanzar múltiples hilos de host al mismo tiempo
        if (partidaIniciada) return;

        new Thread(() -> {
            PlayerHandler host;
            synchronized (handlers) {
                if (handlers.isEmpty()) return;
                host = handlers.get(0);
            }

            try {
                host.enviarMensaje("\nSERVIDOR: Hay al menos " + handlers.size() + " jugadores conectados.");
                host.enviarMensaje("SERVIDOR: Escribe 'start' para iniciar la partida. Timeout: "
                        + HOST_DECISION_TIMEOUT_SEC + "s");

                String decision = host.pollNextMessage(HOST_DECISION_TIMEOUT_SEC, TimeUnit.SECONDS);

                if ("start".equalsIgnoreCase(decision)) {
                    partidaIniciada = true;
                    broadcast("SERVIDOR: Host ha decidido iniciar la partida!");
                    iniciarPartida();
                } else {
                    host.enviarMensaje("SERVIDOR: No se recibió 'start'. Se sigue esperando jugadores.");
                }
            } catch (InterruptedException e) {
                System.out.println("Tiempo de espera del host interrumpido.");
            }
        }).start();
    }

    private void iniciarPartida() {
        // Aquí inicializarías tu clase Partida y el loop de turnos
        broadcast("\nSERVIDOR: Inicializando partida con jugadores:");
        ArrayList<Jugador> jugadores = new ArrayList<>();

        synchronized (handlers) {
            for (PlayerHandler ph : handlers) {
                jugadores.add(ph.getJugador());
                broadcast(" - " + ph.getJugador().getNombre());
            }
        }

        Partida partida = new Partida(jugadores, this);

        // Ejecutar el loop en un hilo separado para no bloquear el servidor
        new Thread(() -> partida.gameLoop(this)).start();
    }

    public void broadcast(String msg) {
        synchronized (handlers) {
            for (PlayerHandler ph : handlers) {
                ph.enviarMensaje(msg);
            }
        }
    }

    public void send(Jugador jugador, String msg) {
        synchronized (handlers) {
            for (PlayerHandler ph : handlers) {
                if (ph.getJugador().equals(jugador)) {
                    ph.enviarMensaje(msg);
                    break;
                }
            }
        }
    }

    private void printBanner() {
        System.out.println("  _______   _       _                 _   _             _    _ _ ");
        System.out.println(" |__   __| (_)     (_)           /\\  | | | |           | |  | | |");
        System.out.println("    | |_ __ ___   ___  __ _     /  \\ | |_| |_ __ _  ___| | _| | |");
        System.out.println("    | | '__| \\ \\ / / |/ _` |   / /\\ \\| __| __/ _` |/ __| |/ / | |");
        System.out.println("    | | |  | |\\ V /| | (_| |  / ____ \\ |_| || (_| | (__|   <|_|_|");
        System.out.println("    |_|_|  |_| \\_/ |_|\\__,_| /_/    \\_\\__|\\__\\__,_|\\___|_|\\_(_|_)");
        System.out.println();
    }

    public String pollNextMessage(Jugador jugador, long timeout, TimeUnit unit) throws InterruptedException {
        PlayerHandler handler = getHandler(jugador);
        if (handler == null) return null;
        return handler.pollNextMessage(timeout, unit);
    }

    public PlayerHandler getHandler(Jugador jugador) {
        int id = jugador.getId();
        return handlers.get(id);
    }
}







/*https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/game/Partida.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/game/Turno.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/model/Pregunta.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/model/Respuesta.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/network/Client.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/network/PlayerHandler.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/network/Server.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/piles/MontonPreguntas.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/player/EnEspera.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/player/EstadoJugador.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/player/Jugador.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/player/Preguntador.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/player/Respondedor.java
https://github.com/osvecino/Trivia-Attack/blob/feature/standard_flow/Trivia%20Attack%20-%20Java/src/trivia/util/Dado.java*/
