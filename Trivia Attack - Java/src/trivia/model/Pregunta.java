package trivia.model;

public class Pregunta {
    private final int id;
    private final Categoria categoria;
    private final String texto;
    private final Respuesta[] respuestas; // tamaño 4 por invariante


    public Pregunta(int id, Categoria categoria, String texto, Respuesta[] respuestas) {
        if (respuestas == null || respuestas.length != 4) throw new IllegalArgumentException("Debe haber 4 respuestas");
        this.id = id;
        this.categoria = categoria;
        this.texto = texto;
        this.respuestas = respuestas;
    }


    public int getId() { return id; }
    public Categoria getCategoria() { return categoria; }
    public String getTexto() { return texto; }
    public Respuesta[] getRespuestas() { return respuestas; }
}
