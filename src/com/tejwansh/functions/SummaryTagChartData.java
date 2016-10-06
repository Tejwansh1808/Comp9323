package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SummaryTagChartData {

	JDBC_Connection connect=new JDBC_Connection();
	Connection con;
	
	/*
	 * Input: String: Tag Name
	 * This Method is used to get total viewcount, 
	 * number of posts, commentcount per distinct year
	 * 
	 * Output: ResultSet 
	 */
	ResultSet resultset, resultset2;
	public ResultSet getYearlyPostCount(String tagName)
	{
		con=connect.Connect();
		String sqlString="Select DISTINCT extract(year from creationdate), count(*) as posts , sum(viewcount) as viewcount, "
				+ "sum(commentcount) as commentcount from \""+tagName+"\" group by extract(year from creationdate);";
		
		try
		{
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultset=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("The Error Occurred In method getYearlyPostCount in SummaryTagChartData Class");
			e.printStackTrace();
		}
		return resultset;
	}
	
	/*
	 * Input: String: Tag Name
	 * This Method is used to get total viewcount, 
	 * commentcount, number of posts 
	 * 
	 * Output: ResultSet 
	 */
	public ResultSet getTotalPostCount(String tagName)
	{
		con=connect.Connect();
		String sqlString="Select count(*) as posts, sum(viewcount) as sumviewcount,"
				+ " sum(commentcount) as sumcommentcount from \""+tagName+"\" ;";
		
		try
		{
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultset2=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("The Error Occurred In method getTotalPostCount in SummaryTagChartData Class");
			e.printStackTrace();
		}
		
		return resultset2;
	}
	
	/*
	 * Input: String: Tag Name
	 * This method return JSON Formatted String 
	 * for Summary Data
	 * 
	 * Output: String : JSON Summary String
	 * Link: https://jsfiddle.net/sbmm9uys/3/
	 */
	public String generateJSONSummaryData(String tagName)
	{
		ResultSet totalCountResultSet=getTotalPostCount(tagName);
		ResultSet yearlyCountResultSet=getYearlyPostCount(tagName);
		float totalPostCount=0,totalViewCount=0,totalCommentCount=0;
		String JSONSummaryData="";
		try
		{
			
			while(totalCountResultSet.next())
			{
				totalPostCount=totalCountResultSet.getFloat(1);
				totalViewCount=totalCountResultSet.getFloat(2);
				totalCommentCount=totalCountResultSet.getFloat(3);
			}
			
			JSONSummaryData+="[\n['Year','Post','View Count','Comment Count']";
			
			while(yearlyCountResultSet.next())
			{
				String year=yearlyCountResultSet.getString(1);
				float post=yearlyCountResultSet.getFloat(2);
				float viewcount=yearlyCountResultSet.getFloat(3);
				float commentcount=yearlyCountResultSet.getFloat(4);
			//	System.out.println(year+" "+post + "  "+ totalPostCount);
				float averagePost=post/totalPostCount*100;
				float averageViewCount=viewcount/totalViewCount*100;
				float averageCommentCount=commentcount/totalCommentCount*100;
				
				JSONSummaryData+=",\n['"+year+"',"+averagePost+","+averageViewCount+","+averageCommentCount+"]";
			}
			
			JSONSummaryData+="\n]";
			
			
			
		}
		catch(SQLException e)
		{
			System.out.println("The Error Occurred In method generateJSONSummaryData in SummaryTagChartData Class");
			e.printStackTrace();
		}
		
		return JSONSummaryData;
	}

	public static void main(String args[])
	{
		SummaryTagChartData cc=new SummaryTagChartData();
		System.out.println(cc.generateJSONSummaryData("java"));
	}
	
}
