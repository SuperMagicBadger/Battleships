/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import AI.DBAI;
import AI.MagicAI;
import AI.RandomAI;
import battleship.Battleship;
import battleship.Battleship.state;
import battleship.LeaderBoard;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 *
 * @author cow
 */
public class Setup extends JPanel {
    //varblok--------------------------

    public boolean connected;
    //interface---------
    BoxLayout layMan;
    JLabel titleLabel;
    JButton hostButton;
    JButton joinButton;
    JButton leader;
    ButtonGroup playerSelect;
    JRadioButton Player;
    JRadioButton mrDubai;
    JRadioButton MagicMan;
    JRadioButton fucktard;
    JTextField nameField;
    JTextField hostField;
    
    //varblok==========================
    //constructor----------------------
    public Setup() {
        super();
        connected = false;
        //setup comps
        layMan = new BoxLayout(this, BoxLayout.Y_AXIS);
        titleLabel = new JLabel("Battleships");
        hostButton = new JButton("Host");
        hostButton.addActionListener(new hostListener());
        joinButton = new JButton("Join");
        joinButton.addActionListener(new joinListener());
        nameField = new JTextField("Geny");
        hostField = new JTextField("localhost");
        leader = new JButton("LeaderBoard");
        leader.addActionListener(new LeaderButtonListener());
        //setup player selector
        playerSelect = new ButtonGroup();
        Player = new JRadioButton("Player");
        Player.addActionListener(new PlayerListener());
        Player.setSelected(true);
        mrDubai = new JRadioButton("AI: Data");
        mrDubai.addActionListener(new DbAIistener());
        MagicMan = new JRadioButton("AI: Mr. Magic");
        MagicMan.addActionListener(new MagicManListener());
        fucktard = new JRadioButton("AI: Mr. Random");
        fucktard.addActionListener(new mrRandomListener());
        playerSelect.add(Player);
        playerSelect.add(mrDubai);
        playerSelect.add(MagicMan);
        playerSelect.add(fucktard);
        //setup layout
        setLayout(layMan);
        add(titleLabel);
        add(hostButton);
        add(joinButton);
        add(leader);
        add(Player);
        add(mrDubai);
        add(MagicMan);
        add(fucktard);
        add(nameField);
        add(hostField);
    }
    //constructor======================

    //controll-------------------------
    private class hostListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!connected) {
                try {
                    Battleship.game.setupHost(Battleship.portNum, 0);
                    Battleship.game.playerName = nameField.getText();
                    connected = true;
                    if(mrDubai.isSelected()){
                        Battleship.MrMagic = new DBAI();
                    } else if (MagicMan.isSelected()){
                        Battleship.MrMagic = new MagicAI();
                    } else if(fucktard.isSelected()){
                        Battleship.MrMagic = new RandomAI();
                    } else {
                        Battleship.MrMagic = null;
                    }
                    Battleship.gameState = state.PLACE_SHIPS;
                    ((CardLayout) getParent().getLayout()).show(getParent(), Battleship.hitBoardName);
                } catch (IOException ex) {
                    System.out.println("Could not connect" + ex);
                    connected = false;
                }
            }
        }
    }

    private class joinListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!connected) {
                try {
                    Battleship.game.setupClient(hostField.getText(), Battleship.portNum);
                    Battleship.game.playerName = nameField.getText();
                    connected = true;
                    if(mrDubai.isSelected()){
                        Battleship.MrMagic = new DBAI();
                    } else if (MagicMan.isSelected()){
                        Battleship.MrMagic = new MagicAI();
                    } else if(fucktard.isSelected()){
                        Battleship.MrMagic = new RandomAI();
                    } else {
                        Battleship.MrMagic = null;
                    }
                    Battleship.gameState = state.PLACE_SHIPS;
                    ((CardLayout) getParent().getLayout()).show(getParent(), Battleship.hitBoardName);
                } catch (IOException ex) {
                    Logger.getLogger(Setup.class.getName()).log(Level.SEVERE, null, ex);
                    connected = false;
                }
            }
        }
    }
    

    private class LeaderButtonListener implements ActionListener{
    	
    	public void actionPerformed(ActionEvent ae){
    		
    		
    		LeaderBoard lb = new LeaderBoard();
    		JFrame frame = new JFrame("LeaderBoard");
			frame.setSize(300, 100);
			frame.setLocationRelativeTo(null);
			frame.getContentPane().add(lb);
			frame.setVisible(true);
    		
    }
    }
    
    private class PlayerListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (Player.isSelected()) {
                nameField.setEditable(true);
                nameField.setText("Geny");
            }
        }
    }

    private class DbAIistener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (mrDubai.isSelected()) {
                nameField.setEditable(false);
                nameField.setText("Mr. Dbai");
            }
        }
    }

    private class MagicManListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (MagicMan.isSelected()) {
                nameField.setEditable(false);
                nameField.setText("Mr. Magic");
            }
        }
    }

    private class mrRandomListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent ae) {
            if (fucktard.isSelected()) {
                nameField.setEditable(false);
                nameField.setText("Mr. Random");
            }
        }
    }
    //controll=========================
}
