package com.nh.dataspider.audio;

import java.io.File;

import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;

public class ReadAudio {

	
	public static void main(String[] args)  {
		File file = new File("");
		MultimediaObject m = new MultimediaObject(file);
		try {
			MultimediaInfo info = m.getInfo();
			long ls = info.getDuration();
			System.out.println("此视频时长为:" + ls / 60000 + "分" + ls / 1000 + "秒！");
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		
	}
}
