
package kartracinggame;

import java.awt.Color;
import java.awt.Graphics;
import java.util.HashMap;
import javax.swing.JPanel;

public abstract class Scene extends JPanel implements ServerUpdateListener{
    public final String ASSETS_FOLDER = "/assets";
    public final String IMAGES_FOLDER = String.format("%s/images", ASSETS_FOLDER);
    private HashMap<String, Object> params;
    private String name;
    
    public Scene(){
        CollisionManager.reset();
        Animator.reset();
        setBackground(Color.WHITE);
        setLayout(null);
        
        Animator animator = Animator.getInstance();
        animator.setScene(this);
        animator.startAnimation(100);
        
        Client.getInstance().addListener(this);
    }
    
    public Scene(HashMap<String, Object> params){
        this();
        setParams(params);
    }
    
    public abstract void initComponents();
    
    public abstract void setupDisplay(Graphics g);  // painting components
    
    public abstract void handleKeyPressed(int keyCode);
    
    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        setupDisplay(g);
    }
    
    public void navigateTo(Scene scene){
        SceneManager.getInstance().setCurrentScene(scene);
    }
    
    
    /****************************
     * Getters/Setters
     *****************************/
    
    public HashMap<String, Object> getParams(){
        return params;
    }
    
    public void setParams(HashMap<String, Object> params){
        this.params = params;
    }
    
    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }
    
    protected void finalize() throws Throwable{
        super.finalize();
        Client.getInstance().removeListener(this);
    }
}
