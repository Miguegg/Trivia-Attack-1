package trivia.model;

public class Respuesta {
    private final String texto;
    private final boolean correcta;

    public Respuesta(String texto, boolean correcta) {
        if (texto == null || texto.isBlank())
            throw new IllegalArgumentException("El texto de la respuesta no puede estar vacío");
        this.texto = texto;
        this.correcta = correcta;
    }

    public String getTexto() { return texto; }

    /** Devuelve true si esta es la respuesta correcta */
    public boolean esCorrecta() { return correcta; }

    @Override
    public String toString() {
        return texto + (correcta ? " (Correcta)" : "");
    }
}
