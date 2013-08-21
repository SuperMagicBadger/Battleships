/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Interface;

import battleship.ResultSetTable;
import java.awt.LayoutManager;
import java.sql.ResultSet;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.JTextField;

/**
 *
 * @author Cow
 */
public class JohnsLB extends JFrame{
    enum state{
        PWL,
        FWL        
    }
    
    String[] cbLables = new String[]{"Player Score", "Formation Score"};
    
    ResultSetTable rt;
    ResultSet rs;
    
    LayoutManager layman;
    //JComboBox<String> cb;
    JTextField text;
    JTable jt;
    
    public static void main(String[] args){
        
    }
    
    
    JohnsLB(){
        layman = new BoxLayout(this, BoxLayout.Y_AXIS);
      //  cb = new JComboBox<String>();
    }
    
    
}
