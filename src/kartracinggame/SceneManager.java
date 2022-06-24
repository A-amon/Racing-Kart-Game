
package kartracinggame;

import java.util.LinkedList;
import java.util.List;

public class SceneManager {
    private static SceneManager sceneManager;
    private Scene currentScene;
    private List<SceneChangeListener> listeners;
    
    public SceneManager(){
        listeners = new LinkedList<>();
    }
    
    public static SceneManager getInstance(){
        if(sceneManager == null){
            sceneManager = new SceneManager();
        }
        
        return sceneManager;
    }
    
    public void addListener(SceneChangeListener listener){
        listeners.add(listener);
    }
    
    private void updateListeners(){
        for(SceneChangeListener listener:listeners){
            listener.sceneChanged(currentScene);
        }
    }
    
    public void setCurrentScene(Scene currentScene){
        this.currentScene = currentScene;
        updateListeners();
    }
    
    public Scene getCurrentScene(){
        return currentScene;
    }
}
