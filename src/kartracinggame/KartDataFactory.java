
package kartracinggame;

import java.util.HashMap;
import java.util.LinkedList;

public class KartDataFactory {
    private final int KART_HEIGHT = 50;
    private final int TOTAL_FRAMES = 16;
    
    private int kartsCount = 0;
    private int[] startPosition;
    private HashMap<String, KartData> lastKartDatas;
    private HashMap<String, LinkedList<KartData>> kartDatasBuffer;
    private int accessCount = 0;
    
    public KartDataFactory(int x, int y){
        startPosition = new int[2];
        lastKartDatas = new HashMap<>();
        kartDatasBuffer = new HashMap<>();
        setStartPosition(x, y);
    }
    
    /**
     * Create KartData object and buffer associated to the socketAddress
     * Every new KartData object created is lower in y position by previous KartData object by 50
     * @param socketAddress
     * @param kartName 
     */
    public void createKart(String socketAddress, String kartName){
        KartData newKartData = new KartData(kartName, startPosition[0], startPosition[1] + 50*kartsCount);
        LinkedList<KartData> datas = new LinkedList<>();
        datas.add(newKartData);
        kartDatasBuffer.put(socketAddress, datas);
        lastKartDatas.put(socketAddress, newKartData);
   
        kartsCount++;
    } 
    
    public void setStartPosition(int x, int y){
        startPosition[0] = x;
        startPosition[1] = y;
    }
    
    public int getNumKarts(){
        return kartsCount;
    }
    
    public KartData getLastData(String socketAddress){
        return lastKartDatas.get(socketAddress);
    }
    
    public void removeData(String socketAddress){
        lastKartDatas.remove(socketAddress);
        kartDatasBuffer.remove(socketAddress);
        kartsCount--;
        if(kartsCount < 0){
            kartsCount = 0;
        }
    }
    
    public int getBufferSize(String socketAddress){
        LinkedList<KartData> buffer = kartDatasBuffer.get(socketAddress);
        if(buffer != null){
            return buffer.size();
        }
        return 0;
    }
    
    public void addToBuffer(String socketAddress, KartData kartData){
        kartDatasBuffer.get(socketAddress).add(kartData);
        lastKartDatas.replace(socketAddress, kartData);
    }
    
    /**
     * Get a string containing every kart data
     * User is set to "You" if the respective kart data belongs to the provided socketAddress
     * Else, User is set to "Enemy"
     * @param socketAddress
     * @return 
     */
    public String getKartDatasString(String socketAddress){
        accessCount ++;
        boolean isAccessMax = accessCount == kartsCount;
        String res = "";
        for(HashMap.Entry<String, LinkedList<KartData>> item:kartDatasBuffer.entrySet()){
            LinkedList<KartData> datas = item.getValue();

            if(datas.size() > 0){
                KartData nextKartData = datas.peek();
                if(nextKartData != null){
                    res += String.format("User:%s;%s\n", item.getKey().equals(socketAddress)?"You":"Enemy", nextKartData.toString()); 
                }
                
                // Only remove from buffer
                // If has been called/sent to both clients
                if(isAccessMax){
                    datas.pop();
                }
            }
        }
        
        if(isAccessMax){
            accessCount = 0;
        }
         
        return res;
    }
}
