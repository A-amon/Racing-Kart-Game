
package kartracinggame;

public class KartData {
    private String kartName;
    private KartAction action = KartAction.NONE;
    private double x, y;
    private double speed = 0;
    private double angle;
    
    public KartData(String kartName, double x, double y){
        setName(kartName);
        setPosition(x, y);
    }
    
    public KartData(String kartName, double x, double y, KartAction action, double speed){
        setName(kartName);
        setPosition(x, y);
        setAction(action);
        setSpeed(speed);
    }
    
    public void setName(String kartName){
        this.kartName = kartName;
    }
    
    public String getName(){
        return kartName;
    }
    
    public void setAction(KartAction newAction){
        this.action = newAction;
    }
    
    public KartAction getAction(){
        return action;
    }
    
    public void setPosition(double x, double y){
        this.x = x;
        this.y = y;
    }
    
    public double getX(){
        return x;
    }
    
    public double getY(){
        return y;
    }
    
    public void setSpeed(double speed){
        this.speed = speed;
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public void setAngle(double angle){
        this.angle = angle;
    }
    
    public double getAngle(){
        return angle;
    }
    
    @Override
    public String toString(){
        return String.format("Name:%s;"
                + "Action:%s;"
                + "X:%.2f;"
                + "Y:%.2f;"
                + "Speed:%.2f;", kartName, action, x, y, speed);
    }
}
