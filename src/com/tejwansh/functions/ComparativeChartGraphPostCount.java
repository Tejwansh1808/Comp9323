package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.management.StringValueExp;


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
		sqlString+="Select '"+tagNames.get(0)+"' as Language, count(*) from \""+tagNames.get(0)
				
				+ "\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		for(int i=1;i<tagNames.size();i++)
		{
			sqlString+="UNION ALL Select '"+tagNames.get(i)+"' as Language, count(*) from \""+tagNames.get(i)
					+ "\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		}
		
		sqlString+=";";
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBasePieChartData method  in class : ComparativeChartGraphPostCount");
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
		String sqlString="select '"+tagNames.get(0)+"' as Language, count(*) , sum(viewcount) as Viewed , "
				+ "sum(commentcount) as  Comments  from \""+tagNames.get(0)
				+ "\" where  creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";
		sqlString+="";
		for(int i=1;i<tagNames.size();i++)
		{
			sqlString+="UNION ALL select '"+tagNames.get(i)+"' as Language, count(*) , sum(viewcount) as Viewed , "
					+ "sum(commentcount) as  Comments  from \""+tagNames.get(i)
					+ "\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'";;
		}
		
		sqlString+=";";
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseBubbleChartData method  in class : ComparativeChartGraphPostCount");
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
	 * This Method Generates JSON formatted string to plot BubbleChart
	 * Showing the corelation between the number of views and comments
	 * 
	 * Data Output: ArrayList-->
	 * 		(-Index1: JSON String 
	 * 		 -Index2: IndexData:ArrayList(ArrayList(Language,ViewIndex,CommentIndex,PopularityIndex,CuriosityIndex)))
	 * Link: https://jsfiddle.net/p5got4yd/3/
	 */
	
	public ArrayList generateJSONBubbleChartData(Date startDate,Date endDate,ArrayList<String> tagNames)
	{
		String bubbleChart="";
		ResultSet results=getDataBaseBubbleChartData(startDate, endDate, tagNames);
		
		ArrayList<String> numericData;
		ArrayList<ArrayList<String>> data=new ArrayList<ArrayList<String>>();
		
		ArrayList bubbleChartData=new ArrayList();
		
		
		

		try {
			double viewCountSum=0;
			double commentCountSum=0;
			while(results.next())
			{
				numericData=new ArrayList<String>();
				numericData.add(results.getString(1));
				numericData.add(results.getString(2));
				
				float view=Integer.valueOf(results.getInt(3));
				numericData.add(String.valueOf(view));
				float comment=Integer.valueOf(results.getInt(4));
				numericData.add(String.valueOf(comment));
				data.add(numericData);
				viewCountSum=view+viewCountSum;
				commentCountSum=commentCountSum+comment;
				
			}
			
			
			
			
			

			ArrayList<String> temp;
			ArrayList<ArrayList<String>> indexData=new ArrayList<ArrayList<String>>();
			ArrayList<String> temp1;
			bubbleChart+="[\n['Programming Language','Number Of Times Viewed','Number Of Comments',"
					+ "'View Index','Comment Index']";
			for(int i=0;i<data.size();i++)
			{
				
				temp=new ArrayList<String>();
				temp=data.get(i);
				
				double viewIndex=Double.parseDouble(temp.get(2))/viewCountSum;
				
				double commentIndex=Double.parseDouble(temp.get(3))/commentCountSum;
				double popularityIndex=((viewIndex*2+commentIndex)/3)*100;
				double curiosityIndex=((commentIndex*2+viewIndex)/3)*100;
				bubbleChart+=",\n['"+temp.get(0)+"',"+temp.get(2)+","+temp.get(3)+","+viewIndex*100+","+commentIndex*100+"]";
				temp1=new ArrayList<String>();
				temp1.add(temp.get(0));
				temp1.add(String.valueOf(viewIndex));
				temp1.add(String.valueOf(commentIndex));
				temp1.add(String.valueOf(popularityIndex));
				temp1.add(String.valueOf(curiosityIndex));
				temp1.add(temp.get(1));
				indexData.add(temp1);
				
			}
			
			bubbleChart+="\n]";
			
			bubbleChartData.add(bubbleChart);
			bubbleChartData.add(indexData);
			
			
		} catch (SQLException e) {
			System.out.println("Error Occured in generateJSONPieChartData method  in class : ComparativeChartGraphPostCount");
			e.printStackTrace();
		}
		
		
		
		
		return bubbleChartData;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate, ArrayList(String) Tags
	 * This Method Generates JSON formatted string to plot BubbleChart
	 * Showing the corelation between the popularity Index and curiosity Index
	 * 
	 * Popularity (Index=ViewIndex*2+CommentIndex/3)*100
	 * Curiosity (Index=CommentIndex*2+ViewIndex/3)*100
	 * 
	 * Data Output: String: JSON Formatted String for BubbleChart
	 * 
	 * Link: https://jsfiddle.net/p5got4yd/5/
	 * 
	 */
	public String generateJSONBubbelChartDataIndex(ArrayList data)
	{
		String bubbleChartData="";
		bubbleChartData+="[\n['Programming Language','Popularity Index','Curiosity Index','Number Of Posts']";
		ArrayList<String> indexData;
		for(int i=0;i<data.size();i++)
		{
			indexData=new ArrayList<String>();
			indexData=(ArrayList)data.get(i);
			
			bubbleChartData+=",\n['"+indexData.get(0)+"',"+indexData.get(3)+","+indexData.get(4)+","+indexData.get(5)+"]";
			
		}
		
		bubbleChartData+="\n]";
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
		ArrayList d=c.generateJSONBubbleChartData(startDate, endDate, r);
		ArrayList dd=(ArrayList)d.get(1);
		System.out.println(c.generateJSONBubbelChartDataIndex(dd));
		System.out.println(String.valueOf(d.get(0)));
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy,M,dd");
		String date = sdf.format(startDate);
		System.out.println(date); //15/10/2013
		
		System.out.println(c.generateJSONPieChartData(startDate, endDate, r));
	}

}
