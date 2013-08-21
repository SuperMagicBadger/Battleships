/*
 * a bit of vocablary for you:
 * your board, where you place you ships and record the enemy's shots is the hitboard
 * the board where you record youre own shots is the shotboard.
 */
package battleship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author cow
 */
public class Board {
    //varblok--------------------------
    //db buffer items--------

    public String playerName;
    private int playerID;
    private String oponentName;
    private Ship formation[];
    //private Board oposingBoard;
    //shot boards------------	
    private char shootBoard[][];
    private char hitBoard[][];
    private int shipbuffer[][];
    private int hitcount;
    //stream controll--------
    private boolean Host;
    private ServerSocket server = null;
    private Socket client = null;
    //data com streams-------
    private DataOutputStream os;
    private DataInputStream is;
    //varblok==========================

    public Board() {
        init();
    }

    public void init() {

        playerName = "geny";
        playerID = 1;
        formation = new Ship[Battleship.numShips];
        shootBoard = new char[Battleship.boardSize][Battleship.boardSize];
        hitBoard = new char[Battleship.boardSize][Battleship.boardSize];
        shipbuffer = new int[Battleship.boardSize][Battleship.boardSize];

        hitcount = 0;

        for (int i = 0; i < Battleship.boardSize; i++) {
            for (int j = 0; j < Battleship.boardSize; j++) {
                shootBoard[i][j] = shootBoard[i][j] = ' ';
                shipbuffer[i][j] = -1;
            }
        }
    }

    @Override
    public String toString() {
        String s = new String();
        for (int i = 0; i < shipbuffer.length; i++) {
            for (int j = 0; j < shipbuffer[i].length; j++) {
                s = s + shipbuffer[i][j];
            }
            s = s + '\n';
        }
        return null;
    }

    public boolean isWinner() {
        int sum = 0;
        for (int i = 0; i < Battleship.numShips; i++) {
            sum += Battleship.shipPegs[i];
        }
        System.out.println("hitcout: " + hitcount + " to win: " + sum);
        return hitcount >= sum;
    }

    public boolean isLoser() {
        boolean test = true;
        for (int i = 0; i < formation.length; i++) {
            if (!formation[i].isSunk()) {
                test = false;
                break;
            }
        }
        return test;
    }

    //handle shots-----------------------------------------
    public void sendShot(Shot s) {
        try {
            os.writeInt(s.x);
            os.writeInt(s.y);
            s.hit = is.readBoolean();
            s.sunk = is.readBoolean();
            if (s.hit) {
                hitcount++;
                shootBoard[s.x][s.y] = 'x';
                s.targetHit = is.readUTF();
            } else {
                shootBoard[s.x][s.y] = 'o';
            }
        } catch (IOException ex) {
            s.hit = false;
            s.x = s.y = -1;
        }
    }

    public void sendShot(int x, int y) throws IOException {
        Shot s = new Shot();
        s.x = x;
        s.y = y;
        sendShot(s);
    }

    public Shot recieveShot() {
        try {
            //varblok--------
            Shot s = new Shot();
            int shipHit = 0;
            //varblok========

            //build the shot-
            s.x = is.readInt();
            s.y = is.readInt();
            System.out.println("shot recieved");
            s.hit = false;

            //test for a hit-
            for (int i = 0; i < formation.length; i++) {
                if (formation[i].recordHit(s)) {
                    System.out.println("hit recorded");
                    s.hit = true; // shit... haha...
                    shipHit = i;
                    break;
                }
            }

            //espond----------
            os.writeBoolean(s.hit);
            os.writeBoolean(s.sunk);
            if (s.hit) {
                hitBoard[s.x][s.y] = 'x';
                //return what ship was hit
                os.writeUTF(Battleship.shipNames[shipHit]);
            } else {
                hitBoard[s.x][s.y] = 'o';
            }
            return s;
        } catch (IOException ex) {
            Logger.getLogger(Board.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    public boolean done() {
        return false;
    }

    public boolean checkShotGood(int x, int y) {
        if (shootBoard[x][y] == ' ') {
            return true;
        }
        return false;
    }

    //handle shots========================================= 
    //db dump tools----------------------------------------
    public String getPlayerName(boolean oponent) {
        if (oponent) {
            return oponentName;
        } else {
            return playerName;
        }
    }

    public void getShots() {
    }

    public void dumpBoard() {
        if (isWinner()) {
            System.out.println("Recording....");
            recordWinner();
            System.out.println("winner recoreded");
            recordLooser();
            System.out.println("loser recoreded");
            System.out.println(Battleship.dbInterface);
            try {
                Battleship.dbInterface.dump();
                Battleship.dbInterface.dc();
            } catch (SQLException e) {
                System.err.println("Could not dump\n" + e + '\n' + e.getSQLState());
            }
            System.out.println("Done.");
        } else {
            System.out.println("Sending....");
            sendBoard();
            System.out.println("Done.");
        }
    }

    private void recordWinner() {
        RiceCooker db = Battleship.dbInterface;

        //set name
        db.winnerName = playerName;
        db.winnerID = playerID;

        //set formation
        db.winningFormation = formation;

    }

    private void recordLooser() {
        RiceCooker db = Battleship.dbInterface;

        try {
            //set name
            db.looserName = is.readUTF();
            System.out.print("- " + db.looserName + " -");
            db.looserID = is.readInt();
            System.out.println("- " + db.looserID + " -");
            int numEnShips = is.readInt();
            System.out.println("num en ships: " + numEnShips);
            db.loosingFormation = new Ship[Battleship.numShips];
            for (int i = 0; i < db.loosingFormation.length; i++) {
                db.loosingFormation[i] = new Ship(is);
            }
        } catch (IOException e) {
            System.err.println(e + "Shitfuck!");
        }
    }

    private void sendBoard() {
        try {
            os.writeUTF(playerName);
            os.writeInt(playerID);
            os.writeInt(Battleship.numShips);
            for (Ship s : formation) {
                s.writeShip(os);
            }
        } catch (IOException e) {
            System.err.println("wtf... e");
        }
    }
    //db dump tools========================================

    //formation--------------------------------------------
    public boolean addShip(Ship s, int num) {
        for (int i = 0; i < num; i++) {
            if (s.intersects(formation[i])) {
                System.out.println("intersects");
                return false;
            }
        }
        //tests the head, then the tail
        if (s.posY >= 0 && s.posY < Battleship.boardSize && s.posX >= 0 && s.posX < Battleship.boardSize) {
            if (s.horizontal) {
                if (s.posX + s.pegs - 1 < Battleship.boardSize) {
                    formation[num] = s;
                    return true;
                } else {
                    System.out.println("is it here?");
                }
            } else {
                if (s.posY + s.pegs - 1 < Battleship.boardSize) {
                    formation[num] = s;
                    return true;
                } else {
                    System.out.println("or here?");
                }
            }
        } else {
            System.out.println("Head out of bounds");
            return false;
        }
        System.out.println("Tail out of bounds");
        return false;
    }
    //formation============================================

    //netwroking methods-----------------------------------
    public boolean isHost() {
        return Host;
    }

    public void setupHost(int port, int timeoutSecs) throws IOException {
        try {
            Host = true;
            server = new ServerSocket(port);
            System.out.println(server.getInetAddress());
            System.out.println(server.getLocalSocketAddress());
            System.out.println(server);
            server.setSoTimeout(timeoutSecs);
            client = server.accept();
            createStreams();
            os.writeUTF(playerName);
            oponentName = is.readUTF();
        } catch (IOException e) {
            System.out.println(e + "\n could not create connection\n");
            System.exit(-1);
        }
    }

    public void setupClient(String host, int port) throws IOException {
        try {
            client = new Socket(host, port);
            createStreams();
            oponentName = is.readUTF();
            os.writeUTF(playerName);
        } catch (UnknownHostException ex) {
            System.out.println(ex + "\n could not find host\n");
            System.exit(-1);
        } catch (IOException ex) {
            System.out.println(ex + "\n io exception");
            System.exit(-1);
        }
    }

    private void createStreams() throws IOException {
        os = new DataOutputStream(client.getOutputStream());
        is = new DataInputStream(client.getInputStream());
    }

    public void close() {
        try {
            if (server != null) {
                server.close();
            }
            client.close();
            init();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
    //networking methods===================================
}
