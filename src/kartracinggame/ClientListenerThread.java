
package kartracinggame;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashMap;

public class ClientListenerThread extends Thread{
    private Socket clientSocket;
    private Server server;
    
    public ClientListenerThread(Socket socket){
        clientSocket = socket;
        server = server.getInstance();
    }
    
    @Override
    public void run(){
        try{
            DataInputStream stream = new DataInputStream(clientSocket.getInputStream());
            while(!clientSocket.isClosed()){
                String data = stream.readUTF();
                String[] lines = data.split("\n");
                PayloadType type = PayloadType.fromString(lines[0]);
                
                if(type == null){
                    server.sendError(clientSocket, Status.INVALID_TYPE);
                }
                else{
                    switch(type){
                       case KART_SELECTED:
                           // Validate data
                           if(lines[1].equals("kartA") || lines[1].equals("kartB")){
                               server.addKart(clientSocket, lines[1]);                        
                           }
                           else{
                               server.sendError(clientSocket, Status.INVALID_DATA);
                           }
                           break;
                       case KART_UPDATE:
                           String[] dataArr = lines[1].split(";");
                           HashMap<String, String> mappedData = Response.convertDataToHashMap(dataArr);

                           // Validate data
                           if(mappedData.containsKey("Action") && mappedData.containsKey("Angle")){
                               server.updateKart(clientSocket, KartAction.fromString(mappedData.get("Action")), Double.parseDouble(mappedData.get("Angle")));                        
                           }
                           else{
                               server.sendError(clientSocket, Status.INVALID_DATA);
                           }
                           break;
                       case KART_EXIT:
                           server.removeKartData(clientSocket);
                           break;
                       case CLIENT_EXIT:
                           server.removeClient(clientSocket);
                           clientSocket.close();
                           break;
                       default:
                           break;
                   }   
                }
            }
            stream.close();
        }
        catch(IOException ex){
            System.out.println("Failed to listen to client stream");
            ex.printStackTrace();
        }
       
    }
}
