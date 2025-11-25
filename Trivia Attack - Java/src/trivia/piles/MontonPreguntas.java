package trivia.piles;

import trivia.model.Pregunta;
import trivia.model.Respuesta;
import trivia.model.Categoria;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.net.URI;
import java.net.URL;

public class MontonPreguntas {

    private final Categoria categoria;
    private final List<Pregunta> preguntas = new ArrayList<>();

    public MontonPreguntas(Categoria categoria) {
        this.categoria = categoria;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void addPregunta(Pregunta p) {
        preguntas.add(p);
    }

    public int size() {
        return preguntas.size();
    }

    public Pregunta robar() {
        if (preguntas.isEmpty()) return null;
        int idx = (int) (Math.random() * preguntas.size());
        return preguntas.remove(idx);
    }

    /**
     * Carga preguntas desde CSV.
     * @param rutaCSV puede ser path local o URL
     */
    public void cargarDesdeCSV(String rutaCSV) throws IOException {
        try (BufferedReader br = abrirCSV(rutaCSV)) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.startsWith("ID;")) continue;

                String[] partes = linea.split(";");
                if (partes.length != 7) continue;

                int id = Integer.parseInt(partes[0].trim());
                String textoPregunta = partes[1].trim();
                Respuesta[] respuestas = new Respuesta[4];
                int indiceCorrecta = Integer.parseInt(partes[6].trim()) - 1;

                for (int i = 0; i < 4; i++) {
                    boolean correcta = (i == indiceCorrecta);
                    respuestas[i] = new Respuesta(partes[i + 2].trim(), correcta);
                }

                Pregunta pregunta = new Pregunta(id, categoria, textoPregunta, respuestas);
                preguntas.add(pregunta);
            }
        }
    }



    private BufferedReader abrirCSV(String ruta) throws IOException {
        if (ruta.startsWith("http://") || ruta.startsWith("https://")) {
            try {
                URI uri = new URI(ruta);
                URL url = uri.toURL();  // evita deprecated constructor directo
                return new BufferedReader(new InputStreamReader(url.openStream()));
            } catch (Exception e) {
                throw new IOException("Error al abrir URL: " + ruta, e);
            }
        } else {
            return Files.newBufferedReader(Path.of(ruta));
        }
    }


    public List<Pregunta> getPreguntas() {
        return preguntas;
    }
}
