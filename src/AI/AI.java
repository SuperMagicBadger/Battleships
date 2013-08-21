/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AI;

import battleship.Ship;
import battleship.Shot;

/**
 *
 * @author Cow
 */
public interface AI {
    public Shot shoot();
    public void shotFeedback(Shot s);
    public Ship[] fillShips();
}
