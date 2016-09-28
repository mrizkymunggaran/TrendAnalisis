/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package trendanalisis.main.util;

import com.mysql.jdbc.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asus
 */
public class ConnectionDb {
 private String url = "jdbc:mysql://localhost/web_crawler_app";
    private String username = "root";
    private String password = "";
    private String driver = "com.mysql.jdbc.Driver";
    private Connection conn = null;

    public ConnectionDb()
    {

    }

   
  public Connection GetconConnection()
    {
        
         try {
            try {
                Class.forName(driver).newInstance();
                
            } catch (InstantiationException ex) {
                Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
            }
           
            this.conn=(Connection) DriverManager.getConnection(url, username, password);
            System.out.println("Koneksi Berhasil");
            return conn;
            
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException e) {
         // e.printStackTrace();
            System.out.println("Koneksi Gagal");
        }
        return conn;
    }
   

    public int ExecuteNonQuery(String sql)
    {
        int success;
        try {
            Statement stat = (Statement) conn.createStatement();
            success=stat.executeUpdate(sql);
            return success;

        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return -1;
    }

    public ResultSet ExecuteQuery(String sql)
    {
        try {
             GetconConnection(); 
             ResultSet rs = null;
            Statement stat = (Statement) conn.createStatement();                    
            rs = (ResultSet) stat.executeQuery(sql);
                        return rs;
        } catch (SQLException ex) {
            Logger.getLogger(ConnectionDb.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

  

}
