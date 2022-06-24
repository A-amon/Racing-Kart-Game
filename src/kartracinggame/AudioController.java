
package kartracinggame;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class AudioController {
    private final String ASSETS_FOLDER = "./src/assets";
    private final String AUDIO_FOLDER = String.format("%s/audio", ASSETS_FOLDER);
    private HashMap<String,Clip> clips;
    
    public AudioController(){
        clips = new HashMap<>();
    }
    
    /**
     * Add Clip object to clips
     * @param name //clip's name
     * @throws UnsupportedAudioFileException
     * @throws IOException
     * @throws LineUnavailableException 
     */
    public void addClip(String name) throws UnsupportedAudioFileException, IOException, LineUnavailableException{
        String filePath = String.format("%s/%s.wav", AUDIO_FOLDER, name);
        AudioInputStream stream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        Clip newClip = AudioSystem.getClip();
        newClip.open(stream);
        clips.put(name, newClip);
    }
    
    public void play(String name, boolean doFull){
        Clip clip = getClip(name);
        
        if(!doFull){    // restart audio from 0
            clip.stop();
            clip.setFramePosition(0);
        }
        
        if(!clip.isRunning()){  // let audio finish playing to end
            clip.start();
        }
    }
    
    public void stop(String name){
        getClip(name).stop();
    }
    
    /**
     * Increase/ Decrease volume (+ float for increase/ - float for decrease)
     * @param name
     * @param volume 
     */
    public void updateVolume(String name, float volume){
        FloatControl gainControl = (FloatControl) getClip(name).getControl(FloatControl.Type.MASTER_GAIN);
        gainControl.setValue(volume);
    }
    
    public Clip getClip(String name){
        return clips.get(name);
    }        
}
