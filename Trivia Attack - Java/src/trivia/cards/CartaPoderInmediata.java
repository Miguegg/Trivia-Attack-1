package trivia.cards;

import trivia.model.Rareza;

public class CartaPoderInmediata extends CartaPoder {
    private final String nombre;
    private final String descripcion;

    public CartaPoderInmediata(int id, Rareza rareza, int espacio, String nombre, String descripcion, trivia.cards.strategy.EfectoCarta efecto) {
        super(id, rareza, espacio, efecto);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }

    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
}
