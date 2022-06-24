package kartracinggame;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Timer;

public class Animator{    
    private static Animator animator;
    private Scene scene;
    private Timer timer;
    private List<Animated> animatedElements;
    private BufferedImage bufferedImage;

    public Animator(){
        animatedElements = new LinkedList<>();
    }
    
    public static Animator getInstance(){
        if(animator == null){
            animator = new Animator();
        }
        
        return animator;
    }
    
    public static void reset(){
        animator = null;
    }
    
    public void addAnimated(Animated animated){
        animatedElements.add(animated);
    }
    
    /**
     * Display current frame
     * If Timer is running/ Animating
     * - update current frame
     */
    public void update(){   
        Graphics gScene = scene.getGraphics();
        if(gScene != null){
            Graphics gBuffer = bufferedImage.createGraphics();

            // Paint into buffer
            for(Animated animated: animatedElements){
                if(animated.getIsPaint()){
                    animated.paint(null, gBuffer);
                }
                if(timer.isRunning()){
                    animated.updateFrame();
                }
            }     
            
            gScene.drawImage(bufferedImage, 0, 0, scene);
            scene.paintComponent(gBuffer);
        }        
    }
    
    public void startAnimation(int animationDelay){
        timer = new Timer(animationDelay, new TimerHandler());
        timer.restart();
    }
    
    public void stopAnimation(){
        timer.stop();
    }
    
    private class TimerHandler implements ActionListener{
        public void actionPerformed(ActionEvent actionEvent){
            if(actionEvent.getSource() == timer){
                update();
            }
        }
    }
    
    public void setScene(Scene scene){
        this.scene = scene;
        bufferedImage = new BufferedImage(1100, 900, BufferedImage.TYPE_INT_ARGB);
    }
}
