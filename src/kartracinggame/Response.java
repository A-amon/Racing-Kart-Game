
package kartracinggame;

import java.util.HashMap;

public class Response extends Payload{
    private String data;
    private String[] lines;
    private Status status;
    
    /**********************************
     * For receiver usage - START
     **********************************/
    public Response(String respString){
        super(null);
        /**
         * Format of respString
         * First Line = PayloadType
         * Subsequent Lines = Data
         */
        setLines(respString.split("\n"));
    }
    //END
    
    /**********************************
     * For sender usage - START
     **********************************/
    public Response(Status status){
        super(null);
        setStatus(status);
    }
    
    public Response(Status status, PayloadType type){
        super(type);
        setStatus(status);
    }
    
    public Response(Status status, PayloadType type, String data) {
        this(status, type);
        setData(data);
    }
    //END
    
    public void extractStatusAndType(){
        String line = lines[0];
        String[] items = line.split("/");
        if(items.length > 1){
            setStatus(Status.fromString(items[0]));
            setType(PayloadType.fromString(items[1]));
        }
    }
    
    public String[] getLines(){
        return lines;
    }
    
    public void setLines(String[] lines){
        this.lines = lines;
        extractStatusAndType();
    }
    
    public void setStatus(Status status){
        this.status = status;
    }
    
    public Status getStatus(){
        return status;
    }
    
    public void setData(String data){
        this.data = data;
    }
    
    public String getData(){
        return data;
    }
     
    /**
     * Convert data in String to HashMap
     * E.g. ["User:You", "Action:MOVE_RIGHT"]
     * To {
     *  "User":"You",
     *  "Action":"MOVE_RIGHT"
     * }
     * @param data
     * @return 
     */
    public static HashMap<String, String> convertDataToHashMap(String[] data){
        HashMap<String, String> res = new HashMap<>();
        for(String d: data){
            int colonInd = d.indexOf(":");
            if(colonInd >= 0){
                String key = d.substring(0, colonInd);
                String value = d.substring(colonInd + 1, d.length());
                res.put(key, value);
            }
        }
        
        return res;
    }
    
    @Override
    public String toString(){
        String res = String.format("%d/%s\n", status.getCode(), 
                                            getType() == null? status.getText(): getType().toString()); // Display status text if type is null
        if(data != null && !data.isEmpty()){
            res += data;
        }
        return res;
    }
}
