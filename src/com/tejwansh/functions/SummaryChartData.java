package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
/*These Class methods are comparing the summary of 
 * any 4 languages at a time. They would be compared 
 * on a Time Line based on the number of Posts
 * 
 * Input: ArrayList(String) : tag names (only 4) 
 */
public class SummaryChartData {
	JDBC_Connection connect=new JDBC_Connection();
	Connection con;
	
	public ResultSet getSummaryPostData(ArrayList<String> tagNames)
	{	
		int size=0;
		if(tagNames.size()>4)
		{
			size=4;
			
		}
		else
		{
			size=tagNames.size();
		}
		ResultSet summaryDataResultSet=null;
		con=connect.Connect();
		PreparedStatement pd=null;
		try{
			
		String sqlString="";
		sqlString="Select Distinct extract(year from creationdate) || ' "+tagNames.get(0)+"' as year, count(*) as count "
				+ "from \""+tagNames.get(0)+"\" group by extract(year from creationdate)";
		for(int i=1;i<size;i++)
		{
			sqlString+="UNION ALL Select Distinct extract(year from creationdate) || ' "+tagNames.get(i)+"' as year, count(*) as count "
					+ "from \""+tagNames.get(i)+"\" group by extract(year from creationdate)";
		}
		sqlString+=";";
		
		pd=con.prepareStatement(sqlString);
		summaryDataResultSet=pd.executeQuery();
			
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getSummaryPostData method  in class : SummaryChartData");
			e.printStackTrace();
		}
		
		return summaryDataResultSet;
	}
	
	/*These Class methods are comparing the summary of 
	 * any 4 languages at a time. They would be compared 
	 * on a Time Line based on ViewCount 
	 * 
	 * Input: ArrayList(String) : tag names (only 4) 
	 */
	public ResultSet getSummaryViewCountData(ArrayList<String> tagNames)
	{	
		int size=0;
		if(tagNames.size()>4)
		{
			size=4;
			
		}
		else
		{
			size=tagNames.size();
		}
		ResultSet summaryDataResultSet=null;
		String sqlString="";
		
		con=connect.Connect();
		PreparedStatement pd;
		try{
			
			sqlString="Select Distinct extract(year from creationdate) || ' "+tagNames.get(0)+"' as year, sum(viewcount) "
					+ "from \""+tagNames.get(0)+"\" group by extract(year from creationdate)";
			for(int i=1;i<size;i++)
			{
				sqlString+="UNION ALL Select Distinct extract(year from creationdate) || ' "+tagNames.get(i)+"' as year, "
						+ "sum(viewcount) "
						+ "from \""+tagNames.get(i)+"\" group by extract(year from creationdate)";
			}
			sqlString+=";";
			pd=con.prepareStatement(sqlString);
			summaryDataResultSet=pd.executeQuery();
		
		
		
			
			
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getSummaryViewCountData method  in class : SummaryChartData");
			e.printStackTrace();
		}
		
		return summaryDataResultSet;
	}
	
	/*These Class methods are comparing the summary of 
	 * any 4 languages at a time. They would be compared 
	 * on a Time Line based on CommentCount
	 * 
	 * Input: ArrayList(String) : tag names (only 4) 
	 */
	public ResultSet getSummaryCommentCountData(ArrayList<String> tagNames)
	{	
		int size=0;
		if(tagNames.size()>4)
		{
			size=4;
			
		}
		else
		{
			size=tagNames.size();
		}
		ResultSet summaryDataResultSet=null;
		String sqlString="";
		
		con=connect.Connect();
		PreparedStatement pd;
		try{
			
			
			sqlString="Select Distinct extract(year from creationdate) || ' "+tagNames.get(0)+"' as year, sum(commentcount) as commentcount "
					+ "from \""+tagNames.get(0)+"\" group by extract(year from creationdate)";
			
			for(int i=1;i<size;i++)
			{
				sqlString+="UNION ALL Select Distinct extract(year from creationdate) || ' "+tagNames.get(i)+"' as year, "
						+ "sum(commentcount) as commentcount "
						+ "from \""+tagNames.get(i)+"\" group by extract(year from creationdate)";
			}
			sqlString+=";";
			pd=con.prepareStatement(sqlString);
			summaryDataResultSet=pd.executeQuery();
		
			
			
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getSummaryCommentCountData method  in class : SummaryChartData");
			e.printStackTrace();
		}
		
		return summaryDataResultSet;
	}
	
	/*
	 * This Method is used to format the String 
	 * for JSON Data 
	 * Plots the step Area Chart for POST Comparison
	 * 
	 * Input: ArrayList(String) : tag names (only 4) 
	 * 
	 * Output: String: JSONFormatString
	 * Link: https://jsfiddle.net/k86v7dkL/
	 */
	public String generateJSONPostStepChartData(ArrayList<String> tagNames)
	{	
		int size=0;
		if(tagNames.size()>4)
		{
			size=4;
		}
		else
		{
			size=tagNames.size();
		}
		String dataPostJSON="";
		HashMap<ArrayList<String>, Long> dataPostHashMap=new HashMap<ArrayList<String>, Long>();
		dataPostJSON+="[\n['Year'";
		for(int k=0;k<size;k++)
		{
			dataPostJSON+=",'"+tagNames.get(k)+"'";
		}
		dataPostJSON+="]";
		
		ResultSet dataResultSet=getSummaryPostData(tagNames);
		try{
			
			while(dataResultSet.next())
			{
				ArrayList<String> tempKey=new ArrayList<String>();
				String[] split=dataResultSet.getString(1).split(" ");
				tempKey.add(split[0]);
				tempKey.add(split[1]);
				dataPostHashMap.put(tempKey, dataResultSet.getLong(2));
				
			}
			
			for(int i=2008;i<=2016;i++)
			{	
				
				String tempString="";
				tempString=",\n['"+i+"'";
				
				for(int j=0;j<size;j++)
				{
					ArrayList<String> temp=new ArrayList<String>();
					temp.add(String.valueOf(i));
					temp.add(tagNames.get(j));
					tempString+=","+dataPostHashMap.get(temp);
					
				}
				tempString+="]";
				dataPostJSON+=tempString;
			}
			dataPostJSON+="\n]";
			
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in generateJSONPostStepChartData method  in class : SummaryChartData");
			e.printStackTrace();
		}
		
		return dataPostJSON;
	}
	
	/*
	 * This Method is used to format the String 
	 * for JSON Data 
	 * Plots the step Area Chart for ViewCount Comparison
	 * 
	 * Input: ArrayList(String) : tag names (only 4) 
	 * 
	 * Output: String: JSONFormatString
	 * Link:
	 */
	public void generateJSONViewCountStepChartData(ArrayList<String> tagNames)
	{
		
	}
	
	/*
	 * This Method is used to format the String 
	 * for JSON Data 
	 * Plots the step Area Chart for CommentCount Comparison
	 * 
	 * Input: ArrayList(String) : tag names (only 4) 
	 * 
	 * Output: String: JSONFormatString
	 * Link:
	 */
	public void generateJSONCommentCountStepChartData(ArrayList<String> tagNames)
	{
		
	}
	
	
	public static void main(String args[])
	{
		SummaryChartData c=new SummaryChartData();
		ArrayList<String> test=new ArrayList<String>();
		test.add("java");
		test.add("c++");
		test.add("javascript");
		System.out.println(c.generateJSONPostStepChartData(test));
		
	}

}
