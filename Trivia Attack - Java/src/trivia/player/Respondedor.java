package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.model.Pregunta;
import trivia.model.Respuesta;
import trivia.network.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Respondedor extends EstadoJugador {

    public Respondedor(Jugador j) {
        super(j);
    }

    @Override
    public void onTurnStart(Partida partida, Server server) {
        // No hace nada al iniciar turno
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida, Server server) {
        if (turno.getJugadorRespondedor() != jugador) return;

        if (turno.getRespuestaRespondedor() != null && turno.getRespuestaRespondedor().esCorrecta()) {
            server.send(jugador, "¡Has respondido correctamente! Has ganado " + turno.puntosAsignados + " puntos.");
        } else {
            server.send(jugador,"Fallaste la pregunta. No obtienes puntos.");
        }

        // Nota: el bonus se consume y resetea cuando el jugador es elegido (en Preguntador),
        // por tanto aquí no hace falta resetearlo de nuevo.
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida, Server server) {
        Pregunta p = turno.getPregunta();

        // Mezclar respuestas
        List<Respuesta> lista = new ArrayList<>();
        Collections.addAll(lista, p.getRespuestas());

        StringBuilder sb = new StringBuilder();
        sb.append("Tienes 30 segundos para responder (A/B/C/D):");
        server.send(jugador, sb.toString());

        long startTime = System.currentTimeMillis();
        char opcion = ' ';
        while ((System.currentTimeMillis() - startTime) < 30000 && opcion == ' ') {
            try {
                String resp = server.getHandler(jugador).pollNextMessage(1, TimeUnit.SECONDS);
                if (resp != null) {
                    resp = resp.trim().toUpperCase();
                    if (resp.length() == 1 && resp.charAt(0) >= 'A' && resp.charAt(0) <= 'D') {
                        opcion = resp.charAt(0);
                    } else {
                        server.send(jugador, "Respuesta inválida, intenta A/B/C/D:");
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        if (opcion == ' ') {
            server.send(jugador, "Tiempo agotado. No respondiste la pregunta.");
            turno.setRespuestaRespondedor(null);
        } else {
            int idx = opcion - 'A';
            turno.setRespuestaRespondedor(lista.get(idx));
            server.send(jugador, "Has elegido: " + opcion + " -> " + lista.get(idx).getTexto());
        }

        turno.asignarPuntos(partida);
        partida.agregarTurnoHistorial(turno);
    }
}
