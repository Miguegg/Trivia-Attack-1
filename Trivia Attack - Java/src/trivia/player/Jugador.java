package trivia.player;

import trivia.model.Categoria;

import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/** Estadísticas permanentes del jugador */
public class Jugador {

    private final int id;
    private final String nombre;
    private EstadoJugador estado;
    // Historial de efectividad por categoría
    private final Map<Categoria, AtomicInteger> historialEfectividad;

    public Jugador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;

        // Inicializamos el EnumMap con todas las categorías
        historialEfectividad = new EnumMap<>(Categoria.class);
        for (Categoria c : Categoria.values()) {
            historialEfectividad.put(c, new AtomicInteger(0));
        }
    }

    public void setEstado(EstadoJugador estado){
        this.estado = estado;
    }
    public EstadoJugador getEstado() {return estado;}


    public int getId() { return id; }

    public String getNombre() { return nombre; }

    /** Obtiene la efectividad de todas las categorías como un Map */
    public Map<Categoria, Integer> getHistorialEfectividad() {
        Map<Categoria, Integer> resultado = new EnumMap<>(Categoria.class);
        for (Map.Entry<Categoria, AtomicInteger> entry : historialEfectividad.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().get());
        }
        return resultado;
    }

    /** Obtiene la efectividad en una categoría concreta */
    public int getEfectividad(Categoria c) {
        return historialEfectividad.get(c).get();
    }

    /** Devuelve un String con la info del jugador (para mostrar en consola) */
    public String mostrarInformacion() {
        StringBuilder sb = new StringBuilder();
        sb.append("Jugador: ").append(nombre).append(" | Efectividad: ");
        for (Categoria c : Categoria.values()) {
            sb.append(c.name()).append("=").append(getEfectividad(c)).append(" ");
        }
        return sb.toString();
    }


}
