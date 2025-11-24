package trivia.player;


import trivia.game.Turno;


/** Estado del jugador: Patrón Estado */
public abstract class EstadoJugador {
    protected final Jugador jugador;


    public EstadoJugador(Jugador jugador) { this.jugador = jugador; }


    public abstract void onTurnStart();
    public abstract void onTurnEnd();
    public abstract void recibirPregunta(Turno turno);
}