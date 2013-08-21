package battleship;
import javax.swing.table.AbstractTableModel;
import java.util.Vector;

class MyTableModel extends AbstractTableModel {
	
	//instance variables
    private String[] columnNames;
    private final int MAXROWS = 25; 
    private Vector<Vector> data;

    //constructor
    public MyTableModel(String[] cn){
    	columnNames = cn;
    	data = new Vector<Vector>();
    }
    
    public void updateptable(String name, int pid){
    	Vector<Object> vo = new Vector<Object>(2);
    	vo.add(name);
    	vo.add(pid);
    	data.addElement(vo);
    	System.out.println("ptable updated");
    }
   
    public void updatestable(int id, int pegs, int num, int sunk, int xpos, int ypos, boolean horzi){
    	Vector<Object> vo = new Vector<Object>(8);
    	vo.add(id);
    	vo.add(pegs);
    	vo.add(num);
    	vo.add(sunk);
    	vo.add(xpos);
    	vo.add(ypos);
    	vo.add(horzi);
    	data.addElement(vo);
    }
    
    public void updateftable(int id, int s1, int s2, int s3, int s4, int s5){
    	Vector<Object> vo = new Vector<Object>(7);
    	vo.add(id);
    	vo.add(s1);
    	vo.add(s2);
    	vo.add(s3);
    	vo.add(s4);
    	vo.add(s5);
    	data.addElement(vo);
    }
    
    public void updategtable(int gid, int wid, int wfid, int lid, int lfid){
    	Vector<Object> vo = new Vector<Object>(7);
    	vo.add(gid);
    	vo.add(wid);
    	vo.add(wfid);
    	vo.add(lid);
    	vo.add(lfid);
    	data.addElement(vo);
    }
    
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
    	return data.elementAt(row); 
    }
    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 2) {
            return false;
        } else {
            return true;
        }
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        //data[row][col] = value;
        fireTableCellUpdated(row, col);
    }
}