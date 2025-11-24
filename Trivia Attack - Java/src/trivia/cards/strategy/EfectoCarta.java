package trivia.cards.strategy;

import trivia.cards.CartaPoder;
import trivia.game.Partida;
import trivia.player.Jugador;


/** Strategy: cada carta define su efecto implementando usar() */
@FunctionalInterface
public interface EfectoCarta{
    void usar(Partida partida, Jugador actor, CartaPoder carta);
}
