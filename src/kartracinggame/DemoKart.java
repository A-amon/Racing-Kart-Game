
package kartracinggame;

public class DemoKart extends Animated{
    public DemoKart(String name, int x, int y){
        super(x, y);
        setName(name);
        setIsPaint(false);
    }
     
    @Override
    public void updateFrame() {
        int currentFrame = getCurrentFrame();
        int totalFrames = getTotalFrames();
        setCurrentFrame((currentFrame + 1) % totalFrames);
    }    
}
