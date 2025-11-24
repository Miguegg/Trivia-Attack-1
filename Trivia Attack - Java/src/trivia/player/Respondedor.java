package trivia.player;

import trivia.game.Turno;
import trivia.game.Partida;
import trivia.model.Pregunta;
import trivia.model.Respuesta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class Respondedor extends EstadoJugador {

    public Respondedor(Jugador j) {
        super(j);
    }

    @Override
    public void onTurnStart(Partida partida) {
        // Normalmente no hace nada al iniciar turno
    }

    @Override
    public void onTurnEnd(Turno turno, Partida partida) {
        if (turno.getJugadorRespondedor() != jugador) return;

        if (turno.getRespuestaRespondedor().esCorrecta()) {
            System.out.println("¡Has respondido correctamente! Has ganado " + turno.puntosAsignados + " puntos.");
        } else {
            System.out.println("Fallaste la pregunta. No obtienes puntos.");
        }
    }

    @Override
    public void recibirPregunta(Turno turno, Partida partida) {
        System.out.println("\n" + jugador.getNombre() + ", ¡te han elegido para responder!");
        Pregunta p = turno.getPregunta();

        // Mostrar respuestas en orden aleatorio
        List<Respuesta> lista = new ArrayList<>();
        Collections.addAll(lista, p.getRespuestas());
        Collections.shuffle(lista);

        char letra = 'A';
        for (Respuesta r : lista) {
            System.out.println(letra + ": " + r.getTexto());
            letra++;
        }

        Scanner sc = new Scanner(System.in);
        char resp = ' ';
        while (resp != 'A' && resp != 'B' && resp != 'C' && resp != 'D') {
            System.out.print("Elige tu respuesta (A/B/C/D): ");
            resp = sc.next().toUpperCase().charAt(0);
        }

        // Convertir letra a Respuesta
        int idx = resp - 'A';
        turno.setRespuestaRespondedor(lista.get(idx));

        // Calcular puntos según respuesta
        turno.asignarPuntos(partida);
    }
}
