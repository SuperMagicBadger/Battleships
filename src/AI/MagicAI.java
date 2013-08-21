/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AI;

import battleship.Battleship;
import battleship.Ship;
import battleship.Shot;
import java.util.Random;

/**
 *
 * @author Cow
 *
 * A java implementation of Christopher Stoll's Battleship AI algorithm
 *
 * http://www.christopherstoll.org/2012/06/battleship-ai-algorithm-using-dynamic.html
 *
 */
public class MagicAI implements AI {

    enum direction {

        UP,
        DOWN,
        LEFT,
        RIGHT
    }
    //var blok--------------------
    protected static int numMaxs = 6;
    protected Random rng;
    protected Table table;
    private Shot lastHit;
    private Shot firstHit;
    private boolean reverseD;
    private boolean previousReversal;
    //var blok====================

    //constructor-----------------
    public MagicAI() {
        rng = new Random();
        table = new Table();
    }
    //constructor=================

    //base methods----------------
    @Override
    public Shot shoot() {
        System.out.println("Shooting-------------------");
        if (firstHit == null) {
            System.out.println("searching...");
            table.calulateValues();
            int[] point = table.Select(rng.nextInt(numMaxs) + 1);
            System.out.println(table);
            System.out.println("Shooting===================");
            return new Shot(point[0], point[1]);
        } else {
            System.out.println("follow-up...");
            Shot s = followUp();
            table.Select(s.x, s.y);
            System.out.println("Shooting===================");
            return s;
        }
    }

    @Override
    public void shotFeedback(Shot s) {
        System.out.println("Feedback----------------------------");
        //shot sunk the ship, so reset
        if (s.sunk) {
            System.out.println("sunk, do a reset");
            firstHit = null;
            lastHit = null;
            reverseD = false;
            previousReversal = false;
            return;
        }
        //record hit
        if (s.hit) {
            System.out.print("hit...  ");
            if (firstHit == null) {
                System.out.println("its the first");
                firstHit = s;
                reverseD = false;
            } else {
                System.out.println("updating last hit");
                lastHit = s;
                reverseD = false;
            }
        } else {
            //signal a direction reverse
            System.out.print("miss...  ");
            if (lastHit != null) {
                System.out.println("reversing d");
                reverseD = true;
            } else {
                System.out.println("no reverse");
            }

        }
        System.out.println("End Result--");
        System.out.println(firstHit);
        System.out.println(lastHit);
        System.out.println("End Result==");
        System.out.println("Feedback==============================");
    }

    @Override
    public Ship[] fillShips() {
        Ship[] s = new Ship[Battleship.numShips];
        int j = 0;

        for (int i = 0; i < s.length; i++) {
            s[i] = new Ship(Battleship.shipNames[i], Battleship.shipPegs[i]);
            System.out.println("placing " + s[i].name);
            j = 0;
            do {
                j++;
                s[i].posX = rng.nextInt(Battleship.boardSize - 1);
                s[i].posY = rng.nextInt(Battleship.boardSize - 1);
                s[i].horizontal = rng.nextBoolean();
                System.out.println("attempt: " + j);
            } while (!Battleship.game.addShip(s[i], i));
            System.out.println(s[i].name + " placed: " + s[i]);
        }

        return s;
    }

    public Shot followUp() {
        Shot s = null;
        direction d; //count up for cw -- up, right, down, left

        if(reverseD && previousReversal){
            System.out.println("Second reversal with no hit.  Random Search");
            firstHit = lastHit = null;
            reverseD = false;
            previousReversal = false;
            return shoot();
        }
        
        System.out.println("inited shot");

        //random check
        if (lastHit == null) {
            System.out.println("random follow up");
            s = RandomFollowUp();
        } 
        if (s == null){
            s = new Shot(lastHit.x, lastHit.y);
            System.out.println("continueing the line");
        } else {
            return s;
        }
        

        System.out.println("Getting dir...");
        d = getLineDirection();

        //direction tests
        if (reverseD) {
            System.out.println("reversing d");
            previousReversal = true;
            s.x = firstHit.x;
            s.y = firstHit.y;
            switch (d) {
                case UP:
                    System.out.println("down");
                    d = direction.DOWN;
                    break;
                case RIGHT:
                    System.out.println("left");
                    d = direction.LEFT;
                    break;
                case DOWN:
                    System.out.println("up");
                    d = direction.UP;
                    break;
                case LEFT:
                    System.out.println("right");
                    d = direction.RIGHT;
                    break;
            }
        }

        //continure on
        switch (d) {
            case UP:
                s.y++;
                break;
            case RIGHT:
                s.x++;
                break;
            case DOWN:
                s.y--;
                break;
            case LEFT:
                s.x--;
                break;
        }

        //check for bounds
        if (outOfBounds(s.x, s.y)) {
            reverseD = true;
            System.out.println("oob, reversing d");
            return followUp();
        }

        if (!Battleship.game.checkShotGood(s.x, s.y)) {
            reverseD = true;
            System.out.println("bad shot, reversing d: " + s);
        }
        return s;
    }

    private Shot RandomFollowUp() {
        if (lastHit == null) {
            Shot s = new Shot(firstHit.x, firstHit.y);
            if (!outOfBounds(s.x, s.y + 1) && Battleship.game.checkShotGood(s.x, s.y + 1)) {
                s.y++;
                System.out.println("check up");
            } else if (!outOfBounds(s.x, s.y - 1) && Battleship.game.checkShotGood(s.x, s.y - 1)) {
                s.y--;
                System.out.println("check down");
            } else if (!outOfBounds(s.x + 1, s.y) && Battleship.game.checkShotGood(s.x + 1, s.y)) {
                s.x++;
                System.out.println("check left");
            } else if (!outOfBounds(s.x - 1, s.y) && Battleship.game.checkShotGood(s.x - 1, s.y)) {
                s.x--;
                System.out.println("check right");
            }
            return s;
        }
        System.out.println("isolated hit.  returning to search pattern");
        return null;
    }

    private boolean outOfBounds(int x, int y) {
        return x < 0 || x >= Battleship.boardSize || y < 0 || y >= Battleship.boardSize;
    }

    private direction getLineDirection() {
        //test direction
        direction d = direction.UP;
        if (lastHit.x == firstHit.x) {
            if (lastHit.y > firstHit.y) {
                System.out.println("up...");
                d = direction.UP;
            } else {
                System.out.println("down..");
                d = direction.DOWN;
            }
        } else if (lastHit.y == firstHit.y) {
            if (lastHit.x > firstHit.x) {
                System.out.println("right...");
                d = direction.RIGHT;
            } else {
                System.out.println("left...");
                d = direction.LEFT;
            }
        }
        return d;
    }

    //base methods================
   
}
