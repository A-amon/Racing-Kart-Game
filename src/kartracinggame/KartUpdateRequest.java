
package kartracinggame;

public class KartUpdateRequest extends Payload{
    private KartAction action;
    private double angle;
    
    public KartUpdateRequest(KartAction action, double angle) {
        super(PayloadType.KART_UPDATE);
        setAction(action);
        setAngle(angle);
    }
    
    public void setAction(KartAction action){
        this.action = action;
    }
    
    public KartAction getAction(){
        return action;
    }
    
    public void setAngle(double angle){
        this.angle = angle;
    }
    
    public double getAngle(){
        return angle;
    }
    
    @Override
    public String toString(){
        return String.format("%s\nAction:%s;Angle:%s;", getType(), action, angle);
    }
}
