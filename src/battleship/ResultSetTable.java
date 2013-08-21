/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package battleship;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 *
 * @author Cow
 */
public class ResultSetTable {

    public static void main(String[] args) throws SQLException{
        RiceCooker rc = new RiceCooker();
        rc.connect(Battleship.databaseFile);
        ResultSetTable tb = new ResultSetTable(rc.runQuerry("select * from PlayerWinLoss;"), 25);
    }
    
    public String[] colHeaders;
    public Object[][] tableData;

    public ResultSetTable(ResultSet s, int size) throws SQLException {
        ResultSetMetaData md = s.getMetaData();
        colHeaders = new String[md.getColumnCount()];
        for(int i = 0; i < colHeaders.length; i++){
            colHeaders[i] = md.getColumnName(i + 1);
        }
        tableData = new Object[size][colHeaders.length];
        for(int i = 0; i < size && s.next(); i++){
            for(int j = 0; j < colHeaders.length; j++){
                switch(md.getColumnType(j + 1)){
                    case 4: //int
                        tableData[i][j] = new Integer(s.getInt(md.getColumnName(j + 1)));
                        System.out.println(s.getInt(md.getColumnName(j + 1)));
                        break;
                    case 12:
                        tableData[i][j] = s.getString(md.getColumnName(j + 1));
                        System.out.println(s.getString(md.getColumnName(j + 1)));
                        break;
                    default:
                        System.out.println("dafuq? : " + md.getColumnTypeName(j + 1) + " " + md.getColumnType(j + 1));
                        break;
                }
            }
        }
        
    }
}
