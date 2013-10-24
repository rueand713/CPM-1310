package com.randerson.cloudcontacts;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

@SuppressLint("DefaultLocale")
public class NewContact extends Activity {

	boolean didBackOut = true;
	boolean isNewContact = true;
	Intent returnData;
	String entityID = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_new_contact);
		
		// get the data passed in (if any)
		Intent sentData = getIntent();
		
		// verify that the intent is valid
		if (sentData != null)
		{
			Bundle bundle = sentData.getExtras();
			
			if (bundle != null && bundle.containsKey("editContact"))
			{
				// set the new contact boolean
				isNewContact = false;
				
				// get the contact id string
				entityID = bundle.getString("entityID");
				
				// populate the editText fields
				populateFields(bundle);
			}
		}
		
		// create the button from layout
		Button saveButton = (Button) findViewById(R.id.saveButton);
		
		// verify that the button is valid
		if (saveButton != null)
		{
			// set the click listener for the button
			saveButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// set the backout boolean to false
					didBackOut = false;
					
					// save the new data
					saveData();
					
				}
			});
		}
		
		// create the cancel button from layout
		Button cancelButton = (Button) findViewById(R.id.cancelButton);
		
		// verify that the button is valid
		if (cancelButton != null)
		{
			// set the button click listener
			cancelButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// set the backout bool to true
					didBackOut = true;
					
					finish();
				}
			});
		}
	}

	@Override
	public void finish() {
		
		if (didBackOut == false)
		{
			if (returnData != null)
			{
				// set the return data and params for the activity
				setResult(Activity.RESULT_OK, returnData);
			}
		}
		else
		{
			// set the param to reflect the user cancel action
			setResult(Activity.RESULT_CANCELED);
		}
	
		super.finish();
	}


	@SuppressLint("DefaultLocale")
	public void saveData()
	{
		// create references to the edit text objects in the layout
		EditText fnameField = (EditText) findViewById(R.id.firstNameEdit);
		EditText lnameField = (EditText) findViewById(R.id.lastNameEdit);
		EditText emailField = (EditText) findViewById(R.id.emailAddressEdit);
		EditText phoneField = (EditText) findViewById(R.id.phoneNumberEdit);
		EditText ageField = (EditText) findViewById(R.id.ageEdit);
		EditText heightField = (EditText) findViewById(R.id.heightEdit);
		EditText weightField = (EditText) findViewById(R.id.weightEdit);
		EditText employedField = (EditText) findViewById(R.id.employedEdit);
		
		// grab the string values of the edit text fields
		String first = fnameField.getText().toString();
		String last = lnameField.getText().toString();
		String email = emailField.getText().toString();
		String phone = phoneField.getText().toString();
		String age = ageField.getText().toString();
		String weight = weightField.getText().toString();
		String height = heightField.getText().toString();
		String employed = employedField.getText().toString();
		
		// initialize the returnData intent
		returnData = new Intent();
		
		// put the textfield values in the intent for returning
		returnData.putExtra("firstName", first);
		returnData.putExtra("lastName", last);
		returnData.putExtra("emailAddress", email);
		returnData.putExtra("phoneNumber", phone);
		returnData.putExtra("age", Integer.parseInt(age));
		returnData.putExtra("weight", Integer.parseInt(weight));
		returnData.putExtra("height", Integer.parseInt(height));
		
		employed = employed.toLowerCase();
		
		// determine the bool value to pass for the employed field
		if (employed.equals("true") || employed.equals("yes"))
		{
			returnData.putExtra("employed", true);
		}
		else
		{
			returnData.putExtra("employed", false);
		}
		
		// check if this contact is new or existing
		if (isNewContact == false)
		{
			// add the entityID of the contact to be edited
			returnData.putExtra("entityID", entityID);
		}
		
		// end the activity
		finish();
		
	}
	
	public void populateFields(Bundle bundle)
	{
		// create references to the edit text objects in the layout
		EditText fnameField = (EditText) findViewById(R.id.firstNameEdit);
		EditText lnameField = (EditText) findViewById(R.id.lastNameEdit);
		EditText emailField = (EditText) findViewById(R.id.emailAddressEdit);
		EditText phoneField = (EditText) findViewById(R.id.phoneNumberEdit);
		EditText ageField = (EditText) findViewById(R.id.ageEdit);
		EditText heightField = (EditText) findViewById(R.id.heightEdit);
		EditText weightField = (EditText) findViewById(R.id.weightEdit);
		EditText employedField = (EditText) findViewById(R.id.employedEdit);
		
		// grab the string values of the edit text fields
		String first = bundle.getString("firstName");
		String last = bundle.getString("lastName");
		String email = bundle.getString("emailAddress");
		String phone = bundle.getString("phoneNumber");
		int age = bundle.getInt("age");
		int weight = bundle.getInt("weight");
		int height = bundle.getInt("height");
		boolean employed = bundle.getBoolean("employed");
		
		// set the edit textfield text with the contact data
		fnameField.setText(first);
		lnameField.setText(last);
		emailField.setText(email);
		phoneField.setText(phone);
		ageField.setText("" + age);
		heightField.setText("" + height);
		weightField.setText("" + weight);
		
		// set the employed string to match the employed boolean value
		if (employed == true)
		{
			employedField.setText("TRUE");
		}
		else
		{
			employedField.setText("FALSE");
		}
	}
	
}
