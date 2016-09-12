package com.tejwansh.functions;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CountryCode {

	static String countryCode;
	static boolean checkCode;
	static Map<String, String> countries = new HashMap<>();
	
	public static String getCountryCode(String country)
	{
		
        for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
        countryCode=countries.get(country);
		return countryCode;
	}
	
	public static boolean checkCountryCode(String country)
	{
		for (String iso : Locale.getISOCountries()) {
            Locale l = new Locale("", iso);
            countries.put(l.getDisplayCountry(), iso);
        }
		
		if(countries.get(country)!=null)
		{
			checkCode=true;
		}
		else
		{
			checkCode=false;
		}
		 
		return checkCode;
	}
}
