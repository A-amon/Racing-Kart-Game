
package kartracinggame;

public class Payload {
    private PayloadType type;
    
    public Payload(PayloadType type){
        setType(type);
    }
    
    public void setType(PayloadType type){
        this.type = type;
    }
    
    public PayloadType getType(){
        return type;
    }
    
    @Override
    public String toString(){
        return type.toString();
    }
}
