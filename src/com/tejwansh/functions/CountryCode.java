package com.tejwansh.functions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang3.text.WordUtils;

import com.sun.xml.internal.fastinfoset.algorithm.BuiltInEncodingAlgorithm.WordListener;

public class CountryCode {

	static String countryCode,countryName;
	static boolean checkCountryCode;
	static boolean checkCountryName;
	static Map<String, String> countries = new HashMap<>();
	
	/*
	 * Method to Return Country Code for a CountryName.
	 */
	public static String getCountryCode(String countryName)
	{
		
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
        countryCode=countries.get(countryName);
		return countryCode;
	}
	
	/*
	 * Method to Return CountryName for a CountryCode.
	 */
	public static String getCountryName(String countryCode)
	{
		
		Locale locale=new Locale("",countryCode);
		countryName=locale.getDisplayCountry();
		
		return countryName;
	
	}
	
	
	/* This Method Checks whether the country name exists or not
	 * This is done by taking input as COUNTRYNAME and checking 
	 * whether it has an equivalent country code or not.
	 */
	public static boolean checkCountryName(String countryName)
	{
		countryName=WordUtils.capitalizeFully(countryName);
		for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
		
		if(countries.get(countryName)!=null)
		{
			checkCountryName=true;
		}
		else
		{
			checkCountryName=false;
		}
		 
		return checkCountryName;
	}
	
	/* This Method Checks whether the country code exists or not
	 * This is done by taking input as COUNTRYCODE and checking 
	 * whether it has an equivalent country name or not.
	 */
	public static boolean checkCountryCode(String countryCode)
	{
		Locale locale=new Locale("",countryCode);
		String countryName=locale.getDisplayCountry();
		if(countryName.equalsIgnoreCase(countryCode))
		{
			
			checkCountryCode=false;
		}
		else if(countryName !=countryCode)
		{
			
			checkCountryCode=true;
		}
		return checkCountryCode;
	}
	
	public static void main(String args[])
	{
		boolean f=checkCountryCode("US");
		System.out.println(f);
		if(f==true)
		{
			System.out.println("SSS");
		}
		if(f==false)
		{
			System.out.println("aassassa");
		}
	}
}
