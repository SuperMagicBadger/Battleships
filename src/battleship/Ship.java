/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author cow
 */
public class Ship {
    
    //varlok----------------------
    public String name;
    public int posX, posY;
    public boolean horizontal;
    public int pegs;
    public boolean[] hits;
    //varblok=====================

    public Ship(String name, int pegs) {
        this.name = name;
        this.pegs = pegs;
        hits = new boolean[pegs];
        for(int i = 0; i < pegs; i++){
            hits[i] = false;
        }
    }

    public Ship(DataInputStream is) throws IOException {
        name = is.readUTF();
        System.out.println("name: " + name);
        pegs = is.readInt();
        System.out.println("pegs " + pegs);
        posX = is.readInt();
        System.out.print("pos: " + posX);
        posY = is.readInt();
        System.out.println(", " + posY);
        horizontal = is.readBoolean();
        System.out.println("horiz: " + horizontal);
        hits = new boolean[pegs];
        for(int i = 0; i < pegs; i++){
            hits[i] = is.readBoolean();
            System.out.println(hits[i]);
        }
    }
    
    @Override
    public String toString(){
        String s = name + " " + pegs + " ";
        s = s + "Pos: " + posX + ", " + posY + " ";
        
        return s;
    }

    //hit tests-------------------
    // these are just used to test
    // hits, not record them. if you
    // want it recorded as well, call
    // recordHit instead
    //----------------------------    
    public boolean isHit(Shot s) {
        s.hit = false;
        s.sunk = false;
        if (isHit(s.x, s.y)) {
            s.hit = true;
        }
        return s.hit;
    }

    public boolean isHit(int x, int y) {
        //initial testing--------------
        if (!testXRange(x) || !testYRange(y)) {
            return false;
        }
        //initial testing==============

        //Bute test--------------------
        // not a very brutish test 
        // aymore...
        //-----------------------------
        if (horizontal) {
            if (posY == y) {
                return true;
            }
        } else {
            System.out.print("mathcing x's on a vert target... ");
            if (posX == x) {
                return true;
            }
        }
        //Brute test===================
        return false;
    }

    public boolean recordHit(Shot s) {
        boolean hit = isHit(s);
        if (hit) {
            hits[pegPos(s.x, s.y)] = isHit(s);
            s.sunk = isSunk();
        }
        return s.hit;
    }

    public boolean recordHit(int x, int y) {
        if (isHit(x, y)) {
            hits[pegPos(x, y)] = true;
            printHit();
            return true;
        }
        return false;
    }

    public boolean isSunk() {
        boolean sunk = true;
        for (int i = 0; i < hits.length; i++) {
            if (!hits[i]) {
                sunk = false;
                System.out.println("aint sunk");
                break;
            }
        }
        return sunk;
    }

    //pointless helper tests
    private boolean testXRange(int x) {
        return (x >= posX) && (x < posX + pegs);
    }

    private boolean testYRange(int y) {
        return (y >= posY) && (y < posY + pegs);
    }

    public void printHit() {
        if (isSunk()) {
            Battleship.single.displayMessage("You sunk his " + name);
        } else {
            Battleship.single.displayMessage("You hit his " + name);
        }

    }

    //hit tests===================
    public int[] pegPos(int i) {
        int pos[] = new int[]{posX, posY};
        if (horizontal) {
            pos[0] += i;
        } else {
            pos[1] += i;
        }
        return pos;
    }

    public int pegPos(int x, int y) {
        int i = -1;
        if (testXRange(x) && testYRange(y)) {
            if (horizontal) {
                i = x - posX;
            } else {
                i = y - posY;
            }
        }
        return i;
    }

    public boolean intersects(Ship s) {
        for (int i = 0; i < pegs; i++) {
            int pos[] = pegPos(i);
            if (s.isHit(pos[0], pos[1])) {
                System.out.println("this ship intersects");
                return true;
            }
        }
        System.out.println("this ship does not intersect");
        return false;
    }

    public void writeShip(DataOutputStream os) throws IOException {
        os.writeUTF(name);
        System.out.println("name: " + name);
        os.writeInt(pegs);
        System.out.println("pegs: " + pegs);
        os.writeInt(posX);
        System.out.print("pos: " + posX);
        os.writeInt(posY);
        System.out.println(", " + posY);
        os.writeBoolean(horizontal);
        System.out.println("horiz: " + horizontal);
        for (boolean s : hits) {
            os.writeBoolean(s);
        }
    }
}
