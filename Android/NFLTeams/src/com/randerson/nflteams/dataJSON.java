package com.randerson.nflteams;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class dataJSON {

	// json formatted string 
	public static String JSON = "{\"leagues\":[{\"name\":\"National Football League\",\"abbreviation\":\"nfl\",\"shortName\":\"NFL\",\"teams\":[{\"id\":1,\"location\":\"Atlanta\",\"name\":\"Falcons\",\"nickname\":\"Atlanta\",\"abbreviation\":\"ATL\",\"color\":\"000000\",\"conference\":\"NFC\",\"division\":\"South\"},{\"id\":2,\"location\":\"Buffalo\",\"name\":\"Bills\",\"nickname\":\"Buffalo\",\"abbreviation\":\"BUF\",\"color\":\"04407F\",\"conference\":\"AFC\",\"division\":\"East\"},{\"id\":3,\"location\":\"Chicago\",\"name\":\"Bears\",\"nickname\":\"Chicago\",\"abbreviation\":\"CHI\",\"color\":152644,\"conference\":\"NFC\",\"division\":\"North\"},{\"id\":4,\"location\":\"Cincinnati\",\"name\":\"Bengals\",\"nickname\":\"Cincinnati\",\"abbreviation\":\"CIN\",\"color\":\"FF2700\",\"conference\":\"AFC\",\"division\":\"North\"},{\"id\":5,\"location\":\"Cleveland\",\"name\":\"Browns\",\"nickname\":\"Cleveland\",\"abbreviation\":\"CLE\",\"color\":\"4C230E\",\"conference\":\"AFC\",\"division\":\"North\"},{\"id\":6,\"location\":\"Dallas\",\"name\":\"Cowboys\",\"nickname\":\"Dallas\",\"abbreviation\":\"DAL\",\"color\":\"002E4D\",\"conference\":\"NFC\",\"division\":\"East\"},{\"id\":7,\"location\":\"Denver\",\"name\":\"Broncos\",\"nickname\":\"Denver\",\"abbreviation\":\"DEN\",\"color\":\"002E4D\",\"conference\":\"AFC\",\"division\":\"West\"},{\"id\":8,\"location\":\"Detroit\",\"name\":\"Lions\",\"nickname\":\"Detroit\",\"abbreviation\":\"DET\",\"color\":\"035C98\",\"conference\":\"NFC\",\"division\":\"North\"},{\"id\":9,\"location\":\"Green Bay\",\"name\":\"Packers\",\"nickname\":\"Green Bay\",\"abbreviation\":\"GB\",\"color\":\"204E32\",\"conference\":\"NFC\",\"division\":\"North\"},{\"id\":10,\"location\":\"Tennessee\",\"name\":\"Titans\",\"nickname\":\"Tennessee\",\"abbreviation\":\"TEN\",\"color\":\"2F95DD\",\"conference\":\"AFC\",\"division\":\"South\"},{\"id\":11,\"location\":\"Indianapolis\",\"name\":\"Colts\",\"nickname\":\"Indy\",\"abbreviation\":\"IND\",\"color\":\"00417E\",\"conference\":\"AFC\",\"division\":\"South\"},{\"id\":12,\"location\":\"Kansas City\",\"name\":\"Chiefs\",\"nickname\":\"KC\",\"abbreviation\":\"KC\",\"color\":\"BE1415\",\"conference\":\"AFC\",\"division\":\"West\"},{\"id\":13,\"location\":\"Oakland\",\"name\":\"Raiders\",\"nickname\":\"Oakland\",\"abbreviation\":\"OAK\",\"color\":\"000000\",\"conference\":\"AFC\",\"division\":\"West\"},{\"id\":14,\"location\":\"St. Louis\",\"name\":\"Rams\",\"nickname\":\"St. Louis\",\"abbreviation\":\"STL\",\"color\":\"00295B\",\"conference\":\"NFC\",\"division\":\"West\"},{\"id\":15,\"location\":\"Miami\",\"name\":\"Dolphins\",\"nickname\":\"Miami\",\"abbreviation\":\"MIA\",\"color\":\"006B79\",\"conference\":\"AFC\",\"division\":\"East\"},{\"id\":16,\"location\":\"Minnesota\",\"name\":\"Vikings\",\"nickname\":\"Minnesota\",\"abbreviation\":\"MIN\",\"color\":\"240A67\",\"conference\":\"NFC\",\"division\":\"North\"},{\"id\":17,\"location\":\"New England\",\"name\":\"Patriots\",\"nickname\":\"NE\",\"abbreviation\":\"NE\",\"color\":\"02244A\",\"conference\":\"AFC\",\"division\":\"East\"},{\"id\":18,\"location\":\"New Orleans\",\"name\":\"Saints\",\"nickname\":\"NO\",\"abbreviation\":\"NO\",\"color\":\"020202\",\"conference\":\"NFC\",\"division\":\"South\"},{\"id\":19,\"location\":\"New York\",\"name\":\"Giants\",\"nickname\":\"Giants\",\"abbreviation\":\"NYG\",\"color\":\"052570\",\"conference\":\"NFC\",\"division\":\"East\"},{\"id\":20,\"location\":\"New York\",\"name\":\"Jets\",\"nickname\":\"Jets\",\"abbreviation\":\"NYJ\",\"color\":174032,\"conference\":\"AFC\",\"division\":\"East\"},{\"id\":21,\"location\":\"Philadelphia\",\"name\":\"Eagles\",\"nickname\":\"Philly\",\"abbreviation\":\"PHI\",\"color\":\"06424D\",\"conference\":\"NFC\",\"division\":\"East\"},{\"id\":22,\"location\":\"Arizona\",\"name\":\"Cardinals\",\"nickname\":\"Arizona\",\"abbreviation\":\"ARI\",\"color\":\"A40227\",\"conference\":\"NFC\",\"division\":\"West\"},{\"id\":23,\"location\":\"Pittsburgh\",\"name\":\"Steelers\",\"nickname\":\"Pittsburgh\",\"abbreviation\":\"PIT\",\"color\":\"000000\",\"conference\":\"AFC\",\"division\":\"North\"},{\"id\":24,\"location\":\"San Diego\",\"name\":\"Chargers\",\"nickname\":\"San Diego\",\"abbreviation\":\"SD\",\"color\":\"042453\",\"conference\":\"AFC\",\"division\":\"West\"},{\"id\":25,\"location\":\"San Francisco\",\"name\":\"49ers\",\"nickname\":\"SF\",\"abbreviation\":\"SF\",\"color\":981324,\"conference\":\"NFC\",\"division\":\"West\"},{\"id\":26,\"location\":\"Seattle\",\"name\":\"Seahawks\",\"nickname\":\"Seattle\",\"abbreviation\":\"SEA\",\"color\":224970,\"conference\":\"NFC\",\"division\":\"West\"},{\"id\":27,\"location\":\"Tampa Bay\",\"name\":\"Buccaneers\",\"nickname\":\"Tampa Bay\",\"abbreviation\":\"TB\",\"color\":\"A80D08\",\"conference\":\"NFC\",\"division\":\"South\"},{\"id\":28,\"location\":\"Washington\",\"name\":\"Redskins\",\"nickname\":\"Washington\",\"abbreviation\":\"WSH\",\"color\":650415,\"conference\":\"NFC\",\"division\":\"East\"},{\"id\":29,\"location\":\"Carolina\",\"name\":\"Panthers\",\"nickname\":\"Carolina\",\"abbreviation\":\"CAR\",\"color\":\"2177B0\",\"conference\":\"NFC\",\"division\":\"South\"},{\"id\":30,\"location\":\"Jacksonville\",\"name\":\"Jaguars\",\"nickname\":\"Jax\",\"abbreviation\":\"JAC\",\"color\":\"00839C\",\"conference\":\"AFC\",\"division\":\"South\"},{\"id\":33,\"location\":\"Baltimore\",\"name\":\"Ravens\",\"nickname\":\"Baltimore\",\"abbreviation\":\"BAL\",\"color\":\"2B025B\",\"conference\":\"AFC\",\"division\":\"North\"},{\"id\":34,\"location\":\"Houston\",\"name\":\"Texans\",\"nickname\":\"Houston\",\"abbreviation\":\"HOU\",\"color\":\"00133F\",\"conference\":\"AFC\",\"division\":\"South\"}]}]}";
	
	// method for creating a json object from a json formatted string
	public static JSONObject JSONify(String json)
	{
		// create a new empty json object
		JSONObject jsonObject = new JSONObject();
		
		// verify that the json string is valid
		if (json != null)
		{
			try {
				
				// create a json object from the json string
				jsonObject = new JSONObject(json);
				
				Log.i("JSON", jsonObject.toString());
				
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		
		// return the json object
		return jsonObject;
	}
}
