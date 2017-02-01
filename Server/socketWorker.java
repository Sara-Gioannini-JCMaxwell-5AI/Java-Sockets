/*
 * socketWorker.java ha il compito di gestire la connessione al socket da parte di un Client.
 * Elabora il testo ricevuto che in questo caso viene semplicemente mandato indietro con l'aggiunta 
 * di una indicazione che e' il testo che viene dal Server.
 */
import java.net.*;
import java.io.*;

/**
 *
 * @author Prof. Matteo Palitto
 */
class SocketWorker implements Runnable {
  private Socket client;
  public String GroupChat="";
    //Constructor: inizializza le variabili
    SocketWorker(Socket client) {
        this.client = client;
        System.out.println("Connesso con: " + client);
    }

    // Questa e' la funzione che viene lanciata quando il nuovo "Thread" viene generato
    public void run(){
        
        BufferedReader in = null;
        PrintWriter out = null;
        try{
          // connessione con il socket per ricevere (in) e mandare(out) il testo
          in = new BufferedReader(new InputStreamReader(client.getInputStream()));
          out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
          System.out.println("Errore: in|out fallito");
          System.exit(-1);
        }

        String line = "";
        int clientPort = client.getPort(); //il "nome" del mittente (client)
        while(line != null){
          try{
            line = in.readLine();
            //Manda lo stesso messaggio appena ricevuto con in aggiunta il "nome" del client
            out.println("Server-->" + clientPort + ">> " + line);
            //scrivi messaggio ricevuto su terminale
            System.out.println(clientPort + ">> " + line);
           } catch (IOException e) {
            System.out.println("lettura da socket fallito");
            System.exit(-1);
           }
        }
        try {
            client.close();
            System.out.println("connessione con client: " + client + " terminata!");
        } catch (IOException e) {
            System.out.println("Errore connessione con client: " + client);
        }
    }
  public void gestioneCommandi(String line) {
if(GroupChat.equals("")){
            if(line.length()>5){
            if(line.substring(0, 5).equals("/join"))
            {
                GroupChat = line.substring(6, line.length());
                out.println("Joined to "+line.substring(6, line.length())+" successfully");
                return;
            }
            }
            //Manda lo stesso messaggio appena ricevuto con in aggiunta il "nome" del client
                out.println("Server-->" + nickname + ">> " + line);
            //scrivi messaggio ricevuto su terminale
                System.out.println(nickname + ">> " + line);
                }else
                    {
                        if(line.equals("/quit"))
                        {
                            GroupChat="";
                            return;
                        }
                        if(line.substring(0,7).equals("/invite"))
                        {
                            String nickname = line.substring(7,line.length());
                           for(int i=0;i<ServerTestoMultiThreaded.SocketList.size();i++)
                            {
                                if(!ServerTestoMultiThreaded.SocketList.get(i).nickname.equals(nickname) && ServerTestoMultiThreaded.SocketList.get(i).GroupChat.equals(GroupChat) && ServerTestoMultiThreaded.SocketList.get(i).GroupChat.equals(""))
                                {
                                    ServerTestoMultiThreaded.SocketList.get(i).GroupChat = GroupChat;
                                    ServerTestoMultiThreaded.SocketList.get(i).sendMessage("You joined "+GroupChat+" chat");
                                }
                                else
                                {
                                    sendMessage(nickname+" can't join in this groupchat");
                                }
                            }
                        }
                        for(int i=0;i<ServerTestoMultiThreaded.SocketList.size();i++)
                            {
                                if(!ServerTestoMultiThreaded.SocketList.get(i).nickname.equals(nickname) && ServerTestoMultiThreaded.SocketList.get(i).GroupChat.equals(GroupChat))
                                {
                                    ServerTestoMultiThreaded.SocketList.get(i).sendMessage(nickname+">"+line);
                                }
                            }
                        
                    }
                }
}
