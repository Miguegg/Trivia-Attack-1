package trivia.cards;

import trivia.model.Rareza;

public class CartaPoderEterea extends CartaPoder {
    public CartaPoderEterea(int id, Rareza rareza, String nombre, String
            descripcion, trivia.cards.strategy.EfectoCarta efecto) {
        super(id, rareza, 0, efecto); // espacio en inventario = 0
        // invariante explícita
        assert this.espacioEnInventario == 0 :
                "EtereaNoOcupa: cartas etéreas no deben ocupar inventario";
    }
}