package com.nh.dataspider.missevan.constant;

import com.nh.dataspider.common.constant.EnumValue;

public enum Episode implements EnumValue<Episode>{

	ONE("一", "1"),
	
	;
	
	private String value;
	
	private String transValue;
	
	Episode(String value, String transValue) {
		
		this.value = value;
		
		this.transValue = transValue;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public Episode[] enums() {
		return Episode.values();
	}

	public String getTransValue() {
		return transValue;
	}

}
