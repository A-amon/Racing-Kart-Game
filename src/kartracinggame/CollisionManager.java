
package kartracinggame;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.util.List;

public class CollisionManager {
    private List<Collider> colliders;
    private static CollisionManager collisionManager;
    private Scene scene;
    
    public CollisionManager(){
        colliders = new LinkedList<>();
    }
    
    public static CollisionManager getInstance(){
        if(collisionManager == null){
            collisionManager = new CollisionManager();
        }
        return collisionManager;
    }
    
    public void addCollider(Collider collider){
        colliders.add(collider);
    }
    
    public static void reset(){
        collisionManager = null;
    }
    
    public boolean isHit(Collider collider){
        for(Collider _collider: colliders){
            if(!_collider.equals(collider)){
               if(isHitHorizontal(collider, _collider) && isHitVertical(collider, _collider)){
                   return true;
               }
            }
        }
        
        return false;
    }
    
    private boolean isHitHorizontal(Collider colliderA, Collider colliderB){
        // colliderA
        double x = colliderA.getX();
        double width = colliderA.getWidth();
        // colliderB
        double _x = colliderB.getX();
        double _width = colliderB.getWidth();
        
        return (_x < x) && (x < (_x + _width)) || 
                (x < _x) && (_x < (x + width));
    }
    
    private boolean isHitVertical(Collider colliderA, Collider colliderB){
        // colliderA
        double y = colliderA.getY();
        double height = colliderA.getHeight();
        // colliderB
        double _y = colliderB.getY();
        double _height = colliderB.getHeight();
        
        return (_y < y) && (y < (_y + _height)) || 
                (y < _y) && (_y < (y + height));
    }
    
    public void paint(Graphics g, Color color){
        Graphics2D g2d = (Graphics2D)g;
        for(Collider collider:colliders){
            g2d.setColor(color);
            g2d.fill(collider);
            g2d.draw(collider);
        }
    }
    
    public void setScene(Scene scene){
        this.scene = scene;
    }
    
    public List<Collider> getColliders(){
        return colliders;
    }
}
