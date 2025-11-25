package trivia.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Pregunta {
    private final int id;
    private final Categoria categoria;
    private final String texto;
    private final Respuesta[] respuestas; // siempre 4

    public Pregunta(int id, Categoria categoria, String texto, Respuesta[] respuestas) {
        if (respuestas == null || respuestas.length != 4)
            throw new IllegalArgumentException("Debe haber 4 respuestas");
        this.id = id;
        this.categoria = categoria;
        this.texto = texto;
        this.respuestas = respuestas;
    }

    public int getId() { return id; }
    public Categoria getCategoria() { return categoria; }
    public String getTexto() { return texto; }

    /** Devuelve las respuestas en orden aleatorio */
    public List<Respuesta> getRespuestasAleatorias() {
        List<Respuesta> lista = new ArrayList<>();
        Collections.addAll(lista, respuestas);
        Collections.shuffle(lista);
        return lista;
    }

    /** Devuelve la respuesta correcta */
    public Respuesta getRespuestaCorrecta() {
        for (Respuesta r : respuestas) {
            if (r.esCorrecta()) return r;
        }
        return null; // nunca debería pasar si la pregunta está bien formada
    }

    public Respuesta[] getRespuestas() {return respuestas;}
}
