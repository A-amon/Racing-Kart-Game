package kartracinggame;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JFrame;

public class Main extends JFrame implements KeyListener, SceneChangeListener, ServerUpdateListener{
    private Client client;
    private Scene scene;
    private Set<Integer> pressedKeys;
    private boolean isAllowedExit = false;
    
    public Main(){
        pressedKeys = new HashSet<>();
        client = Client.getInstance();
        client.addListener(this);
        SceneManager.getInstance().addListener(this);
        initComponents();
        
        setSize(1100, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                Client.getInstance().notifyServer(new Payload(PayloadType.CLIENT_EXIT));
                while(!isAllowedExit){
                    try{
                        Thread.sleep(2000);
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        new Main().setVisible(true); 
    }
    
    private void initComponents(){           
        showScene(new SelectKartScene());
        addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        pressedKeys.add(e.getKeyCode());
        for(int key:pressedKeys){
            scene.handleKeyPressed(key);        
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        
    }

    @Override
    public void keyReleased(KeyEvent e) {
        pressedKeys.remove(e.getKeyCode());
    }

    @Override
    public void sceneChanged(Scene currentScene) {
        showScene(currentScene);
    }
    
    // Switch scene to new scene
    private void showScene(Scene currentScene){
        if(scene != null){
            try{
                scene.finalize();
            }
            catch(Throwable ex){
                ex.printStackTrace();
            }
            remove(scene);
        }
        scene = currentScene;
        scene.setSize(getSize());
        add(scene);  

        revalidate();
        requestFocus();
    }

    @Override
    public void serverUpdated(Response response) {
        if(response.getStatus() == Status.OK && response.getType() == PayloadType.CLIENT_EXIT){
            isAllowedExit = true;
            client.removeListener(this);
        }
    }
}
