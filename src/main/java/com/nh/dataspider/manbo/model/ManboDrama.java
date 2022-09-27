package com.nh.dataspider.manbo.model;

import java.util.List;

public class ManboDrama {
	
	private long radioDramaId;
	
	private String radioDramaIdStr;
	
	private String title;
	
	private int bizType;
	
	private String desc;
	
	private String coverPic;
	
	private List<ManboDramaSet> setRespList;
	
	private List<ManboCVResp> cvRespList;
	

	public long getRadioDramaId() {
		return radioDramaId;
	}

	public void setRadioDramaId(long radioDramaId) {
		this.radioDramaId = radioDramaId;
	}

	public String getRadioDramaIdStr() {
		return radioDramaIdStr;
	}

	public void setRadioDramaIdStr(String radioDramaIdStr) {
		this.radioDramaIdStr = radioDramaIdStr;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getCoverPic() {
		return coverPic;
	}

	public void setCoverPic(String coverPic) {
		this.coverPic = coverPic;
	}

	public List<ManboDramaSet> getSetRespList() {
		return setRespList;
	}

	public void setSetRespList(List<ManboDramaSet> setRespList) {
		this.setRespList = setRespList;
	}

	public List<ManboCVResp> getCvRespList() {
		return cvRespList;
	}

	public void setCvRespList(List<ManboCVResp> cvRespList) {
		this.cvRespList = cvRespList;
	}

}
