package trivia.cards;

import trivia.model.Rareza;

public class CartaPoderAmbiental extends CartaPoder {
    private final String nombre;
    private final String descripcion;
    private boolean activa = false;


    public CartaPoderAmbiental(int id, Rareza rareza, int espacio, String nombre, String descripcion, trivia.cards.strategy.CardEffect efecto) {
        super(id, rareza, espacio, efecto);
        this.nombre = nombre;
        this.descripcion = descripcion;
    }


    public void setActiva(boolean a) { this.activa = a; }
    public boolean isActiva() { return activa; }
}
