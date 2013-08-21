package battleship;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LeaderBoard extends JPanel {
	
    private JComboBox box;
    private JButton go;
    private static final String[] list = {"PlayerWL", "FormationWL", "ShipData"};
    private int choice; 
    
    public LeaderBoard() {
        super(new FlowLayout());
        init();
    }

    public void init() {

        box = new JComboBox(list);
       box.addActionListener(new ButtonHandler());
        add(box);
        go = new JButton("GO");
        go.addActionListener(new ButtonHandler());
        add(go);

    }
  
    public class ButtonHandler implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            String newSelection = (String) box.getSelectedItem();
            System.out.println(newSelection);

            if (newSelection == "PlayerWL") {

            	String g = "Game Statistics";
            	Leader playerwl = new Leader();
            	playerwl.playerWinLoss(g);

            } else if (newSelection == "FormationWL") {

            	String l = "Formation Statistics";
            	Leader formation = new Leader();
            	formation.formationWinLoss(l);


                System.out.println("formation slut");
            }

        }
    }
}
