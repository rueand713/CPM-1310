package com.randerson.cloudcontacts;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import com.kinvey.android.AsyncAppData;
import com.kinvey.android.Client;
import com.kinvey.android.callback.KinveyListCallback;
import com.kinvey.android.callback.KinveyUserCallback;
import com.kinvey.java.User;
import com.kinvey.java.core.KinveyClientCallback;
import com.randerson.libs.ApplicationDefaults;
import com.randerson.libs.SQLmanager;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

public class MainActivity extends Activity {

	Client kinveyClient;
	AsyncAppData<CloudContact> dataStore;
	CloudContact[] contacts;
	SQLmanager sqlManager;
	ApplicationDefaults defaults;
	String[] query;
	ListView list;
	Cursor resultSet;
	Spinner querySpinner;
	boolean queriedResults = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		// set the default query string
		query = new String[]{"*"};
		
		// retrieve the api credentials from string resources
		String appKey = getResources().getString(R.string.app_key);
		String appSecret = getResources().getString(R.string.app_secret);
		
		// initialize the sql manager class
		sqlManager = new SQLmanager(this, "CloudContacts.db");
		
		// initialize the app defaults class
		defaults = new ApplicationDefaults(this);
		
		// initialize the kinveyClient class
		kinveyClient = new Client.Builder(appKey, appSecret, this.getApplicationContext()).build();
		
		// create the spinner from layout
		querySpinner = (Spinner) findViewById(R.id.queryData);
		
		// verify that the spinner is valid and set up the functionality
		if (querySpinner != null)
		{
			String[] values = {"All", "Employed = true", "Employed = false", "Age > 30", "Age < 30", "Height > 50", "Height < 50", "Weight > 120", "Weight < 120"};
			
			// create an ArrayAdapter object
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, values);
			
			// set the spinner dropdown resource
			adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
			
			// set the spinner adapter
			querySpinner.setAdapter(adapter);
		}
		
		// create query button from layout
		Button queryButton = (Button) findViewById(R.id.queryButton);
		
		// verify that the button is valid
		if (queryButton != null)
		{
			// set the button click listener
			queryButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// call method to query the sql
					doResultQuery();
				}
			});
		}
		
		// set the reference to the layout listview
		list = (ListView) findViewById(R.id.contactList);
		
		// set the listview item click listener
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int row,
					long arg3) {
				
				// method to start the viewing/editing contact details activity
				viewContact(row);
				
			}
		});
		
		// verify that the client is created properly
		if (kinveyClient != null)
		{
			// initialize the appData class with the kinvey data collection and cloudcontact class
			dataStore = kinveyClient.appData("ContactsCollection", CloudContact.class);
						
			// check if there is still an active user session
			if (kinveyClient.user().isUserLoggedIn() == false)
			{
				// create and log the user in
				userLogin();
			}
			else if (kinveyClient.user().isUserLoggedIn() == true)
			{
				// log the previous user session out
				//kinveyClient.user().logout().execute();
				
				// login a new user session
				//userLogin();
				
				retrieveKinveyData();
			}
		}
		
		// create the new button reference from the layout
		Button newButton = (Button) findViewById(R.id.newButton);
		
		// verify that the button is valid object
		if (newButton != null)
		{
			// set the click listener
			newButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
				
					// create the new contact activity intent
					Intent newContact = new Intent(getApplicationContext(), NewContact.class);
					
					// verify that the intent is valid
					if (newContact != null)
					{
						// start the add contact activity
						startActivityForResult(newContact, 1);
					}
				}
			});
		}
		
		// create the sync button reference from the layout
		Button syncButton = (Button) findViewById(R.id.syncButton);
		
		// verify that the button is valid object
		if (syncButton != null)
		{
			// set the button click listener
			syncButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// refetch the kinveyData and check if the local data needs to be
					// synchronized
					retrieveKinveyData();
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	public void retrieveKinveyData()
	{		
		// retrieve all of the kinvey datastore records
		dataStore.get(new KinveyListCallback<CloudContact>()
		{

			@Override
			public void onFailure(Throwable error) {
				Log.e("Fetch Error", error.toString());
			}

			@Override
			public void onSuccess(CloudContact[] kinveyContacts) {
				
				// on success set the global cloudContacts array to the one returned from kinvey store
				contacts = kinveyContacts;
				
				boolean firstRun = defaults.getData().getBoolean("first-run", true);
				
				// check if this is the first application run
				if (firstRun)
				{
					// first run detected, setup the sql database and table
					setupSQL();
					
					// set the first run to false
					defaults.setBool("first-run", false);
					
					// set the last modified date value
					defaults.setString("lastModified", getTimestamp());
				}
				else
				{
					// the sql database has been setup previously set the database table to use
					sqlManager.setTable("Contacts");
					
					// bool value for determing if synchronization is required
					boolean doSync = false;
					
					// query the sql database to get the results for counting
					resultSet = sqlManager.query(query, null);
					
					// store the number of items in each database
					int numKinveyRecords = contacts.length;
					int numSQLRecords = resultSet.getCount();
					
					if (numKinveyRecords != numSQLRecords)
					{
						// table is out of sync and needs to be updated
						doSync = true;
					}
					else
					{
						// get the last modified date string for the local database
						String localLMDStr = defaults.getData().getString("lastModified", getTimestamp());
						
						// retrieve a java date object from the date string
						java.util.Date localLMD = getDate(localLMDStr);
						
						for (int i = 0; i < contacts.length; i++)
						{
							// get the last modified date string for the kinvey object
							String kinveyLMDStr = contacts[i].metaData.getLastModifiedTime().replaceAll("[.][A-Za-z0-9]{1,}", "GMT -0000");
							
							// retrieve a java date object from the date string
							java.util.Date kinveyLMD = getDate(kinveyLMDStr);
							
							// verify the date objects are valid
							if (localLMD != null && kinveyLMD != null)
							{
								// check if the local sql table has a modified date the precedes that of the kinvey db date
								if (localLMD.before(kinveyLMD))
								{
									// table is out of sync and needs to be updated
									doSync = true;
									
									break;
								}
							}
						}
					}
					
					// check if the table needs to be synchronized with the kinvey db
					if (doSync)
					{	
						// check if there are more records in the kinvey db than the sql table to replace the table
						// or if the records are equal to run an update sequence
						if (numKinveyRecords > numSQLRecords)
						{
							// remove the old table data
							sqlManager.removeTable();
							
							// recreate the local sql table with the kinvey data
							setupSQL();
						}
						else if (numKinveyRecords == numSQLRecords)
						{
							// update the local sql database table
							updateSQL();
						}
						
					}
					else
					{
						// show the sql data results in table
						displayResults(null, true);
					}
				}
			}
	
		});
	}

	@Override
	public void finish() {
		super.finish();
		
		// log the user out
		kinveyClient.user().logout().execute();
	}
	
	public void userLogin()
	{
		// create a new implicit user (no login)
		kinveyClient.user().login(new KinveyUserCallback() {
			
			@Override
			public void onSuccess(User result) {
				Log.i("User Success", "User logged in with ID: " + result.getId());
				
				// retrieve the contacts in the kinvey datastore
				retrieveKinveyData();
				
			}
			
			@Override
			public void onFailure(Throwable error) {
				Log.e("User Error", error.toString());
			}
		});
	}
	
	public void setupSQL()
	{
		if (contacts != null)
		{
			// create an empty contentValues array for storing the column data for each row
			ContentValues[] columnArray = new ContentValues[contacts.length];
			
			// iterate over the contacts array extracting the kinveydata and setting it to a CV object for table insertion
			for (int i = 0; i < contacts.length; i++)
			{
				// create a cloud contact object from the current index of cloudContact object array
				CloudContact contact = contacts[i];
				
				// create a new contentValues object for storing the contact data
				ContentValues columnData = new ContentValues();
				
				// add the contact data to the column CV object
				columnData.put("firstName", contact.firstName);
				columnData.put("lastName", contact.lastName);
				columnData.put("emailAddress", contact.emailAddress);
				columnData.put("phoneNumber", contact.phoneNumber);
				columnData.put("age", contact.age);
				columnData.put("weight", contact.weight);
				columnData.put("height", contact.height);
				columnData.put("employed", contact.employed);
				columnData.put("entityID", contact.id);
				
				// set the current index of the CV array to the CV object created
				columnArray[i] = columnData;
			}
			
			// create a new contentValues object for creating the table
			ContentValues tableData = new ContentValues();
			
			// add the contact data to the column CV object
			tableData.put("_id", "INTEGER PRIMARY KEY AUTOINCREMENT");
			tableData.put("firstName", "TEXT");
			tableData.put("lastName", "TEXT");
			tableData.put("emailAddress", "TEXT");
			tableData.put("phoneNumber", "TEXT");
			tableData.put("age", "INTEGER");
			tableData.put("weight", "INTEGER");
			tableData.put("height", "INTEGER");
			tableData.put("employed", "INTEGER");
			tableData.put("entityID", "TEXT");
			
			// create the table
			sqlManager.createTable("Contacts", tableData);
			
			// iterate over the columnArray and insert the records
			for (int n = 0; n < columnArray.length; n++)
			{
				// not the first loop, all other CV data is inserted into the table
				sqlManager.insertRecord(columnArray[n]);
			}
		}
		
		// show the sql data table
		displayResults(null, true);
	}
	
	public void displayResults(ContentValues where, boolean doQuery)
	{
		if (doQuery)
		{
			resultSet = sqlManager.query(query, where);
		}
		
		if (resultSet != null)
		{
			// retrieve the column names from the cursor
			String[] colNames = resultSet.getColumnNames();
			
			// retrieve the number of columns in the cursor
			int numCols = resultSet.getColumnCount();
			
			// create an array list for populating listview adapter
			ArrayList<HashMap<String, String>> listData = new ArrayList<HashMap<String, String>>();
			
			// iterate through the cursor and extract the data as key/pairs
			while (resultSet.moveToNext())
			{
				HashMap<String, String> qMap = new HashMap<String, String>();
				
				for (int i = 0; i < numCols; i++)
				{
					// get the array index for the current key
					int keyIndex = resultSet.getColumnIndex(colNames[i]);
					
					// extract the cursor data for the keyIndex
					String colData = resultSet.getString(keyIndex);
					
					// store the column data in hashmap and remove the quotes
					qMap.put(colNames[i], colData.replace("\"", ""));
				}
				
				// store the hashmap into the listData array
				listData.add(qMap);
			}
			
			// create simple adapter for list view
			SimpleAdapter listAdapter = new SimpleAdapter(this, listData, R.layout.listview, 
			new String[] {"firstName", "lastName"}, new int[] {R.id.fnameField, R.id.lnameField});
			
			// set the list view adapter
			list.setAdapter(listAdapter);
		}
	}
	
	// method for getting a timestamp of the current time in ISO 8601
	public String getTimestamp()
	{
		// get a current date time object
		java.util.Date date = new java.util.Date();
		
		// create a dateformatting object for the ISO 8601 format
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		
		// set the formatted date string
		String dateString = df.format(date);
		
		return dateString;
	}
	
	// method for returning a date from a date formatted string
	public java.util.Date getDate(String dateString)
	{
		// init a null date object
		java.util.Date date = null;
		
		// create a dateformatting object for the ISO 8601 date format
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US);
		
		try {
			
			// try to parse the date string to a java date object
			date = df.parse(dateString);
		
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return date;
	}
	
	// method for updating the sql table
	public void updateSQL()
	{
		// iterate over the contacts array and update each record
		for (int i = 0; i < contacts.length; i++)
		{
			// create a cloud contact object from the current index of cloudContact object array
			CloudContact contact = contacts[i];
			
			// create a new contentValues object for storing the contact data
			ContentValues columnData = new ContentValues();
			
			// add the contact data to the column CV object
			columnData.put("firstName", contact.firstName);
			columnData.put("lastName", contact.lastName);
			columnData.put("emailAddress", contact.emailAddress);
			columnData.put("phoneNumber", contact.phoneNumber);
			columnData.put("age", contact.age);
			columnData.put("weight", contact.weight);
			columnData.put("height", contact.height);
			columnData.put("employed", contact.employed);
			columnData.put("entityID", contact.id);
			
			// create a CV object to hold the WHERE clause data
			ContentValues where = new ContentValues();
			
			// add the WHERE clause data for the primary key
			where.put("_id", (i+1));
			
			// pass in the CV objects to update the table data
			sqlManager.updateRecord(columnData, where);
		}
		
		// set the last modified date value
		defaults.setString("lastModified", getTimestamp());
		
		// show the sql data results in table
		displayResults(null, true);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		
		// ensure that the return result is fine before taking action
		if (requestCode == 1 && resultCode == Activity.RESULT_OK)
		{
			Bundle bundle = data.getExtras();
			
			if (bundle != null)
			{
				CloudContact newContact = new CloudContact();
				
				newContact.set("firstName", bundle.get("firstName"));
				newContact.set("lastName", bundle.get("lastName"));
				newContact.set("emailAddress", bundle.get("emailAddress"));
				newContact.set("phoneNumber", bundle.get("phoneNumber"));
				newContact.set("age", bundle.get("age"));
				newContact.set("weight", bundle.get("weight"));
				newContact.set("height", bundle.get("height"));
				newContact.set("employed", bundle.get("employed"));
				
				// check if the bundle has an entityID key
				// if so, set the new contact id to the entityID so that the contact
				// is updated and a new one is not created
				if (bundle.containsKey("entityID"))
				{
					newContact.set("_id", bundle.get("entityID"));
				}
				
				// access kinvey saving method
				dataStore.save(newContact, new KinveyClientCallback<CloudContact>() {
					
					@Override
					public void onSuccess(CloudContact result) {
						Log.i("Save Success", "Contact saved successfully");
						
						// reretrieve the kinvey data for synchonization
						retrieveKinveyData();
					}
					
					@Override
					public void onFailure(Throwable error) {
						Log.e("Save Error", error.toString());
					}
				});
			}
		}
	}
	
	public void viewContact(int contactPosition)
	{
		// create a cloudcontact object from the contact object at the index of the row clicked in the table
		CloudContact contact = contacts[contactPosition];;
		
		// checks if the view is for queried results
		if (queriedResults)
		{
			resultSet.moveToPosition(contactPosition);
			
			// get the id of the clicked item contact
			String id = resultSet.getString(resultSet.getColumnIndex("_id"));
			
			// iterate over the contacts for the matching id
			/*for (int i = 0; i < contacts.length; i++)
			{
				// creates a temp contact
				CloudContact thisContact = contacts[i];
				
				// compare the id value of the temp contact with the id of the clicked contact
				if (thisContact.id.equals(id))
				{
					// ids match set the contact to equal the temp contact
					contact = thisContact;
					break;
				}
			}*/
			
			contact = contacts[Integer.parseInt(id)-1];
		}
		
		// verify that the contact exists
		if (contact != null)
		{
			// create the activity intent
			Intent contactDetails = new Intent(this, NewContact.class);
			
			// add the details for the contact of the clicked position
			// add a intent property 'editcontact' to signal to the activity that this is
			// a contact that already exists
			contactDetails.putExtra("editContact", true);
			contactDetails.putExtra("firstName", contact.firstName);
			contactDetails.putExtra("lastName", contact.lastName);
			contactDetails.putExtra("emailAddress", contact.emailAddress);
			contactDetails.putExtra("phoneNumber", contact.phoneNumber);
			contactDetails.putExtra("age", contact.age);
			contactDetails.putExtra("weight", contact.weight);
			contactDetails.putExtra("height", contact.height);
			contactDetails.putExtra("entityID", contact.id);
			
			// start the activity
			startActivityForResult(contactDetails, 1);
		}
	}
	
	@SuppressLint("DefaultLocale")
	public void doResultQuery()
	{	
		//{"All", "Employed = true", "Employed = false", "Age > 30", "Age < 30", "Height > 50", "Height < 50", "Weight > 120", "Weight < 120"}
		int spinnerItem = querySpinner.getSelectedItemPosition();
		
		String whereString = querySpinner.getSelectedItem().toString();
		
		if (spinnerItem == 1)
		{
			whereString = "Employed = 0";
		}
		else if (spinnerItem == 2)
		{
			whereString = "Employed = 1";
		}
		
		// sets the case to lowercase
		whereString = whereString.toLowerCase();
		
		// check if the where selection is all and change the value to empty string for no where clause
		if (whereString.equals("all"))
		{
			whereString = "";
			queriedResults = false;
		}
		else
		{
			whereString = " WHERE " + sqlManager.TABLE_NAME + "." + whereString;
			queriedResults = true;
		}
		
		// create raw select query
		String select = "SELECT * FROM " + sqlManager.TABLE_NAME + whereString;
		
		// verify that the select string is valid
		if (select != null)
		{
			resultSet = sqlManager.query(select);
			
			displayResults(null, false);
		}
	}

}
