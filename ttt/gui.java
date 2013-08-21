import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class gui extends JPanel {

	work board;
	boolean xturn = true;
	
	public gui() {
		
		super(new GridBagLayout());
		board = new work();
		setSize(500, 500);
		init();
	}

	public void init() {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				
				GridBagConstraints c = new GridBagConstraints();
				JButton button = new JButton();
				button.addActionListener(new buttonlistener(i, j));
				button.setSize(50, 50);
				c.weightx = 0.5;
				c.ipady = 200;
				c.gridx = i;
				c.gridy = j;
				c.fill = GridBagConstraints.BOTH;
				add(button, c);
			}
		}
	}
	

	private class buttonlistener implements ActionListener {

		int _x;
		int _y;
		
		public buttonlistener(int x, int y) {
		
			_x = x;
			_y = y;
		}

		public void actionPerformed(ActionEvent e) {
			
			 JButton bx = (JButton) e.getSource();
			if(xturn == true){
			board.changevalue(_x, _y, true);
			bx.setLabel("X");
			xturn = false;
			board.iswinner();
			}
			else if(xturn == false){
				board.changevalue(_x, _y, false);
				bx.setLabel("O");
				xturn = true;
			board.iswinner();
			}
			
			}
			
		}

	public static void main(String[] args) {

		JFrame f = new JFrame("TIC TAC TOE");
		f.setSize(500, 500);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		gui game = new gui();
		f.getContentPane().add(game);
		f.setVisible(true);
	}

}
