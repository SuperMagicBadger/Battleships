/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;

/**
 *
 * @author Cow
 */
public class RiceCooker {

    //varblok----------------
    //statements--------
    private static String formationGenerStatement = "CREATE TABLE if not exists Formation (FormID, CarrierID INT, BattleshipID INT, DestID INT, SubID INT, MineSweepID  INT);";
    private static String gameGenerStatement = "CREATE TABLE if not exists Game (GameID INT, WinnerID INT, WinnerFormID INT, LoserID INT, LoserFormID  INT); ";
    private static String playerGenerStatement = "CREATE TABLE if not exists Player (PlayerName VARCHAR, PlayerID INT);";
    private static String shipGenerStatement = "CREATE TABLE if not exists Ship (ShipID INT, Pegs INT, Used INT, Sunk INT, posX INT, posY INT, horizontal BOOLEAN);";
    
    private static String formationLossView = "create view if not exists FormationLoss as select PlayerName, PlayerID, FormID, count(FormID) as PWW_Wins from Game join Player on Game.WinnerID = Player.PlayerID join Formation on Game.WinnerFormID = Formation.FormID group by FormID;";
    private static String formationWinView = "create view if not exists FormationWin as select FormID, count(GameId) as WinCount from Formation join Game on Game.WinnerFormID = Formation.FormID group by FormID;";
    private static String playerLossesWithView = "create view if not exists PlayerLoosesWith as select PlayerName, PlayerID, FormID, count(FormID) as PWW_Losses from Game join Player on Game.LoserID = Player.PlayerID join Formation on Game.LoserFormID = Formation.FormID group by FormID;";
    private static String playerWinsWithView = "create view if not exists PlayerWinsWith as select PlayerName, PlayerID, FormID, count(FormID) as PWW_Wins from Game join Player on Game.WinnerID = Player.PlayerID join Formation on Game.WinnerFormID = Formation.FormID group by FormID;";
    private static String playerFormUseView = "create view if not exists PlayerFormationUse as select PlayerWinsWith.PlayerName, PlayerWinsWith.PlayerID, PlayerWinsWith.FormID, PWW_Losses + PWW_Wins from PlayerLoosesWith join PlayerWinsWith on PlayerWinsWith.PlayerName = PlayerLoosesWith.PlayerName and PlayerWinsWith.PlayerID = PlayerLoosesWith.PlayerID and PlayerWinsWith.FormID = PlayerLoosesWith.FormID;";
    private static String playerWins = "create view if not exists PlayerWins as select PlayerName, PlayerID, count(GameID) as WinCount from Player left join Game on Game.WinnerID = Player.PlayerID group by PlayerID;";
    private static String playerLosses = "create view if not exists PlayerLosses as select PlayerName, PlayerID, count(GameID) as LossCount from Player left join Game on Game.LoserID = Player.PlayerID group by PlayerID;";
    private static String playerWL = "create view if not exists PlayerWinLoss as SELECT PlayerName , PlayerID , Wins , Losses FROM ( SELECT PlayerName , PlayerID , count ( WinnerID ) AS Wins FROM Player LEFT JOIN Game ON WinnerID = PlayerID GROUP BY PlayerName ) JOIN ( SELECT PlayerName AS pname2 , PlayerID AS pid2 , count ( LoserID ) AS Losses FROM Player LEFT JOIN Game ON LoserID = PlayerID GROUP BY PlayerName ) ON PlayerID = pid2 ORDER BY PlayerID;";
    
    private static String playerWinsWithShipView = "create view if not exists PlayerWinsWithShips as "
            + "SELECT PlayerName, SID, Pegs, posX, posY, Horizontal "
            + "FROM  (SELECT PlayerName, CarrierID AS SID "
            + "FROM PlayerLoosesWith "
            + "JOIN Formation ON PlayerLoosesWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, BattleshipID AS SID FROM PlayerLoosesWith JOIN Formation ON PlayerLoosesWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, DestID AS SID FROM PlayerLoosesWith JOIN Formation ON PlayerLoosesWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, SubID AS SID FROM PlayerLoosesWith JOIN Formation ON PlayerLoosesWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, MineSweepID AS SID FROM PlayerLoosesWith JOIN Formation ON PlayerLoosesWith.FormID = Formation.FormID) "
            + "join Ship on ShipID = SID;";
    
    private static String playerLoosesWithShipView = "create view if not exists PlayerLoosesWithShips as "
            + "SELECT PlayerName, SID, Pegs, posX, posY, Horizontal "
            + "FROM  (  SELECT PlayerName, CarrierID AS SID "
            + "FROM PlayerWinsWith "
            + "JOIN Formation ON PlayerWinsWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, BattleshipID AS SID FROM PlayerWinsWith JOIN Formation ON PlayerWinsWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, DestID AS SID FROM PlayerWinsWith JOIN Formation ON PlayerWinsWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, SubID AS SID FROM PlayerWinsWith JOIN Formation ON PlayerWinsWith.FormID = Formation.FormID "
            + "UNION SELECT PlayerName, MineSweepID AS SID  FROM PlayerWinsWith JOIN Formation ON PlayerWinsWith.FormID = Formation.FormID ) "
            + "join Ship on ShipID = SID;";
    //db info-----------
    boolean connected;
    Connection con;
    Statement stat;
    ResultSet re;
    //game--------------
    public int gameID;
  
    //players-----------
    public String winnerName;
    public String looserName;
    public int winnerID;
    public int looserID;
    //formatins---------
    public Ship[] winningFormation;
    public int[] winningShipID;
    public int winningFormID;
    public Ship[] loosingFormation;
    public int[] loosingShipID;
    public int loosingFormID;
    
    
    //jamal's variables for leaderboard GUI
    
    private final static String[] gcolumns = {"GameID", "WinnerID", "WinnerFormID", "LoserID", "LoserFormID"};
    private final static String[] fcolumns = {"FormID", "CarrierID", "BattleshipID", "DestID", "SubID", "MineSweepID"};
    private final static String[] pcolumns = {"PlayerName", "PlayerID"};
    private final static String[] scolumns = {"ShipID", "Pegs", "Used", "Sunk", "posX", "posY", "horizontal"};
    private static MyTableModel gdata = new MyTableModel(gcolumns);
    private static MyTableModel fdata = new MyTableModel(fcolumns);
    private static MyTableModel pdata = new MyTableModel(pcolumns);
    private static MyTableModel sdata = new MyTableModel(scolumns);
    //varblok================

    public RiceCooker() {
        connected = false;
        con = null;
        stat = null;
        re = null;
       
    }

    @Override
    public String toString() {
        String s = "";
        s = s + ("Winner: " + winnerName + " " + winnerID + '\n');
        s = s + ("Looser: " + looserName + " " + looserID + '\n');

        s = s + '\n';

        return s;
    }

    //open and close--------------
    public boolean connect(String filename) {
        if (!connected) {
            try {
                Class.forName("org.sqlite.JDBC");
                con = DriverManager.getConnection("jdbc:sqlite:" + filename);
                con.setAutoCommit(false);
                stat = con.createStatement();
                genTables();
                genViews();
                connected = true;
            } catch (SQLException ex) {
                Logger.getLogger(RiceCooker.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                System.err.printf("shit done fucked up, yo.");
            }
        }
        return connected;
    }

    public void dc() throws SQLException {
        winningFormation = loosingFormation = null;
        winnerName = looserName = null;
        winningShipID = loosingShipID = null;
        winningFormID = winnerID = looserID = loosingFormID = 0;
        re = null;
        stat = null;
        con.commit();
        con.close();
        connected = false;
    }
    //open and close==============

    //create tables---------------
    private void genTables() {
        try {
            stat.execute(playerGenerStatement);
            stat.execute(shipGenerStatement);
            stat.execute(formationGenerStatement);
            stat.execute(gameGenerStatement);
        } catch (SQLException ex) {
            System.err.println("dafuq, mate?  Gen'en shit aint workin'" + ex);
        }
    }
    private void genViews(){
        try{
            stat.execute(formationWinView);
            stat.execute(formationLossView);
            stat.execute(playerWins);
            stat.execute(playerLosses);
            stat.execute(playerWinsWithView);
            stat.execute(playerLossesWithView);
            stat.execute(playerFormUseView);
            stat.execute(playerWinsWithShipView);
            stat.execute(playerLoosesWithShipView);
            stat.execute(playerWL);
        } catch (SQLException e){
            System.err.println("dafuq? couldnt gen views\n" + e);
        }
    }
    //create tables===============

    //dup the db------------------
    public boolean checkData() {
        if(winningFormation == null || loosingFormation == null || winnerName.compareTo("") == 0 || looserName.compareTo("") == 0){
            return false;
        }
        winningShipID = new int[]{0, 0, 0, 0, 0};
        loosingShipID = new int[]{0, 0, 0, 0, 0};
        return true;
    }

    public void dump() throws SQLException {
        if (!checkData()) {
            System.err.println("Bad Data.  The fuck'd you give me?");
            return;
        }
        //insert players
        winnerID = dupCleanPlayer(winnerName);
        looserID = dupCleanPlayer(looserName);

        //insert ships
        for (int i = 0; i < winningFormation.length; i++){
            winningShipID[i] = dupShip(winningFormation[i]);
        }
        for(int i = 0; i < loosingFormation.length; i++){
            loosingShipID[i] = dupShip(loosingFormation[i]);
        }
        winningFormID = dupFormation(winningShipID);
        loosingFormID = dupFormation(loosingShipID);
        dupGame();
    }
    //dup the db==================
    //data analysis---------------

    private int dupCleanPlayer(String PlayerName) throws SQLException {
        String qName = "select * from Player where Player.PlayerName = \'" + PlayerName + "\';";
        String qNum = "select max(PlayerID) from Player;";
        int id;

        stat.execute(qName);
        re = stat.getResultSet();
        if (!re.next()) {
            stat.execute(qNum);
            re = stat.getResultSet();
            id = re.getInt("max(PlayerID)") + 1;
            stat.execute("insert into Player values(\'" + PlayerName + "\', " + id + ");");
            
            //jamals code
            pdata.updateptable(PlayerName,id);
            
            System.out.println("inserted " + PlayerName + " " + id);
        } else {
            id = re.getInt("PlayerID");
        }
        System.out.println(PlayerName + " " + id);
        return id;
    }

    private int dupShip(Ship s) throws SQLException {
        //var blok-----------
        int ID;
        //var blok===========
        
        //queries------------
        String findMaxId = "select max(ShipID) from Ship;";
        String findExisting = "select * from Ship "
                + "where Ship.Pegs = " + s.pegs
                + " and Ship.posX = " + s.posX
                + " and Ship.posY = " + s.posY
                + " and Ship.horizontal = \'" + s.horizontal + "\';";
        String update;
        String insert;
        //queries============
        stat.execute(findExisting);
        re = stat.getResultSet();
        if (re.next()) {
            int used = re.getInt("Used") + 1;
            int sunk = re.getInt("Sunk");
            ID = re.getInt("ShipID");
            if (s.isSunk()) {
                sunk = sunk + 1;
            }
            update = "update Ship set Used = " + used + ", Sunk = " + sunk + " where ShipId = " + ID + ";";
            stat.execute(update);
        } else {
            stat.execute(findMaxId);
            re = stat.getResultSet();
            ID = re.getInt("max(ShipID)") + 1;
            int sunk = 0;
            if (s.isSunk()) {
                sunk++;
            }
            insert = "insert into ship values "
                    + "(" + ID + ", " + s.pegs + ", " + 1 + ", " + sunk + "," + s.posX + "," + s.posY + ", \'" + s.horizontal + "\');";
            
            //jamals code
            sdata.updatestable(ID,s.pegs,1,sunk,s.posX,s.posY,s.horizontal);
            
            stat.execute(insert);
        }
        
        return ID;
    }
    
    private int dupFormation(int[] shipIDs) throws SQLException{
        //varblok------------
        int ID = 0;
        //varblok============
        
        //queries------------
        String findDuplicate = "select FormID from Formation where CarrierID = " + shipIDs[0] + 
                " and BattleshipID = " + shipIDs[1] +
                " and DestID = " + shipIDs[2] +
                " and SubID = " + shipIDs[3] +
                " and MineSweepID = " + shipIDs[4] + ";";
        String findMaxID = "select max(FormID) from Formation;";
        String insert;
        //queries============
        
        //find ID------------
        stat.execute(findDuplicate);
        re = stat.getResultSet();
        if(re.next()){
            ID = re.getInt("FormID");
        } else {
            stat.execute(findMaxID);
            ID = stat.getResultSet().getInt("max(FormID)") + 1;
            insert = "insert into Formation values(" + ID + ", " + shipIDs[0] + ", " + shipIDs[1] + ", " + shipIDs[2] + ", " + shipIDs[3] + ", " + shipIDs[4] + ");";
            
            //jamals code
            fdata.updateftable(ID,shipIDs[0],shipIDs[1], shipIDs[2],shipIDs[3],shipIDs[4]);
            
            stat.execute(insert);
        }
        //find ID============
        
        return ID;
    }
    
    private void dupGame() throws SQLException{
        //var blok-----------
        int GameID;
        //var blok===========
        
        //queries------------
        String maxGameID = "select max(GameID) from Game";
        String insert;
        //quaries============
        
        //get max id---------
        stat.execute(maxGameID);
        re = stat.getResultSet();
        GameID = re.getInt(1) + 1;
        //get max id=========
        
        //insert game--------
        insert = "insert into Game values (" + GameID + ", " + winnerID + ", " + winningFormID + ", " + looserID + ", " + loosingFormID + ");";
        
        //jamals code
        gdata.updategtable(GameID,winnerID,winningFormID,looserID,loosingFormID);
        
        stat.execute(insert);
        //insert game========
    }
    //data analysis===============
    
    //run queries-----------------
    public ResultSet runQuerry(String query) throws SQLException{
        stat.execute(query);
        return stat.getResultSet();
    }
    //run queries=================    
    
    /*
     * John's Notes
     * You might want to use this section to fill the tables.
     * Theres a number of views stored to help run queries, just check out the database in sqlite studio or something
     * 
     * might want to use something like bs.dbinterface.runquery(some_sql_query) to get a result set.
     * Then you can cram data into an array using a loop, maybe?  for(i -> max_entries or !resulset.hasNext())
     * 
     * Then you can use pass that to the table model or something and use the getValueAt funtions to return data out of that array
     * I think the table will take care of the rest after that, but im not entirley sure.
     */

    //jamals methods to update jtables
    public JTable updatetable(int t){
    	
    	switch(t){
    	
    	case(0):
    		//update game table
    		return new JTable(gdata);
    		
    	
    	case(1):
    		//update formation table
    		return new JTable(fdata);
    		
    	
    	case(2):
    		//update player table
    		return new JTable(pdata);
    	
    	
    	case(3):
    		//update ship table
    		return new JTable(sdata);
    		
    	}
    	return null;
    }
}

