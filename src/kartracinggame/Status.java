
package kartracinggame;

public enum Status {
    OK(200, "OK"),
    INVALID_DATA(400, "INVALID DATA SYNTAX"),
    INVALID_TYPE(404, "INVALID PAYLOAD TYPE");
    
    private final int code;
    private final String text;

    Status(int code, String text){
        this.code = code;
        this.text = text;
    }
    
    public int getCode(){
        return code;
    }
    
    public String getText(){
        return text;
    }
    
    public static Status fromString(String codeStr){
        switch(codeStr){
            case "200":
                return OK;
            case "400":
                return INVALID_DATA;
            case "404":
                return INVALID_TYPE;
        }
        return null;
    }
}
