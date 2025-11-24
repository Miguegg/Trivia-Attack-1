package trivia.network;/*import java.io.*;
import java.net.*;
import java.util.Scanner;

public class trivia.network.Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 12345);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        Scanner sc = new Scanner(System.in);

        new Thread(() -> {
            try {
                String msg;
                while((msg = in.readLine()) != null){
                    System.out.println(msg);
                }
            } catch(Exception e){ e.printStackTrace(); }
        }).start();

        while(true){
            String entrada = sc.nextLine();
            out.println(entrada);
        }
    }
}*/



/*
Cómo probarlo

Compila ambos:
javac trivia.network.Server.java trivia.network.Client.java

Abre un terminal y ejecuta el servidor:
java trivia.network.Server

Abre N terminales (por ejemplo 3) y ejecuta el cliente en cada uno:
java trivia.network.Client

Cada jugador verá sus mensajes y podrá escribir respuestas en su propia terminal.
*/




import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Client {

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);
        Socket socket = new Socket("localhost", 12345);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

        // Hilo para leer mensajes del servidor
        new Thread(() -> {
            try{
                String msg;
                while((msg = in.readLine())!=null){
                    System.out.println(msg);
                }
            }catch(Exception e){ e.printStackTrace();}
        }).start();

        // Leer entrada del usuario
        while(true){
            String entrada = sc.nextLine();
            out.println(entrada);
        }
    }
}




