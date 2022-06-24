
package kartracinggame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class RoadManager {
    List<Rectangle> roads;
    HashMap<Kart, Integer> kartsRoad;
    
    public RoadManager(){
        roads = new LinkedList<>();
        kartsRoad = new HashMap<>();
    }
    
    public void addRoad(Rectangle road){
        roads.add(road);
    }
    
    /**
     * Get index of current Road object
     * Where Kart object is within
     * @param kart
     * @return 
     */
    public int getCurrentRoadInd(Kart kart){
        double kartX = kart.getX();
        double kartY = kart.getY();
        int currentPos = 0;
        
        if(kartsRoad.containsKey(kart)){
            currentPos = kartsRoad.get(kart);
        }
        
        for(int i=currentPos;i<roads.size();i++){
            Rectangle road = roads.get(i);
            int roadX = road.getLocation().x;
            int roadY = road.getLocation().y;
            int roadWidth = road.getSize().width;
            int roadHeight = road.getSize().height;
            
            if((kartX >= roadX && kartX <= (roadX + roadWidth)) && 
                    (kartY >= roadY && kartY <= (roadY + roadHeight))){
                if(i - currentPos == 1){    // Prevent from skipping road - e.g. moving backward from road 0 to road 4(max)
                    kartsRoad.put(kart, i);                
                    return i;
                }
            }
        }
        
        return currentPos;
    }
    
    public void paint(Graphics g, Color color){
        Graphics2D g2d = (Graphics2D)g;
        for(Rectangle road:roads){
            g2d.setColor(color);
            g2d.fill(road);
            g2d.draw(road);
        }
    }
}
