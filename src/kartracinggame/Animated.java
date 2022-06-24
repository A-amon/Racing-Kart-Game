package kartracinggame;

import java.awt.Graphics;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public abstract class Animated {
    private final String ASSETS_FOLDER = "assets";
    private final String FRAMES_FOLDER = String.format("%s/frames", ASSETS_FOLDER);
    
    private ImageIcon[] frames;
    private int currentFrame = 0;
    private double x = 0;
    private double y = 0;
    private double width = 0;
    private double height = 0;
    private int totalFrames = 0;
    private String name;
    
    private boolean isPaint = true;
    
    public Animated(double x, double y){
        setX(x);
        setY(y);
        Animator.getInstance().addAnimated(this);
    }
    
    /**
     * Load frames images from folder
     */
    public void initFrames(String targetName){
        String targetFolder = String.format("%s/%s", FRAMES_FOLDER, targetName);
                
        // Get total number of frames in folder
        if(totalFrames == 0){
            totalFrames = new File("./src/" + targetFolder).list().length;
            frames = new ImageIcon[totalFrames];
        }
        
        for(int count=1; count<=totalFrames; count++){
            String frameImage = String.format("/%s/%d.png", targetFolder, count);

            try{
                frames[count - 1] = new ImageIcon(this.getClass().getResource(frameImage));
            }
            catch(NullPointerException ex){
                ex.printStackTrace();
                System.err.println("Exception: Frames images not found");
            }
        }
    }
    
    /**
     * Display current frame
     * @param panel
     * @param g 
     */
    public void paint(JPanel panel, Graphics g){
        frames[currentFrame].paintIcon(panel, g, (int) x, (int) y);
    }
    
    /****************************
     * Abstract methods
     *****************************/
    public abstract void updateFrame();
    
    /****************************
     * Getters/Setters
     *****************************/
    public void setTotalFrames(int totalFrames){
        this.totalFrames = totalFrames;
    }
    
    public int getTotalFrames() {
        return totalFrames;
    }
    
    public void setCurrentFrame(int currentFrame){
        if(currentFrame < 0){
            currentFrame = totalFrames - 1;
        }
        else if(currentFrame == totalFrames){
            currentFrame = 0;
        }
        this.currentFrame = currentFrame;
        height = frames[currentFrame].getIconHeight();
        width = frames[currentFrame].getIconWidth();
    }
    
    public int getCurrentFrame(){
        return currentFrame;
    }
    
    public ImageIcon getCurrentFrameIcon(){
        return frames[currentFrame];
    }
    
    public ImageIcon getFrameIcon(int frame){
        return frames[frame];
    }
    
    public void setX(double x){
        this.x = x;
    }
    
    public double getX(){
        return x;
    }
    
    public void setY(double y){
        this.y = y;
    }
    
    public double getY(){
        return y;
    }
    
    public double getWidth(){
        return width;
    }
    
    public double getHeight(){
        return height;
    }
    
    public void setIsPaint(boolean isPaint){
        this.isPaint = isPaint;
    }
    
    public boolean getIsPaint(){
        return isPaint;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
        initFrames(name);
    }
}
