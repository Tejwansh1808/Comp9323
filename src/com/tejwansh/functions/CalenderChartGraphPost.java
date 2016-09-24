package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

public class CalenderChartGraphPost {
	
	JDBC_Connection connect=new JDBC_Connection();
	Connection con;
	
	HashMap<ArrayList<Integer>, Integer> daysCountData=new HashMap<ArrayList<Integer>, Integer>();
	HashMap<ArrayList<Integer>, Integer> monthsCountData=new HashMap<ArrayList<Integer>, Integer>();
	/*
	 * Input: sql.Date: startDate and endDate, String tagName: 
	 * This method gets the data from the database based on the tags and the date range
	 * Data Returned: Calendar Chart Data 
	 */
	public ResultSet getDataBaseCalendarChartData(Date startDate,Date endDate,String tagName)
	{
		ResultSet resultSet=null;
		String sqlString="";
		sqlString="Select DISTINCT creationdate::timestamp::date ,count(*) from \""+tagName
				+ "\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'"
						+ "group by creationdate::timestamp::date;";
		
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseCalenderChartData method  in class : CalenderChartGraphPostCount");
			e.printStackTrace();	
		}
		
		
		return resultSet;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate, String tagName: 
	 * This method gets the data from the database based on the tags and the date range
	 * Data Returned: Days of the Week Combo Chart Data 
	 */
	public ResultSet getDataBaseDayComboChartData(Date startDate, Date endDate , String tagName)
	{
		ResultSet resultSet=null;
		String sqlString="select extract(dow from creationdate) as day, extract(year from creationdate), "
				+ "creationdate::timestamp::date as year "
				+ "from \""+tagName+"\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'; ";
		
		
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseDayComboChartData method  in class : CalenderChartGraphPostCount");
			e.printStackTrace();	
		}
		
		
		return resultSet;
	}
	
	/*
	 * Input: sql.Date: startDate and endDate, String tagName: 
	 * This method gets the data from the database based on the tags and the date range
	 * Data Returned: Month Combo Chart Data 
	 */
	public ResultSet getDataBaseMonthComboChartData(Date startDate, Date endDate , String tagName)
	{
		ResultSet resultSet=null;
		String sqlString="select extract(month from creationdate) as month, extract(year from creationdate), "
				+ "creationdate::timestamp::date as year "
				+ "from \""+tagName+"\" where creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'; ";
		
		
		
		try{
			con=connect.Connect();
			PreparedStatement pd=con.prepareStatement(sqlString);
			resultSet=pd.executeQuery();
		}
		catch(SQLException e)
		{
			System.out.println("Error Occured in getDataBaseMonthComboChartData method  in class : CalenderChartGraphPostCount");
			e.printStackTrace();	
		}
		
		
		return resultSet;
	}
	
	
	
	
	
	/*
	 * Input: sql.Date: startDate and endDate, String Tag
	 * This Method Generates HashMap to calculate the number of posts 
	 * for days of the week for each year
	 *
	 * Data Output: String: HashMap(ArrayList(Integer):[year,day],Integer:count) 
	 * 
	 * 
	 */
	public HashMap<ArrayList<Integer>, Integer> createDaysHashMap(Date startDate,Date endDate,String tagName)
	{
		ResultSet daysChartResultSet=getDataBaseDayComboChartData(startDate, endDate, tagName);
	try
		{	
		 while(daysChartResultSet.next())
		 {
			 int year=daysChartResultSet.getInt(2);
			 int day=daysChartResultSet.getInt(1);
			 
			 ArrayList<Integer>temp=new ArrayList<Integer>();
			 temp.add(year);
			 temp.add(day);
			 
			 if(daysCountData.get(temp)==null)
			 {
				
				 daysCountData.put(temp, 1);
			 }
			 else
			 {
				 daysCountData.put(temp, daysCountData.get(temp)+1);
			 }
				 
			
		 }
		 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	 return daysCountData;

	}
	
	/*
	 * Input: sql.Date: startDate and endDate, String Tag
	 * This Method Generates HashMap to calculate the number of posts 
	 * for each month of the year  for each year
	 *
	 * Data Output: String: HashMap(ArrayList(Integer):[year,month],Integer:count) 
	 * 
	 * 
	 */
	public HashMap<ArrayList<Integer>, Integer> createMonthHashMap(Date startDate,Date endDate,String tagName)
	{
		ResultSet monthsChartResultSet=getDataBaseMonthComboChartData(startDate, endDate, tagName);
	try
		{	
		 while(monthsChartResultSet.next())
		 {
			 int year=monthsChartResultSet.getInt(2);
			 int month=monthsChartResultSet.getInt(1);
			 
			 ArrayList<Integer>temp=new ArrayList<Integer>();
			 temp.add(year);
			 temp.add(month);
			 
			 if(monthsCountData.get(temp)==null)
			 {
				
				 monthsCountData.put(temp, 1);
			 }
			 else
			 {
				 monthsCountData.put(temp, monthsCountData.get(temp)+1);
			 }
				 
			
		 }
		 
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
		
	 return monthsCountData;

	}
	
	
	/*
	 * Input: sql.Date: startDate and endDate, String Tag
	 * This Method Generates JSON formatted string to plot Calendar Chart
	 *
	 * Data Output: String: JSON Formatted String for Calendar Chart 
	 * 
	 * 
	 */
	public String generateJSONCalendarChartData(Date startDate,Date endDate, String tagName)
	{
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy,M,dd");
		String calendarChartData="";
		
		ResultSet calendarChartResultSet=getDataBaseCalendarChartData(startDate, endDate, tagName);
		calendarChartData+="[\n";
		try{
			
			while(calendarChartResultSet.next())
			{
				Date tempDate=calendarChartResultSet.getDate(1);
				calendarChartData+="[ new Date("+sdf.format(tempDate)+"),"+calendarChartResultSet.getString(2)+"],\n";
			
			}
			
			calendarChartData+="\n]";
		}
		catch(SQLException e)
		{
			
			System.out.println("Error Occured in generateJSONCalenderChartData method  in class : CalenderChartGraphPostCount");
			e.printStackTrace();
		
		}
		
		
		return calendarChartData;
	}
	
	
	/*
	 * Input: ResultSet: daysChartResultSet, sql.Date: startDate and endDate
	 * This Method Generates JSON formatted string to plot combo Chart
	 * for number of post on each day in a week for each year
	 *
	 * Data Output: String: Days JSON Formatted String for Combo Chart 
	 * Link: https://jsfiddle.net/sbmm9uys/1/
	 */
	public String generateJSONDayComboChartData(HashMap<ArrayList<Integer>, Integer> map,Date startDate,Date endDate)
	{
		String date1=String.valueOf(startDate);
		String date2=String.valueOf(endDate);
		String JSONDaysComboChartData="[\n['Year','Sunday','Monday','Tuesday','Wednesday','Thursday','Friday','Saturday']";
		int datePart1=Integer.parseInt(date1.split("-")[0]);
		int datePart2=Integer.parseInt(date2.split("-")[0]);
		
		ArrayList<Integer> temp;
		
		for(int i=datePart1;i<=datePart2;i++)
		{
			
			JSONDaysComboChartData+=",\n['"+i+"'";
			String tempString="";
			for(int j=0;j<=6;j++)
			{	temp=new ArrayList<Integer>();
				temp.add(i);
				temp.add(j);
				tempString+=","+map.get(temp)+"";
			}
			JSONDaysComboChartData+=tempString+"]";
		}
		JSONDaysComboChartData+="\n]";
		return JSONDaysComboChartData;
	}
	/*
	 * Input: ResultSet: daysChartResultSet, sql.Date: startDate and endDate
	 * This Method Generates JSON formatted string to plot combo Chart
	 * for number of post on each month in a year for each year
	 *
	 * Data Output: String: Month JSON Formatted String for Combo Chart 
	 * Link: https://jsfiddle.net/sbmm9uys/2/
	 */
	public String generateJSONMonthComboChartData(HashMap<ArrayList<Integer>, Integer> map,Date startDate,Date endDate)
	{
		String date1=String.valueOf(startDate);
		String date2=String.valueOf(endDate);
		String JSONMonthsComboChartData="[\n['Year','January','February','March','April','May','June','July','August','September','October',"
				+ "'November','December']";
		int datePart1=Integer.parseInt(date1.split("-")[0]);
		int datePart2=Integer.parseInt(date2.split("-")[0]);
		
		ArrayList<Integer> temp;
		
		for(int i=datePart1;i<=datePart2;i++)
		{
			
			JSONMonthsComboChartData+=",\n['"+i+"'";
			String tempString="";
			for(int j=1;j<=12;j++)
			{	temp=new ArrayList<Integer>();
				temp.add(i);
				temp.add(j);
				if(map.get(temp)==null)
				{
					tempString+=",0";
				}
				else
				{
				tempString+=","+map.get(temp)+"";
				}
			}
			JSONMonthsComboChartData+=tempString+"]";
		}
		JSONMonthsComboChartData+="\n]";
		return JSONMonthsComboChartData;
	}
	

	/*
	 * Input:HashMap(ArrayList(Integer),Integer)
	 * This method get the json String to plot the average number of
	 * posts for each day in a week (cumulative) 
	 * 
	 * Link: https://jsfiddle.net/7waxvrb3/
	 * 
	 * Data Bar Link : https://jsfiddle.net/767mduLq/
	 */
	public String generateJSONDaysGaugeChartData(HashMap<ArrayList<Integer>, Integer> daysHashMap,Date startDate,Date endDate)
	{
		String JSONDaysGaugeChartData="";
		
		long totalSum=0;
		for(Map.Entry<ArrayList<Integer>,Integer> printPairs: daysHashMap.entrySet()) {
			
			ArrayList<Integer> temp=printPairs.getKey();
			int count=printPairs.getValue();
			totalSum=totalSum+count;
		
	  }
		String date1=String.valueOf(startDate);
		String date2=String.valueOf(endDate);
		int datePart1=Integer.parseInt(date1.split("-")[0]);
		int datePart2=Integer.parseInt(date2.split("-")[0]);
		
		ArrayList<Float> daysAverage=new ArrayList<Float>();
		
		for(int i=0;i<=6;i++)
		{	float count=0;
			for(int j=datePart1;j<=datePart2;j++)
			{
				ArrayList<Integer> temp=new ArrayList<Integer>();
				temp.add(j);
				temp.add(i);
				float numTemp=daysHashMap.get(temp);
				count=count+numTemp;
			}
			daysAverage.add(count/totalSum*100);
		}
		
		JSONDaysGaugeChartData+="[\n['Days','Posts']"
				+",\n['Sunday',"+daysAverage.get(0)+"]"
				+",\n['Monday',"+daysAverage.get(1)+"]"
				+",\n['Tuesday',"+daysAverage.get(2)+"]"
				+",\n['Wednesday',"+daysAverage.get(3)+"]"
				+",\n['Thursday',"+daysAverage.get(4)+"]"
				+",\n['Friday',"+daysAverage.get(5)+"]"
				+",\n['Saturday',"+daysAverage.get(6)+"]\n]";
		
		return JSONDaysGaugeChartData;
				
	}
	

	/*
	 * Input:HashMap(ArrayList(Integer),Integer)
	 * This method get the json String to plot the average number of
	 * posts for each month in a year (cumulative) 
	 * 
	 * Link: https://jsfiddle.net/767mduLq/1/
	 */
	public String generateJSONMonthsBarChartData(HashMap<ArrayList<Integer>, Integer> monthsHashMap,Date startDate,Date endDate)
	{
		String JSONMonthsBarChartData="";
		
		long totalSum=0;
		for(Map.Entry<ArrayList<Integer>,Integer> printPairs: monthsHashMap.entrySet()) {
			
			ArrayList<Integer> temp=printPairs.getKey();
			int count=printPairs.getValue();
			totalSum=totalSum+count;
		
	  }
		String date1=String.valueOf(startDate);
		String date2=String.valueOf(endDate);
		int datePart1=Integer.parseInt(date1.split("-")[0]);
		int datePart2=Integer.parseInt(date2.split("-")[0]);
		
		ArrayList<Float> monthsAverage=new ArrayList<Float>();
		
		for(int i=1;i<=12;i++)
		{	float count=0;
			for(int j=datePart1;j<=datePart2;j++)
			{
				ArrayList<Integer> temp=new ArrayList<Integer>();
				temp.add(j);
				temp.add(i);
				
				
				float numTemp=0;
				
				if(monthsHashMap.get(temp)==null)
				{
					numTemp=0;
				}
				else
				{
					numTemp=monthsHashMap.get(temp);
				}
				count=count+numTemp;
			}
			monthsAverage.add(count/totalSum*100);
		}
		
		JSONMonthsBarChartData+="[\n['Days','Posts']"
				+",\n['January',"+monthsAverage.get(0)+"]"
				+",\n['February',"+monthsAverage.get(1)+"]"
				+",\n['March',"+monthsAverage.get(2)+"]"
				+",\n['April',"+monthsAverage.get(3)+"]"
				+",\n['May',"+monthsAverage.get(4)+"]"
				+",\n['June',"+monthsAverage.get(5)+"]"
				+",\n['July',"+monthsAverage.get(6)+"]"
				+",\n['August',"+monthsAverage.get(7)+"]"
				+",\n['September',"+monthsAverage.get(8)+"]"
				+",\n['October',"+monthsAverage.get(9)+"]"
				+",\n['November',"+monthsAverage.get(10)+"]"
				+",\n['December',"+monthsAverage.get(11)+"]\n]";
		
		return JSONMonthsBarChartData;
				
	}
	/*
	 * Print a HashMap 
	 */
	public void printHashMap(HashMap<ArrayList<Integer>,Integer> map)
	{
		for(Map.Entry<ArrayList<Integer>,Integer> printPairs: map.entrySet()) {
					
				ArrayList<Integer> temp=printPairs.getKey();
				int count=printPairs.getValue();
				
				System.out.println(temp.get(0)+"  "+temp.get(1)+"   "+count +"  ");
				
		    
		  }
		
	}
	
	
	public static void main(String args[])
	{
		CalenderChartGraphPost c=new CalenderChartGraphPost();
		Date startDate = Date.valueOf("2008-01-10");
		Date endDate = Date.valueOf("2016-11-10");
		HashMap<ArrayList<Integer>,Integer> map=c.createMonthHashMap(startDate, endDate, "java");
		System.out.println(c.generateJSONMonthComboChartData(map, startDate, endDate));
		System.out.println(c.generateJSONMonthsBarChartData(map, startDate, endDate));
		//System.out.println(c.generateJSONDayComboChartData(map, startDate, endDate));
		//System.out.println(c.generateJSONDaysGaugeChartData(map, startDate, endDate));
		
		
		
		//System.out.println(c.generateJSONCalendarChartData(startDate, endDate, "java"));
		//System.out.println("Hello");
		
		
	
	}
}
