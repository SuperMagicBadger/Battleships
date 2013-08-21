package battleship;

import java.awt.FlowLayout;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Leader {

	String query1 = "select * from PlayerWinLoss;";
	String query2 = "select * from FormationWinLoss";
	ResultSet result1;
	private RiceCooker rc;
	private static final String[] pwllist = { "Player_Name", "Player_ID", "Win", "Loss"};
	private static final String[] fwllist = { "Formation_ID", "Wins", "Losses"};
	private static Object[][] pwldata = new Object[25][4];
	private static Object[][] fwldata = new Object[25][3];

	public Leader() {
            rc = new RiceCooker();  //forgot to initiate rice cooker
	}

	public void playerWinLoss(String name) {

		boolean worked = rc.connect(Battleship.databaseFile);
		if (worked) {
			try {
                            //there was some esoteric problem with the result set. both
                            //result1 and result2 pointed to the same memory address.
                            //so calling the second runQuery overwrote the previous instance.
				result1 = rc.runQuerry(query1); 
				int i = 0;
				while (result1.next() && i < 25) {
					int pid = result1.getInt("PlayerID");  //switched this to the string method, because its easier to read
					String pname = result1.getString("PlayerName"); //but both methods are viable.
					int win = result1.getInt("Wins");
					int loss = result1.getInt("Losses");
					pwldata[i][0] = pname;
					pwldata[i][1] = pid;
					pwldata[i][2] = win;
					pwldata[i][3] = loss;
					i++;
				}
				rc.dc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("almost there");
			JTable table = new JTable(pwldata, pwllist);
			JScrollPane s = new JScrollPane(table);
			JFrame frame = new JFrame(name);
			frame.setSize(500, 500);
			frame.setLocationRelativeTo(null);
			frame.getContentPane().add(s);
			frame.setVisible(true);
		} else
			System.out.println("Shit");
	}
	
	public void formationWinLoss(String name) {

		boolean worked = rc.connect(Battleship.databaseFile);
		if (worked) {
			try {
                            //there was some esoteric problem with the result set. both
                            //result1 and result2 pointed to the same memory address.
                            //so calling the second runQuery overwrote the previous instance.
				result1 = rc.runQuerry(query2); 
				int i = 0;
				while (result1.next() && i < 25) {
					int fid = result1.getInt("FormID");  //switched this to the string method, because its easier to read
					int win = result1.getInt("Wins");
					int loss = result1.getInt("Losses");
					fwldata[i][0] = fid;
					fwldata[i][1] = win;
					fwldata[i][2] = loss;
					i++;
				}
				rc.dc();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			System.out.println("almost there");
			JTable table = new JTable(fwldata, fwllist);
			JScrollPane s1 = new JScrollPane(table);
			JFrame frame1 = new JFrame(name);
			frame1.setSize(500, 500);
			frame1.setLocationRelativeTo(null);
			frame1.getContentPane().add(s1);
			frame1.setVisible(true);
		} else
			System.out.println("Shit");
	}
	
}
