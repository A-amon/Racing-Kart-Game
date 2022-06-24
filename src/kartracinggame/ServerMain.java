
package kartracinggame;

public class ServerMain {
    public static void main(String[] args){
        Server server = Server.getInstance();
        server.acceptClients();
    }
}
