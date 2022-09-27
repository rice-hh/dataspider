package com.nh.dataspider.bilibili;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.bilibili.model.MediaData;
import com.nh.dataspider.bilibili.model.MediaEpList;
import com.nh.dataspider.bilibili.model.VideoData;
import com.nh.dataspider.bilibili.model.VideoDataPages;
import com.nh.dataspider.util.DataSpiderUtil;
import com.nh.dataspider.util.DateUtil;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class Bilibili {
	
	private static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36";
	
	private static String VIDEO_URL = "";
	private static String VIDEO_URL2 = "";
	
	public static void main(String[] args) throws Exception {
		
		String videoUrl = "https://www.bilibili.com/video/BV1m5411N7qS";
		boolean downList = false;
		int start = 1;
		boolean isMerge = false;
		
		VIDEO_URL = videoUrl;
		VIDEO_URL2 = videoUrl;
		
		String downPath = "E:/mybilibilidown/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonthDay()+"/";
		
		htmlParse(videoUrl, downPath, downList, start, isMerge);
	}
	
	private static void htmlParse (String url, String downPath, boolean downList, int start, boolean isMerge) throws Exception {
		
		HttpResponse res = HttpRequest.get(url).timeout(2000).execute();
		
		String html = res.body();
		
		Pattern pattern = Pattern.compile("(?<=<script>window.__INITIAL_STATE__=).*?(?=</script>)");
		
		Matcher matcher = pattern.matcher(html);
		
		if (matcher.find()) {
			
			String str = matcher.group();
			
			JSONObject json = JSONObject.parseObject(str.substring(0, str.indexOf(";(function()")));
			
			if(json.containsKey("videoData")) {
				parseVideo(json.getString("videoData"), downPath, downList, start, isMerge);
			} else if(json.containsKey("mediaInfo")) {
				parseMedia(json.toJSONString(), downPath, downList, start, isMerge);
			}
			
			System.out.println("===================下载完成！=======================");
		}else {
            throw new Exception("未匹配到视频信息，退出程序！");
        }
	}
	
	private static void parseVideo (String str, String dPath, boolean downList, int start, boolean isMerge) throws Exception {
		
		VideoData video = JSONObject.parseObject(str, VideoData.class);
		
		List<VideoDataPages> pages = video.getPages();
		
		pages = pages.stream().sorted(Comparator.comparing(VideoDataPages::getPage)).collect(Collectors.toList());
		
		if(pages != null && pages.size()>0) {
			
			if(downList) {
				for(int i=start-1; i<pages.size();i++) {
					getVideo(pages.get(i), video, dPath, isMerge);
				}
			} else {
				getVideo(pages.get(start-1), video, dPath, isMerge);
			}
		}
	}
	
	private static void getVideo (VideoDataPages p, VideoData video, String dPath, boolean isMerge) throws Exception {
		
		String videoUrl = "https://api.bilibili.com/x/player/playurl?qn=0&type=&otype=json&fourk=1&fnver=0&fnval=80";
		
//		video.setTitle("rrrr");
		
		String downPath = dPath + video.getTitle()+"/"+p.getPart()+"/";
		
		String retStr = DataSpiderUtil.requestUrl(videoUrl + "&cid="+p.getCid()+"&bvid="+video.getBvid());
		
		JSONObject json = JSONObject.parseObject(retStr);
		
		System.out.println(json.toJSONString());
		
		if(json.getInteger("code") == 0) {
			
			JSONObject dashData = json.getJSONObject("data").getJSONObject("dash");
			
			VIDEO_URL = VIDEO_URL2+"?p="+p.getPage();
			
			String fullVideoPath = downVideo(video.getTitle(), downPath, dashData);
			
			downJB(downPath, video.getTitle(), video.getCid()+"", false);
			
			if(isMerge) {
				
				String assFullPath = downDMAss(downPath, video.getTitle(), video.getCid()+"");
				
				DataSpiderUtil.mergeVideoAndAss(downPath, video.getTitle(), fullVideoPath, assFullPath);
			}
		}
	}
	
	private static void parseMedia (String str, String dPath, boolean downList, int start, boolean isMerge) throws Exception {
		
		MediaData media = JSONObject.parseObject(str, MediaData.class);
		
		List<MediaEpList> epList = media.getEpList();
		
		epList = epList.stream().sorted(Comparator.comparing(MediaEpList::getI)).collect(Collectors.toList());
		
		if(epList != null && epList.size()>0) {
			
			if(downList) {
				for(int i=start-1; i<epList.size();i++) {
					getMedia(epList.get(i), media, dPath, isMerge);
				}
			} else {
				getMedia(epList.get(start-1), media, dPath, isMerge);
			}
		}
	}
	
	
	private static String getMedia(MediaEpList ep, MediaData media, String dPath, boolean isMerge) throws Exception {
		
		String mediaUrl = "https://api.bilibili.com/pgc/player/web/playurl?qn=0&type=&otype=json&fourk=1&fnver=0&fnval=80";
		
		String name = ep.getTitleFormat()+ (ep.getBadge().equals("") ? "" : "·"+ep.getBadge());
		
		String downPath = dPath + media.getMediaInfo().getTitle()+"/"+name+"/";
		
		String retStr = DataSpiderUtil.requestUrl(mediaUrl + "&cid="+ep.getCid()+"&avid="+ep.getAid());
		
		JSONObject json = JSONObject.parseObject(retStr);
		
		System.out.println(json.toJSONString());
		
		if(json.getInteger("code") == 0) {
			
			JSONObject dashData = json.getJSONObject("result").getJSONObject("dash");
			
			VIDEO_URL = "https://www.bilibili.com/bangumi/play/ep"+ep.getId();
			
			String fullVideoPath = downVideo(media.getMediaInfo().getTitle()+"·"+name, downPath, dashData);
			
			if(isMerge) {

				String assFullPath = downDMAss(downPath, media.getMediaInfo().getTitle()+"·"+name, ep.getCid()+"");
				
				DataSpiderUtil.mergeVideoAndAss(downPath, media.getMediaInfo().getTitle()+"·"+name, fullVideoPath, assFullPath);
			}
		}
		
		return null;
	}
	
	
	private static String downVideo(String title, String downPath, JSONObject dashData) throws Exception{
		boolean videoFlag = true;
		// 获取视频的基本信息
        JSONArray videoInfoArr = dashData.getJSONArray("video");
        String videoBaseUrl = videoInfoArr.getJSONObject(0).getString("baseUrl");
        String videoBaseRange = videoInfoArr.getJSONObject(0).getJSONObject("SegmentBase").getString("Initialization");
        HttpResponse videoRes = HttpRequest.get(videoBaseUrl)
                .header("Referer", VIDEO_URL)
                .header("Range", "bytes=" + videoBaseRange)
                .header("User-Agent", USER_AGENT)
                .timeout(2000)
                .execute();
        String videoSize = "";
        if(videoRes.header("Content-Range") != null && videoRes.header("Content-Range").split("/").length>1) {
        	videoSize = videoRes.header("Content-Range").split("/")[1];
        }else {
        	videoFlag = false;
        }
        

        boolean audioFlag = true;
        // 获取音频基本信息
        JSONArray audioInfoArr = dashData.getJSONArray("audio");
        String audioBaseUrl = audioInfoArr.getJSONObject(0).getString("baseUrl");
        String audioBaseRange = audioInfoArr.getJSONObject(0).getJSONObject("SegmentBase").getString("Initialization");
        HttpResponse audioRes = HttpRequest.get(audioBaseUrl)
                .header("Referer", VIDEO_URL)
                .header("Range", "bytes=" + audioBaseRange)
                .header("User-Agent", USER_AGENT)
                .timeout(2000)
                .execute();
        String audioSize = "";
        if(audioRes.header("Content-Range") != null && audioRes.header("Content-Range").split("/").length>1) {
        	audioSize = audioRes.header("Content-Range").split("/")[1];
        }else {
        	audioFlag = false;
        }
        
        return downloadFile(title, downPath, videoBaseUrl, audioBaseUrl, videoSize, audioSize, videoFlag, audioFlag);
    }
	
	/**
	 * 
	 * @param title
	 * @param downPath
	 * @param videoUrl
	 * @param audioUrl
	 * @param videoSize
	 * @param audioSize
	 * @param videoFlag
	 * @param audioFlag
	 * @return 下载的视频的完整路径（路径+视频名称）
	 * @throws Exception
	 */
	private static String downloadFile(String title, String downPath, String videoUrl, String audioUrl, String videoSize, String audioSize, boolean videoFlag, boolean audioFlag) throws Exception {
        // 保存音视频的位置
        File fileDir = new File(downPath);
        if (!fileDir.exists()){
            fileDir.mkdirs();
        }

        String videoPath = downPath + "_video.mp4";
        if(videoFlag) {
        	// 下载视频
            File videoFile = new File(videoPath);
            if (!videoFile.exists()){
                System.out.println("--------------开始下载视频文件--------------");
                HttpResponse videoRes = HttpRequest.get(videoUrl)
                        .header("Referer", VIDEO_URL)
                        .header("Range", "bytes=0-" + videoSize)
                        .header("User-Agent", USER_AGENT)
                        .execute();
                videoRes.writeBody(videoFile);
                System.out.println("--------------视频文件下载完成--------------");
            }
        }else {
        	System.out.println("--------------开始下载视频文件--------------");
        	DataSpiderUtil.down(videoUrl, downPath, "_video.mp4");
        	System.out.println("--------------视频文件下载完成--------------");
        }

        String audioPath = downPath + "_audio.mp4";
        if(audioFlag) {
        	// 下载音频
            File audioFile = new File(audioPath);
            if (!audioFile.exists()){
                System.out.println("--------------开始下载音频文件--------------");
                HttpResponse audioRes = HttpRequest.get(audioUrl)
                        .header("Referer", VIDEO_URL)
                        .header("Range", "bytes=0-" + audioSize)
                        .header("User-Agent", USER_AGENT)
                        .execute();
                audioRes.writeBody(audioFile);
                System.out.println("--------------音频文件下载完成--------------");
            }
        }else {
        	System.out.println("--------------开始下载音频文件--------------");
        	DataSpiderUtil.down(audioUrl, downPath, "_audio.mp4");
        	System.out.println("--------------音频文件下载完成--------------");
        }
        
        return DataSpiderUtil.mergeVidoeAndAudio(downPath, title, videoPath, audioPath);
    }
	
	public static void downJB (String downloadPath, String fileName, String cid, boolean dmListOrder) throws Exception {
		 
		 String url = "http://comment.bilibili.com/"+cid+".xml";
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 DataSpiderUtil.write(retStr, downloadPath, "temp.xml");
		 
		 readXML(fileName, downloadPath, dmListOrder);
		 
		 File tempFile = new File(downloadPath + "temp.xml");
		 if(tempFile.isFile() && tempFile.exists()) {
			 tempFile.delete();
		 }
	 }
	
	public static String downDMAss (String downloadPath, String fileName, String cid) throws Exception {
		 
		 String url = "http://comment.bilibili.com/"+cid+".xml";
		 
		 return DataSpiderUtil.downDMAss(downloadPath, fileName, url);
	}
	
	private static void readXML (String fileName, String downloadPath, boolean dmListOrder) throws Exception {
		 List<String> contentList = new ArrayList<String>();
		 //1.创建Reader对象
		 SAXReader reader = new SAXReader();
		 //2.加载xml
		 Document document = reader.read(new File(downloadPath+"temp.xml"));
		 //3.获取根节点
		 Element rootElement = document.getRootElement();
		 Iterator iterator = rootElement.elementIterator();
		 while (iterator.hasNext()){
			 Element stu = (Element) iterator.next();
			 List<Attribute> attributes = stu.attributes();
           
			 if(attributes != null && attributes.size()>0) {
               for (Attribute attribute : attributes) {
               	String nodeName = attribute.getValue();
               	if(nodeName.contains(",4,")) {
               		contentList.add(attribute.getParent().getText());
               	}
               }
			 }
		 }
		 
		 if(contentList != null && contentList.size()>0) {
			 //如果是倒序，则要重新排列字幕顺序
			 if(!dmListOrder) {
				 Collections.reverse(contentList);
			 }
			 contentList.stream().forEach(c -> {
				 String name = fileName + " 野生剧本.doc";
				 if(fileName.contains("ED")) {
					 name = fileName + " 野生歌词.doc";
				 }
				 try {
					 if(!(c.contains("：") || c.contains(":"))) {
						 c = "\t "+c;
					 }
					DataSpiderUtil.write(c+"\r\n\r\n", downloadPath, name);
				 } catch (Exception e) {
					e.printStackTrace();
				 }
			 });
		 }
	 }
}
