package trivia.cards;

import trivia.model.Rareza;


public abstract class CartaPoder {
    protected final int id;
    protected final Rareza rareza;
    protected final int espacioEnInventario;
    protected boolean jugada = false;
    protected boolean congelada = false;
    protected boolean utilizable = true;


    protected final trivia.cards.strategy.EfectoCarta efecto;


    public CartaPoder(int id, Rareza rareza, int espacio, trivia.cards.strategy.EfectoCarta efecto) {
        this.id = id;
        this.rareza = rareza;
        this.espacioEnInventario = espacio;
        this.efecto = efecto;
    }


    public int getId() { return id; }
    public Rareza getRareza() { return rareza; }
    public int getEspacioEnInventario() { return espacioEnInventario; }


    public boolean isJugada() { return jugada; }
    public void setJugada(boolean jugada) { this.jugada = jugada; }


    public boolean isCongelada() { return congelada; }
    public void setCongelada(boolean congelada) { this.congelada = congelada; }


    public boolean isUtilizable() { return utilizable; }
    public void setUtilizable(boolean u) { this.utilizable = u; }


    // Strategy: delega el efecto concreto a implementaciones de EfectoCarta
    public void usar(trivia.game.Partida partida, trivia.player.Jugador actor) {
        if (!utilizable) throw new IllegalStateException("Carta no utilizable en este momento");
        efecto.usar(partida, actor, this);
        this.jugada = true;
    }
}
