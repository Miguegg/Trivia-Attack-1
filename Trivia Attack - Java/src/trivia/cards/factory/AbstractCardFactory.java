package trivia.cards.factory;
import trivia.cards.CartaPoder;
import trivia.piles.MontonCartas;

public abstract class AbstractCardFactory {
    public abstract MontonCartas crearMontonCompleto();
    public abstract CartaPoder crearCartaPorId(int id);
}
