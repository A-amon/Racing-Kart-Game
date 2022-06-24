
package kartracinggame;

import java.awt.Rectangle;

public class Collider extends Rectangle{    
    public Collider(int x, int y, int width, int height){
        super(x, y, width, height);
        CollisionManager.getInstance().addCollider(this);
    }
    
    public void centerize(double contentWidth, double contentHeight){
        setLocation((int) (getLocation().x + contentWidth/4), (int) (getLocation().y + contentHeight/4));
    }
}
