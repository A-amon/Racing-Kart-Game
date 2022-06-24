
package kartracinggame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class Client {
    private final int SERVER_PORT = 6666;
    private static Client client = new Client();
    private Socket socket;
    private List<ServerUpdateListener> listeners;
    
    public Client(){
        while(socket == null){
            try{
                socket = new Socket("localhost", SERVER_PORT);
                listeners = new LinkedList<>();
            }
            catch(Exception ex){
                System.out.println("Failed to establish client socket");
                ex.printStackTrace();
            }
        }
        listenToServer();
    }
    
     private void listenToServer(){
        new Thread(new Runnable(){
            @Override
            public void run(){
                try{
                    DataInputStream stream = new DataInputStream(socket.getInputStream());
                    while(!socket.isClosed()){
                        String data = stream.readUTF();
                        Response response = new Response(data);
                        
                        //window closing
                        if(response.getType() == PayloadType.CLIENT_EXIT){
                            socket.close();
                        }
                        updateListeners(response);
                    }                   
                }
                catch(IOException ex){
                    System.out.println("Error reading stream");
                    ex.printStackTrace();
                }
            }
        }).start();
    }
     
    public void notifyServer(Payload payload){
        if(socket != null){
            try{
                DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
                stream.writeUTF(payload.toString());
            }
            catch(Exception ex){
                ex.printStackTrace();
            }
        }
    }
     
    private void updateListeners(Response response){
        for(ServerUpdateListener listener: listeners){
            listener.serverUpdated(response);
        }
    }
    
    public void addListener(ServerUpdateListener listener){
        listeners.add(listener);
    }
    
    public void removeListener(ServerUpdateListener listener){
        listeners.remove(listener);
    }
    
    public static Client getInstance(){
        return client;
    }
}
