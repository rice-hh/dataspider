package com.nh.dataspider.bilibili.constant;

import com.nh.dataspider.common.constant.EnumValue;

public enum BilibiliMediaType implements EnumValue<BilibiliMediaType>{
	
	ACTIVITY("activity"),
	
	WEB_GAME("web_game"),
	
	CARD("card"),
	
	MEDIA_BANGUMI("media_bangumi"),
	
	MEDIA_FT("media_ft"),
	
	BILI_USER("bili_user"),
	
	USER("user"),
	
	STAR("star"),
	
	VIDEO("video"),
	
	;

	private String value;
	
	BilibiliMediaType(String value) {
		
		this.value = value;
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public BilibiliMediaType[] enums() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
