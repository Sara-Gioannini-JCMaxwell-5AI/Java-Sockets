import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class ServerTestoMultiThreaded {

    static ArrayList<SocketWorker> workers_connessi;
    static ArrayList<String> group_chats;
    
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Uso: java ServerTestoMultithreaded <Porta Server>");
            return;
        }

        int portNumber = Integer.parseInt(args[0]);
        workers_connessi = new ArrayList();
        group_chats = new ArrayList();
        group_chats.add("default");

        try{
            ServerSocket server = new ServerSocket(portNumber);
            System.out.println("Server di Testo in esecuzione...  (CTRL-C quits)\n");

            while(true){
                SocketWorker w;
                try {
                    //server.accept returns a client connection
                    w = new SocketWorker(server.accept());
                    Thread t = new Thread(w);
                    t.start();
                    workers_connessi.add(w);
                } catch (IOException e) {
                    System.out.println("Connessione NON riuscita con client: ");
                    System.exit(-1);
                }
            }
        } catch (IOException e) {
            System.out.println("Error! Porta: " + portNumber + " non disponibile");
            System.exit(-1);
        }
    }   
}
