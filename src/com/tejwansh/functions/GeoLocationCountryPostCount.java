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
			String sqlString = "Select location as Location from users a join posts b on a.id=b.owneruserid where a.location "
					+ "not like '%,%' and "
					+ "a.location <> '' "
					+ " and b.tags like '%<"
					+ tagName
					+ ">%' and b.creationdate::timestamp::date between '"
					+ startDate + "' and '" + endDate + "'; ";
			/*String sqlString2 = "select split_part(location,',',2) from users a join posts b on a.id=b.owneruserid where "
					+ "split_part(a.location,',',2) <> ''"
					+ " and b.tags like '%<"
					+ tagName
					+ ">%' and b.creationdate::timestamp::date between '"
					+ startDate + "' and '" + endDate + "'; ";
			*/
		

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

	public String getJSONCountryPostData(Date startDate,Date endDate,String tagName)
	{
		String JSONData="";
		HashMap<String, Integer> hashmapData = new HashMap<String, Integer>();
		 hashmapData= createCountryPostHashMap(startDate, endDate, tagName);
		 JSONData=LocationHelpMethods.generateJSONStringFormat(hashmapData,1);		 
		
		return JSONData;
	}
	
	
	/*
	 * Input: HashMap(String:Country, Integer: Number of Posts)
	 * This method is used to get the top 10 countries based on the
	 * number of posts in particular language
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
	
	
	
	public static void main(String args[]) {
		GeoLocationCountryPostCount c = new GeoLocationCountryPostCount();
		/*
		 * Test Data
		Date startDate = Date.valueOf("2015-01-10");
		Date endDate = Date.valueOf("2016-01-10");
		System.out.println(c.getJSONCountryPostData(startDate, endDate, "c++"));
		c.getJSONCountryBarChartData(c.createCountryPostHashMap(startDate, endDate, "java"));
		c.getJSONCountryPostData(startDate, endDate, "java");
		*/
		
		
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