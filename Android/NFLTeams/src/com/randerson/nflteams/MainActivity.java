package com.randerson.nflteams;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	Spinner spinnerCon;
	Spinner spinnerDiv;
	String selectedConference;
	String selectedDivision;
	SQLmanager teamsDB;
	ListView list;
	ApplicationDefaults defaults;
	boolean firstRun;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// setup the app preferences object
		defaults = new ApplicationDefaults(this);
		
		// check if the table should be setup
		firstRun = defaults.getData().getBoolean("run-setup", true);
		
		// set reference to the spinner objects
		spinnerCon = (Spinner) findViewById(R.id.spin_conference);
		spinnerDiv = (Spinner) findViewById(R.id.spin_division);
		
		// create a array adapter from resource file for the conference spinner
		ArrayAdapter<CharSequence> adapterC = ArrayAdapter.createFromResource(this,
		        R.array.conference_array, android.R.layout.simple_spinner_item);
		
		// create a array adapter from resource file for the division spinner
		ArrayAdapter<CharSequence> adapterD = ArrayAdapter.createFromResource(this,
		        R.array.division_array, android.R.layout.simple_spinner_item);
		
		// set the dropdown layout for the conference spinner and apply the conference adapter
		adapterC.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerCon.setAdapter(adapterC);
		
		// set the dropdown layout for the division spinner and apply the division adapter
		adapterD.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerDiv.setAdapter(adapterD);
		
		// set the query button to reference the button in layout
		Button queryBtn = (Button) findViewById(R.id.queryBtn);
		
		// set the list view to reference the listview in layout
		list = (ListView) findViewById(R.id.sqlList);
		
		// verify that the query button is valid
		if (queryBtn != null)
		{
			// set the click listener
			queryBtn.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// get the values for the spinners
					selectedConference = (String) spinnerCon.getSelectedItem();
					selectedDivision = (String) spinnerDiv.getSelectedItem();
					
					// create the CV for the WHERE clause
					ContentValues where = new ContentValues();
					
					if (selectedConference.equals("All") && !selectedDivision.equals("All"))
					{
						// add the CV pairs for the where clause
						where.put("Division", selectedDivision);
					}
					else if (!selectedConference.equals("All") && selectedDivision.equals("All"))
					{
						// add the CV pairs for the where clause
						where.put("Conference", selectedConference);
					}
					else if (!selectedConference.equals("All") && !selectedDivision.equals("All"))
					{
						// add the CV pairs for the where clause
						where.put("Conference", selectedConference);
						where.put("Division", selectedDivision);
					}
					else
					{
						// set the where to null for global query
						where = null;
					}
					
					// method for querying the db and displaying the data
					displayResults(where);
				}
			});
		}
		
			// call the method for creating and opening the SQL database
			// passing in the JSON object
			createDatabase(dataJSON.JSONify(dataJSON.JSON));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	// method for creating the SQL database from the JSON object
	public void createDatabase(JSONObject json)
	{
		try {
			
			// get the base JSON array
			JSONArray jArr = json.getJSONArray("leagues").getJSONObject(0).getJSONArray("teams");
			
			// create the SQLite database
			teamsDB = new SQLmanager(this, "teamsDB.db");
			
			if (firstRun)
			{
				// set the app prefs to not run setup again
				defaults.setBool("run-setup", false);

				// iterate through the json setting the column/row data
				for (int x = 0; x < jArr.length(); x++)
				{
					
					// set the json object to the object at the current index
					JSONObject jsonQuery = jArr.getJSONObject(x);
					
					// create strings from the the json object fields for representing
					// the table column names and values
					String col_team = jsonQuery.getString("name");
					String col_location = jsonQuery.getString("location");
					String col_conference = jsonQuery.getString("conference");
					String col_division = jsonQuery.getString("division");
					String col_abbr = jsonQuery.getString("abbreviation");
					
					// create the content values object for populating the table values
					ContentValues values = new ContentValues();
					
					// add the json data to the content values object
					values.put("Team", col_team);
					values.put("City", col_location);
					values.put("Conference", col_conference);
					values.put("Division", col_division);
					values.put("Abbr", col_abbr);
					
					// check if this is the first entry, if so create the table
					// otherwise insert the new row into the table
					if (x == 0)
					{
						teamsDB.createTable("NFL", values);
					}
					else
					{
						// inserts the table row and data into the table
						teamsDB.insertRecord(values);
					}
					
				}
			}
			else
			{
				// table is created so set it to open
				teamsDB.setTable("NFL");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// method for showing the SQL query result data
	public void displayResults(ContentValues where)
	{
		// create a string array for holding the select data to query for
		String[] selectData = {"*"};
		
		// get the query results cursor
		Cursor results = teamsDB.query(selectData, where);
		
		// retrieve the column names from the cursor
		String[] colNames = results.getColumnNames();
		
		// retrieve the number of columns in the cursor
		int numCols = results.getColumnCount();
		
		// create an array list for populating listview adapter
		ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
		
		// iterate through the cursor and extract the data as key/pairs
		while (results.moveToNext())
		{
			HashMap<String, String> qMap = new HashMap<String, String>();
			
			for (int i = 0; i < numCols; i++)
			{
				// get the array index for the current key
				int keyIndex = results.getColumnIndex(colNames[i]);
				
				// extract the cursor data for the keyIndex
				String colData = results.getString(keyIndex);
				
				// store the column data in hashmap
				qMap.put(colNames[i], colData);
			}
			
			// store the hashmap into the listData array
			listData.add(qMap);
		}
		
		// empty the listview to prevent duplicate data
		//list.removeAllViews();
		
		// create simple adapter for list view
		SimpleAdapter listAdapter = new SimpleAdapter(this, listData, R.layout.listview, 
		new String[] {"Team", "City", "Abbr", "Conference", "Division"}, new int[] {R.id.team, R.id.city, R.id.abbr, R.id.conference, R.id.division});
		
		//list.addHeaderView(findViewById(R.id.listHeader));
		
		// set the list view adapter
		list.setAdapter(listAdapter);
	}
}
