
package kartracinggame;

public class SpeedMeter extends Animated{
    private double speed = 0;
    
    public SpeedMeter(int x, int y){
        super(x, y);
        setName("speed-meter");
        initFrames(getName());
    }
    
    @Override
    public void updateFrame() {
        setCurrentFrame((int) (speed * 10 / 2));
    }
    
    public double getSpeed(){
        return speed;
    }
    
    public void setSpeed(double speed){
        this.speed = speed;
    }
}
