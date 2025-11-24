package trivia.piles;

import trivia.model.Pregunta;
import trivia.model.Categoria;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MontonPreguntas implements Iterable<Pregunta> {
    private final Categoria categoria;
    private final List<Pregunta> preguntas = new ArrayList<>();


    public MontonPreguntas(Categoria categoria) {
        this.categoria = categoria;
    }


    public Categoria getCategoria() { return categoria; }
    public void addPregunta(Pregunta p) { preguntas.add(p); }


    @Override
    public Iterator<Pregunta> iterator() {
        return preguntas.iterator();
    }


    public Pregunta robar() {
        if (preguntas.isEmpty()) return null;
        return preguntas.remove(0);
    }
}