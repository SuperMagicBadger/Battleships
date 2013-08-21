/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AI;

import battleship.Battleship;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cow
 */
public class DBAI extends MagicAI {

    HeatMap heat;

    public DBAI() {
        rng = new Random();
        try {
            heat = new HeatMap(Battleship.dbInterface, Battleship.game.getPlayerName(true), 1.0f, 1.0f);
            table = new Table(heat);
        } catch (SQLException ex) {
            Logger.getLogger(DBAI.class.getName()).log(Level.SEVERE, null, ex);
            table = new Table();
        }

    }
}
//heat map====================

