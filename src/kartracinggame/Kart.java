
package kartracinggame;

import java.util.LinkedList;
import java.util.List;

public class Kart extends Animated{
    private final String CAR_MOVING_AUDIO = "car-moving";
    private final String CAR_EXPLOSION_AUDIO = "car-explosion";
    
    private KartData data;
    
    private Collider collider;
    private AudioController audio;
    private boolean isHit = false;
    private List<KartMoveListener> moveListeners;
    private List<KartCollideListener> collideListeners;
    
    public Kart(KartData data){
        super(data.getX(), data.getY());
        setName(data.getName());
        setCurrentFrame(4);
        
        setData(data);

        collider = new Collider((int) data.getX(), (int) data.getY(), (int) (getWidth() - 25), (int) (getHeight() - 25));
        collider.centerize(getWidth(), getHeight());
        moveListeners = new LinkedList<>();
        collideListeners = new LinkedList<>();
        initAudio();
    }
    
    private void initAudio(){
        audio = new AudioController();

        try{
            audio.addClip(CAR_MOVING_AUDIO);
            audio.addClip(CAR_EXPLOSION_AUDIO);
        }
        catch(Exception ex){
            ex.printStackTrace();
            audio = null;
        }
    }
    
    @Override
    public void updateFrame() {
        int currentFrame = getCurrentFrame();
        int totalFrames = getTotalFrames();
        
        playAudio();

        switch(data.getAction()){                
            case MOVE_RIGHT:
                setCurrentFrame((currentFrame + 1) % totalFrames);
                break;
            case MOVE_LEFT:
                setCurrentFrame((currentFrame - 1) % totalFrames);
                break;
            default:
                break;
        }

        setX(data.getX());
        setY(data.getY());

        // Collider
        collider.setLocation((int) data.getX(), (int) data.getY());
        collider.centerize(getWidth(), getHeight());
        if(!isHit && CollisionManager.getInstance().isHit(collider)){
            isHit = true;
            updateCollideListeners();
        }

        updateMoveListeners();
        data.setAction(KartAction.NONE);
    }
    
    private void playAudio(){
        if(audio != null){
            if(isHit){
                audio.play(CAR_EXPLOSION_AUDIO, true);                
            }
            else{
                if(data.getAction() == KartAction.MOVE_FORWARD){
                    audio.updateVolume(CAR_MOVING_AUDIO, -5 + (float) data.getSpeed() * 5);
                    audio.play(CAR_MOVING_AUDIO, false);
                }
                else if(data.getSpeed() <= 0.1){
                    audio.stop(CAR_MOVING_AUDIO);
                }
            }
        }
    }
    
    public double getRotationAngle(){
        return 360/getTotalFrames()*getCurrentFrame();
    }
    
    private void updateMoveListeners(){
        for(KartMoveListener listener:moveListeners){
            listener.kartMoved(this);
        }
    }
    
    public void addMoveListener(KartMoveListener listener){
        moveListeners.add(listener);
    }
    
    private void updateCollideListeners(){
        for(KartCollideListener listener:collideListeners){
            listener.kartCollided(this);
        }
    }
    
    public void addCollideListener(KartCollideListener listener){
        collideListeners.add(listener);
    }

    /****************************
     * Getters/Setters
     *****************************/
    
    public void setData(KartData data){
        this.data = data;
    }
    
    public boolean getIsHit(){
        return isHit;
    }
    
    public double getSpeed(){
        return data.getSpeed();
    }
    
    public void setAction(KartAction action){
        data.setAction(action);
    }
    
    public KartAction getAction(){
        return data.getAction();
    }
}
