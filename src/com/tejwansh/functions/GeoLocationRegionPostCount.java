package com.tejwansh.functions;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.lang3.text.WordUtils;

public class GeoLocationRegionPostCount {
	JDBC_Connection connect = new JDBC_Connection();
	Connection con;
	HashMap<String, Integer> regionPostDataMap = new HashMap<String, Integer>();
	ArrayList<ResultSet> resultSetArray;
	
	/*
	 * Input: SQL.Date: StartDate and EndDate, String: Tag Name and regionName Used to generate
	 * sql.resultset containing the data from DB
	 */
	public ArrayList<ResultSet> getDataFromTable(Date startDate, Date endDate, String tagName,String regionName) {
		ResultSet resultSet;
		
		String regionNameLowerCase=WordUtils.uncapitalize(WordUtils.capitalizeFully(regionName));
		String regionCode=CountryCode.getCountryCode(regionName);
		
		String sqlString="select split_part(location,', ',1) from users a join posts b on a.id=b.owneruserid"
				+ " where  b.tags like '%<"+tagName+">%' and a.location like '%"+regionNameLowerCase+"%' or a.location like '%"+regionName+"%'"
				+ " or a.location like '% "+regionCode+" %'"
				+ "and b.creationdate::timestamp::date between '"+startDate+"' and '"+endDate+"';";
		
		con = connect.Connect();
		try {
			
		
			PreparedStatement pd = con.prepareStatement(sqlString);

			resultSet = pd.executeQuery();
			
			
			resultSetArray=new ArrayList<ResultSet>();
			resultSetArray.add(resultSet);
			
			

		} catch (SQLException e) {
			System.err
					.println("The Error Occurred In method getDataFromTable in GeoLocationRegionPostCount Class");
			e.printStackTrace();

		}

		return resultSetArray;
	}
	
	/*
	 * Input: sql.Date: StartDate and EndDate, String: Tag Name and RegionName
	 * Generates a Hashmap and calculates the number of posts for unique states in a region
	 */
	public HashMap<String, Integer> createRegionPostHashMap(Date startDate,Date endDate, String tagName, String regionName)
	{
		ArrayList<ResultSet> resultSetArrayList = getDataFromTable(startDate,
				endDate, tagName,regionName);
		ResultSet regionPostResultSet;

		String countryName;
		int count = 0, count1 = 0;
		try {
			for (int i = 0; i < resultSetArrayList.size(); i++) {
				regionPostResultSet=resultSetArrayList.get(i);
				while (regionPostResultSet.next()) {
					
					String region = (String) regionPostResultSet.getString(1);
					region=WordUtils.uncapitalize(WordUtils.capitalize(region));
					region=WordUtils.capitalizeFully(region);
					if (regionPostDataMap.get(region) == null) 
					{
							regionPostDataMap.put(region, 1);
					} 
					else 
					{
							regionPostDataMap.put(region,regionPostDataMap.get(region) + 1);

					}

						count1++;
						

				}
			}
		
			System.err.println("Number Of Regions Found: "+count1);

		} catch (SQLException e) {
			System.err.println("The Error Occurred In method createCountryPostHashMap in CountryPostsCount Class");
			e.printStackTrace();
		}
		return regionPostDataMap;
	}
	
	public String getJSONRegionPostData(Date startDate,Date endDate,String tagName,String regionName)
	{
		String JSONData="";
		HashMap<String, Integer> hashmapData = new HashMap<String, Integer>();
	    hashmapData= createRegionPostHashMap(startDate, endDate, tagName,regionName);
	    JSONData=LocationHelpMethods.generateJSONStringFormat(hashmapData,2);		 
		
		return JSONData;
	}
	
	public static void main(String args[])
	{
		
		System.out.println(WordUtils.uncapitalize(WordUtils.capitalizeFully("CanAda")));
		
		GeoLocationRegionPostCount c= new GeoLocationRegionPostCount();
		Date startDate = Date.valueOf("2008-01-10");
		Date endDate = Date.valueOf("2016-01-10");
		System.out.println(c.getJSONRegionPostData(startDate, endDate, "c++","Canada"));
		
	
	
	
		
	}

}
