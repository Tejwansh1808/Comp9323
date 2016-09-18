package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class ComparativeChartGraphPostCount {
	
	JDBC_Connection connect=new JDBC_Connection();
	Connection con;
	
	/*
	 * Input: sql.Date: startDate and endDate, Arraylist(String) tagNames: 
	 * This method gets the data from the database based on the tags and the date range
	 * Data Returned: Pie Chart Data 
	 */
	public ResultSet getDataBasePieChartData(Date startDate,Date endDate,ArrayList<String> tagNames)
	{
		ResultSet resultSet=null;
		String sqlString="";
		sqlString+="Select '"+tagNames.get(0)+"' as Language, count(tags) from posts where tags like '%<"+tagNames.get(0)+">%' and "
				+ "creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		for(int i=1;i<tagNames.size();i++)
		{
			sqlString+="UNION Select '"+tagNames.get(i)+"' as Language, count(tags) from posts where tags like '%<"+tagNames.get(i)+">%' and "
					+ "creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		}
		
		sqlString+=";";
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseData method  in class : ComparativeChartGraphPostCount");
			e.printStackTrace();	
		}
		
		
		return resultSet;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate 
	 * This method gets the data from the database based on date range and tags
	 * 
	 * Data Output : Data for Bubble Chart
	 */
	public ResultSet getDataBaseBubbleChartData(Date startDate,Date endDate,ArrayList<String> tagNames)
	{
		ResultSet resultSet=null;
		String sqlString="select '"+tagNames.get(0)+"' as Language, count(tags) , sum(viewcount) as Viewed , "
				+ "sum(commentcount) as  Comments  from posts where tags like '%<"+tagNames.get(0)+">%' "
				+ "and creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		sqlString+="";
		for(int i=1;i<tagNames.size();i++)
		{
			sqlString+="UNION select '"+tagNames.get(i)+"' as Language, count(tags) , sum(viewcount) as Viewed , "
					+ "sum(commentcount) as  Comments  from posts where tags like '%<"+tagNames.get(i)+">%' "
					+ "and creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";;
		}
		
		sqlString+=";";
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseData method  in class : ComparativeChartGraphPostCount");
			e.printStackTrace();	
		}
		
		
		return resultSet;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate, ArrayList(String) Tags
	 * This Method Generates JSON formatted string to plot PieChart
	 * 
	 * Link: https://jsfiddle.net/y1p5mtnt/
	 */
	public String generateJSONPieChartData(Date startDate,Date endDate,ArrayList<String> tagNames)
	{
		String pieChartData="";
		ResultSet results=getDataBasePieChartData(startDate, endDate, tagNames);
		pieChartData+="[\n['Programming Languages','Number Of Posts']";
		try {
			while(results.next())
			{
				pieChartData+=",\n['"+results.getString(1)+"',"+results.getString(2)+"]";
			}
			
			pieChartData+="\n ]";
		} catch (SQLException e) {
			System.out.println("Error Occured in generateJSONPieChartData method  in class : ComparativeChartGraphPostCount");
			e.printStackTrace();
		}
		
		
		
		
		return pieChartData;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate, ArrayList(String) Tags
	 * This Method Generates JSON formatted string to plot PieChart
	 * 
	 * Link: https://jsfiddle.net/y1p5mtnt/
	 */
	public String generateJSONBubbleChartData(Date startDate,Date endDate,ArrayList<String> tagNames)
	{
		String bubbleChartData="";
		ResultSet results=getDataBaseBubbleChartData(startDate, endDate, tagNames);
		bubbleChartData+="[\n['Tag','Number of Times Viewed','Number of Comments','Number Of Posts']";
		try {
			while(results.next())
			{
				 bubbleChartData+=",\n'"+results.getString(1)+"',"+results.getString(3)+","+results.getString(4)+","+results.getString(2)+"]";
			}
			
			bubbleChartData+="\n]";
			
		} catch (SQLException e) {
			System.out.println("Error Occured in generateJSONPieChartData method  in class : ComparativeChartGraphPostCount");
			e.printStackTrace();
		}
		
		
		
		
		return bubbleChartData;
	}
	public static void main(String args[])
	{
		ComparativeChartGraphPostCount c=new ComparativeChartGraphPostCount();
		Date startDate = Date.valueOf("2008-01-10");
		Date endDate = Date.valueOf("2016-01-10");
		ArrayList<String> r=new ArrayList<String>();
		r.add("java");
		r.add("javascript");
		r.add("c++");
		r.add("c");
		r.add("vb.net");
		System.out.println(c.generateJSONBubbleChartData(startDate, endDate, r));
	}

}
