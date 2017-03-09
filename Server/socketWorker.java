import java.net.*;
import java.io.*;

class SocketWorker implements Runnable {
    private Socket client;
    public String nickname;
    public String group_chat;
    
    public String group_chat_invito;
    public boolean accettando;
    
    
    public BufferedReader in = null;
    public PrintWriter out = null;
        
    //Constructor: inizializza le variabili
    SocketWorker(Socket client) {
        this.client = client;
        System.out.println("Connesso con: " + client);
    }

    // Questa e' la funzione che viene lanciata quando il nuovo "Thread" viene generato
    public void run(){
        
        try{
            // connessione con il socket per ricevere (in) e mandare(out) il testo
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            out = new PrintWriter(client.getOutputStream(), true);
        } catch (IOException e) {
            System.out.println("Errore: in|out fallito");
            System.exit(-1);
        }
        
        try{
            nickname = in.readLine();
            group_chat = ServerTestoMultiThreaded.group_chats.get(0);
            accettando = false;
            group_chat_invito = "";
        } catch (IOException e) {
            System.out.println("Lettura nickname fallita!");
        }
        
        out.println(" -------------------------------------------------"
                + "\n Lista comandi che si possono usare:"
                + "\n • /listautenti --> stampa elenco degli utenti connessi"
                + "\n • /creagroupchat --> crea una nuova groupchat"
                + "\n • /invitautente --> invia un invito ad un utente"
                + "\n • /escigroupchat --> esce dalla groupchat"
                + "\n • /listagroupchat --> stampa elenco delle groupchat"
                + "\n -------------------------------------------------");

        String line = "";
        //int clientPort = client.getPort(); //il "nome" del mittente (client)
        while(line != null){
            try{
                line = in.readLine();
                
                if(line.equals("/listautenti"))
                    out.println(getListaUtenti());
                else if(line.equals("/listagroupchat"))
                {
                    out.println(getListaGroupChat());
                }
                else if(line.equals("/creagroupchat"))
                {
                    out.println("Inserisci il nome della Group Chat:");
                    String name_gc = in.readLine();
                    if(creaGroupChat(name_gc))
                    {
                        out.println("Group Chat creata con successo!");
                        group_chat = name_gc;
                    }
                    else
                        out.println("Il nome esiste gia'");
                }
                else if(line.equals("/invitautente"))
                {
                    out.println("Inserisci il nome dell'utente da invitare:");
                    String nick_inv = in.readLine();
                    String messaggio = invitoUtente(nick_inv, group_chat);
                    out.println(messaggio);
                }
                else if(line.equals("/escigroupchat"))
                {
                    if(utentiNellaGroupChat(group_chat) == 0)
                    {
                        ServerTestoMultiThreaded.group_chats.remove(group_chat);
                    }
                    
                    group_chat = ServerTestoMultiThreaded.group_chats.get(0);
                }
                else if(line.equals("/accetto"))
                {
                    accettando = false;
                    group_chat = group_chat_invito;
                    group_chat_invito = "";
                }
                else if(line.equals("/rifiuto"))
                {
                    accettando = false;
                    group_chat_invito = "";
                } 
                else
                    invioMessaggio(group_chat+ ">>" +nickname+": "+line, group_chat);  
                
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
    
    private void invioMessaggio(String msg, String gc)
    {
        for(int i=0; i<ServerTestoMultiThreaded.workers_connessi.size(); i++) {
            if(ServerTestoMultiThreaded.workers_connessi.get(i).group_chat.equals(gc))
            {
                SocketWorker sw = ServerTestoMultiThreaded.workers_connessi.get(i);
                
                if(sw != this)
                    sw.out.println(msg);
            }
        }
    }
    
    private int utentiNellaGroupChat(String ngc)
    {
        int nsw = 0;
        
        for(int i=0; i<ServerTestoMultiThreaded.workers_connessi.size(); i++) {
            if(ServerTestoMultiThreaded.workers_connessi.get(i).group_chat.equals(ngc))
            {
                nsw ++;
            }
        }
        return nsw;
    }
    
    private String getListaUtenti()
    {
        String str = "";
        for(int i=0; i<ServerTestoMultiThreaded.workers_connessi.size(); i++) {
            str += ServerTestoMultiThreaded.workers_connessi.get(i).nickname + "\n";
        }
        
        return str;
    }
    
    private String getListaGroupChat()
    {
        String str = "";
        for(int i=0; i<ServerTestoMultiThreaded.group_chats.size(); i++) {
            str += ServerTestoMultiThreaded.group_chats.get(i) + "\n";
        }
        
        return str;
    }
    
    private boolean creaGroupChat(String ngc)
    {
        if(!ServerTestoMultiThreaded.group_chats.contains(ngc))
        {
            ServerTestoMultiThreaded.group_chats.add(ngc);
            return true;
        }
        
        return false;
    }
    
    private String invitoUtente(String nick, String ngc)
    {
        boolean trovato = false;
        for(int i=0; i<ServerTestoMultiThreaded.workers_connessi.size(); i++) {
            if(ServerTestoMultiThreaded.workers_connessi.get(i).nickname.equals(nick))
            {
                trovato = true;
                SocketWorker sw = ServerTestoMultiThreaded.workers_connessi.get(i);
                
                if(!sw.group_chat.equals(ServerTestoMultiThreaded.group_chats.get(0)))
                    return "Utente gia' in un altra group chat.";
                
                if(sw.accettando)
                    return "Ha gia' ricevuto un invito.";
                
                sw.accettando = true;
                sw.group_chat_invito = ngc;
                sw.inviaRichiesta();
            }
        }
        
        if(trovato == false)
            return "Utente non trovato.";
        else
            return "Richiesta inviata con successo!";
    }
    
    public void inviaRichiesta()
    {
        out.println("Sei stato invitato nel gruppo: " + group_chat_invito + ". [/accetto,/rifiuto]");
    }
}
