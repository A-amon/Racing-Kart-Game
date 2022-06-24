
package kartracinggame;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Server {
    private final int MAX_NUM_KARTS = 2;
    
    private final int PORT = 6666;
    private static Server server = new Server();
    
    private ServerSocket serverSocket;
    private List<Socket> clientSockets;
    
    private KartDataFactory kartDataFactory;
    private LinkedList<String> exitedKarts;
    private int clientCount = 0;
    private int kartExitCount = 0;
    
    private ScheduledExecutorService executor;
    
    
    public Server(){
        while(serverSocket == null){
            try{
                serverSocket = new ServerSocket(PORT);
                clientSockets = new LinkedList<>();
                kartDataFactory = new KartDataFactory(425, 500);
                exitedKarts = new LinkedList<>();
            }
            catch(Exception ex){
                System.out.println("Failed to establish server socket");
                ex.printStackTrace();
            }
        }
    }
    
    public static Server getInstance(){
        return server;
    }
    
    public void acceptClients(){
        while(true){
            try{
                Socket clientSocket = serverSocket.accept();
                clientSockets.add(clientSocket);
                new ClientListenerThread(clientSocket).start();
                
                clientCount++;
                if(clientCount == MAX_NUM_KARTS){
                    Thread.sleep(2000); // To give time for Client to add ServerUpdateListener
                    for(Socket socket: clientSockets){
                        notifyClient(socket, new Response(Status.OK, PayloadType.ALL_JOINED).toString());
                    }
                }
            }
            catch(IOException ex){
                System.out.println("Failed to accept client socket");
                ex.printStackTrace();
            }
            catch(InterruptedException ex){
                System.out.println("Thread sleep interrupted");
                ex.printStackTrace();
            }
        }
    }
    
    private void notifyClient(Socket socket, String data){
        try{
            if(!socket.isClosed()){
                DataOutputStream stream = new DataOutputStream(socket.getOutputStream());
                stream.writeUTF(data);
            }
        }
        catch(IOException ex){
            System.out.println("Failed to notify client");
            ex.printStackTrace();
        }
    }
    
    public void sendError(Socket socket, Status status){
        notifyClient(socket, new Response(status).toString());
    }
    
    public synchronized void addKart(Socket socket, String kartName){
        kartDataFactory.createKart(getSocketAddress(socket), kartName);
        if(kartDataFactory.getNumKarts() == MAX_NUM_KARTS){
            for(Socket _csocket:clientSockets){
                notifyClient(_csocket, new Response(Status.OK, PayloadType.KART_SELECTED, kartDataFactory.getKartDatasString(getSocketAddress(_csocket))).toString());
            }
            
            updateKartAtInterval();
        }
    }
    
    /**
     * Automatically send updates to clients
     * For every 25 milliseconds
     */
    private void updateKartAtInterval(){
        executor = Executors.newSingleThreadScheduledExecutor();

        Runnable periodicTask = new Runnable() {
            public void run() {
                synchronized(this){
                    for(Socket _csocket:clientSockets){
                        String socketAddress = getSocketAddress(_csocket);
                        int bufferSize = kartDataFactory.getBufferSize(socketAddress);
                        KartData lastKartData = kartDataFactory.getLastData(socketAddress);

                        String kartDatasString = kartDataFactory.getKartDatasString(socketAddress);
                        if(!kartDatasString.isEmpty()){
                            notifyClient(_csocket, new Response(Status.OK, PayloadType.KART_UPDATE, kartDatasString).toString());                    
                        }

                        // If not further user action
                        // Add NONE action to reduce speed
                        if(bufferSize - 1 == 0){
                            if(lastKartData.getSpeed() > 0){
                                updateKart(_csocket, KartAction.NONE, lastKartData.getAngle());                                                
                            }
                        }
                    }
                }
            }
        };

        executor.scheduleAtFixedRate(periodicTask, 0, 25, TimeUnit.MILLISECONDS);
    }

    /**
     * Update kart data associated to client
     * - x & y positions and speed
     * @param socket
     * @param action
     * @param angle 
     */
    public synchronized void updateKart(Socket socket, KartAction action, double angle){
        String socketAddress = getSocketAddress(socket);
        KartData lastKartData = kartDataFactory.getLastData(socketAddress);
        
        if(lastKartData != null){
            double speed = lastKartData.getSpeed();
            double x = lastKartData.getX();
            double y = lastKartData.getY();

            switch(action){
                case MOVE_FORWARD:
                    if(speed < 1){
                        speed = speed + 0.1;
                    }
                    // 0 TO 90
                    if(angle >= -11.25 && angle <= 11.25){
                        y = y - 10 * speed;
                    }
                    else if(angle >= 11.25 && angle <= 33.75){
                        x = x + 2.5 * speed;
                        y = y - 7.5 * speed;
                    }
                    else if(angle >= 33.75 && angle <= 56.25){
                        x = x + 5 * speed;
                        y = y - 5 * speed;
                    }
                    else if(angle >= 56.25 && angle <= 78.75){
                        x = x + 7.5 * speed;
                        y = y - 2.5 * speed;
                    }
                    else if(angle >= 78.75 && angle <= 101.25){
                        x = x + 10 * speed;
                    }
                    // 90 TO 180
                    else if(angle >= 101.25 && angle <= 123.75){
                        x = x + 7.5 * speed;
                        y = y + 2.5 * speed;
                    }
                    else if(angle >= 123.75 && angle <= 146.25){
                        x = x + 5 * speed;
                        y = y + 5 * speed;
                    }
                    else if(angle >= 146.25 && angle <= 168.75){
                        x = x + 2.5 * speed;
                        y = y + 7.5 * speed;
                    }
                    else if(angle >= 168.75 && angle <= 191.25){
                        y = y + 10 * speed;
                    }
                    // 180 to 270
                    if(angle >= 191.25 && angle <= 213.75){
                        x = x - 2.5 * speed;
                        y = y + 7.5 * speed;
                    }
                    else if(angle >= 213.75 && angle <= 236.25){
                        x = x - 5 * speed;
                        y = y + 5 * speed;
                    }
                    else if(angle >= 236.25 && angle <= 258.75){
                        x = x - 7.5 * speed;
                        y = y + 2.5 * speed;
                    }
                    else if(angle >= 258.75 && angle <= 281.25){
                        x = x - 10 * speed;
                    }
                    else if(angle >= 281.25 && angle <= 303.75){
                        x = x - 7.5 * speed;
                        y = y - 2.5 * speed;
                    }
                    // 270 to 360
                    else if(angle >= 303.75 && angle <= 326.25){
                        x = x - 5 * speed;
                        y = y - 5 * speed;
                    }
                    else if(angle >= 326.25 && angle <= 348.75){
                        x = x - 2.5 * speed;
                        y = y - 7.5 * speed;
                    }
                    break;
                case MOVE_BACKWARD:
                    if(speed < 1){
                        speed = speed + 0.1;
                    }
                    // 0 TO 90
                    if(angle >= -11.25 && angle <= 11.25){
                        y = y + 10 * speed;
                    }
                    else if(angle >= 11.25 && angle <= 33.75){
                        x = x - 2.5 * speed;
                        y = y + 7.5 * speed;
                    }
                    else if(angle >= 33.75 && angle <= 56.25){
                        x = x - 5 * speed;
                        y = y + 5 * speed;
                    }
                    else if(angle >= 56.25 && angle <= 78.75){
                        x = x - 7.5 * speed;
                        y = y + 2.5 * speed;
                    }
                    else if(angle >= 78.75 && angle <= 101.25){
                        x = x - 10 * speed;
                    }
                    // 90 TO 180
                    else if(angle >= 101.25 && angle <= 123.75){
                        x = x - 7.5 * speed;
                        y = y - 2.5 * speed;
                    }
                    else if(angle >= 123.75 && angle <= 146.25){
                        x = x - 5 * speed;
                        y = y - 5 * speed;
                    }
                    else if(angle >= 146.25 && angle <= 168.75){
                        x = x - 2.5 * speed;
                        y = y - 7.5 * speed;
                    }
                    else if(angle >= 168.75 && angle <= 191.25){
                        y = y - 10 * speed;
                    }
                    // 180 to 270
                    if(angle >= 191.25 && angle <= 213.75){
                        y = y - 10 * speed;
                    }
                    else if(angle >= 213.75 && angle <= 236.25){
                        x = x + 2.5 * speed;
                        y = y - 7.5 * speed;
                    }
                    else if(angle >= 236.25 && angle <= 258.75){
                        x = x + 5 * speed;
                        y = y - 5 * speed;
                    }
                    else if(angle >= 258.75 && angle <= 281.25){
                        x = x + 10 * speed;
                    }
                    else if(angle >= 281.25 && angle <= 303.75){
                        x = x + 7.5 * speed;
                        y = y + 2.5 * speed;
                    }
                    // 270 to 360
                    else if(angle >= 303.75 && angle <= 326.25){
                        x = x + 5 * speed;
                        y = y + 5 * speed;
                    }
                    else if(angle >= 326.25 && angle <= 348.75){
                        x = x + 2.5 * speed;
                        y = y + 7.5 * speed;
                    }
                    else if(angle >= 348.75 && angle <= 371.25){
                        y = y - 10 * speed;
                    }
                    break;
                case MOVE_RIGHT:
                    y = y + 10 * speed;
                    break;
                case MOVE_LEFT:
                    y = y - 10 * speed;         
                    break;
                case NONE:
                    if(speed > 0){
                        speed = speed - 0.1;               
                    }
                    break;
                default:
                    break;
            }

            KartData newKartData = new KartData(lastKartData.getName(), x, y, action, speed);
            newKartData.setAngle(angle);
            kartDataFactory.addToBuffer(socketAddress, newKartData);
        }
    }
    
    /**
     * Clear kart data of client
     * Notify after all kart exit race scene
     * @param socket 
     */
    public synchronized void removeKartData(Socket socket){
        String socketAddress = getSocketAddress(socket);
        if(!exitedKarts.contains(socketAddress)){
            kartExitCount++;
            exitedKarts.add(socketAddress);
        }
        
        if(kartExitCount == MAX_NUM_KARTS){
            int socketsCount = clientSockets.size();
            for(Socket _csocket:clientSockets){
                socketAddress = getSocketAddress(socket);
                kartDataFactory.removeData(socketAddress);
                notifyClient(_csocket, new Response(Status.OK, PayloadType.KART_EXIT).toString());
                if(socketsCount == MAX_NUM_KARTS){
                    notifyClient(_csocket, new Response(Status.OK, PayloadType.ALL_JOINED).toString());
                }
            }
            kartExitCount = 0;
            exitedKarts.clear();
            executor.shutdownNow();
        }
    }
    
    public synchronized void removeClient(Socket socket){
        clientCount--;
        clientSockets.remove(socket);
        removeKartData(socket); // If kart data still exist
        notifyClient(socket, new Response(Status.OK, PayloadType.CLIENT_EXIT).toString());
    }
    
    public String getSocketAddress(Socket socket){
        return socket.getLocalAddress() + ":" + socket.getPort();
    }
}
