
package kartracinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class SelectKartScene extends Scene{
    private DemoKart demoKartA, demoKartB;
    private Client client;
    
    private JButton buttonA, buttonB;
    private JLabel title, raceStatus, clientsStatus;
    
    private boolean isAllClientsJoined = false;
    
    public SelectKartScene(){
        super();
        init();
    }
    
    public SelectKartScene(HashMap<String, Object> params){
        super(params);
        init();
    }
    
    private void init(){
        setName("Kart Selection");
        initComponents();
        client = Client.getInstance();
    }
    
    @Override
    public void initComponents() {        
        demoKartA = new DemoKart("kartA", 0, 0);
        demoKartB = new DemoKart("kartB", 0, 0);
                
        buttonA = new JButton();
        buttonA.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                client.notifyServer(new KartSelectedRequest("kartA"));
            }
        });
        
        buttonB = new JButton();
        buttonB.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                client.notifyServer(new KartSelectedRequest("kartB"));
            }
        });
        
        title = new JLabel();
        raceStatus = new JLabel();
        clientsStatus = new JLabel();

        add(buttonA);
        add(buttonB);
        add(title);
        add(raceStatus);
        add(clientsStatus);
    }

    @Override
    public void handleKeyPressed(int keyCode) {
    }    
    
    @Override
    public void setupDisplay(Graphics g){
        Color buttonColor = new Color(245,245,245);
        
        // center of jpanel
        int y = (int) (getHeight() / 2) - 50;
        int x = (int) (getWidth() / 2) - 50;
        
        buttonA.setText("Kart A");
        buttonA.setVerticalTextPosition(SwingConstants.BOTTOM);
        buttonA.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonA.setBounds(x - 55, y, 100, 100);
        buttonA.setBackground(buttonColor);
        buttonA.setBorderPainted(false);
        
        buttonB.setText("Kart B");
        buttonB.setVerticalTextPosition(SwingConstants.BOTTOM);
        buttonB.setHorizontalTextPosition(SwingConstants.CENTER);
        buttonB.setBounds(x + 55, y, 100, 100);
        buttonB.setBackground(buttonColor);
        buttonB.setBorderPainted(false);
        
        buttonA.setIcon(demoKartA.getCurrentFrameIcon());
        buttonB.setIcon(demoKartB.getCurrentFrameIcon());
        
        title.setText("Select your kart");
        title.setBounds(x - 25, y - 200, 300, 300);
        title.setFont(new Font("Serif", Font.PLAIN, 25));
                
        HashMap<String, Object> params = getParams();
        if(params != null){
            raceStatus.setText("YOU " + params.get("status").toString().toUpperCase() + "!");
            raceStatus.setBounds(x - 20, y - 300, 300, 300);
            raceStatus.setFont(new Font("Serif", Font.BOLD, 35));
        }
        else{
            raceStatus.setText("JOIN THE RACE!");
            raceStatus.setBounds(x - 75, y - 300, 300, 300);
            raceStatus.setFont(new Font("Serif", Font.BOLD, 35));
        }
        
        if(!isAllClientsJoined){
            clientsStatus.setText("Waiting for 1 more player...");
            clientsStatus.setBounds(x - 50, y + 200, 300, 50);
            clientsStatus.setFont(new Font("Serif", Font.BOLD, 16));
        }
    }

    @Override
    public void serverUpdated(Response response) {
        if(response.getStatus() == Status.OK){
            String[] lines = response.getLines();

            if(response.getType() == PayloadType.KART_SELECTED){
                // Extract selected karts data from all clients
                HashMap<String, Object> kartDatas = new HashMap<>();
                for(int i=1;i<lines.length; i++){
                    String[] data = lines[i].split(";");
                    HashMap<String, String> mappedData = Response.convertDataToHashMap(data);

                    KartData kartData = new KartData(mappedData.get("Name"), 
                            Double.parseDouble(mappedData.get("X")), 
                            Double.parseDouble(mappedData.get("Y")), 
                            KartAction.fromString(mappedData.get("Action")),
                            Double.parseDouble(mappedData.get("Speed")));

                    kartDatas.put(mappedData.get("User"), kartData);
                }
                
                navigateTo(new RaceScene(kartDatas));   // Pass selected karts datas to RaceScene
            }
            else if(response.getType() == PayloadType.ALL_JOINED){
                isAllClientsJoined = true;
            }
        }
    }
}
