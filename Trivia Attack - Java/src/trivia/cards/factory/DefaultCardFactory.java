package trivia.cards.factory;

import trivia.cards.CartaPoder;
import trivia.piles.MontonCartas;
import trivia.model.Rareza;

public class DefaultCardFactory extends AbstractCardFactory {

    @Override
    public MontonCartas crearMontonCompleto() {
        MontonCartas m = new MontonCartas();
        // TODO: poblar con cartas concretas
        return m;
    }

    @Override
    public CartaPoder crearCartaPorId(int id) {
        // TODO: switch/registro de cartas según id
        return null;
    }
}
