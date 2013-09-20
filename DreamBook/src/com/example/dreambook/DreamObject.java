package com.example.dreambook;

import java.util.Date;

public class DreamObject {

	private Date dreamDate;
	private String dreamSubject;
	private String dreamContent;

	public DreamObject() {
	}

	public void setDreamText(String string) {
		String[] splittedString = string.split(":~:");
		dreamSubject = splittedString[0];
		dreamContent = splittedString[1];
	}

	public void setDreamDate(Date date) {
		dreamDate = date;
	}

	public String getDreamContent() {
		return dreamContent;
	}

	public String getDreamSubject() {
		return dreamSubject;
	}


	public String getDreamDate() {
		return dreamDate.toString();
	}
}