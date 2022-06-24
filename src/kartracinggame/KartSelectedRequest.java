
package kartracinggame;

public class KartSelectedRequest extends Payload{
    private String kartName;
    
    public KartSelectedRequest(String kartName) {
        super(PayloadType.KART_SELECTED);
        setKartName(kartName);
    }
    
    public void setKartName(String kartName){
        this.kartName = kartName;
    }
    
    public String getKartName(){
        return kartName;
    }
    
    @Override
    public String toString(){
        return String.format("%s\n%s", getType(), kartName);
    }
}
