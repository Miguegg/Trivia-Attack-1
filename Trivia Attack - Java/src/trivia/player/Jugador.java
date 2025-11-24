package trivia.player;


import trivia.model.Categoria;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

/** Estadísticas permanentes del jugador */
public class Jugador {
    private final int id;
    private final String nombre;
    // orden alfabético: Arte, Ciencia, Deporte, Entretenimiento, Geografia, Historia
    private final AtomicInteger[] historialEfectividad = new AtomicInteger[6];
    // CAMBIAR A DICCIONARIO

    public Jugador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        for (int i=0;i<6;i++) historialEfectividad[i]=new AtomicInteger(0);
    }

    public int getId() { return id; }
    public String getNombre() { return nombre; }


    public void aumentarEfectividad(Categoria c, int delta) {
        historialEfectividad[c.ordinal()].addAndGet(delta);
    }


    public int getEfectividad(Categoria c) { return historialEfectividad[c.ordinal()].get(); }
}