package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.text.WordUtils;

public class GeoLocationCountryPostCount {

	JDBC_Connection connect = new JDBC_Connection();
	Connection con;
	HashMap<String, Integer> countryPostDataMap = new HashMap<String, Integer>();
	ArrayList<ResultSet> resultSetArray;

	/*
	 * Input: SQL.Date: StartDate and EndDate, String: Tag Name Used to generate
	 * sql.resultset containing the data from DB
	 */
	public ArrayList<ResultSet> getDataFromTable(Date startDate, Date endDate,
			String tagName) {
		ResultSet resultSet, resultSet2,resultSet3;

		con = connect.Connect();
		try {
			String sqlString = "Select location as Location from "+tagName+"  where location "
					+ "not like '%,%' and "
					+ "location <> '' "
					+"and creationdate::timestamp::date between '"
					+ startDate + "' and '" + endDate + "'; ";
			
		

			PreparedStatement pd = con.prepareStatement(sqlString);

			resultSet = pd.executeQuery();
			//pd = con.prepareStatement(sqlString2);
			//resultSet2 = pd.executeQuery();
			
			
			resultSetArray=new ArrayList<ResultSet>();
			resultSetArray.add(resultSet);
			//resultSetArray.add(resultSet2);
			

		} catch (SQLException e) {
			System.err
					.println("The Error Occurred In method getDataFromTable in CountryPostsCount Class");
			e.printStackTrace();

		}

		return resultSetArray;
	}

	
	/*
	 * Input: sql.Date: StartDate and EndDate, String: Tag Name 
	 * Generates a Hashmap and calculates the number of posts for each unique country
	 */
	public HashMap<String, Integer> createCountryPostHashMap(Date startDate,
			Date endDate, String tagName) {
		ArrayList<ResultSet> resultSetArrayList = getDataFromTable(startDate,
				endDate, tagName);
		ResultSet countryPostResultSet;

		String countryName;
		int count = 0, count1 = 0;
		try {
			for (int i = 0; i < resultSetArrayList.size(); i++) {
				countryPostResultSet=resultSetArrayList.get(i);
				while (countryPostResultSet.next()) {
					
					String country = (String) countryPostResultSet.getString(1);
					countryName = LocationHelpMethods
							.checkCountryExists(country);

					if (!countryName.equalsIgnoreCase("NotACountry")) {

						if (countryPostDataMap.get(countryName) == null) {
							countryPostDataMap.put(countryName, 1);
						} else {
							countryPostDataMap.put(countryName,countryPostDataMap.get(countryName) + 1);

						}

						count1++;
					} else {
						
						count++;

					}

				}
			}
			System.out.println("Number Of Countries Not Found: "+count);
			System.err.println("Number Of Countries Found: "+count1);

		} catch (SQLException e) {
			System.err.println("The Error Occurred In method createCountryPostHashMap in GeoLocationCountryPostCount Class");
			e.printStackTrace();
		}

		return countryPostDataMap;
	}

	/*Input: sql.Date: startDate and endDate,String: Tag Name
	 * This Method returns the formatted string for plotting the geo location chart	
	 * Link: https://jsfiddle.net/0q5uzy6n/ 
	 */
	public String getJSONCountryPostData(Date startDate,Date endDate,String tagName)
	{
		String JSONData="";
		HashMap<String, Integer> hashmapData = new HashMap<String, Integer>();
		 hashmapData= createCountryPostHashMap(startDate, endDate, tagName);
		 JSONData=LocationHelpMethods.generateJSONStringFormat(hashmapData,1);		 
		
		return JSONData;
	}
	
	
	
	
	
	public static void main(String args[]) throws SQLException {
		GeoLocationCountryPostCount c = new GeoLocationCountryPostCount();
		Date startDate = Date.valueOf("2008-01-10");
		Date endDate = Date.valueOf("2016-01-10");
		/*
		 * ResultSet r=c.getDataFromTable(startDate, endDate, "java").get(0);
		 
		
		while(r.next())
		{
			System.out.println(r.getString(1));
		}
		*/
		//c.getJSONCountryPostData(startDate, endDate, "java");
		//c.getDataFromTable(startDate, endDate, "java");
		System.out.println("java".toUpperCase());
		
	//	c.getJSONCountryBarChartData(c.createCountryPostHashMap(startDate, endDate, "java"));
		//System.out.println(c.getJSONCountryPostData(startDate, endDate, "java"));
		
		
		
	}

}

class ValueComparator1 implements Comparator<String> {

    Map<String, Integer> base;
    public ValueComparator1(HashMap<String,Integer> base) {
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