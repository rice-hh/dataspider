package com.nh.dataspider.bilibili.model;

import java.util.List;

public class VideoData {

	private long aid;

	private String bvid;
	
	private int videos;
	
	private int tid;
	
	private String tname;
	
	private String pic;
	
	private String title;
	
	private String desc;
	
	private long cid;
	
	private String dynamic;
	
	private List<VideoDataPages> pages;

	public long getAid() {
		return aid;
	}

	public void setAid(long aid) {
		this.aid = aid;
	}

	public String getBvid() {
		return bvid;
	}

	public void setBvid(String bvid) {
		this.bvid = bvid;
	}

	public int getVideos() {
		return videos;
	}

	public void setVideos(int videos) {
		this.videos = videos;
	}

	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public String getTname() {
		return tname;
	}

	public void setTname(String tname) {
		this.tname = tname;
	}

	public String getPic() {
		return pic;
	}

	public void setPic(String pic) {
		this.pic = pic;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public long getCid() {
		return cid;
	}

	public void setCid(long cid) {
		this.cid = cid;
	}

	public String getDynamic() {
		return dynamic;
	}

	public void setDynamic(String dynamic) {
		this.dynamic = dynamic;
	}

	public List<VideoDataPages> getPages() {
		return pages;
	}

	public void setPages(List<VideoDataPages> pages) {
		this.pages = pages;
	}
	
}
