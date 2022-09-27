package com.nh.dataspider.manbo.model;

public class ManboDramaSet {
	
	private long setId;
	
	private String setIdStr;
	
	private long radioDramaId;
	
	private int bizType;
	
	private int setNo;
	
	private String setTitle;
	
	private String desc;
	
	private String setPic;
	
	private String setAudioUrl;
	
	private String setLrcUrl;
	
	private String linkUrl;
	
	public long getSetId() {
		return setId;
	}

	public void setSetId(long setId) {
		this.setId = setId;
	}

	public String getSetIdStr() {
		return setIdStr;
	}

	public void setSetIdStr(String setIdStr) {
		this.setIdStr = setIdStr;
	}

	public long getRadioDramaId() {
		return radioDramaId;
	}

	public void setRadioDramaId(long radioDramaId) {
		this.radioDramaId = radioDramaId;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	public int getSetNo() {
		return setNo;
	}

	public void setSetNo(int setNo) {
		this.setNo = setNo;
	}

	public String getSetTitle() {
		return setTitle;
	}

	public void setSetTitle(String setTitle) {
		this.setTitle = setTitle;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getSetPic() {
		return setPic;
	}

	public void setSetPic(String setPic) {
		this.setPic = setPic;
	}

	public String getSetAudioUrl() {
		return setAudioUrl;
	}

	public void setSetAudioUrl(String setAudioUrl) {
		this.setAudioUrl = setAudioUrl;
	}

	public String getSetLrcUrl() {
		return setLrcUrl;
	}

	public void setSetLrcUrl(String setLrcUrl) {
		this.setLrcUrl = setLrcUrl;
	}

	public String getSetAudioResp() {
		return setAudioResp;
	}

	public void setSetAudioResp(String setAudioResp) {
		this.setAudioResp = setAudioResp;
	}

	private String setAudioResp;

	public String getLinkUrl() {
		return linkUrl;
	}

	public void setLinkUrl(String linkUrl) {
		this.linkUrl = linkUrl;
	}
	
}
