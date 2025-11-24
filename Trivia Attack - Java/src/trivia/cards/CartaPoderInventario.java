package trivia.cards;

import trivia.model.Rareza;


public class CartaPoderInventario extends CartaPoder {
    private final String nombre;
    private final String descripcion;


    public CartaPoderInventario(int id, Rareza rareza, int espacio, String nombre, String descripcion, trivia.cards.strategy.CardEffect efecto) {
        super(id, rareza, espacio, efecto);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}