package com.nh.dataspider.bilibili.model;

public class VideoDataPages {

	private long cid;
	
	private int page;
	
	private String from;
	
	private String part;
	
	private String vid;
	
	private String weblink;
	
	private int duration;

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getPart() {
		return part;
	}

	public void setPart(String part) {
		this.part = part;
	}

	public String getVid() {
		return vid;
	}

	public void setVid(String vid) {
		this.vid = vid;
	}

	public String getWeblink() {
		return weblink;
	}

	public void setWeblink(String weblink) {
		this.weblink = weblink;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}
	
}
