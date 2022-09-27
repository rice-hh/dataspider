package com.nh.dataspider.bilibili.model;

import java.util.List;

public class MediaData {

	private MediaInfo mediaInfo;
	
	private List<MediaEpList> epList;

	public MediaInfo getMediaInfo() {
		return mediaInfo;
	}

	public void setMediaInfo(MediaInfo mediaInfo) {
		this.mediaInfo = mediaInfo;
	}

	public List<MediaEpList> getEpList() {
		return epList;
	}

	public void setEpList(List<MediaEpList> epList) {
		this.epList = epList;
	}
	
	
}
