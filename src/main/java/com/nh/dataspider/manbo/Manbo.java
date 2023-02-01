package com.nh.dataspider.manbo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.nh.dataspider.manbo.model.ManboCVResp;
import com.nh.dataspider.manbo.model.ManboDrama;
import com.nh.dataspider.manbo.model.ManboDramaSet;
import com.nh.dataspider.missevan.model.MissEvanDrama;
import com.nh.dataspider.missevan.model.MissEvanSound;
import com.nh.dataspider.util.DataSpiderUtil;
import com.nh.dataspider.util.DateUtil;
import com.nh.dataspider.util.NumberUtil;

public class Manbo {
	
	public static void main(String[] args) {

		 try {
//			 String radioDramaSetId = "1646390531103653974";
			 String radioDramaId = "1768011429262131230";
			 boolean onlyLrc = true;
			 int start = 1;
			 boolean downList = true;
			 
			 String orgName = "[莘羽]";
			 if(onlyLrc) {
				 orgName = radioDramaId;
			 }
			 
//			 ManboDrama manboDrama = getManboDramaSet(radioDramaSetId);
			 ManboDrama manboDrama = getManboDrama(radioDramaId);
			 
			 getManboDramaSet(manboDrama, orgName, start, downList, onlyLrc);
			 
			 
			 System.out.println("download success！！");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ManboDrama getManboDrama(String radioDramaId) throws Exception {
		 
		 String url = "https://manbo.hongrenshuo.com.cn/api/v11/radio/drama/h5/detail?radioDramaId="+radioDramaId;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 if(retStr == null || retStr.isEmpty()) {
			 throw new Exception("返回结果为空！");
		 }
		 
		 JSONObject json = JSONObject.parseObject(retStr);
		 
		 boolean success = (boolean) json.getJSONObject("h").get("success");

		 if(!success) {
			 throw new Exception("返回结果失败！");
		 }
		 
		 String sound = json.getJSONObject("b").toString();
		 
		 System.out.println(sound);
		 
		 ManboDrama mDrama = JSONObject.parseObject(sound, ManboDrama.class);
		
		 return mDrama;
	 }

	 public static ManboDrama getManboDramaSet(String radioDramaSetId) throws Exception {
		 
		 String url = "https://manbo.hongrenshuo.com.cn/api/v207/radio/drama/set/h5/detail?radioDramaSetId="+radioDramaSetId;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 if(retStr == null || retStr.isEmpty()) {
			 throw new Exception("返回结果为空！");
		 }
		 
		 JSONObject json = JSONObject.parseObject(retStr);
		 
		 boolean success = (boolean) json.getJSONObject("h").get("success");

		 if(!success) {
			 throw new Exception("返回结果失败！");
		 }
		 
		 String sound = json.getJSONObject("b").getString("radioDramaResp").toString();
		 
		 System.out.println(sound);
		 
		 ManboDrama mDrama = JSONObject.parseObject(sound, ManboDrama.class);
		
		 return mDrama;
	 }
	 
	 public static void getManboDramaSet(ManboDrama manboDrama, String orgName, int start, boolean downList, boolean onlyLrc) throws Exception {
		 
		 if(manboDrama != null) {
			 
			 List<ManboDramaSet> setRespList = manboDrama.getSetRespList();
			 
			 if(setRespList != null && setRespList.size()>0) {
				 if(downList) {
					 for(int i=start-1; i<setRespList.size(); i++) {
						 downManbo(manboDrama.getTitle(), orgName, setRespList.get(i), manboDrama, onlyLrc);
					 }
				 }else {
					 downManbo(manboDrama.getTitle(), orgName, setRespList.get(start-1), manboDrama, onlyLrc);
				 }
			 }
			 
			 
		 }
	 }
	 
	 public static String prepareFileName (String title, String orgName, String name) throws Exception {
		 
		 String dirName = "";
		 
//		 if(name.contains("第")) {
		 if(name.indexOf("第")==0) {
			 
//			 int arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
//			 int arabicNum = Integer.parseInt(name.substring(1,name.length()-1));
			 int arabicNum = 0;
			 char [] ch = name.toCharArray();
			 int i=-1;
			 boolean isChineseNum = false;
			 for (char c : ch) {
				 i++;
				 if(i==1) {
					 if(NumberUtil.isChineseNum(String.valueOf(c))) {
						 isChineseNum = true;
					 }
				 }
				 if(!NumberUtil.isNum(String.valueOf(c)) && i>1) {
					 break;
				 }
			 }
			 if(isChineseNum) {
				 arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
			 }else {
				 arabicNum = Integer.parseInt(name.substring(1,i));
			 }
			 
			 int lastIndex = name.length()-1;
			 if(name.contains("期")) {
				 lastIndex = name.indexOf("期");
			 }
			 if(name.contains("集")) {
				 lastIndex = name.indexOf("集");
			 }
			 
			 if(arabicNum < 10) {
				 dirName = "《"+title+"》第0"+arabicNum+name.substring(lastIndex, name.length());
			 }else {
				 dirName = "《"+title+"》第"+arabicNum+name.substring(lastIndex, name.length());
			 }
		 }else {
			 dirName = "《"+title+"》"+name;
		 }
		 
		 return orgName+dirName;
	 }
	 
	 public static String prepareLrcName (String title, String orgName, String name) throws Exception {
		 
		 String dirName = "";
		 System.out.println("name="+name);
//		 if(name.contains("第")) {
		 if(name.indexOf("第")==0) {
			 
			 int arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
//			 int arabicNum = Integer.parseInt(name.substring(1,name.length()-1));
//			 int arabicNum = 0;
			 char [] ch = name.toCharArray();
			 int i=-1;
			 boolean isChineseNum = false;
			 for (char c : ch) {
				 i++;
				 if(i==1) {
					 if(NumberUtil.isChineseNum(String.valueOf(c))) {
						 isChineseNum = true;
					 }
				 }
				 if(!NumberUtil.isNum(String.valueOf(c)) && i>1) {
					 break;
				 }
			 }
			 if(isChineseNum) {
				 arabicNum = NumberUtil.chineseNumToArabicNum(name.substring(1,name.length()-1));
			 }else {
				 arabicNum = Integer.parseInt(name.substring(1,i));
			 }
			 
			 int lastIndex = name.length()-1;
			 if(name.contains("期")) {
				 lastIndex = name.indexOf("期");
			 }
			 if(name.contains("集")) {
				 lastIndex = name.indexOf("集");
			 }
			 
			 if(arabicNum < 10) {
				 dirName = "0"+arabicNum;
			 }else {
				 dirName = arabicNum+"";
			 }
		 }else {
			 dirName = name;
		 }
		 
		 return orgName+dirName;
	 }
	 
	 public static void downManbo(String title, String orgName, ManboDramaSet manboDramaSet, ManboDrama manboDrama, boolean onlyLrc) throws Exception {
		 
		 String fileName = "";
		 
		 String downloadPath = "";
		 if(onlyLrc) {
			 downloadPath = "G:/nh/man&missevan/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonthDay()+"/"+title+"/";
		 }else {
			 fileName = prepareFileName(title, orgName, manboDramaSet.getSetTitle());
			 
			 fileName = fileName.substring(fileName.lastIndexOf("]")+1, fileName.length());
			 downloadPath = "G:/nh/man&missevan/"+DateUtil.getCurrentYear()+"/"+DateUtil.getCurrentMonthDay()+"/"+title+"/"+"/"+fileName+"/";
		 }
		 
//		 System.out.println("===========音频文件下载start=============");
		 if(!onlyLrc) {
			 String fullVideoPath = downMp3(title, orgName, downloadPath, manboDramaSet);
		 }
//		 System.out.println("===========音频文件下载success=============");
//		 
//		 System.out.println("===========海报文件下载start=============");
		 if(!onlyLrc) {
			 downPic(title, orgName, downloadPath, manboDramaSet);
		 }
//		 System.out.println("===========海报文件下载success=============");
		 
		 System.out.println("===========字幕文件下载start=============");
		 if(onlyLrc) {
			 downOnlyLrc(title, orgName, downloadPath, manboDramaSet);
		 }else {
			 downLrc(title, orgName, downloadPath, manboDramaSet);
		 }
		 System.out.println("===========字幕文件下载success=============");
		 
//		 System.out.println("===========SC文件下载start=============");
		 if(!onlyLrc) {
			 downSC(title, orgName, downloadPath, manboDramaSet, manboDrama);
		 }
//		 System.out.println("===========SC文件下载success=============");
//		 System.out.println("===========伪剧本文件下载start=============");
//		 downJB(downloadPath, fileName, soundId, dmListOrder);
//		 System.out.println("===========伪剧本文件下载success=============");
//		 System.out.println("===========视频下载start=============");
		 if(!onlyLrc) {
			 downVideo(title, orgName, downloadPath, manboDramaSet);
		 }
//		 System.out.println("===========视频下载success=============");
//		 System.out.println("===========弹幕视频下载start=============");
//		 transDMVideo(downloadPath, fileName, fullVideoPath, soundId);
//		 System.out.println("===========弹幕视频下载success=============");
		 
	 }
	 
	 public static String downMp3 (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet) throws Exception {
		 
		 JSONObject setAudioResp = JSONObject.parseObject(manboDramaSet.getSetAudioResp());
		 
		 String fileName = prepareFileName(title, orgNamem, manboDramaSet.getSetTitle());
		 
		 if(setAudioResp.containsKey("superQualityResp")) {
			 
			//highQualityResp：高音质；normalQualityResp标准音质；superQualityResp：无损音质
			 String audioUrl = setAudioResp.getJSONObject("superQualityResp").get("audioUrl").toString();
			 
			 fileName += audioUrl.substring(audioUrl.lastIndexOf("."), audioUrl.length());
			 
			 DataSpiderUtil.down(audioUrl, downloadPath, fileName);
		 }else if(setAudioResp.containsKey("highQualityResp")) {
			 String audioUrl = setAudioResp.getJSONObject("highQualityResp").get("audioUrl").toString();
			 
			 fileName += audioUrl.substring(audioUrl.lastIndexOf("."), audioUrl.length());
			 
			 DataSpiderUtil.down(audioUrl, downloadPath, fileName);
		 }
		 
		 return downloadPath+fileName;
	 }
	 
	 public static void downPic (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet) throws Exception {
		 
		 String frontCover = manboDramaSet.getSetPic();
		 
		 String fileName = prepareFileName(title, orgNamem, manboDramaSet.getSetTitle());
		 
		 fileName += " 海报" + frontCover.substring(frontCover.lastIndexOf("."), frontCover.length());
		 
		 DataSpiderUtil.down(frontCover, downloadPath, fileName);
	 }
	 
	 public static void downSC (String downloadPath, String fileName, MissEvanSound mSound) throws Exception {
		 
		 fileName += " SC.txt";
		 
		 String content = mSound.getIntro().replace("<p>", "");
		 content = content.replace("<br />", "");
		 content = content.replace("</p>", "\r\n\r\n");
		 
		 DataSpiderUtil.write(content, downloadPath, fileName);
	 }
	 
	 public static void downSC (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet, ManboDrama manboDrama) throws Exception {
		 
		 String fileName = prepareFileName(title, orgNamem, manboDramaSet.getSetTitle());
		 
		 fileName += " SC.txt";
		 
		 List<ManboCVResp> cvRespList = manboDrama.getCvRespList();
		 
		 if(cvRespList != null && cvRespList.size()>0) {
			 String content = "";
			 for(ManboCVResp cv: cvRespList) {
				 content += cv.getCvNickname()+" "+cv.getRole()+"\r";
			 }
			 
			 DataSpiderUtil.write(content, downloadPath, fileName);
		 }
	 }
	 
	 public static void downLrc (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet) throws Exception {
		 
		 String lrcUrl = manboDramaSet.getSetLrcUrl();
		 
		 if(!lrcUrl.isEmpty()) {
			 String fileName = prepareFileName(title, orgNamem, manboDramaSet.getSetTitle());
			 
			 fileName += lrcUrl.substring(lrcUrl.lastIndexOf("."), lrcUrl.length());
			 
			 DataSpiderUtil.down(lrcUrl, downloadPath, fileName);
		 }
	 }
	 
	 public static void downOnlyLrc (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet) throws Exception {
		 
		 String lrcUrl = manboDramaSet.getSetLrcUrl();
		 
		 if(!lrcUrl.isEmpty()) {
			 String fileName = prepareLrcName(title, "", manboDramaSet.getSetTitle());
			 
			 fileName += lrcUrl.substring(lrcUrl.lastIndexOf("."), lrcUrl.length());
			 
			 fileName = fileName.replaceAll("\"", "");
			 
			 DataSpiderUtil.down(lrcUrl, downloadPath+"lrc"+orgNamem+"/", fileName);
		 }
	 }
	 
	 public static void downJB (String downloadPath, String fileName, String soundid, boolean dmListOrder) throws Exception {
		 
		 String url = "https://www.missevan.com/sound/getdm?soundid="+soundid;
		 
		 String retStr = DataSpiderUtil.requestUrl(url);
		 
		 DataSpiderUtil.write(retStr, downloadPath, "temp.xml");
		 
		 readXML(fileName, downloadPath, dmListOrder);
		 
		 File tempFile = new File(downloadPath + "temp.xml");
		 if(tempFile.isFile() && tempFile.exists()) {
			 tempFile.delete();
		 }
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
                	if(nodeName.contains(",195,")) {
//                	if(nodeName.contains(",240,")) {
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
	 
	 public static void downVideo (String title, String orgNamem, String downloadPath, ManboDramaSet manboDramaSet) throws Exception {
		 
		 if(manboDramaSet.getLinkUrl() != null) {
			 
			 String linkUrl = manboDramaSet.getLinkUrl();
			 String videoId = linkUrl.substring(linkUrl.indexOf("video_id=")+"video_id=".length(), linkUrl.length());
			 String videoUrl = "https://hrslive.hongrenshuo.com.cn/videoWatermarkDir/"+videoId+".mp4";
			 
			 String fileName = prepareFileName(title, orgNamem, manboDramaSet.getSetTitle());
			 
			 fileName += videoUrl.substring(videoUrl.lastIndexOf("."), videoUrl.length());
			 
			 DataSpiderUtil.down(videoUrl, downloadPath, fileName);
		 }
	 }
	 
	 private static void transDMVideo (String downPath, String fileName, String fullVideoPath, String soundid) throws Exception {
		 
		 String url = "https://www.missevan.com/sound/getdm?soundid="+soundid;
		 
		 String assFullPath = DataSpiderUtil.downDMAss(downPath, fileName, url);
		 
		 DataSpiderUtil.mergeVideoAndAss(downPath, fileName, fullVideoPath, assFullPath);
	 }
	 
}
