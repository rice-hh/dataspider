package com.nh.dataspider.missevan;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.missevan.model.MissEvanDrama;
import com.nh.dataspider.missevan.model.MissEvanSound;
import com.nh.dataspider.util.DataSpiderUtil;
import com.nh.dataspider.util.DateUtil;
import com.nh.dataspider.util.NumberUtil;

public class MissEvan {
	
	public static void main(String[] args) {
		 try {
			 String sountId = "5713708";//5575430
			 boolean onlyLrc = true;
			 int start = 1;
			 boolean downList = true;
			 boolean isList = true;
			 String orgName = "[南硕]";
			 if(onlyLrc) {
				 orgName = "";
			 }
			 
			 if(isList) {
				 Map<String, List<MissEvanDrama>> reMap = getMaoerDrama(sountId);
				 List<MissEvanDrama> episodesList = reMap.get("episode");
				 List<MissEvanDrama> musicList = reMap.get("music");
				 List<MissEvanDrama> ftList = reMap.get("ft");
				 if(episodesList != null && episodesList.size()>0) {
					 if(!downList) {
						 if(onlyLrc) {
							 prepareDownLrc(episodesList.get(start-1), sountId);
						 }else {
							 prepareDown(episodesList.get(start-1), orgName);
						 }
					 }else {
						 for(int i=start-1; i<episodesList.size();i++) {
							 if(onlyLrc) {
								 prepareDownLrc(episodesList.get(i), sountId);
							 }else {
								 prepareDown(episodesList.get(i), orgName);
							 }
						 }
					 }
				 }
				 
				 if(musicList != null && musicList.size()>0) {
					 for(int i=0; i<musicList.size();i++) {
						 if(onlyLrc) {
							 prepareDownLrc(musicList.get(i), sountId);
						 }else {
							 prepareDown(musicList.get(i), orgName);
						 }
					 }
				 }
				 
				 if(ftList != null && ftList.size()>0) {
					 for(int i=0; i<ftList.size();i++) {
						 if(onlyLrc) {
							 prepareDownLrc(ftList.get(i), sountId);
						 }else {
							 prepareDown(ftList.get(i), orgName);
						 }
					 }
				 }
			 }else {
				 getMissEvanSound(sountId, "妖惑", "妖惑");
			 }
			 
			 
			 System.out.println("download success！！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void prepareDownLrc (MissEvanDrama l, String sountId) throws Exception {
		 
		 String soundId = l.getSound_id();
		 String dirName = "";
		 String fileName = "";
		 
		 String name = l.getName();
		 System.out.println("name="+name);
		 name = name.replaceAll("第二季·", "");
		 name = name.replaceAll("第二季", "");
		 if(name.contains("第")) {
			 
			 int arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
			 
			 int lastIndex = l.getName().length()-1;
			 if(l.getName().contains("期")) {
				 lastIndex = l.getName().indexOf("期");
			 }
			 if(l.getName().contains("集")) {
				 lastIndex = l.getName().indexOf("集");
			 }
			 
			 if(arabicNum < 10) {
				 dirName = "0"+arabicNum;
			 }else {
				 dirName = arabicNum+"";
			 }
		 }else {
			 dirName = l.getName();
			 dirName = dirName.replace("|", "");
		 }
		 
		 dirName = dirName.replace(" ", "");
		 dirName = dirName.replace("<", "《");
		 dirName = dirName.replace(">", "》");
		 
		 getMissEvanSound(soundId, l.getDramaName()+"/lrc"+sountId, dirName);
	 }

	 public static MissEvanSound getMaoerContenat(String sountId) throws Exception {
		 
		 String url = "https://www.missevan.com/sound/getsound?soundid="+sountId;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 if(retStr == null || retStr.isEmpty()) {
			 throw new Exception("返回结果为空！");
		 }
		 
		 JSONObject json = JSONObject.parseObject(retStr);
		 
		 boolean success = (boolean) json.get("success");

		 if(!success) {
			 throw new Exception("返回结果失败！");
		 }
		 
		 String info = json.get("info").toString();
		 
		 if(info == null || info.isEmpty()) {
			 throw new Exception("info返回结果为空！");
		 }
		 
		 JSONObject infoJson = JSONObject.parseObject(info);
		 
		 String sound = infoJson.get("sound").toString();
		 
		 MissEvanSound mSound = JSONObject.parseObject(sound, MissEvanSound.class);
		
		 return mSound;
	 }
	 
	 public static Map<String, List<MissEvanDrama>> getMaoerDrama(String sountId) throws Exception {
		 
		 Map<String, List<MissEvanDrama>> reMap = new HashMap<String, List<MissEvanDrama>>();
		 
		 String url = "https://www.missevan.com/dramaapi/getdramabysound?sound_id="+sountId;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 if(retStr == null || retStr.isEmpty()) {
			 throw new Exception("返回结果为空！");
		 }
		 
		 JSONObject json = JSONObject.parseObject(retStr);
		 
		 boolean success = (boolean) json.get("success");

		 if(!success) {
			 throw new Exception("返回结果失败！");
		 }
		 
		 String info = json.get("info").toString();
		 
		 if(info == null || info.isEmpty()) {
			 throw new Exception("info返回结果为空！");
		 }
		 
		 JSONObject infoJson = JSONObject.parseObject(info);
		 
		 String drama = infoJson.get("drama").toString();
		 
		 JSONObject dramaJson = JSONObject.parseObject(drama);
		 
		 String episodes = infoJson.get("episodes").toString();
		 
		 if(episodes == null || episodes.isEmpty()) {
			 throw new Exception("episodes返回结果为空！");
		 }
		 
		 JSONObject episodesJson = JSONObject.parseObject(episodes);
		 
		 String episode = episodesJson.get("episode").toString();
		 
		 List<MissEvanDrama> mDramaList = JSONArray.parseArray(episode, MissEvanDrama.class);
		 
		 if(mDramaList != null && mDramaList.size()>0) {
			 mDramaList.stream().forEach(m -> {
				 m.setDramaName(dramaJson.getString("name"));
			 });
		 }
		 
		 String music = episodesJson.get("music").toString();
		 
		 List<MissEvanDrama> mMusicDramaList = JSONArray.parseArray(music, MissEvanDrama.class);
		 
		 if(mMusicDramaList != null && mMusicDramaList.size()>0) {
			 mMusicDramaList.stream().forEach(m -> {
				 m.setDramaName(dramaJson.getString("name"));
			 });
		 }
		 
		 String ft = episodesJson.get("ft").toString();
		 
		 List<MissEvanDrama> mFTDramaList = JSONArray.parseArray(ft, MissEvanDrama.class);
		 
		 if(mFTDramaList != null && mFTDramaList.size()>0) {
			 mFTDramaList.stream().forEach(m -> {
				 m.setDramaName(dramaJson.getString("name"));
			 });
		 }
		 
		 reMap.put("episode", mDramaList);
		 reMap.put("music", mMusicDramaList);
		 reMap.put("ft", mFTDramaList);
		 
		 return reMap;
	 }
	 
	 public static String downMp (String downloadPath, String fileName, MissEvanSound mSound) throws Exception {
		 
		 String soundurl = mSound.getSoundurl();
		 
		 fileName += soundurl.substring(soundurl.lastIndexOf("."), soundurl.length());
		 
		 DataSpiderUtil.down(soundurl, downloadPath, fileName);
		 
		 return downloadPath+fileName;
	 }
	 
	 public static void downPic (String downloadPath, String fileName, MissEvanSound mSound) throws Exception {
		 
		 String frontCover = mSound.getFront_cover();
		 
		 fileName = fileName + " 海报" + frontCover.substring(frontCover.lastIndexOf("."), frontCover.length());
		 
		 DataSpiderUtil.down(frontCover, downloadPath, fileName);
	 }
	 
	 public static void downSC (String downloadPath, String fileName, MissEvanSound mSound) throws Exception {
		 
		 fileName += " SC.txt";
		 
		 String content = mSound.getIntro().replace("<p>", "");
		 content = content.replace("<br />", "");
		 content = content.replace("</p>", "\r\n\r\n");
		 
		 DataSpiderUtil.write(content, downloadPath, fileName);
	 }
	 
	 public static void downJB (String downloadPath, String fileName, String soundid) throws Exception {
		 
		 String url = "https://www.missevan.com/sound/getdm?soundid="+soundid;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 DataSpiderUtil.write(retStr, downloadPath, "temp.xml");
		 
		 readXML(fileName, downloadPath);
		 
		 File tempFile = new File(downloadPath + "temp.xml");
		 if(tempFile.isFile() && tempFile.exists()) {
			 tempFile.delete();
		 }
	 }
	 
	 /**
	  * <d p="0,1,25,16777215,1312863760,0,eff85771,42759017">前排占位置</d>
		这行内容的意义呢
		先说内容“前排站位bai置”就不解释了
		p这个字段里面的内容：
		0,1,25,16777215,1312863760,0,eff85771,42759017
		中几个逗号分割的数据
		第一个参数是弹幕出现的时间 以秒数为单位。
		第二个参数是弹幕的模式1..3 滚动弹幕 4底端弹幕 5顶端弹幕 6.逆向弹幕 7精准定位 8高级弹幕
		第三个参数是字号， 12非常小,16特小,18小,25中,36大,45很大,64特别大
		第四个参数是字体的颜色 以HTML颜色的十位数为准
		第五个参数是Unix格式的时间戳。基准时间为 1970-1-1 08:00:00
		第六个参数是弹幕池 0普通池 1字幕池 2特殊池 【目前特殊池为高级弹幕专用】
		第七个参数是发送者的ID，用于“屏蔽此弹幕的发送者”功能
		第八个参数是弹幕在弹幕数据库中rowID 用于“历史弹幕”功能。
	  * @param fileName
	  * @param downloadPath
	  * @throws Exception
	  */
	 private static void readXML (String fileName, String downloadPath) throws Exception {
//		 List<String> contentList = new ArrayList<String>();
		 List<Map<Double, String>> contentList = new ArrayList<Map<Double, String>>();
		 Map<Double, String> contentMap = new HashMap<Double, String>();
		 Map<Double, Integer> colorMap = new HashMap<Double, Integer>();
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
                	String[] nodes = nodeName.split(",");
                	double sort = 0;
                	if(Integer.parseInt(nodes[1]) == 4) {
//                	if(Integer.parseInt(nodes[5]) == 195) {
                		sort = Double.parseDouble(nodeName.substring(0, nodeName.indexOf(",")).toString());
                		if(contentMap.containsKey(sort)) {
                			sort = sort+1;
                		}
                		contentMap.put(sort, attribute.getParent().getText());
                		colorMap.put(sort, Integer.parseInt(nodes[3]));
                	}
                }
			 }
		 }
		 
//		 downJb(contentMap, fileName, downloadPath);
		 downLrc(contentMap, fileName, downloadPath);
	 }
	 
	 private static void downJb(Map<Double, String> contentMap, String fileName, String downloadPath) throws Exception {
		 
		 if(contentMap != null && contentMap.size()>0) {
			 Set<Double> keySet = contentMap.keySet();
			 List<Double> keyList = new ArrayList<Double>();
			 keyList.addAll(keySet);
			 Collections.sort(keyList);
			 
			 if(keyList != null && keyList.size()>0) {
				 keyList.stream().forEach(c -> {
					 String name = fileName + " 野生剧本.doc";
					 if(fileName.contains("ED")) {
						 name = fileName + " 野生歌词.doc";
					 }
					 try {
						 String l = contentMap.get(c);
						 if(!(l.contains("：") || l.contains(":"))) {
							 l = "\t "+l;
						 }
						 DataSpiderUtil.write(l+"\r\n\r\n", downloadPath, name);
					 } catch (Exception e) {
						e.printStackTrace();
					 }
				 });
			 }
		 }
	 }
	 
	 
	 private static void downLrc(Map<Double, String> contentMap, String fileName, String downloadPath) throws Exception {
		 
		 if(contentMap != null && contentMap.size()>0) {
			 Set<Double> keySet = contentMap.keySet();
			 List<Double> keyList = new ArrayList<Double>();
			 keyList.addAll(keySet);
			 Collections.sort(keyList);
			 
			 if(keyList != null && keyList.size()>0) {
				 keyList.stream().forEach(c -> {
					 String name = fileName + ".lrc";
					 if(fileName.contains("ED")) {
						 name = fileName + ".lrc";
					 }
					 try {
						 int min = (int)Math.floor(Math.floor(c)/60);
						 String s = min>9 ? min+":" : "0"+min+":";
						 int mil = (int)Math.floor(c)%60;
						 String m = mil>9 ? mil+"" : "0"+mil;
						 String time = "["+s+m+(c+"").substring((c+"").indexOf("."), (c+"").length())+"]";
						 String conte = time+contentMap.get(c);
						 DataSpiderUtil.write(conte+"\r\n", downloadPath, name);
					 } catch (Exception e) {
						e.printStackTrace();
					 }
				 });
			 }
		 }
	 }
	 
	 public static void downVideo (String downloadPath, String fileName, MissEvanSound mSound) throws Exception {
		 
		 if(mSound.getVideourl() != null && !mSound.getVideourl().isEmpty()) {
			 
			 String videoUrl = mSound.getVideourl();
			 
			 fileName = fileName + videoUrl.substring(videoUrl.lastIndexOf("."), videoUrl.length());
			 
			 DataSpiderUtil.down(videoUrl, downloadPath, fileName);
			 
		 }
	 }
	 
	 private static void transDMVideo (String downPath, String fileName, String fullVideoPath, String soundid) throws Exception {
		 
		 String url = "https://www.missevan.com/sound/getdm?soundid="+soundid;
		 
		 String assFullPath = DataSpiderUtil.downDMAss(downPath, fileName, url);
		 
		 DataSpiderUtil.mergeVideoAndAss(downPath, fileName, fullVideoPath, assFullPath);
	 }
	 
	 public static void getMissEvanSound(String soundId, String dirName, String fileName) throws Exception {
		 
		 MissEvanSound mSound = getMaoerContenat(soundId);
		 
		 dirName = dirName.replace(" ", "");
		 
		 String downloadPath = "G:/nh/mymissevandown/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonthDay()+"/"+dirName+"/";
//		 System.out.println("===========音频文件下载start=============");
//		 downMp(downloadPath, fileName, mSound);
//		 System.out.println("===========音频文件下载success=============");
//		 System.out.println("===========海报文件下载start=============");
//		 downPic(downloadPath, fileName, mSound);
//		 System.out.println("===========海报文件下载success=============");
//		 System.out.println("===========SC文件下载start=============");
//		 downSC(downloadPath, fileName, mSound);
//		 System.out.println("===========SC文件下载success=============");
		 System.out.println("===========伪剧本文件下载start=============");
		 downJB(downloadPath, fileName, soundId);
		 System.out.println("===========伪剧本文件下载success=============");
//		 System.out.println("===========视频下载start=============");
//		 downVideo(downloadPath, fileName, mSound);
//		 System.out.println("===========视频下载success=============");
//		 System.out.println("===========弹幕视频下载start=============");
//		 transDMVideo(downloadPath, fileName, fullVideoPath, soundId);
//		 System.out.println("===========弹幕视频下载success=============");
	 }
	 
	 public static void prepareDown (MissEvanDrama l, String orgName) throws Exception {
		 
		 String soundId = l.getSound_id();
		 String dirName = "";
		 String fileName = "";
		 
		 String name = l.getName();
		 if(name.contains("第")) {
			 
			 int arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
			 
			 int lastIndex = l.getName().length()-1;
			 if(l.getName().contains("期")) {
				 lastIndex = l.getName().indexOf("期");
			 }
			 if(l.getName().contains("集")) {
				 lastIndex = l.getName().indexOf("集");
			 }
			 
			 if(arabicNum < 10) {
				 dirName = "《"+l.getDramaName()+"》第0"+arabicNum+l.getName().substring(lastIndex, l.getName().length());
			 }else {
				 dirName = "《"+l.getDramaName()+"》第"+arabicNum+l.getName().substring(lastIndex, l.getName().length());
			 }
			 
//			 dirName = "《"+l.getDramaName()+"》"+l.getName().substring(l.getName().indexOf("第一季-")+4, l.getName().length());
		 }else {
			 dirName = "《"+l.getDramaName()+"》"+l.getName();
		 }
		 
		 dirName = dirName.replace(" ", "");
		 dirName = dirName.replace("<", "《");
		 dirName = dirName.replace(">", "》");
		 
		 fileName = orgName+dirName;
		 
		 getMissEvanSound(soundId, l.getDramaName()+"/"+dirName, fileName);
	 }
	 
}
