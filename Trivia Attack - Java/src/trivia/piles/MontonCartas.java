package trivia.piles;


import trivia.cards.CartaPoder;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class MontonCartas implements Iterable<CartaPoder> {
    private final List<CartaPoder> cartas = new ArrayList<>();


    public void addCarta(CartaPoder c) { cartas.add(c); }
    public CartaPoder robar() { return cartas.isEmpty() ? null : cartas.remove(0); }
    @Override
    public Iterator<CartaPoder> iterator() { return cartas.iterator(); }
}