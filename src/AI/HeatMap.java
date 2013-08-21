/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AI;

import battleship.RiceCooker;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author Cow
 */
//heat map--------------------
class HeatMap {
    public static void main(String[] args) throws SQLException{
        RiceCooker r = new RiceCooker();
        r.connect("fuckingshitfuck.db");
        HeatMap heat = new HeatMap(r, "Mr. Magic");
        System.out.println(heat);
    }
    
    //var blok-----------
    RiceCooker rc;
    float[][] blocks;
    private float winWeight;
    private float lossWeight;
    //var blok===========

    //generation---------
    public HeatMap(RiceCooker db, String playerName) throws SQLException {
        this(db, playerName, 1.0f, 0.8f);
    }
    
    public HeatMap(RiceCooker db, String playerName, float winW, float lossW) throws SQLException {
        winWeight = winW;
        lossWeight = lossW;
        rc = db;
        blocks = new float[battleship.Battleship.boardSize][battleship.Battleship.boardSize];
        genWinPositions(playerName);
        System.out.println("Finished Wins");
        genLossPositions(playerName);
        System.out.println("Finished Losses");
        System.out.println("heatmap : \n" + this);
        factor();
    }

    private void genWinPositions(String playerName) throws SQLException {
        ResultSet r = rc.runQuerry("select * from PlayerWinsWithShips where PlayerName = \'" + playerName + "\';");
        
        while (r.next()) {
            int x = r.getInt("posX");
            int y = r.getInt("posY");
            int pegs = r.getInt("Pegs");
            boolean horizontal = r.getString("horizontal").matches("true");
            System.out.print(r.getString("PlayerName") + ' ');
            place(x, y, pegs, horizontal, winWeight);
        }
    }

    private void genLossPositions(String playerName) throws SQLException {
        ResultSet r = rc.runQuerry("select * from PlayerLoosesWithShips where PlayerName = \'" + playerName + "\';");
        while (r.next()) {
            int x = r.getInt("posX");
            int y = r.getInt("posY");
            int pegs = r.getInt("Pegs");
            boolean horizontal = r.getString("horizontal").matches("true");
            System.out.print(r.getString("PlayerName") + ' ' + r.getInt("SID") + ' ');
            place(x, y, pegs, horizontal, lossWeight);
        }
    }

    private void place(int x, int y, int pegs, boolean horiz, float score) {
        System.out.println(x + " " + y + " " + pegs + " " + horiz + " " + score);
        for (int i = 0; i < pegs; i++) {
            if (horiz) {
                blocks[x + i][y] += score;
            } else {
                blocks[x][y + 1] += score;
            }
        }
    }
    
    private void factor(){
        float max = 0;
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0 ; j < blocks[i].length; j++){
                if(blocks[i][j] > max){
                    max = blocks[i][j];
                }
            }
        }
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0 ; j < blocks[i].length; j++){
                blocks[i][j] /= max;
            }
        }
        
    }
    
    public float valueAt(int x, int y){
        return blocks[x][y];
    }
    
    public String toString(){
        String s = new String();
        for(int i = 0; i < blocks.length; i++){
            for(int j = 0; j < blocks.length; j++){
                s = s + String.format("%.2f", blocks[j][i]);
                if(blocks[j][i] > 0.5){
                    s = s + '-';
                } else {
                    s = s + ' ';
                }
            }
            s = s + '\n';
        }
        return s;
    }
}
//generation=========

