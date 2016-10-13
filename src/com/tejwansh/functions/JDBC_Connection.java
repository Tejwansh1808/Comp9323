
package com.tejwansh.functions;


import java.sql.Connection;

import java.sql.DriverManager;








import java.sql.SQLException;


import javax.swing.JOptionPane;

/**
 *
 * @author Tejwansh Singh
 */
public class JDBC_Connection {
    public Connection con;
    
    public Connection Connect()
    { 
    
        try{
        //	System.out.println("Starting Connection Attempt");
            Class.forName("org.postgresql.Driver");
            con=DriverManager.getConnection("jdbc:postgresql://localhost:5432/e_enterprise?user=schi&password=123456");
         //   System.out.println("Attempt Successfull!! Connection Established!!!!");
          
          
            
        }
        catch(Exception e)
        {
            JOptionPane.showMessageDialog(null, "Connection Cannot be Established !!!");
            
            e.printStackTrace();
        }
            return con;
        
    }
    public void closeConnection(Connection c)
    {
    	
    	try{
    	c.close();
    	System.out.println("Connection Closed!!");
    	}
    	catch(SQLException e)
    	{
    		
    		e.printStackTrace();
    		System.out.println("Error Occured in Database Connection Closing!!!!!");
    		JOptionPane.showMessageDialog(null, "Connection Cannot be Closed!!!! Error !!!");
    		
    	}
    	}
 
  
}