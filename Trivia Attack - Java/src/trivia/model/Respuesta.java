package trivia.model;

public class Respuesta {
    private final String texto;
    private final boolean correcta;


    public Respuesta(String texto, boolean correcta) {
        this.texto = texto;
        this.correcta = correcta;
    }


    public String getTexto() { return texto; }
    public boolean esCorrecta() { return correcta; }
}
