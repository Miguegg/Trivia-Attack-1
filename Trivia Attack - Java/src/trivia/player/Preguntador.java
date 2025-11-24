package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.model.Pregunta;
import java.util.Scanner;

public class Preguntador extends EstadoJugador {

    public Preguntador(Jugador j) {
        super(j);
    }

    @Override
    public void onTurnStart(Partida partida) {
        System.out.println("\n--- Turno de " + jugador.getNombre() + " (Preguntador) ---");
        System.out.println("Vuelta actual: " + partida.getVueltaActual());
        mostrarInventario();

        System.out.println("Opciones:");
        System.out.println("[1] Robar carta (no disponible)");
        System.out.println("[2] Usar carta (no disponible)");
        System.out.println("[3] Lanzar dado");

        // Por ahora, ignoramos las cartas
        Scanner sc = new Scanner(System.in);
        System.out.print("Elige opción: ");
        String opcion = sc.nextLine().trim();

        System.out.println("Lanzando dado para elegir categoría...");
        int cara = (int)(Math.random() * 6) + 1; // simula dado 6 caras
        System.out.println("Resultado dado: " + cara);
        // TODO: Asignar categoría según dado y sacar pregunta
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida) {
        if (turno.getRespuestaRespondedor() != null) {
            if (turno.getRespuestaRespondedor().esCorrecta()) {
                System.out.println("El respondedor acertó. No ganaste puntos.");
            } else {
                System.out.println("El respondedor falló. Has ganado " + turno.puntosAsignados + " puntos.");
            }
        }
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida) {
        throw new IllegalStateException("El preguntador no puede recibir preguntas.");
    }
}
