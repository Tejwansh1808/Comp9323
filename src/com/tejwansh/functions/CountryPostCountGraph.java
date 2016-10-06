package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.text.WordUtils;

public class CountryPostCountGraph {
	Connection con;
	JDBC_Connection connect=new JDBC_Connection();
	ResultSet postCountryCountResultSet;
	
	/*
	 * Input: SQL.Date: StartDate and EndDate, String: Tag Name Used to generate
	 * sql.resultset containing the data from DB
	 */
	public ResultSet getCountryPostData(Date startDate,Date endDate,String tagName)
	{
		con=connect.Connect();
		String sqlString="";
		
		sqlString="Select b.location from \""+tagName+"\" a join users b on a.user_id=b.id "
				+ "where a.creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"'; ";
		
		try{
			PreparedStatement pd=con.prepareStatement(sqlString);
			postCountryCountResultSet=pd.executeQuery();
		}catch(SQLException e)
		{
			System.out.println("The Error Occurred In method getCountryPostData in CountryPostsCount Class");
			e.printStackTrace();
		}
		
		return postCountryCountResultSet;
	}
	
	
	/*
	 * Input: sql.Date: StartDate and EndDate, String: Tag Name 
	 * Generates a Hashmap and calculates the number of posts for each unique country
	 */
	public HashMap<String, Integer> createHashMap(Date startDate, Date endDate, String tagName)
	{
		HashMap<String, Integer> countryPostDataMap=new HashMap<String, Integer>();
		ResultSet countryResultSet=getCountryPostData(startDate, endDate, tagName);
		
		
		try
		{
			
			while(countryResultSet.next())
			{
				String countryName="";
				String countryCode="";
				int c=0;
				countryCode=countryResultSet.getString(1).toString();
				if(!countryCode.equals(String.valueOf(c)))
				{
					countryCode=countryCode.toUpperCase();
					countryName=CountryCode.getCountryName(countryCode);
					
					

						if (countryPostDataMap.get(countryName) == null) 
						{
							countryPostDataMap.put(countryName, 1);
						} 
						else 
						{
							countryPostDataMap.put(countryName,countryPostDataMap.get(countryName) + 1);

						}
					
				}
				
			}
		}
		catch(SQLException e)
		{
			System.out.println("The Error Occurred In method createHashMap in CountryPostsCount Class");
			e.printStackTrace();
			
		}
		
		return countryPostDataMap;
		
	}
	
	/*
	 * Input: HashMap(String:Country, Integer: Number of Posts)
	 * This method is used to get the top 10 countries based on the
	 * number of posts in particular language
	 * 
	 * Data: Returned Bar Chart Data
	 * Link: https://jsfiddle.net/Lrv1d4fq/
	 */
	public String getJSONCountryBarChartData(HashMap<String, Integer> hashMap)
	{
		String barChartData="";
	    ValueComparator bvc =  new ValueComparator(hashMap);
        TreeMap<String,Integer> sorted_map = new TreeMap<String,Integer>(bvc);
        	
        sorted_map.putAll(hashMap);
        
        barChartData+="[\n['Country','Number Of Posts']";
        Iterator reader = sorted_map.entrySet().iterator();
        int i=0;
        while(reader.hasNext()) {
            if(i<10)
            {
           	@SuppressWarnings("rawtypes")
			Map.Entry entry = (Map.Entry)reader.next();
            barChartData+=",\n['"+entry.getKey().toString()+"',"+entry.getValue()+"]";
            i++;
            }
            else
            {
            	break;
            }
            	
         }
		barChartData+="\n]";
		return  barChartData;
	}
	
	  
	 /*Input: sql.Date: startDate and endDate,String: Tag Name
	 * This Method returns the formatted string for plotting the geo location chart	
	 * Link: https://jsfiddle.net/0q5uzy6n/2/ 
	 */
	 public String generateJSONStringFormat(HashMap<String,Integer> countryPostCount)
	{
		
		String JSONDataString="";
		
		JSONDataString+="[ \n ['Country' ,'Posts']";
		
		for(String name: countryPostCount.keySet())
		{
			String key=name.toString();
			Integer value=countryPostCount.get(name);
			if(value>100)
			{
			JSONDataString+=", \n ['"+key+"',"+value+"]";
			}
			
			//System.out.println(key + "  "+ value);		
					
		}
		JSONDataString+="\n]";
		
		
		return JSONDataString;
		
	}
	 
	 
	
	public static void main(String args[]) throws SQLException {
		CountryPostCountGraph c=new CountryPostCountGraph();
		Date startDate = Date.valueOf("2008-01-10");
		Date endDate = Date.valueOf("2016-01-10");
		HashMap<String, Integer> cc=c.createHashMap(startDate, endDate, "java");

		
		//System.out.println(c.getJSONCountryBarChartData(cc));
		System.out.println(c.generateJSONStringFormat((cc)));
		
		
	}

}



class ValueComparator implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator(HashMap<String,Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}