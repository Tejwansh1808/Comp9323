package com.tejwansh.functions;

import java.util.HashMap;

import org.apache.commons.lang3.text.WordUtils;

public class LocationHelpMethods {
	
	/*
	 * Input: String: Country
	 * This Method is used to Check if the String is a country or a 
	 * valid country code. 
	 * If it is a country name then it formats country string into a proper formed string 
	 * using WORDUTILS from APACHE.Lang API and returns the country name. 
	 * If it is a country code then it returns a country name.
	 * If it is neither a country or country code it returns a NOTACOUNTRY
	 */
	public static String  checkCountryExists(String country)
	{
		boolean countryNameExists=true;
		boolean countryCodeExists=true;
		String countryName="";
		countryNameExists=CountryCode.checkCountryName(country);
		countryCodeExists=CountryCode.checkCountryCode(country);
		if(countryNameExists)
		{
			
			countryName=WordUtils.capitalizeFully(country);
		}
		else if(countryCodeExists)
		{	
			
			countryName=CountryCode.getCountryName(country);
		}
		else
		{
			
			countryName="NotACountry";
		}
				
		return countryName;
	}
	
	
	
	/*Input: HashMap(String: CountryName, Integer: Number of Post) 
	 * This Method is used to convert a HashMap<String,Integer> to 
	 * a String which is formatted according to JSON
	 * 
	 */
	public static String generateJSONStringFormat(HashMap<String, Integer> countryPostCount,int type)
	{
		String JSONDataString="";
		if(type==1)
		{
		JSONDataString+="[ \n ['Country' ,'Posts']";
		}
		if(type==2)
		{
		JSONDataString+="[ \n ['State' ,'Posts']";
		}
		for(String name: countryPostCount.keySet())
		{
			String key=name.toString();
			Integer value=countryPostCount.get(name);
			JSONDataString+=", \n ['"+key+"',"+value+"]";
			
			//System.out.println(key + "  "+ value);		
					
		}
		JSONDataString+="\n]";
		
		
		return JSONDataString;
		
		
	}

}
