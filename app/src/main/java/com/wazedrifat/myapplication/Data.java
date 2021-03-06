package com.wazedrifat.myapplication;

import java.util.Calendar;
import java.util.Date;

public class Data {
	private Date time;
	private String value;

	public Data() {
	}

	public Data(String val) {
		time = Calendar.getInstance().getTime();
		this.value = val;
	}

	public Data(Date time, String value) {
		this.time = time;
		this.value = value;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
