
package kartracinggame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class RaceScene extends Scene implements KartMoveListener, KartCollideListener{
    private final int START_LINE_X = 425;
    
    private Client client;
    private RoadManager roadManager;
    private Kart yourKart, enemyKart;
    private SpeedMeter yourSpeedMeter, enemySpeedMeter;
    private Kart firstRank;
    private boolean isYourKartDone = false;
    private boolean isEnemyKartDone = false;
    private boolean isYourKartCollided = false;
    private boolean isEnemyKartCollided = false;
    
    private HashMap<String, LinkedList<KartData>> kartDataBuffer;
    
    private JPanel rankPanel;
    private JLabel rankTitle, rankFirst, rankSecond;
    private JLabel yourKartLabel, enemyKartLabel;
    
    public RaceScene(HashMap<String, Object> params){
        super(params);
        setName("Race");
        roadManager = new RoadManager();
        client = Client.getInstance();
        kartDataBuffer = new HashMap<>();
        Animator.getInstance().startAnimation(25);
        
        initComponents();
    }

    @Override
    public void initComponents(){
        HashMap<String, Object> params = getParams();
        for(Map.Entry<String, Object> item:params.entrySet()){
            String socketAddr = item.getKey();
            KartData data = (KartData) item.getValue();

            if(socketAddr.equals("You")){
                yourKart = new Kart(data);
            }
            else{
                enemyKart = new Kart(data);
            }
        }
        
        kartDataBuffer.put("You", new LinkedList<>());
        kartDataBuffer.put("Enemy", new LinkedList<>());
        
        yourSpeedMeter = new SpeedMeter(50, 650);
        enemySpeedMeter = new SpeedMeter(600, 650);
                
        new Collider(0, 100, 50, 500);      // left
        new Collider(50, 50, 750, 50);      // top
        new Collider(800, 100, 50, 500);    // right
        new Collider(50, 600, 750, 50);     // bottom
        
        new Collider(150, 200, 50, 300);      // left
        new Collider(150, 200, 550, 50);      // top
        new Collider(650, 200, 50, 300);    // right
        new Collider(150, 450, 550, 50);     // bottom
        
        roadManager.addRoad(new Rectangle(425,500,275,100)); // bottom
        roadManager.addRoad(new Rectangle(700,200,100,400)); // right
        roadManager.addRoad(new Rectangle(150,100,650,100)); // top
        roadManager.addRoad(new Rectangle(50,100,100,400));  // left
        roadManager.addRoad(new Rectangle(50,500,375,100));  // bottom-left
        
        yourKart.addMoveListener(this);
        yourKart.addCollideListener(this);

        enemyKart.addMoveListener(this);
        enemyKart.addCollideListener(this);
        
        rankPanel = new JPanel();
        rankTitle = new JLabel();
        rankFirst = new JLabel();
        rankSecond = new JLabel();
        
        rankPanel.add(rankTitle);
        rankPanel.add(rankFirst);
        rankPanel.add(rankSecond);

        add(rankPanel);
        
        yourKartLabel = new JLabel("You");
        enemyKartLabel = new JLabel("Enemy");
        
        add(yourKartLabel);
        add(enemyKartLabel);
    }
    
    @Override
    public void handleKeyPressed(int keyCode){
        KartAction action = KartAction.NONE;
        switch(keyCode){
            // W
            case 87:
                action = KartAction.MOVE_FORWARD;
                break;
            // A
            case 65:
                action = KartAction.MOVE_LEFT;
                break;            
            // D
            case 68:
                action = KartAction.MOVE_RIGHT;
                break;
            // S
            case 83:
                action = KartAction.MOVE_BACKWARD;
                break;
            default:
                break;
        }
        
        if(!(isYourKartDone || isYourKartCollided)){
            client.notifyServer(new KartUpdateRequest(action, yourKart.getRotationAngle()));
        }
    }
     
    @Override
    public void setupDisplay(Graphics g){
        Color c1 = Color.green;
        g.setColor(c1);
        g.fillRect(150, 200, 550, 300); // grass
        
        Color c2 = Color.black;
        g.setColor(c2);
        g.drawRect(50, 100, 750, 500); // outer edge
        g.drawRect(150, 200, 550, 300); // inner edge
        
        Color c3 = Color.yellow;
        g.setColor(c3);
        g.drawRect(100, 150, 650, 400); // mid-lane marker
        
        // starting line
        Color c4 = Color.white;
        g.setColor(c4);
        g.drawLine(START_LINE_X, 500, 425, 600);
        
        String trackPath = String.format("%s/track.png", IMAGES_FOLDER);
        ImageIcon track = new ImageIcon(getClass().getResource(trackPath));
        track.paintIcon(this, g, 50, 100);
        
        String startLinePath = String.format("%s/start-line.png", IMAGES_FOLDER);
        ImageIcon startLine = new ImageIcon(getClass().getResource(startLinePath));
        startLine.paintIcon(this, g, START_LINE_X, 500);
        
        updateKartData("You");
        updateKartData("Enemy");
       
        updateRankPanel();
        
        /**
         * Labels for identifying each kart
         */
        yourKartLabel.setBounds((int) (yourKart.getX() + yourKart.getWidth()/4), (int) yourKart.getY() - 25, 70, 25);
        yourKartLabel.setFont(new Font("sans-serif", Font.BOLD, 20));
        yourKartLabel.setForeground(Color.WHITE);
        
        enemyKartLabel.setBounds((int) (enemyKart.getX() + enemyKart.getWidth()/4) - 25, (int) enemyKart.getY() - 25, 70, 25);
        enemyKartLabel.setFont(new Font("sans-serif", Font.BOLD, 20));
        enemyKartLabel.setForeground(Color.WHITE);
       
        // Uncomment this line to display colliders
//        CollisionManager.getInstance().paint(g, Color.RED);

        // Uncomment this line to display roads
//        roadManager.paint(g, Color.YELLOW);
    }
    
    private void updateRankPanel(){
        rankPanel.setBounds(850, 100, 200, 400);
        rankPanel.setBackground(new Color(137, 207, 240));
        rankPanel.setLayout(new BoxLayout(rankPanel, BoxLayout.Y_AXIS));
        
        rankTitle.setText("Ranking");
        rankTitle.setSize(rankPanel.getWidth(), 100);
        rankTitle.setBorder(new EmptyBorder(10, 20, 10, 20));
        rankTitle.setFont(new Font("sans-serif", Font.BOLD, 20));
        rankTitle.setForeground(Color.WHITE);
        
        String firstRankName = "You/Enemy";
        String secondRankName = "You/Enemy";
        
        if(firstRank == yourKart){
            firstRankName = "You" + (isYourKartCollided?"(Collided)":"");
            secondRankName = "Enemy" + (isEnemyKartCollided?"(Collided)":"");
        }
        else if(firstRank == enemyKart){
            firstRankName = "Enemy" + (isEnemyKartCollided?"(Collided)":"");
            secondRankName = "You" + (isYourKartCollided?"(Collided)":"");
        }
        
        rankFirst.setText("1st: " + firstRankName);
        rankFirst.setSize(rankPanel.getWidth(), 100);
        rankFirst.setBorder(new EmptyBorder(10, 20, 10, 20));
        rankFirst.setForeground(Color.WHITE);
        
        rankSecond.setText("2nd: " + secondRankName);
        rankSecond.setSize(rankPanel.getWidth(), 100);
        rankSecond.setBorder(new EmptyBorder(10, 20, 10, 20));
        rankSecond.setForeground(Color.WHITE);
    }
    
    /**
     * Update kart data associated to a user
     * Using data in buffer
     * @param user 
     */
    private synchronized void updateKartData(String user){
        LinkedList<KartData> userKartDatas = kartDataBuffer.get(user);
        int kartDatasSize = userKartDatas.size();
        if(kartDatasSize > 0){
            KartData nextKartData = userKartDatas.pop();
            
            if(user.equals("You")){
                yourKart.setData(nextKartData); 
            }       
            else if(user.equals("Enemy")){
                enemyKart.setData(nextKartData); 
            }
        }
    }


    @Override
    public void kartMoved(Kart kart) {
        if(kart == yourKart){
            yourSpeedMeter.setSpeed(yourKart.getSpeed());
        }
        else if(kart == enemyKart){
            enemySpeedMeter.setSpeed(enemyKart.getSpeed());            
        }
          
        updateFirstRank(kart);
                
        // Return to kart selection scene
        // If race is over
        if(isYourKartDone || isYourKartCollided){
            client.notifyServer(new Payload(PayloadType.KART_EXIT));
        }
    }
    
    @Override
    public void kartCollided(Kart kart) {
        if(kart == yourKart){
            isYourKartCollided = true;
        }
        else if(kart == enemyKart){
            isEnemyKartCollided = true;
        }
    }
    
    private void updateFirstRank(Kart kart){
        if(firstRank == null){
            firstRank = kart;
        }
        else{
            int kartRoadInd = roadManager.getCurrentRoadInd(kart);
            if(firstRank != kart && !(isYourKartDone || isEnemyKartDone)){
                int firstRoadInd = roadManager.getCurrentRoadInd(firstRank);
                
                if(kartRoadInd > firstRoadInd){
                    firstRank = kart;
                }
                else if(kartRoadInd == firstRoadInd){
                    // Determine kart with further position 
                    // Based on current road and position on it
                    if(
                        (kartRoadInd == 0 && kart.getX() > firstRank.getX()) || 
                        (kartRoadInd == 1 && kart.getY() < firstRank.getY()) || 
                        (kartRoadInd == 2 && kart.getX() < firstRank.getX()) ||
                        (kartRoadInd == 3 && kart.getY() > firstRank.getY()) ||
                        (kartRoadInd == 4 && kart.getX() > firstRank.getX())
                    ){
                        firstRank = kart;
                    }
                }
            }
            
            // Check if cross finish line
            if(kart.getAction() == KartAction.MOVE_FORWARD &&
                kartRoadInd == 4 && 
                kart.getX() >= START_LINE_X - kart.getWidth()){
                if(kart == yourKart){
                    isYourKartDone = true;
                }
                else{
                    isEnemyKartDone = true;
                }
            }
        }
    }
        
    @Override
    public void serverUpdated(Response response) {
        if(response.getStatus() == Status.OK){
            String[] lines = response.getLines();
        
            switch(response.getType()){
                case KART_UPDATE:
                    for(int i=1;i<lines.length; i++){
                        String[] data = lines[i].split(";");
                        HashMap<String, String> mappedData = Response.convertDataToHashMap(data);

                        // New kart data
                        KartData kartData = new KartData(mappedData.get("Name"), 
                                Double.parseDouble(mappedData.get("X")), 
                                Double.parseDouble(mappedData.get("Y")), 
                                KartAction.fromString(mappedData.get("Action")),
                                Double.parseDouble(mappedData.get("Speed")));
                        
                        String user = mappedData.get("User");
                        
                        // Add kart data to buffer
                        if(kartDataBuffer.containsKey(user)){
                            LinkedList<KartData> userKartDatas = kartDataBuffer.get(user);
                            userKartDatas.add(kartData); 
                        }                        
                    }

                    break;
                case KART_EXIT:
                    HashMap<String, Object> params = new HashMap<>();
                    params.put("status", firstRank == yourKart? "win": "lose");
                    navigateTo(new SelectKartScene(params));
                    break;
                default:
                    break;
            }
        }
    }
}
