
package kartracinggame;

public enum KartAction {
    ROTATE,
    MOVE_LEFT,
    MOVE_RIGHT,
    MOVE_FORWARD,
    MOVE_BACKWARD,
    NONE;
    
    public static KartAction fromString(String actionString){
        switch(actionString){
            case "ROTATE":
                return ROTATE;
            case "MOVE_LEFT":
                return MOVE_LEFT;
            case "MOVE_RIGHT":
                return MOVE_RIGHT;
            case "MOVE_FORWARD":
                return MOVE_FORWARD;
            case "MOVE_BACKWARD":
                return MOVE_BACKWARD;
            case "NONE":
                return NONE;
        }
        
        return null;
    }
}
