/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AI;

import java.util.Arrays;
import java.util.PriorityQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Cow
 */
//tables----------------------
class Table {
    //var blok-----------

    private HeatMap heat;
    private Cell[][] table;
    PriorityQueue<Cell> queue;
    //var blok===========

    public Table() {
        table = new Cell[battleship.Battleship.boardSize][battleship.Battleship.boardSize];
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table[i].length; j++) {
                table[i][j] = new Cell(i, j);
            }
        }
        queue = new PriorityQueue<Cell>();
    }

    public Table(HeatMap h) {
        this();
        heat = h;
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                    s += this.peek(j, i) + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].c1 + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].c2 + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].tb + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].bt + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].rl + " ";
            }
            s += '\n';
        }
        s += "\n\n";
        for (int i = 0; i < table.length; i++) {
            for (int j = 0; j < table.length; j++) {
                s += table[j][i].lr + " ";
            }
            s += '\n';
        }
        return s;
    }

    public void calulateValues() {
        try {
            Thread t1 = new Thread(new VertTracer());
            Thread t2 = new Thread(new HorzTracer());

            t1.start();
            t2.start();

            t1.join();
            t2.join();

            for (int i = 0; i < table.length; i++) {
                for (int j = 0; j < table.length; j++) {
                    table[i][j].calcC1();
                    table[i][j].calcC2();
                    if (heat == null) {
                        table[i][j].getValue();
                    } else {
                        table[i][j].getValue(heat);
                    }
                }
            }

            for (int i = 0; i < table.length; i++) {
                queue.addAll(Arrays.asList(table[i]));
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(MagicAI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public int[] Select(int depth) {
        int[] test = peek(depth);
        Select(test[0], test[1]);
        return test;
    }

    public int[] peek(int depth) {
        System.out.println("depth: " + depth);
        int[] point = new int[2];
        Cell t = null;
        for (int i = 0; i < depth && i < queue.size(); i++) {
            t = queue.poll();
            point[0] = t.x;
            point[1] = t.y;
        }
        return point;

    }

    public void Select(int x, int y) {
        table[x][y].stop();
    }

    public float peek(int x, int y) {
        if(heat != null){
            return table[x][y].getValue(heat);
        } else {
            return table[x][y].getValue();
        }
    }
    //threading-------------------

    private class VertTracer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < table.length; i++) {
                for (int j = 1; j < table.length; j++) {
                    table[i][j].tb = table[i][j - 1].nextTB();
                }
                for (int j = table.length - 2; j >= 0; j--) {
                    table[i][j].bt = table[i][j + 1].nextBT();
                }
            }
        }
    }

    private class HorzTracer implements Runnable {

        @Override
        public void run() {
            for (int i = 0; i < table.length; i++) {
                for (int j = 1; j < table.length; j++) {
                    table[j][i].lr = table[j - 1][i].nextLR();
                }
                for (int j = table.length - 2; j >= 0; j--) {
                    table[j][i].rl = table[j + 1][i].nextRL();
                }
            }
        }
    }
    //threading===================

    class Cell implements Comparable<Cell> {
        //var blok-----------

        public int x, y;
        public int tb, bt;
        public int lr, rl;
        public int c1, c2;
        public int value;
        public boolean hardSet;
        //var blok===========

        public Cell(int _x, int _y) {
            x = _x;
            y = _y;
            tb = 1;
            bt = 1;
            rl = 1;
            lr = 1;
            c1 = 1;
            c2 = 1;
            value = 1;
            hardSet = false;
        }

        public void calcC1() {
            c1 = (tb + bt) - Math.abs(tb - bt);
        }

        public void calcC2() {
            c2 = (rl + lr) - Math.abs(rl - lr);
        }

        public int nextTB() {
            if (!hardSet) {
                return tb + 1;
            }
            return 1;
        }

        public int nextBT() {
            if (!hardSet) {
                return bt + 1;
            }
            return 1;
        }

        public int nextRL() {
            if (!hardSet) {
                return rl + 1;
            }
            return 1;
        }

        public int nextLR() {
            if (!hardSet) {
                return lr + 1;
            }
            return 1;
        }

        public void stop() {
            tb = bt = lr = rl = c1 = c2 = value = 0;
            hardSet = true;
        }

        public float getValue() {
            if (!hardSet) {
                calcC1();
                calcC2();
                value = c1 * c2;
                return value;
            }
            return 0;
        }

        public float getValue(HeatMap m) {
            System.out.println(m.blocks[x][y]);
            return getValue() * m.blocks[x][y];
        }

        @Override
        public int compareTo(Cell t) {
            return t.value - value;
        }
    }
}
    //tables======================
