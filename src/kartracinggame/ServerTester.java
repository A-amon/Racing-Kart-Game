
package kartracinggame;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ServerTester {
    private final static int SERVER_PORT = 6666;
    private static Socket clientSocket;
    private static Scanner scanner;
    
    public static void main(String[] args){
        scanner = new Scanner(System.in);
        
        try{
            clientSocket = new Socket("localhost", SERVER_PORT);
            
            DataInputStream inputStream = new DataInputStream(clientSocket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(clientSocket.getOutputStream());
            String typeInput = null;
            String dataInput = null;
            do{
                System.out.print("PayloadType:");
                typeInput = scanner.nextLine();
                if(typeInput == null || typeInput.isEmpty()){
                    clientSocket.close();
                    break;
                }
                
                System.out.print("Data:");
                dataInput = scanner.nextLine();
                outputStream.writeUTF(String.format("%s\n%s", typeInput, dataInput));
                
                System.out.println("Response");
                System.out.println("-------------------------");
                String response = inputStream.readUTF();
                for(String line:response.split("\n")){
                    System.out.println(line);
                }
                System.out.println("\n\n");
            }while(!clientSocket.isClosed());
        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }
}
