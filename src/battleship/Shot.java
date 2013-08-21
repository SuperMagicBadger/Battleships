/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

/**
 *
 * @author cow
 * 
 * This is the shot class, pretty much used to
 * lump these three variables together and make it easier
 * to send them across classes.  Nothing important in here.
 * 
 */
public class Shot {
	public int x, y;
	public boolean hit;
        public boolean sunk;
        public String targetHit;
	
        public Shot(){
            this(0, 0);
        }
        
        public Shot(int _x, int _y){
            x = _x;
            y = _y;
            hit = sunk = false;
            targetHit = "Water";
        }
        
	@Override
	public String toString(){
		return "X: " + x + " Y: " + y + " Hit: " + hit + " target: " + targetHit + " sunk: " + sunk;
	}
}



