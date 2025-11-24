package trivia.network;/*import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class trivia.network.Server {
    private int puerto = 12345;
    private List<PlayerHandler> jugadores = new ArrayList<>();
    private ExecutorService pool = Executors.newCachedThreadPool();

    public static void main(String[] args) throws IOException {
        new trivia.network.Server().start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en puerto " + puerto);

        while(jugadores.size() < 3) { // espera mínimo 3 jugadores
            Socket socket = serverSocket.accept();
            PlayerHandler ph = new PlayerHandler(socket, jugadores.size()+1);
            jugadores.add(ph);
            pool.submit(ph);
            System.out.println("Jugador conectado: " + ph.id);
        }

        System.out.println("Comenzando la partida con " + jugadores.size() + " jugadores");
        runGame();
    }

    private void runGame() {
        // Aquí implementas el loop de turnos, tirada de dados,
        // preguntas, cálculo de puntos y envío de mensajes a los clientes
    }

    class PlayerHandler implements Runnable {
        Socket socket;
        int id;
        BufferedReader in;
        PrintWriter out;
0*/


import trivia.model.*;
import trivia.player.Jugador;
import trivia.piles.MontonPreguntas;
import trivia.game.Partida;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {

    private int puerto = 12345;
    private List<PlayerHandler> jugadores = new ArrayList<>();
    private ExecutorService pool = Executors.newCachedThreadPool();
    private Partida partida;

    public static void main(String[] args) throws IOException {
        new Server().start();
    }

    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(puerto);
        System.out.println("Servidor iniciado en puerto " + puerto);

        // Espera mínimo 3 jugadores y máximo 6
        while(jugadores.size() < 3) {
            Socket socket = serverSocket.accept();
            Jugador j = new Jugador(jugadores.size()+1, "Jugador" + (jugadores.size()+1));
            PlayerHandler ph = new PlayerHandler(socket, j);
            jugadores.add(ph);
            pool.submit(ph);
            System.out.println(j.getNombre() + " conectado.");
        }

        // Crear montones de ejemplo
        List<MontonPreguntas> montones = crearMontonesEjemplo();
        partida = new Partida(jugadores, montones);

        broadcast("Comenzando la partida con " + jugadores.size() + " jugadores!");

        // Ejecutar 6 vueltas
        for(int vuelta=1; vuelta<=6; vuelta++){
            broadcast("\n=== Vuelta " + vuelta + " ===");
            ejecutarVuelta();
        }

        // Mostrar puntuaciones finales
        broadcast("\n=== Partida finalizada! ===");
        for(int i=0;i<jugadores.size();i++){
            broadcast(jugadores.get(i).getNombre() + " = " + partida.getPuntuacion(i) + " puntos");
        }

        System.exit(0);
    }

    private void ejecutarVuelta() {
        for(PlayerHandler preguntador : jugadores){
            // Elegir rival aleatorio distinto
            PlayerHandler respondedor;
            do {
                respondedor = jugadores.get(new Random().nextInt(jugadores.size()));
            } while(respondedor == preguntador);

            // Tirar dado para categoría
            Categoria categoria = Categoria.values()[new Random().nextInt(6)];

            MontonPreguntas mp = partida.getMontonDeCategoria(categoria);
            Pregunta p = mp.robarPrimeraPregunta();

            preguntador.enviarMensaje("Tu turno. Pregunta para " + respondedor.jugador.getNombre() +
                    " en categoría " + categoria);
            respondedor.enviarMensaje("Pregunta: " + p.getPregunta() + " (A/B/C/D)");

            // Enviar opciones
            List<Respuesta> resps = p.getOpciones();
            char letra = 'A';
            for(Respuesta r: resps){
                respondedor.enviarMensaje(letra + ") " + r.texto);
                letra++;
            }

            // Esperar respuesta con temporizador
            String respuesta = respondedor.leerRespuestaConTimeout(15);
            boolean acierto = false;
            if(respuesta == null){
                respondedor.enviarMensaje("Tiempo agotado! Se considera fallo.");
            } else {
                acierto = p.responderLetra(respuesta);
                respondedor.enviarMensaje(acierto ? "ACERTASTE!" : "FALLASTE!");
            }

            int puntos = acierto ? 1 : 0;
            if(!acierto) puntos = 1; // regla del juego: si falla, gana el preguntador
            preguntador.enviarMensaje("Ganaste " + puntos + " puntos por esta pregunta.");
            partida.sumarPuntos(preguntador.jugador, puntos);
        }
    }

    private List<MontonPreguntas> crearMontonesEjemplo(){
        List<MontonPreguntas> montones = new ArrayList<>();
        int id = 1;
        for(Categoria c : Categoria.values()){
            MontonPreguntas mp = new MontonPreguntas(c);
            for(int i=0;i<3;i++){
                List<Respuesta> resp = new ArrayList<>();
                resp.add(new Respuesta("Correcta", true));
                resp.add(new Respuesta("Incorrecta1", false));
                resp.add(new Respuesta("Incorrecta2", false));
                resp.add(new Respuesta("Incorrecta3", false));
                Pregunta p = new Pregunta(id++, c, "Pregunta " + id + " de " + c, resp);
                mp.addPregunta(p);
            }
            montones.add(mp);
        }
        return montones;
    }

    private void broadcast(String msg){
        for(PlayerHandler ph : jugadores){
            ph.enviarMensaje(msg);
        }
    }

    class PlayerHandler implements Runnable {
        Socket socket;
        Jugador jugador;
        BufferedReader in;
        PrintWriter out;

        PlayerHandler(Socket s, Jugador j){
            this.socket = s;
            this.jugador = j;
            try{
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);
                enviarMensaje("Bienvenido " + j.getNombre());
            }catch(Exception e){ e.printStackTrace();}
        }

        @Override
        public void run() {
            try{
                String linea;
                while((linea=in.readLine())!=null){
                    System.out.println(jugador.getNombre() + " dice: " + linea);
                }
            }catch(IOException e){ e.printStackTrace();}
        }

        void enviarMensaje(String msg){
            out.println(msg);
        }

        String leerRespuestaConTimeout(int segundos){
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> in.readLine());
            try{
                return future.get(segundos, TimeUnit.SECONDS);
            }catch(Exception e){
                future.cancel(true);
                return null;
            }finally{
                executor.shutdownNow();
            }
        }
    }
}
