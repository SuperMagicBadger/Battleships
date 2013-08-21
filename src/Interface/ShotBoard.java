/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import battleship.Battleship;
import battleship.Battleship.state;
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
 *
 * @author cow
 */
public class ShotBoard extends JPanel {

    //varblok--------------------------
    //interface---------
    GridLayout layMan;
    JButton button[][];
    //threading---------
    Thread shotThread;
    //varblok==========================

    //constructor----------------------
    public ShotBoard() {
        //init
        super();
        setBackground(Color.red);
        //create vars
        layMan = new GridLayout();
        button = new JButton[Battleship.boardSize][Battleship.boardSize];
        //setup layout
        layMan.setRows(Battleship.boardSize);
        layMan.setColumns(Battleship.boardSize);
        layMan.setHgap(Battleship.spaceing);
        layMan.setVgap(Battleship.spaceing);
        setLayout(layMan);
        addComponentListener(new focusCon());
        for (int i = 0; i < Battleship.boardSize; i++) {
            for (int j = 0; j < Battleship.boardSize; j++) {
                button[j][i] = new JButton(" ");
                button[j][i].addActionListener(new buttonListener(j, i));
                button[j][i].setEnabled(false);
                add(button[j][i]);
            }
        }
    }
    //constructor======================

    //controll-------------------------
    void flipButtons(boolean enabled, String include) {
        for (int i = 0; i < button.length; i++) {
            for (int j = 0; j < button[i].length; j++) {
                if (include.contains(button[i][j].getText())) {
                    button[i][j].setEnabled(enabled);
                }
            }
        }
    }

    synchronized void displayShot(Shot s) {
        if (s.hit) {
            if (s.sunk) {
                Battleship.single.displayMessage("you sank their " + s.targetHit);
            } else {
                Battleship.single.displayMessage("hit");
            }
            button[s.x][s.y].setText("x");
        } else {
            Battleship.single.displayMessage("miss");
            button[s.x][s.y].setText("o");
        }
        button[s.x][s.y].setEnabled(false);
    }
    //controll=========================

    //helpers--------------------------
    private class focusCon implements ComponentListener {

        @Override
        public void componentResized(ComponentEvent ce) {
        }

        @Override
        public void componentMoved(ComponentEvent ce) {
        }

        @Override
        public void componentShown(ComponentEvent ce) {
            switch (Battleship.gameState) {
                case SHOOT:
                    if (Battleship.MrMagic == null) {
                        flipButtons(true, " ");
                    } else {
                        System.out.println("AI turn");
                        Shot s;
                        do{
                            s = Battleship.MrMagic.shoot();
                        } while(!Battleship.game.checkShotGood(s.x, s.y));
                        shotThread = new Thread(new sendShotWorker(s));
                        shotThread.start();
                        System.out.println(s + " -- done!");
                    }
                    break;
                case RECIEVE:
                    Battleship.single.swapTo(Battleship.hitBoardName);
                    break;
            }
        }

        @Override
        public void componentHidden(ComponentEvent ce) {
            if (shotThread != null) {
                try {
                    shotThread.join();
                    shotThread = null;
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    private class buttonListener implements ActionListener {

        int x, y;

        buttonListener(int _x, int _y) {
            x = _x;
            y = _y;
        }

        //threads here
        @Override
        public void actionPerformed(ActionEvent e) {
            //generate and send shot
            flipButtons(false, " xo");
            Shot s = new Shot(x, y);
            System.out.append("sending...");
            shotThread = new Thread(new sendShotWorker(s));
            shotThread.start();
            updateUI();
        }
    }
    //helpers==========================

    //threads--------------------------
    private class sendShotWorker implements Runnable {

        public Shot s;

        public sendShotWorker(Shot _s) {
            s = _s;
        }

        @Override
        public synchronized void run() {
            Battleship.game.sendShot(s);
            //show on board
            if (s.x != -1) {
                System.out.println("shot at " + s.x + " " + s.y);
                displayShot(s);
                if(Battleship.MrMagic != null){
                    Battleship.MrMagic.shotFeedback(s);
                }
                //wintest
                if (Battleship.game.isWinner()) {
                    Battleship.single.displayMessage("You win, faggot");
                    System.out.println("i win");
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
                        Logger.getLogger(ShotBoard.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    Battleship.gameState = Battleship.state.RECIEVE;
                    Battleship.single.swapTo(Battleship.hitBoardName);
                    System.out.println("sent");
                }
            } else {
                System.out.println("could not send");
                flipButtons(true, " ");
            }
        }
    }
    //threads==========================
}