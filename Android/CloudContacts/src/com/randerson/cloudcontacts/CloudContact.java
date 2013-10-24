package com.randerson.cloudcontacts;

import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;
import com.kinvey.java.model.KinveyMetaData;

public class CloudContact extends GenericJson {

	// GSON key name mappings
	@Key("_id")
	public String id;
	@Key("firstName")
	public String firstName;
	@Key("lastName")
	public String lastName;
	@Key("age")
	public int age;
	@Key("height")
	public int height;
	@Key("weight")
	public int weight;
	@Key("emailAddress")
	public String emailAddress;
	@Key("phoneNumber")
	public String phoneNumber;
	@Key("employed")
	public boolean employed;
	@Key("_kmd")
	public KinveyMetaData metaData;
	
	// constructor
	public CloudContact(){};
}
