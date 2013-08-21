package battleship;

import AI.AI;
import Interface.HitBoard;
import Interface.Setup;
import Interface.ShotBoard;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author cow
 *
 * This is the code to launch the main window and keep track of various
 * constants and globals.
 *
 * update this as bugs are found or new features thought of
 *
 * TODO - add a textbox so that messages can be displayed (eg, miss, hit, sunk a
 * battleship, etc) +shits done, yo. - shots are not recording hits correctly +
 * fixed... just needs a bit more testing - when adding the ships, the
 * intersection test tests one too far to the right. + fixed the block for 1 too
 * far, check ship.isHit + now there's an error, where a ship can straddle the
 * middle if place perpendicular + i think the fix in "isHit" fixed the problem
 * here. needs testing though + added a fix where ships could not lie up against
 * the far right or bottom edge - no end-game test + one way to do this would be
 * to track the total number of hits in the board and return a win/loss when one
 * side has hit the max. +one way to do this would be to query all the ships in
 * board, asking them if they're drunk. + theres a working end-game condition
 * that uses both aproaches. uses a ships sunk approach to test loss and a hit
 * count to test a win - no way to dump to a database + started adding helper
 * functions to board + need a "dump game" function to database that takes two
 * boards - no end-game screen + just kind of froze the boards in place and
 * added a message - no leader-board - add an effect to hit board so that the
 * player can easily track where shots have been placed.
 *
 *
 */
public class Battleship extends JPanel {
    //main-----------------------------

    public static void main(String[] args) throws IOException {
        //varblok-----------------
        Battleship bs = new Battleship();
        JFrame frame = new JFrame();
        //varblok=================
        frame.add(bs);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setVisible(true);
        bs.go();
    }
    //main=============================

    public enum state {

        NONE,
        PLACE_SHIPS,
        SHOOT,
        RECIEVE
    };

    public enum endGameCommand {

        NONE,
        QUIT,
        SETUP_NEW
    };
    //var blok-------------------------
    //constants---------
    public static final String databaseFile = "battleshipdata.db";
    public static final int transitionTimeMS = 20;
    public static final int numShips = 5;
    public static final int spaceing = 2;
    public static final int boardSize = 10;
    public static final int portNum = 9999;
    public static final String shipNames[] = {"Carrier", "Battleship", "Destroyer", "Submarine", "Mine Sweeper"};
    public static final char shipMarker[] = {'C', 'B', 'D', 'S', 'M'};
    public static final int shipPegs[] = {5, 4, 3, 3, 2};
    public static final String setupScreenName = "setup";
    public static final String shotBoardName = "shot";
    public static final String hitBoardName = "hit";
    //globals-----------
    public static endGameCommand egcmd = endGameCommand.SETUP_NEW;
    public static Battleship single;
    public static Board game;
    public static state gameState;
    public static RiceCooker dbInterface;
    public static AI MrMagic;
    //user gui----------
    private CardLayout layMan;
    private Setup setupScreen;
    private ShotBoard shotBoard;
    private HitBoard hitBoard;
    private JLabel message;
    //var blok=========================

    //constructor----------------------
    Battleship() {
        super();
        game = new Board();
        dbInterface = new RiceCooker();
        Battleship.dbInterface.connect(databaseFile);
        gameState = state.NONE;

        //setup comps
        layMan = new CardLayout();
        setupScreen = new Setup();
        shotBoard = new ShotBoard();
        hitBoard = new HitBoard();
        message = new JLabel();

        //setup layout
        setLayout(layMan);
        add(setupScreen, setupScreenName);
        add(shotBoard, shotBoardName);
        add(hitBoard, hitBoardName);
    }
    //constructor======================

    //controll-------------------------
    public int go() throws IOException {
        single = this;
        swapTo(setupScreenName);
        getParent().add(message, BorderLayout.SOUTH);
        setVisible(true);

        displayMessage("Lets play some battleship, mothafukas!");

        return 0;
    }

    public void swapTo(String panelName) {
        System.out.println("swapping screen to " + panelName);
        ((CardLayout) (getLayout())).show(this, panelName);
        System.out.println("done swapping");
    }

    public void displayMessage(String str) {
        message.setText(str);
    }

    public void reset() {
        setupScreen.connected = false;
        game.close();
        gameState = state.NONE;
        swapTo(Battleship.setupScreenName);
    }
    //controll=========================
}
