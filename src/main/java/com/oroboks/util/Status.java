package com.oroboks.util;

/**
 * @author Aditya Narain
 * Status possibilities for an entity.
 */
public enum Status {

	/**
	 * Status is active returning status code of 1
	 */
	ACTIVE(1),
	/**
	 * Status is inactive returning status code of 0
	 */
	INACTIVE(0);
	private  int statusValue;
	private Status(int val){
		statusValue = val;
	}
	/**
	 * Returns the status of the user
	 * @return status of the user. 
	 */
	public Integer getStatus(){
		return  statusValue;
	}
	
}
