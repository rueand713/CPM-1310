package com.randerson.libs;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class SQLmanager {
	
	// database name
	public String DATABASE_NAME = null;
	
	// table name
	public String TABLE_NAME = null;
	
	// class context
	public Context CONTEXT = null;
	
	// sql database
	public SQLiteDatabase DATABASE = null;


	// constructor for creating AND OR opening an sql database
	public SQLmanager(Context context, String database)
	{
		// set the class context
		CONTEXT = context;
		
		// set the database
		DATABASE_NAME = database;
		
		File dbFile = new File(context.getFilesDir() + DATABASE_NAME);
		
		try {
			DATABASE = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.CREATE_IF_NECESSARY);
			
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
		  catch (Exception e) {
			e.printStackTrace();
		}
	}

	// method to create a new sql table
	public void createTable(String table, ContentValues columns)
	{	
		// set the current table to the new table
		TABLE_NAME = table;

		// verify that the table does NOT exist
		if (SQLiteDatabase.findEditTable(table) != null)
		{	
			 // creates a list of the object keys
			 ArrayList<String> keys = parseCV(columns);
			 
			 // string to represent the column data
			 String strColumns = "";
			 
			 // iterate over the keys adding the key (column name) and the value (column value)
			 for (int i = 0; i < keys.size(); i++)
			 {
		
				 // add the key/value pairs
				 strColumns += keys.get(i) + " " + columns.getAsString(keys.get(i));
				 
				 // add the value separator (comma)
				 if (i+1 != keys.size())
				 {
					 strColumns += ", ";
				 }
			 }
			 
			 Log.i("TABLE DATA", strColumns);
			
			 // creates the formatted sql table creation statement
			 String CREATE_TABLE = "CREATE TABLE IF NOT EXISTS " + table + " ( " + strColumns + " )";
			 
			 // executes the statement to create a table
			DATABASE.execSQL(CREATE_TABLE);
		}
	}
	
	// method for updating an sql table record with 0 or more WHERE args of inclusion (AND)
	public void updateRecord(ContentValues columns, ContentValues where)
	{
		// verify that the table does exist
		if (SQLiteDatabase.findEditTable(TABLE_NAME) != null)
		{
			if (where == null)
			{
				DATABASE.update(TABLE_NAME, columns, null, null);
			}
			else if (where != null)
			{
				 // creates a list of the object keys using the parse content values method
				ArrayList<String> keys = parseCV(where);
				
				// method for retrieving the where string
				String whereString = parseWhereString(keys);
				 
				 // method for retrieving the whereArguments
				 String[] whereArgs = parseWhereArgs(where, keys);
				 
				 // update the database record
				DATABASE.update(TABLE_NAME, columns, whereString, whereArgs);
			}
		}
	}
	
	// method for inserting new record into the sql table; keys (column names), values (column values)
	public void insertRecord(ContentValues values)
	{	
		// verify that the table exists
		if (SQLiteDatabase.findEditTable(TABLE_NAME) != null)
		{
			DATABASE.insert(TABLE_NAME, "NULL", values);
		}
	}
	
	// default method for selecting and returning data from database
	public Cursor query(String query)
	{
		Cursor results = null;
		
		// verify that the table exists
		if (SQLiteDatabase.findEditTable(TABLE_NAME) != null)
		{
			results = DATABASE.rawQuery(query, null);
		}
		
		return results;
	}
	
	// method for selecting and returning data from SQL database with WHERE clause
	public Cursor query(String[] selectData, ContentValues where)
	{
		Cursor results = null;
		
		// verify that the table does exist
		if (SQLiteDatabase.findEditTable(TABLE_NAME) != null)
		{
			// create the base selection sql statement string
			String select = "SELECT ";
			
			// iterate over the select data array to concatenate the select string
			for (int i = 0; i < selectData.length; i++)
			{
				// append the string at the current index to the select string
				select += selectData[i];
				
				// verify that there is more than one object in the select data
				// and that the current index is not the last
				if (i+1 != selectData.length)
				{
					// add the comma separator
					select += ", ";
				}
			}
			
			// add the from clause to the selection statement
			select += " FROM " + TABLE_NAME;
			
			// add the where clause if the CV is not null
			if (where != null)
			{
				// get the where string from the parsing method
				String whereString = parseWhereString(parseCV(where));
				
				select += " WHERE " + whereString;
				
				Log.i("WHERE", select);
				
				results = DATABASE.rawQuery(select, parseWhereArgs(where, parseCV(where)));
			}
			else
			{
				results = DATABASE.rawQuery(select, null);
			}
		}
		
		return results;
	}
	
	// method for retrieving CV key set
	private ArrayList<String> parseCV(ContentValues CV)
	{
		// creates a list of the object keys
		 ArrayList<String> keys = new ArrayList<String>();
		
		if (CV != null)
		{
			// creates a keyset to iterate over
			 Iterator<String> keyset = CV.keySet().iterator();
			 
			 // loops through the keyset until their are no more keys
			 while (keyset.hasNext())
			 {
				 // adds each key to the list
				keys.add((String) keyset.next());
			 }
		}
		 
		 // return the CV keys
		 return keys;
	}
	
	// method for creating where arguments from ContentValue object
	private String[] parseWhereArgs(ContentValues CV, ArrayList<String> keys)
	{
		 // string array for where args
		 String whereArgs = "";
		 
		 if (keys != null && CV != null)
		 {
			// iterate over the keys adding the key (column name) and the value (column value)
			 for (int i = 0; i < keys.size(); i++)
			 {
				 // add the values to the where args string
				 whereArgs +=  CV.getAsString(keys.get(i));
				 
				 // add the value separator (comma)
				 if (i+1 < keys.size())
				 {
					 whereArgs += ",";
				 }
			 }
		 }
		 
		 Log.i("Where Args", whereArgs);
		 
		 return whereArgs.split(",");
	}
	
	// method for creating a where string from array list of CV keys
	private String parseWhereString(ArrayList<String> keys)
	{
		// string to represent the column data
		 String whereString = "";
		 
		 if (keys != null)
		 {
			// iterate over the keys adding the key (column name) and the value (column value)
			 for (int i = 0; i < keys.size(); i++)
			 {
		
				 // add the key/value pairs to the where clause string
				 whereString += keys.get(i) + " = ?";
				 
				 // add the value separator (comma)
				 if (i+1 < keys.size())
				 {
					 whereString += " AND ";
				 }
			 }
		 }
		 
		 Log.i("Where Clause", whereString);
		 
		 return whereString;
	}
	
	// method for setting/changing the current target database table
	public void setTable(String name)
	{
		TABLE_NAME = name;
	}
	
	// method for removing a sql lite table
	public void removeTable()
	{
		DATABASE.delete(TABLE_NAME, null, null);
	};
	
	// method for closing the sql lite database
	public void closeDatabase()
	{
		DATABASE.close();
	}
}
