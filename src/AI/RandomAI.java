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
 */
public class RandomAI implements AI{
    
    //varblok----------------
    Random rng;
    boolean[] huntingResults;
    //varblok================
    
    public RandomAI(){
        huntingResults = new boolean[Battleship.numShips];
        rng = new Random();
        for(int i = 0; i < huntingResults.length; i++){
            huntingResults[i] = true;
        }
    }
    
    @Override
    public Shot shoot(){
        Shot s = randomSearch();
        return s;
    }
    
    @Override
    public void shotFeedback(Shot s){
        
    }
    
    //serch patterns---------
    private Shot randomSearch(){
        int x, y;
        int i = 0;
        Shot s = new Shot();
        
        do{
            System.out.println("atempt " + ++i);
            x = rng.nextInt(Battleship.boardSize);
            y = rng.nextInt(Battleship.boardSize);
            s.x = x;
            s.y = y;
        }while(!Battleship.game.checkShotGood(x, y));
        return s;
    }
    //search paterns=========

    @Override
    public Ship[] fillShips() {
        Ship[] s = new Ship[Battleship.numShips];
        int j = 0;
        
        for(int i = 0; i < s.length; i++){
            s[i] = new Ship(Battleship.shipNames[i], Battleship.shipPegs[i]);
            System.out.println("placing " + s[i].name);
            j = 0;
            do{
                j++;
               s[i].posX = rng.nextInt(Battleship.boardSize - 1);
               s[i].posY = rng.nextInt(Battleship.boardSize - 1);
               s[i].horizontal = rng.nextBoolean();
               System.out.println("attempt: " + j);
            } while(!Battleship.game.addShip(s[i], i));
            System.out.println(s[i].name + " placed: " + s[i]);
        }
        
        return s;
    }
}
