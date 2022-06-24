
package kartracinggame;

public enum PayloadType {
    ALL_JOINED,
    KART_SELECTED,
    KART_UPDATE,
    KART_EXIT,
    CLIENT_EXIT;
    
    public static PayloadType fromString(String typeString){
        switch(typeString){
            case "ALL_JOINED":
                return ALL_JOINED;
            case "KART_SELECTED":
                return KART_SELECTED;
            case "KART_UPDATE":
                return KART_UPDATE;
            case "KART_EXIT":
                return KART_EXIT;
            case "CLIENT_EXIT":
                return CLIENT_EXIT;
        }
        
        return null;
    }
}
