package trivia.util;

import java.util.Random;
public class Dado {
    private final int numeroCaras;
    private int resultado;
    private final Random rnd = new Random();

    public Dado(int numeroCaras) {
        if (numeroCaras < 2) throw new IllegalArgumentException("Dados deben tener al menos 2 caras");
        this.numeroCaras = numeroCaras;
    }

    public int tirar() {
        resultado = rnd.nextInt(numeroCaras) + 1;
        return resultado;
    }

    public int getResultado() { return resultado; }
}