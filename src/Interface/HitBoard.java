/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 * 
 */
package Interface;

import battleship.Battleship;
import battleship.Battleship.state;
import battleship.Ship;
import battleship.Shot;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;

/**
 * @author cow
 *
 * This board keeps track of the players formation, and where the enemy has
 * shot. It also handles the placement of ships. Threading is used to keep the
 * network calls from blocking as well as keep the panels from switching
 * imediatly, so the player can see if the shot connected.
 *
 */
public class HitBoard extends JPanel {

    //varblok--------------------------
    private int shipToPlace;
    private int startx, starty;
    private int endx, endy;
    //interface---------
    private GridLayout layMan;
    private JButton squares[][];
    //threading---------
    private Thread hitThread;
    //varblok==========================

    //constructor----------------------
    public HitBoard() {
        super();
        shipToPlace = 0;
        setBackground(Color.blue);
        //setup components
        layMan = new GridLayout();
        squares = new JButton[Battleship.boardSize][Battleship.boardSize];
        //setup layout
        layMan.setRows(Battleship.boardSize);
        layMan.setColumns(Battleship.boardSize);
        layMan.setHgap(Battleship.spaceing);
        layMan.setVgap(Battleship.spaceing);

        setLayout(layMan);
        addComponentListener(new focusCont());

        for (int i = 0; i < Battleship.boardSize; i++) {
            for (int j = 0; j < Battleship.boardSize; j++) {
                squares[j][i] = new JButton(" ");
                squares[j][i].addActionListener(new buttonListener(j, i));
                squares[j][i].setEnabled(false);
                add(squares[j][i]);
            }
        }

    }
    //constructor======================

    //controll-------------------------
    private class focusCont implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            switch (Battleship.gameState) {
                case PLACE_SHIPS:
                    placeShips();
                    break;
                case SHOOT:
                    Battleship.single.swapTo(Battleship.shotBoardName);
                case RECIEVE:
                    prepareToRecieve();
                    break;
            }
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
            if (hitThread != null) {
                try {
                    hitThread.join();
                    hitThread = null;
                } catch (InterruptedException ex) {
                    System.err.println(ex);
                }
            }
        }
    }

    private synchronized void prepareToRecieve() {
        hitThread = new Thread(new recieveShotWorker());
        hitThread.start();
    }

    private synchronized void onRecieve(Shot s) {
        System.out.println("recieveing");
        if (s != null) {
            if (s.hit) {
                squares[s.x][s.y].setText("x");
            } else {
                squares[s.x][s.y].setText("o");
            }
        } else {
            System.out.print("recieved a null");
        }
    }

    private void flipButtons(boolean state) {
        for (int i = 0; i < squares.length; i++) {
            for (int j = 0; j < squares[i].length; j++) {
                squares[i][j].setEnabled(state);
            }
        }
    }
    //controll=========================

    //fleet setup----------------------
    public void placeShips() {
        if (Battleship.MrMagic == null) {
            shipToPlace = 0;
            startx = starty = -1;
            for (int i = 0; i < Battleship.boardSize; i++) {
                for (int j = 0; j < Battleship.boardSize; j++) {
                    squares[i][j].setEnabled(true);
                    squares[i][j].setText(" ");
                }
            }
            Battleship.single.displayMessage("Place the " + Battleship.shipNames[shipToPlace]);
        } else {
            int[] p;
            Ship[] s = Battleship.MrMagic.fillShips();
            System.err.println(s.length);

            for (int i = 0; i < s.length; i++) {
                for (int j = 0; j < s[i].pegs; j++) {
                    p = s[i].pegPos(j);
                    if (p.length < 2) {
                        System.err.println("Bad p");
                    }
                    System.out.println(p[0] + ", " + p[1]);
                    squares[p[0]][p[1]].setText("" + Battleship.shipMarker[i]);
                }
            }
            finalizeSetup();
        }
    }

    private class buttonListener implements ActionListener {

        //varblok-------
        int x, y;
        //varblok=======

        buttonListener(int _x, int _y) {
            x = _x;
            y = _y;
        }

        @Override
        public void actionPerformed(ActionEvent ae) {
            System.out.println("button: " + x + " " + y);
            if (startx == -1) {
                startPlaceing();
            } else {
                finishPlaceing();
            }
        }

        private void startPlaceing() {

            //init position
            startx = x;
            starty = y;

            //kill all buttuns
            flipButtons(false);

            //reenable valid placements
            if (x - Battleship.shipPegs[shipToPlace] + 1 >= 0) {
                squares[x - Battleship.shipPegs[shipToPlace] + 1][y].setEnabled(true);
            }

            if (x + Battleship.shipPegs[shipToPlace] <= Battleship.boardSize) {
                squares[x + Battleship.shipPegs[shipToPlace] - 1][y].setEnabled(true);
            }

            if (y - Battleship.shipPegs[shipToPlace] + 1 >= 0) {
                squares[x][y - Battleship.shipPegs[shipToPlace] + 1].setEnabled(true);
            }

            if (y + Battleship.shipPegs[shipToPlace] <= Battleship.boardSize) {
                squares[x][y + Battleship.shipPegs[shipToPlace] - 1].setEnabled(true);
            }
        }

        private void finishPlaceing() {
            Ship s = new Ship(Battleship.shipNames[shipToPlace], Battleship.shipPegs[shipToPlace]);

            //set endings
            endx = x;
            endy = y;

            //test for order
            if (startx == endx) {
                s.horizontal = false;
                System.out.println("Im Vert!");
                if (endy < starty) {
                    System.out.println("reversing");
                    int temp = starty;
                    starty = endy;
                    endy = temp;
                }
            } else if (starty == endy) {
                s.horizontal = true;
                System.out.println("Im Horiz");
                if (endx < startx) {
                    System.out.println("reversing");
                    int temp = startx;
                    startx = endx;
                    endx = temp;
                }
            }

            //create ship
            s.pegs = Battleship.shipPegs[shipToPlace];
            s.posX = startx;
            s.posY = starty;

            if (Battleship.game.addShip(s, shipToPlace)) {
                System.out.println("added succesfully");
                //mark  interface
                for (int i = 0; i < s.pegs; i++) {
                    if (s.horizontal) {
                        squares[startx + i][starty].setText("" + Battleship.shipMarker[shipToPlace]);
                    } else {
                        squares[startx][starty + i].setText("" + Battleship.shipMarker[shipToPlace]);
                    }
                }
                shipToPlace++;
            } else {
                System.out.println("Could not add...");
            }

            if (shipToPlace < Battleship.numShips) {
                resetForNextShip();
                Battleship.single.displayMessage("Place the " + Battleship.shipNames[shipToPlace]);
            } else {
                finalizeSetup();
            }

        }

        void resetForNextShip() {
            startx = starty = -1;
            for (int i = 0; i < Battleship.boardSize; i++) {
                for (int j = 0; j < Battleship.boardSize; j++) {
                    if (squares[i][j].getText().compareTo(" ") == 0) {
                        squares[i][j].setEnabled(true);
                    } else {
                        squares[i][j].setEnabled(false);
                    }
                }
            }
        }
    }

    private void finalizeSetup() {
        flipButtons(false);
        Battleship.single.displayMessage("Lets do this!");
        if (Battleship.game.isHost()) {
            Battleship.gameState = Battleship.state.SHOOT;
            System.out.println("changing to shoot");
            Battleship.single.swapTo(Battleship.shotBoardName);
        } else {
            Battleship.gameState = Battleship.state.RECIEVE;
            System.out.println("changing to hit");
            prepareToRecieve();
        }
    }
    //fleet setup======================

    //threads--------------------------
    private class recieveShotWorker implements Runnable {

        public recieveShotWorker() {
        }

        @Override
        public synchronized void run() {
            Shot s = Battleship.game.recieveShot();
            onRecieve(s);
            //win test
            if (Battleship.game.isLoser()) {
                Battleship.single.displayMessage("You lose, faggot!");
                System.out.println("i lose");
                Battleship.game.dumpBoard();
                    if (Battleship.egcmd == Battleship.endGameCommand.QUIT) {
                        System.exit(0);
                    } else if(Battleship.egcmd == Battleship.endGameCommand.SETUP_NEW){
                        Battleship.single.reset();
                    }
            } else {
                try {
                    wait(Battleship.transitionTimeMS);
                } catch (InterruptedException ex) {
                    Logger.getLogger(HitBoard.class.getName()).log(Level.SEVERE, null, ex);
                }
                System.out.println("swapin to shoot");
                Battleship.gameState = Battleship.state.SHOOT;
                Battleship.single.swapTo(Battleship.shotBoardName);
            }
        }
    }
    //threads==========================
}