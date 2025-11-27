package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.model.Categoria;
import trivia.model.Pregunta;
import trivia.network.Server;
import trivia.piles.MontonPreguntas;
import trivia.util.Dado;

import java.util.concurrent.TimeUnit;

public class Preguntador extends EstadoJugador {

    private final Dado dado;
    private Turno turnoActual; // Guardar turno creado

    public Preguntador(Jugador j) {
        super(j);
        this.dado = new Dado(6);
    }

    public Turno getTurnoActual() {
        return turnoActual;
    }

    @Override
    public void onTurnStart(Partida partida, Server server) {
        int puntosJugados = 1;

        server.send(jugador, "\n--- Turno de " + jugador.getNombre() + " (Preguntador) ---");
        server.send(jugador, "Vuelta actual: " + partida.getVueltaActual());
        mostrarInventario(server);

        server.send(jugador, "Opciones:");
        server.send(jugador, "[1] Robar carta (no disponible)");
        server.send(jugador, "[2] Usar carta (no disponible)");
        server.send(jugador, "[3] Lanzar dado");

        // Esperar opción del jugador vía red
        String opcion = "";
        while (!"3".equals(opcion)) {
            try {
                server.send(jugador, "Elige opción (solo '3' disponible por ahora):");
                String resp = server.getHandler(jugador).pollNextMessage(60, TimeUnit.SECONDS);
                if (resp == null) {
                    server.send(jugador, "No se recibió opción a tiempo. Se usará 'Lanzar dado'.");
                    opcion = "3";
                } else {
                    opcion = resp.trim();
                    if (!"3".equals(opcion)) {
                        server.send(jugador, "Esa opción no está disponible. Debes elegir '3'.");
                    }
                }
            } catch (InterruptedException e) {
                server.send(jugador, "Error esperando opción. Se usará 'Lanzar dado'.");
                opcion = "3";
                Thread.currentThread().interrupt();
            }
        }

        // Lanzar dado y seleccionar categoría
        server.send(jugador, "");
        server.send(jugador, "Lanzando dado para elegir categoría...");
        int resultadoDado = dado.tirar();
        Categoria cat = Categoria.values()[resultadoDado - 1];
        server.send(jugador, "Resultado del dado: " + resultadoDado + " -> Categoría: " + cat);
        server.send(jugador, "");

        // Mostrar info de jugadores nuevamente al preguntador
        server.send(jugador, "Información de jugadores:");
        for (Jugador j : partida.getJugadores()) {
            if(!j.equals(jugador)) {
                server.send(jugador, j.mostrarInformacion());
                server.send(jugador,"Puntuación:" + partida.getPuntuaciones().get(j.getId()));
            }

        }

        // Buscar montón correspondiente
        MontonPreguntas monton = partida.getMontonesPreguntas().stream()
                .filter(m -> m.getCategoria() == cat)
                .findFirst().orElse(null);

        if (monton == null || monton.size() == 0) {
            server.send(jugador, "No hay preguntas disponibles en esta categoría.");
            return;
        }

        // Robar pregunta
        Pregunta pregunta = monton.robar();
        partida.registrarPreguntaUsada(pregunta);

        // Elegir jugador respondedor
        server.send(jugador, "Elige un jugador para responder:");
        int i = 0;
        for (Jugador j : partida.getJugadores()) {
            if (j != jugador) server.send(jugador, i + ": " + j.getNombre());
            i++;
        }

        int idx = -1;
        while (idx < 0 || idx >= partida.getJugadores().size() || partida.getJugadores().get(idx) == jugador) {
            try {
                server.send(jugador, "Introduce el número del jugador:");
                String msg = server.getHandler(jugador).pollNextMessage(60, TimeUnit.SECONDS);
                if (msg != null) idx = Integer.parseInt(msg.trim());
                else {
                    server.send(jugador, "Tiempo agotado. Seleccionando primer rival disponible...");
                    for (i = 0; i < partida.getJugadores().size(); i++) {
                        if (partida.getJugadores().get(i) != jugador) {
                            idx = i;
                            break;
                        }
                    }
                    server.send(jugador, "¡Se ha elegido a " + partida.getJugadores().get(i).getNombre());
                }
            } catch (InterruptedException ex) {
                server.send(jugador, "Tiempo agotado. Seleccionando primer rival disponible...");
                for (i = 0; i < partida.getJugadores().size(); i++) {
                    if (partida.getJugadores().get(i) != jugador) {
                        idx = i;
                        break;
                    }
                }
            } catch (NumberFormatException e) {
                server.send(jugador, "Número inválido. Intenta de nuevo.");
            }
        }

        // Elegir a un jugador respondedor
        Jugador respondedor = partida.getJugadores().get(idx);
        respondedor.setEstado(new Respondedor(respondedor));

        // Obtener bonus acumulado del respondedor (puede ser 0)
        int bonus = partida.getBonus(respondedor);
        if (bonus > 0) {
            puntosJugados += bonus;
            server.send(jugador, "¡Has elegido a un jugador con bonus de " + bonus + "! Se jugarán " + puntosJugados + " puntos en total (incluyendo bonus).");
        }

        // Marcar al respondedor como ya preguntado en esta vuelta
        partida.actualizarPreguntados(respondedor, true);

        // Resetear el bonus del respondedor (se consume al ser preguntado)
        partida.resetBonus(respondedor);

        // Notificar a los jugadores en espera sobre quién fue elegido
        for (Jugador j : partida.getJugadores()) {
            if (j != jugador && j != respondedor) {
                server.send(j, "");
                server.send(j, jugador.getNombre() + " ha escogido a " + respondedor.getNombre() + " como respondedor.");
                if(bonus > 0) server.send(j, "¡Se ha elegido a un jugador con bonus de " + bonus + "! Se jugarán " + puntosJugados + " puntos en total.");
            }
        }

        // Crear y guardar turno
        turnoActual = new Turno(jugador, respondedor, pregunta);
        turnoActual.setPuntosAsignados(puntosJugados);

        // Notificar a todos
        enviarPregunta(server, pregunta);

        // Enviar pregunta al respondedor
        server.send(respondedor, "================================");
        server.send(respondedor, "¡Te han elegido para responder!");
        if(bonus > 0) server.send(respondedor, "Tenías un bonus de " + bonus + " por no ser preguntado. Se jugarán " + puntosJugados + " puntos en este turno.");
        server.send(respondedor, "================================");

        respondedor.getEstado().recibirPregunta(turnoActual, partida, server);
    }

    private static void enviarPregunta(Server server, Pregunta pregunta) {
        server.broadcast("");
        server.broadcast("Pregunta: " + pregunta.getTexto());
        char letra = 'A';
        for (int j = 0; j < pregunta.getRespuestas().length; j++) {
            server.broadcast(letra + ": " + pregunta.getRespuestas()[j].getTexto());
            letra++;
        }
        server.broadcast("");
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida, Server server) {
        if (turno.getRespuestaRespondedor() != null && !turno.getRespuestaRespondedor().esCorrecta()) {
            server.send(jugador, "El respondedor falló. Has ganado " + turno.puntosAsignados + " puntos.");
        } else {
            server.send(jugador, "El respondedor acertó. No has ganado puntos.");
        }
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida, Server server) {
        throw new IllegalStateException("El preguntador no puede recibir preguntas.");
    }

    @Override
    public void mostrarInventario(Server server) {
        server.send(jugador, "Inventario: (aún no disponible)");
    }

    public Dado getDado() {
        return dado;
    }
}
